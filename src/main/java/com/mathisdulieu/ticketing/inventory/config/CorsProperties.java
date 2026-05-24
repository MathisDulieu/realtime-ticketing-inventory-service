package com.mathisdulieu.ticketing.inventory.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.springframework.util.Assert.hasText;

@ConfigurationProperties(prefix = "cors")
public record CorsProperties(
    String allowedOrigin
) implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        hasText(allowedOrigin, "cors.allowedOrigin must be given");
    }
}
