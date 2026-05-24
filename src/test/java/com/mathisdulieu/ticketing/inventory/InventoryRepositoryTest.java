package com.mathisdulieu.ticketing.inventory;

import com.mathisdulieu.ticketing.library.test.mongo.config.MongoTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("test")
@Import({
    RealtimeTicketingInventoryServiceConfigurationTests.class,
    MongoTestConfig.class
})
class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @BeforeEach
    void setup() {
        inventoryRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindByEventId() {
        // Arrange
        Inventory inventory = Inventory.builder()
            .id("inventory-id")
            .eventId("event-id")
            .totalTickets(100)
            .availableTickets(100)
            .build();

        inventoryRepository.save(inventory);

        // Act
        Optional<Inventory> inventoryFound = inventoryRepository.findByEventId("event-id");

        // Assert
        assertThat(inventoryFound).isPresent();
        assertThat(inventoryFound.get()).isEqualTo(inventory);
    }

    @Test
    void shouldReturnEmptyWhenEventIdNotFound() {
        // Arrange

        // Act
        Optional<Inventory> inventoryFound = inventoryRepository.findByEventId("unknown-event-id");

        // Assert
        assertThat(inventoryFound).isNotPresent();
    }
}
