package com.mathisdulieu.ticketing.inventory.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorsPropertiesTest {

    @Test
    void shouldNotThrow_whenPropertiesAreComplete() {
        // Arrange
        CorsProperties corsProperties = new CorsProperties(List.of("allowedOrigin1", "allowedOrigin2"));

        // Act
        corsProperties.afterPropertiesSet();

        // Assert
        assertThat(corsProperties.allowedOrigins()).isEqualTo(List.of("allowedOrigin1", "allowedOrigin2"));
    }

    @ParameterizedTest
    @MethodSource("getCasesWithIncompleteProperties")
    void shouldThrow_whenPropertiesAreIncomplete(List<String> allowedOrigins, String errorMessage) {
        // Arrange
        CorsProperties corsProperties = new CorsProperties(allowedOrigins);

        // Act & Assert
        assertThatThrownBy(corsProperties::afterPropertiesSet)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(errorMessage);
    }

    private static Stream<Arguments> getCasesWithIncompleteProperties() {
        return Stream.of(
            Arguments.of(emptyList(), "cors.allowedOrigins must not be null or empty"),
            Arguments.of(null, "cors.allowedOrigins must not be null or empty"),
            Arguments.of(allowedOriginsList("allowedOrigin1", null), "Each origin in cors.allowedOrigins must be a non-blank string"),
            Arguments.of(allowedOriginsList("allowedOrigin1", " "), "Each origin in cors.allowedOrigins must be a non-blank string")
        );
    }

    private static List<String> allowedOriginsList(String... values) {
        return new ArrayList<>(Arrays.asList(values));
    }

}
