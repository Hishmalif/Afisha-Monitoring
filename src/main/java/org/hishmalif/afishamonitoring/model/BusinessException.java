package org.hishmalif.afishamonitoring.model;

import lombok.extern.log4j.Log4j2;
import org.hishmalif.afishamonitoring.utill.ParseMessage;

@Log4j2
public class BusinessException extends Exception {
    public BusinessException(String message) {
        super(message);
        log.error(super.getMessage());
    }

    public BusinessException(String message, Object... objects) {
        super(ParseMessage.getMessage(message, objects));
        log.error(super.getMessage());
    }
}