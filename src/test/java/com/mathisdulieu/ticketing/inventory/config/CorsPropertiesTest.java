package com.mathisdulieu.ticketing.inventory.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorsPropertiesTest {

    @Test
    void shouldNotThrow_whenPropertiesAreComplete() {
        // Arrange
        CorsProperties corsProperties = new CorsProperties("allowedOrigin");

        // Act
        corsProperties.afterPropertiesSet();

        // Assert
        assertThat(corsProperties.allowedOrigin()).isEqualTo("allowedOrigin");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrow_whenPropertiesAreIncomplete(String allowedOrigin) {
        // Arrange
        CorsProperties corsProperties = new CorsProperties(allowedOrigin);

        // Act & Assert
        assertThatThrownBy(corsProperties::afterPropertiesSet)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("cors.allowedOrigin must be given");
    }

}
