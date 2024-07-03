package org.hishmalif.afishamonitoring.job.partner.rambler;

import feign.Param;
import feign.RequestLine;
import org.hishmalif.afishamonitoring.dto.RamblerResponse;

import java.util.List;

public interface RamblerClient {
    @RequestLine("GET /places?CityId={cityId}")
    List<RamblerResponse.Place> getPlaces(@Param("cityId") String cityId);

    @RequestLine("GET /creations/{eventId}")
    RamblerResponse.Event getEvent(@Param("eventId") String eventId);

    @RequestLine("GET /creations/{eventId}/schedule")
    List<RamblerResponse.Schedule> getSchedules(@Param("eventId") String eventId);

    @RequestLine("GET /halls/{sessionId}")
    RamblerResponse.Scheme getScheme(@Param("sessionId") String sessionId);
}