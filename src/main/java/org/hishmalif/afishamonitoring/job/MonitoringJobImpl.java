package org.hishmalif.afishamonitoring.job;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.hishmalif.afishamonitoring.model.Event;
import org.hishmalif.afishamonitoring.model.Result;
import org.hishmalif.afishamonitoring.time.TimeService;
import org.hishmalif.afishamonitoring.model.ResultTypes;
import org.hishmalif.afishamonitoring.utill.ParseMessage;
import org.hishmalif.afishamonitoring.time.TimeException;
import org.hishmalif.afishamonitoring.dto.MonitoringObject;
import org.hishmalif.afishamonitoring.job.partner.CheckPartner;
import org.hishmalif.afishamonitoring.job.partner.CheckPartnerFactory;

import java.util.*;
import java.time.Instant;
import java.time.ZonedDateTime;

@Service
@AllArgsConstructor
@Log4j2
public class MonitoringJobImpl implements MonitoringJob {
    private final TimeService time;
    private final Set<MonitoringObject.Request> objects = new HashSet<>(); //TODO Заменить на Redis
    private final Map<MonitoringObject.Request, String> retryObjects = new HashMap<>();

    @Override
    public boolean addToMonitoring(MonitoringObject.Request request) {
        objects.removeIf(o -> o.getId().equals(request.getId()) && o.getForeignId().equals(request.getForeignId()));
        log.info("Добавлено событие {} в пул мониторинга!", request.getId());
        return objects.add(request);
    }

    @Override
    public boolean deleteFromMonitoring(String id) {
        log.info("Cобытие {} удалено из пула мониторинга!", id);
        retryObjects.entrySet().parallelStream()
                .filter(entry -> entry.getKey().getId().equals(id))
                .forEach(entry -> retryObjects.remove(entry.getKey()));
        return objects.removeIf(o -> o.getId().equals(id));
    }

    @Override
    public MonitoringObject.Response checkOnce(MonitoringObject.Request request) {
        return getResults(request);
    }

