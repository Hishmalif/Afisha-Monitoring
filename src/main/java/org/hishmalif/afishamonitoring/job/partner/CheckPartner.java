package org.hishmalif.afishamonitoring.job.partner;

import org.hishmalif.afishamonitoring.model.Event;
import org.hishmalif.afishamonitoring.dto.MonitoringObject;
import org.hishmalif.afishamonitoring.job.MonitoringException;

import java.util.List;

public interface CheckPartner {
    /**
     * Retrieves all places from a partner for a unique list of cities based on the provided event.
     *
     * @param event the event containing schedules and places information
     * @return a list of places associated with the event
     * @throws MonitoringException if an error occurs during the place retrieval process
     */
    List<Event.Place> getPlaces(Event event) throws MonitoringException;

    /**
     * This method retrieves an event from a partner and converts it to a generic model.
     *The monitoring object containing the foreign ID
     * @return A generic Event model
     */
    Event getEvent(MonitoringObject.Request request) throws MonitoringException;

    List<String> getSchemas(Event event) throws MonitoringException;
}