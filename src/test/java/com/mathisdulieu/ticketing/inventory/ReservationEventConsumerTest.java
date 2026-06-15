package com.mathisdulieu.ticketing.inventory;

import com.mathisdulieu.ticketing.library.core.dto.reservation.ReservationCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class ReservationEventConsumerTest {

    @Mock
    private InventoryService inventoryService;
    @Mock
    private Acknowledgment acknowledgment;
    @InjectMocks
    private ReservationEventConsumer reservationEventConsumer;

    @Test
    void shouldConsumeAndProcessNotificationEvent() {
        // Arrange
        ReservationCreatedEvent reservationCreatedEvent = ReservationCreatedEvent.builder()
            .eventId("event-id")
            .build();

        // Act
        reservationEventConsumer.consume(reservationCreatedEvent, acknowledgment);

        // Assert
        InOrder inOrder = inOrder(inventoryService, acknowledgment);
        inOrder.verify(inventoryService).handleReservationCreated(reservationCreatedEvent);
        inOrder.verify(acknowledgment).acknowledge();
        inOrder.verifyNoMoreInteractions();
    }

}
