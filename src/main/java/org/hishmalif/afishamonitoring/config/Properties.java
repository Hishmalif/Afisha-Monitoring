package org.hishmalif.afishamonitoring.config;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Properties {
    protected String url;
    private Integer connectTimeout = 15000;
    private Integer readTimeout = 15000;
    private Integer retryPeriod = 100;
    private Integer maxPeriod = 1;
    private Integer maxAttempts = 1;
}