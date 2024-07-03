package org.hishmalif.afishamonitoring.job.partner.rambler;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.hishmalif.afishamonitoring.model.Event;
import org.hishmalif.afishamonitoring.dto.RamblerResponse;
import org.hishmalif.afishamonitoring.dto.MonitoringObject;
import org.hishmalif.afishamonitoring.job.MonitoringException;
import org.hishmalif.afishamonitoring.job.partner.CheckPartner;
import org.hishmalif.afishamonitoring.config.rambler.RamblerProperties;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class RamblerJob implements CheckPartner {
    private RamblerProperties properties;
    private RamblerClient rambler;

    @Override
    public List<Event.Place> getPlaces(Event event) throws MonitoringException {
        List<Event.Place> placeList = new ArrayList<>();
        Set<String> cities = event.getSchedulesPlaces().parallelStream()
                .map(Event.Place::getCityId)
                .collect(Collectors.toSet());

        try {
            cities.forEach(city -> rambler.getPlaces(city).parallelStream()
                    .map(RamblerResponse.Place::getId)
                    .forEach(id -> placeList.add(new Event.Place(id, city))));
        } catch (Exception e) {
            throw new MonitoringException("Ошибка при получении площадок для события: {}! Ошибка: {}", event.getId(), e.getMessage());
        }
        return placeList;
    }

    @Override
    public Event getEvent(MonitoringObject.Request object) throws MonitoringException {
        Event event = new Event(object.getForeignId());

        try {
            RamblerResponse.Event eventResponse = rambler.getEvent(object.getForeignId());
            event.setDescription(eventResponse.getDescription());
            event.setImageUrl(eventResponse.getImages().parallelStream()
                    .filter(RamblerResponse.Image::isPoster)
                    .map(RamblerResponse.Image::getUrl)
                    .findFirst()
                    .orElse(""));
            setSchedules(event);
        } catch (Exception e) {
            throw new MonitoringException("Ошибка при получении события: {} от партнера {}! \n {}", object.getForeignId(),
                    object.getPartner(),
                    e.getMessage().replace(properties.getWidgetKey(), "***"));
        }
        return event;
    }

    @Override
    public List<String> getSchemas(Event event) throws MonitoringException {
        try {
            return event.getSchedulesPlaces().parallelStream()
                    .filter(place -> place.getSchedules() != null)
                    .flatMap(place -> place.getSchedules().stream())
                    .map(schedule -> rambler.getScheme(schedule.getId()).getId())
                    .toList();
        } catch (Exception e) {
            throw new MonitoringException("Ошибка при получении зала! Ошибка: {}", e.getMessage());
        }
    }

    private void setSchedules(Event event) throws MonitoringException {
        try {
            List<RamblerResponse.Schedule> scheduleResponse = rambler.getSchedules(event.getId());

            List<Event.Place> places = scheduleResponse.parallelStream()
                    .map(schedule -> {
                        List<Event.Schedule> schedules = schedule.getSessions().parallelStream()
                                .map(session -> new Event.Schedule(
                                        session.getId(),
                                        session.getSaleEndDate(),
                                        session.getSlotStartDate(),
                                        session.isSaleAvailable(),
                                        session.getMinPrice(),
                                        session.getMaxPrice(),
                                        session.getAvailableTicketsCount(),
                                        null))
                                .toList();

                        return new Event.Place(
                                schedule.getPlace().getId(),
                                schedule.getPlace().getCityId(),
                                schedules);
                    })
                    .toList();
            event.setSchedulesPlaces(places);
        } catch (Exception e) {
            throw new MonitoringException("Ошибка при получении расписания для события: {}! \n {}", event.getId(),
                    e.getMessage().replace(properties.getWidgetKey(), "***"));
        }
    }
}