package com.mathisdulieu.ticketing.inventory;

import com.mathisdulieu.ticketing.library.core.dto.inventory.InventoryEvent;
import com.mathisdulieu.ticketing.library.core.dto.reservation.ReservationCreatedEvent;
import com.mathisdulieu.ticketing.library.core.utils.UuidService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryEventProducer inventoryEventProducer;

    @Mock
    private UuidService uuidService;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void shouldCreateInventory() {
        // Arrange
        InventoryRequest inventoryRequest = InventoryRequest.builder()
            .eventId("event-id")
            .totalTickets(20)
            .build();

        when(uuidService.generateUuid()).thenReturn("generatedUuid");

        // Act
        inventoryService.createInventory(inventoryRequest);

        // Assert
        Inventory expectedInventory = Inventory.builder()
            .id("generatedUuid")
            .eventId("event-id")
            .totalTickets(20)
            .availableTickets(20)
            .build();

        InOrder inOrder = inOrder(uuidService, inventoryRepository);
        inOrder.verify(uuidService).generateUuid();
        inOrder.verify(inventoryRepository).save(expectedInventory);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetInventory() {
        // Arrange
        Inventory inventory = Inventory.builder()
            .id("inventory-id")
            .eventId("event-id")
            .totalTickets(20)
            .availableTickets(15)
            .build();

        when(inventoryRepository.findByEventId("event-id")).thenReturn(Optional.of(inventory));

        // Act
        Inventory foundInventory = inventoryService.getInventory("event-id");

        // Assert
        assertThat(foundInventory).isEqualTo(inventory);
    }

    @Test
    void shouldThrowRuntimeException_whenInventoryNotFoundOnGetInventory() {
        // Arrange
        when(inventoryRepository.findByEventId("event-id")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> inventoryService.getInventory("event-id"))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Inventory not found for eventId: event-id");
    }

    @Test
    void shouldHandleReservationCreated() {
        // Arrange
        Inventory inventory = Inventory.builder()
            .id("inventory-id")
            .eventId("event-id")
            .totalTickets(20)
            .availableTickets(15)
            .build();

        when(inventoryRepository.findByEventId("event-id")).thenReturn(Optional.of(inventory));

        ReservationCreatedEvent event = ReservationCreatedEvent.builder()
            .eventId("event-id")
            .build();

        // Act
        inventoryService.handleReservationCreated(event);

        // Assert
        Inventory expectedUpdatedInventory = Inventory.builder()
            .id("inventory-id")
            .eventId("event-id")
            .totalTickets(20)
            .availableTickets(14)
            .build();

        InventoryEvent expectedInventoryEvent = InventoryEvent.builder()
            .eventId("event-id")
            .build();

        InOrder inOrder = inOrder(inventoryRepository, inventoryEventProducer);
        inOrder.verify(inventoryRepository).save(expectedUpdatedInventory);
        inOrder.verify(inventoryEventProducer).sendReservationConfirmedEvent(expectedInventoryEvent);
        inOrder.verify(inventoryEventProducer).sendInventoryUpdatedEvent(expectedInventoryEvent);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldThrowRuntimeException_whenInventoryNotFoundOnHandleReservationCreated() {
        // Arrange
        when(inventoryRepository.findByEventId("event-id")).thenReturn(Optional.empty());

        ReservationCreatedEvent reservationCreatedEvent = ReservationCreatedEvent.builder()
            .eventId("event-id")
            .build();

        // Act & Assert
        assertThatThrownBy(() -> inventoryService.handleReservationCreated(reservationCreatedEvent))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Inventory not found for eventId: event-id");
    }

    @Test
    void shouldSendReservationFailedEvent_whenNoTicketAvailable() {
        // Arrange
        Inventory inventory = Inventory.builder()
            .id("inventory-id")
            .eventId("event-id")
            .totalTickets(20)
            .availableTickets(0)
            .build();

        when(inventoryRepository.findByEventId("event-id")).thenReturn(Optional.of(inventory));

        ReservationCreatedEvent reservationCreatedEvent = ReservationCreatedEvent.builder()
            .eventId("event-id")
            .build();

        // Act
        inventoryService.handleReservationCreated(reservationCreatedEvent);

        // Assert
        InventoryEvent expectedInventoryEvent = InventoryEvent.builder()
            .eventId("event-id")
            .build();

        InOrder inOrder = inOrder(inventoryRepository, inventoryEventProducer);
        inOrder.verify(inventoryRepository).findByEventId("event-id");
        inOrder.verify(inventoryEventProducer).sendReservationFailedEvent(expectedInventoryEvent);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldSendInventorySoldOutEvent_whenNoTicketAvailableAfterReservation() {
        // Arrange
        Inventory inventory = Inventory.builder()
            .id("inventory-id")
            .eventId("event-id")
            .totalTickets(20)
            .availableTickets(1)
            .build();

        when(inventoryRepository.findByEventId("event-id")).thenReturn(Optional.of(inventory));

        ReservationCreatedEvent reservationCreatedEvent = ReservationCreatedEvent.builder()
            .eventId("event-id")
            .build();

        // Act
        inventoryService.handleReservationCreated(reservationCreatedEvent);

        // Assert
        InventoryEvent expectedInventoryEvent = InventoryEvent.builder()
            .eventId("event-id")
            .build();

        InOrder inOrder = inOrder(inventoryRepository, inventoryEventProducer);
        inOrder.verify(inventoryRepository).findByEventId("event-id");
        inOrder.verify(inventoryEventProducer).sendReservationConfirmedEvent(expectedInventoryEvent);
        inOrder.verify(inventoryEventProducer).sendInventoryUpdatedEvent(expectedInventoryEvent);
        inOrder.verify(inventoryEventProducer).sendInventorySoldOutEvent(expectedInventoryEvent);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldSendInventoryLowStockEvent_when10TicketsOrLessAvailableAfterReservation() {
        // Arrange
        Inventory inventory = Inventory.builder()
            .id("inventory-id")
            .eventId("event-id")
            .totalTickets(20)
            .availableTickets(11)
            .build();

        when(inventoryRepository.findByEventId("event-id")).thenReturn(Optional.of(inventory));

        ReservationCreatedEvent reservationCreatedEvent = ReservationCreatedEvent.builder()
            .eventId("event-id")
            .build();

        // Act
        inventoryService.handleReservationCreated(reservationCreatedEvent);

        // Assert
        InventoryEvent expectedInventoryEvent = InventoryEvent.builder()
            .eventId("event-id")
            .build();

        InOrder inOrder = inOrder(inventoryRepository, inventoryEventProducer);
        inOrder.verify(inventoryRepository).findByEventId("event-id");
        inOrder.verify(inventoryEventProducer).sendReservationConfirmedEvent(expectedInventoryEvent);
        inOrder.verify(inventoryEventProducer).sendInventoryUpdatedEvent(expectedInventoryEvent);
        inOrder.verify(inventoryEventProducer).sendInventoryLowStockEvent(expectedInventoryEvent);
        inOrder.verifyNoMoreInteractions();
    }

}
