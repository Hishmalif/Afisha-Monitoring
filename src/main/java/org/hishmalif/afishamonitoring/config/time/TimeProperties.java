package org.hishmalif.afishamonitoring.config.time;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.hishmalif.afishamonitoring.config.Properties;

@Data
@Component
@ConfigurationProperties("time")
@EqualsAndHashCode(callSuper = true)
public class TimeProperties extends Properties {
    private String token;
    private String channelId;
}