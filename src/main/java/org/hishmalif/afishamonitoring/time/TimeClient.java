package org.hishmalif.afishamonitoring.time;

import feign.RequestLine;
import org.hishmalif.afishamonitoring.dto.TimeMessage;

public interface TimeClient {
    @RequestLine("POST /posts")
    TimeMessage.Response postMessage(TimeMessage.Request request) throws TimeException;
}