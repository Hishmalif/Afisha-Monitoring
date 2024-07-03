package org.hishmalif.afishamonitoring.job;

import org.hishmalif.afishamonitoring.dto.MonitoringObject;

public interface MonitoringJob {
    boolean addToMonitoring(MonitoringObject.Request request);

    MonitoringObject.Response checkOnce(MonitoringObject.Request request);

    boolean deleteFromMonitoring(String id);
}