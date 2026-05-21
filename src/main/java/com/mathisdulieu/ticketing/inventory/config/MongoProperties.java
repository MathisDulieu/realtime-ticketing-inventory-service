package com.mathisdulieu.ticketing.inventory.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

import static org.springframework.util.Assert.hasText;

@Profile("!test")
@ConfigurationProperties(prefix = "inventory.mongodb")
public record MongoProperties(
    String uri,
    String databaseName
) implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        hasText(uri, "inventory.mongodb.uri must be given");
        hasText(databaseName, "inventory.mongodb.databaseName must be given");
    }
}
