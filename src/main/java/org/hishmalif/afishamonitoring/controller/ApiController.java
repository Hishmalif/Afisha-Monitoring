package org.hishmalif.afishamonitoring.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.hishmalif.afishamonitoring.time.TimeService;
import org.hishmalif.afishamonitoring.job.MonitoringJob;
import org.hishmalif.afishamonitoring.time.TimeException;
import org.hishmalif.afishamonitoring.dto.MonitoringObject;

@Log4j2
@RestController
@AllArgsConstructor
public class ApiController {
    private final TimeService time;
    private final MonitoringJob monitoringJob;

    @PostMapping("/monitoring/once")
    public MonitoringObject.Response checkOnce(@RequestBody MonitoringObject.Request request) throws TimeException {
        MonitoringObject.Response response = monitoringJob.checkOnce(request);
        time.sendMessage(time.getChannelId(), response.getMessage());
        return response;
    }

    @PostMapping("/monitoring")
    public boolean addMonitoringEvent(@RequestBody MonitoringObject.Request request) {
        return monitoringJob.addToMonitoring(request);
    }

    @DeleteMapping("/monitoring")
    public boolean deleteMonitoringEvent(@RequestParam("event_id") String eventId) {
        return monitoringJob.deleteFromMonitoring(eventId);
    }
}