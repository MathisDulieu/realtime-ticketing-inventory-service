package com.mathisdulieu.ticketing.inventory.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaProviderTest {

    @Mock
    private KafkaProperties kafkaProperties;
    @InjectMocks
    private KafkaProvider kafkaProvider;

    @Test
    void shouldGetTopicFromProperties() {
        // Arrange
        when(kafkaProperties.topic()).thenReturn("topic");

        // Act
        String topic = kafkaProvider.getTopic();

        // Assert
        assertThat(topic).isEqualTo("topic");
    }

    @Test
    void shouldGetGroupIdFromProperties() {
        // Arrange
        when(kafkaProperties.groupId()).thenReturn("groupId");

        // Act
        String groupId = kafkaProvider.getGroupId();

        // Assert
        assertThat(groupId).isEqualTo("groupId");
    }

}
