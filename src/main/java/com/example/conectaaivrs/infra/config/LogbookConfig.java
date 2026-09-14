package com.example.conectaaivrs.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.Logbook;

@Configuration
public class LogbookConfig {

    @Bean
    public Logbook logbook() {
        return Logbook.builder()
                .condition(request ->
                        request.getContentType() == null
                                || !request.getContentType().contains("multipart/form-data"))
                .build();
    }
}
