package org.hishmalif.afishamonitoring.job.partner.ticketscloud;

import org.hishmalif.afishamonitoring.job.MonitoringException;
import org.hishmalif.afishamonitoring.job.partner.CheckPartner;
import org.hishmalif.afishamonitoring.model.Event;
import org.hishmalif.afishamonitoring.dto.MonitoringObject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component //TODO Сделать реализацию для ТК
public class TicketsCloudJob implements CheckPartner {
    @Override
    public List<Event.Place> getPlaces(Event event) throws MonitoringException {
        return null;
    }

    @Override
    public Event getEvent(MonitoringObject.Request request) throws MonitoringException {
        return null;
    }

    @Override
    public List<String> getSchemas(Event event) throws MonitoringException {
        return null;
    }
}