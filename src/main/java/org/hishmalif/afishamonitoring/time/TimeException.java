package org.hishmalif.afishamonitoring.time;

import org.hishmalif.afishamonitoring.model.BusinessException;

public class TimeException extends BusinessException {
    public TimeException(String message, Object... objects) {
        super(message, objects);
    }
}