package org.hishmalif.afishamonitoring.config.rambler;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.hishmalif.afishamonitoring.config.Properties;

@Data
@Component
@ConfigurationProperties("rambler")
@EqualsAndHashCode(callSuper = true)
public class RamblerProperties extends Properties {
    private String widgetKey;
    private String apikey;
}