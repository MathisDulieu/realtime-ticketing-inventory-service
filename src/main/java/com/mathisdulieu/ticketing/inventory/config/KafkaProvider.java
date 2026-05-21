package com.mathisdulieu.ticketing.inventory.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProvider {

    private final KafkaProperties kafkaProperties;

    public String getTopic() {
        return kafkaProperties.topic();
    }

    public String getGroupId() {
        return kafkaProperties.groupId();
    }
}
