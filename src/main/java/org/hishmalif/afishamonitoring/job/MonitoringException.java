package org.hishmalif.afishamonitoring.job;

import lombok.extern.log4j.Log4j2;
import org.hishmalif.afishamonitoring.model.BusinessException;

@Log4j2
public class MonitoringException extends BusinessException {
    public MonitoringException(String message, Object... objects) {
        super(message, objects);
    }
}