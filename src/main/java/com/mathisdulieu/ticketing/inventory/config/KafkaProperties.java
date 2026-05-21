package com.mathisdulieu.ticketing.inventory.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.springframework.util.Assert.hasText;

@ConfigurationProperties(prefix = "inventory.kafka")
public record KafkaProperties(
    String topic,
    String groupId
) implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        hasText(groupId, "inventory.kafka.groupId must be given");
        hasText(topic, "inventory.kafka.topic must be given");
    }
}