    @Scheduled(fixedRateString = "PT01H")
    private void checkRepeatable() {
        objects.parallelStream()
                .filter(o -> o.getDateFrom() != null && o.getDateTo() != null)
                .filter(o -> o.getDateFrom().isBefore(Instant.now()))
                .filter(o -> o.getDateTo().isAfter(Instant.now()))
                .forEach(o -> {
                    try {
                        MonitoringObject.Response result = getResults(o);
                        String messageId = time.sendMessage(time.getChannelId(), result.getMessage());
                        if (result.getErrors() != null && !result.getErrors().isEmpty()) {
                            retryObjects.put(o, messageId);
                            objects.remove(o);
                        }
                    } catch (TimeException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Scheduled(fixedRateString = "PT30M")
    private void retryCheck() {
        retryObjects.entrySet().parallelStream()
                .filter(entry -> entry.getKey().getDateFrom() != null && entry.getKey().getDateTo() != null)
                .filter(entry -> entry.getKey().getDateFrom().isBefore(Instant.now()))
                .filter(entry -> entry.getKey().getDateTo().isAfter(Instant.now()))
                .forEach(entry -> {
                    try {
                        MonitoringObject.Response result = getResults(entry.getKey());
                        time.sendMessage(time.getChannelId(), result.getMessage(), entry.getValue());
                        if (result.getErrors() == null || result.getErrors().isEmpty()) {
                            retryObjects.remove(entry.getKey());
                            objects.add(entry.getKey());
                        }
                    } catch (TimeException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private MonitoringObject.Response getResults(MonitoringObject.Request request) {
        MonitoringObject.Message messageText = new MonitoringObject.Message();
        log.info("Запущена проверка для события {}", request.getId());

        List<Result> results = checkGetData(request);
        List<String> errors = results.parallelStream()
                .filter(result -> result.getType().equals(ResultTypes.ERROR))
                .map(Result::getMessage)
                .toList();
        List<String> warnings = results.parallelStream()
                .filter(result -> result.getType().equals(ResultTypes.WARN))
                .map(Result::getMessage)
                .toList();

        String message = ParseMessage.getMessage(messageText.getMain()
                + (!errors.isEmpty() ? messageText.getErrors() : "")
                + (!warnings.isEmpty() ? messageText.getWarnings() : "")
                + (errors.isEmpty() && warnings.isEmpty() ? messageText.getOk() : ""), request.getId(), errors, warnings);
        return new MonitoringObject.Response(message, errors, warnings);
    }

    private List<Result> checkGetData(MonitoringObject.Request request) {
        Event event;
        List<Event.Place> places;
        List<Result> results = new ArrayList<>();
        CheckPartner partner = CheckPartnerFactory.getCheckPartner(request);

        try {
            event = partner.getEvent(request);
            results.addAll(checkEvent(event, request));
            if (checkError(results)) {
                return results;
            }

            places = partner.getPlaces(event);
            results.addAll(checkPlace(event, places));
            if (checkError(results)) {
                return results;
            }

            List<String> schemas = partner.getSchemas(event);
            results.add(checkSchemas(event, schemas));
            if (checkError(results)) {
                return results;
            }
        } catch (MonitoringException e) {
            results.add(new Result(ResultTypes.ERROR, e.getMessage()));
            return results;
        }
        return results;
    }

    private Boolean checkError(List<Result> results) {
        return results.parallelStream().anyMatch(result -> result.getType().equals(ResultTypes.ERROR));
    }

    private Result checkSchemas(Event event, List<String> schemas) {
        Result result = new Result(ResultTypes.OK);
        if (schemas == null || schemas.isEmpty()) {
            result = new Result(ResultTypes.ERROR, "Не получен ни один зал для события {}!", event.getId());
        }
        return result;
    }

    private List<Result> checkPlace(Event event, List<Event.Place> places) {
        List<Result> results = new ArrayList<>();
        List<String> placeIds = places.parallelStream()
                .map(Event.Place::getId)
                .toList();

        event.getSchedulesPlaces().parallelStream()
                .map(Event.Place::getId)
                .filter(id -> !placeIds.contains(id))
                .forEach(id -> results.add(new Result(ResultTypes.ERROR, "Не получаем площадку {} от партнера при импорте!", id)));
        return results;
    }

    private List<Result> checkEvent(Event event, MonitoringObject.Request request) {
        List<Result> results = new ArrayList<>();
        List<Event.Place> schedulesPlaces = event.getSchedulesPlaces();

        if (schedulesPlaces.isEmpty()) {
            results.add(new Result(ResultTypes.ERROR, "У события не указана ни одна площадка проведения!"));
        }
        if (schedulesPlaces.parallelStream()
                .allMatch(place -> place.getSchedules() == null || place.getSchedules().isEmpty())) {
            results.add(new Result(ResultTypes.ERROR, "У события не указан ни один сеанс проведения!"));
        }
        if (event.getDescription() == null || event.getDescription().equals("") || Objects.equals(event.getImageUrl(), "")) {
            results.add(new Result(ResultTypes.WARN, "У события отсуствует постер или описание"));
        }
        schedulesPlaces.parallelStream()
                .filter(place -> place.getSchedules() != null)
                .flatMap(place -> place.getSchedules().parallelStream())
                .peek(schedule -> {
                    if (!request.getSlotForeignIds().contains(schedule.getId())) {
                        results.add(new Result(ResultTypes.ERROR, "Не получаем слот от партнера!"));
                    }
                })
                .filter(schedule -> request.getSlotForeignIds().contains(schedule.getId()))
                .forEach(schedule -> results.add(checkSchedules(schedule)));
        return results;
    }

    private Result checkSchedules(Event.Schedule schedule) {
        String id = schedule.getId();
        Result result = new Result(ResultTypes.OK);

        if (!schedule.isSaleAvailable()) {
            return new Result(ResultTypes.ERROR, "Закрыты продажи по слоту {}", id);
        }
        if (schedule.getAvailableTicketsCount() == null || schedule.getAvailableTicketsCount() <= 0) {
            return new Result(ResultTypes.ERROR, "Нет доступных билетов по слоту {}", id);
        }
        if (schedule.getSaleEndDate().isBefore(ZonedDateTime.now())) {
            result = new Result(ResultTypes.WARN, "Продажи по слоту {} завершины!", id);
        }
        if (schedule.getSlotStartDate().isBefore(ZonedDateTime.now())) {
            result = new Result(ResultTypes.WARN, "Слот {} уже начался!", id);
        }
        if (schedule.getMinPrice() == null || schedule.getMinPrice() <= 10
                || schedule.getMaxPrice() == null || schedule.getMaxPrice() <= 10) {
            result = new Result(ResultTypes.WARN, "Для слота {} не указана минимальная цена или она меньше 10", id);
        }
        return result;
    }
}