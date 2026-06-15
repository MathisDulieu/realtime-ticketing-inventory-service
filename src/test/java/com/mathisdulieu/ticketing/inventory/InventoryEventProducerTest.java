package com.mathisdulieu.ticketing.inventory;

import com.mathisdulieu.ticketing.library.core.dto.inventory.InventoryEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InventoryEventProducerTest {

    @Mock
    private KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    @InjectMocks
    private InventoryEventProducer inventoryEventProducer;

    @Test
    void shouldSendReservationConfirmedEvent() {
        // Arrange
        InventoryEvent inventoryEvent = InventoryEvent.builder()
            .eventId("event-id")
            .build();

        // Act
        inventoryEventProducer.sendReservationConfirmedEvent(inventoryEvent);

        // Assert
        verify(kafkaTemplate).send("json_realtime_reservation_confirmed", inventoryEvent);
    }

    @Test
    void shouldSendReservationFailedEvent() {
        // Arrange
        InventoryEvent inventoryEvent = InventoryEvent.builder()
            .eventId("event-id")
            .build();

        // Act
        inventoryEventProducer.sendReservationFailedEvent(inventoryEvent);

        // Assert
        verify(kafkaTemplate).send("json_realtime_reservation_failed", inventoryEvent);
    }

    @Test
    void shouldSendInventoryUpdatedEvent() {
        // Arrange
        InventoryEvent inventoryEvent = InventoryEvent.builder()
            .eventId("event-id")
            .build();

        // Act
        inventoryEventProducer.sendInventoryUpdatedEvent(inventoryEvent);

        // Assert
        verify(kafkaTemplate).send("json_realtime_inventory_updated", inventoryEvent);
    }

    @Test
    void shouldSendInventoryLowStockEvent() {
        // Arrange
        InventoryEvent inventoryEvent = InventoryEvent.builder()
            .eventId("event-id")
            .build();

        // Act
        inventoryEventProducer.sendInventoryLowStockEvent(inventoryEvent);

        // Assert
        verify(kafkaTemplate).send("json_realtime_inventory_low_stock", inventoryEvent);
    }

    @Test
    void shouldSendInventorySoldOutEvent() {
        // Arrange
        InventoryEvent inventoryEvent = InventoryEvent.builder()
            .eventId("event-id")
            .build();

        // Act
        inventoryEventProducer.sendInventorySoldOutEvent(inventoryEvent);

        // Assert
        verify(kafkaTemplate).send("json_realtime_inventory_sold_out", inventoryEvent);
    }

}
