package com.mathisdulieu.ticketing.inventory.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KafkaPropertiesTest {

    @Test
    void shouldNotThrow_whenPropertiesAreComplete() {
        // Arrange
        KafkaProperties kafkaProperties = new KafkaProperties("topic", "groupId");

        // Act
        kafkaProperties.afterPropertiesSet();

        // Assert
        assertThat(kafkaProperties.topic()).isEqualTo("topic");
        assertThat(kafkaProperties.groupId()).isEqualTo("groupId");
    }

    @ParameterizedTest
    @MethodSource("getCasesWithIncompleteProperties")
    void shouldThrow_whenPropertiesAreIncomplete(String topic, String groupId, String errorMessage) {
        // Arrange
        KafkaProperties kafkaProperties = new KafkaProperties(topic, groupId);

        // Act & Assert
        assertThatThrownBy(kafkaProperties::afterPropertiesSet)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(errorMessage);
    }

    private static Stream<Arguments> getCasesWithIncompleteProperties() {
        return Stream.of(
            Arguments.of(" ", "groupId", "inventory.kafka.topic must be given"),
            Arguments.of(null, "groupId", "inventory.kafka.topic must be given"),
            Arguments.of("topic", " ", "inventory.kafka.groupId must be given"),
            Arguments.of("topic", null, "inventory.kafka.groupId must be given")
        );
    }

}
