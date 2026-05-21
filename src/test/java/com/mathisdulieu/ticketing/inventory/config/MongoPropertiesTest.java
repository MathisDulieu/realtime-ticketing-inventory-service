package com.mathisdulieu.ticketing.inventory.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MongoPropertiesTest {

    @Test
    void shouldNotThrow_whenPropertiesAreComplete() {
        // Arrange
        MongoProperties mongoProperties = new MongoProperties("uri", "databaseName");

        // Act
        mongoProperties.afterPropertiesSet();

        // Assert
        assertThat(mongoProperties.uri()).isEqualTo("uri");
        assertThat(mongoProperties.databaseName()).isEqualTo("databaseName");
    }

    @ParameterizedTest
    @MethodSource("getCasesWithIncompleteProperties")
    void shouldThrow_whenPropertiesAreIncomplete(String uri, String databaseName, String errorMessage) {
        // Arrange
        MongoProperties mongoProperties = new MongoProperties(uri, databaseName);

        // Act & Assert
        assertThatThrownBy(mongoProperties::afterPropertiesSet)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(errorMessage);
    }

    private static Stream<Arguments> getCasesWithIncompleteProperties() {
        return Stream.of(
            Arguments.of(" ", "databaseName", "inventory.mongodb.uri must be given"),
            Arguments.of(null, "databaseName", "inventory.mongodb.uri must be given"),
            Arguments.of("uri", " ", "inventory.mongodb.databaseName must be given"),
            Arguments.of("uri", null, "inventory.mongodb.databaseName must be given")
        );
    }

}
