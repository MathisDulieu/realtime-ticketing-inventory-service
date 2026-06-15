package com.mathisdulieu.ticketing.inventory;

import com.mathisdulieu.ticketing.library.core.dto.reservation.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(
        topics = "#{@kafkaProvider.getTopic()}",
        groupId = "#{@kafkaProvider.getGroupId()}"
    )
    public void consume(final ReservationCreatedEvent reservationCreatedEvent, final Acknowledgment acknowledgment) {
        log.debug("Received reservation created event with eventId: {}", reservationCreatedEvent.eventId());
        inventoryService.handleReservationCreated(reservationCreatedEvent);
        acknowledgment.acknowledge();
    }
}
