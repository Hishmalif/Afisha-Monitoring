package org.hishmalif.afishamonitoring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.Configuration;
import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignClientFactory {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(new KotlinModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder(ObjectMapper objectMapper, Properties properties) {
        return Feign.builder()
                .decoder(new JacksonDecoder(objectMapper))
                .encoder(new JacksonEncoder(objectMapper))
                .retryer(new Retryer.Default(properties.getRetryPeriod(), properties.getMaxPeriod(), properties.getMaxAttempts()))
                .options(new Request.Options(properties.getConnectTimeout(), TimeUnit.MILLISECONDS, properties.getReadTimeout(),
                        TimeUnit.MILLISECONDS, true));
    }
}