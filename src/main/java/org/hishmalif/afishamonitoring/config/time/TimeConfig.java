package org.hishmalif.afishamonitoring.config.time;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Configuration;
import feign.Feign;
import org.hishmalif.afishamonitoring.time.TimeClient;
import org.hishmalif.afishamonitoring.time.TimeService;
import org.hishmalif.afishamonitoring.time.TimeServiceImpl;

@Configuration
public class TimeConfig {
    @Bean
    public TimeClient timeClient(Feign.Builder builder, TimeProperties properties) {
        return builder.requestInterceptor(template -> {
                    template.header("Authorization", "Bearer " + properties.getToken());
                })
                .target(TimeClient.class, properties.getUrl());
    }

    @Bean
    @Primary
    public TimeService timeService(TimeClient timeClient, TimeProperties properties) {
        return new TimeServiceImpl(timeClient, properties);
    }
}