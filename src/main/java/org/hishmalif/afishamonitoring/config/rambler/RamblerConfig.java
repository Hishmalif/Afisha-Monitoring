package org.hishmalif.afishamonitoring.config.rambler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.Feign;
import org.hishmalif.afishamonitoring.job.partner.rambler.RamblerClient;

@Configuration
public class RamblerConfig {
    @Bean
    public RamblerClient ramblerClient(Feign.Builder builder, RamblerProperties properties) {
        return builder.requestInterceptor(template -> {
                    template.query("WidgetKey", properties.getWidgetKey());
                    template.header("X-ApiAuth-PartnerKey", properties.getApikey());
                })
                .target(RamblerClient.class, properties.getUrl());
    }
}