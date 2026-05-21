package com.mathisdulieu.ticketing.inventory.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UuidServiceTest {

    @InjectMocks
    private UuidService uuidService;

    @Test
    void shouldGenerateUuid() {
        // Arrange

        // Act
        String generateUuid = uuidService.generateUuid();

        // Assert
        assertThat(generateUuid).isNotBlank();
    }

}
