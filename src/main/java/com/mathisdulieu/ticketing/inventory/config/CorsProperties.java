package com.mathisdulieu.ticketing.inventory.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notEmpty;

@ConfigurationProperties(prefix = "cors")
public record CorsProperties(
    List<String> allowedOrigins
) implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        notEmpty(allowedOrigins, "cors.allowedOrigins must not be null or empty");
        allowedOrigins.forEach(origin -> hasText(origin, "Each origin in cors.allowedOrigins must be a non-blank string"));
    }
}
