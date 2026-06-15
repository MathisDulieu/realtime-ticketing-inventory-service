package com.mathisdulieu.ticketing.inventory;

import com.mathisdulieu.ticketing.library.core.dto.inventory.InventoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventProducer {

    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    private static final String RESERVATION_CONFIRMED_TOPIC = "json_realtime_reservation_confirmed";
    private static final String RESERVATION_FAILED_TOPIC = "json_realtime_reservation_failed";
    private static final String INVENTORY_UPDATED_TOPIC = "json_realtime_inventory_updated";
    private static final String INVENTORY_LOW_STOCK_TOPIC = "json_realtime_inventory_low_stock";
    private static final String INVENTORY_SOLD_OUT_TOPIC = "json_realtime_inventory_sold_out";

    public void sendReservationConfirmedEvent(final InventoryEvent inventoryEvent) {
        log.debug("Send reservation confirmed event with eventId: {}", inventoryEvent.eventId());
        kafkaTemplate.send(RESERVATION_CONFIRMED_TOPIC, inventoryEvent);
    }

    public void sendReservationFailedEvent(final InventoryEvent inventoryEvent) {
        log.debug("Send reservation failed event with eventId: {}", inventoryEvent.eventId());
        kafkaTemplate.send(RESERVATION_FAILED_TOPIC, inventoryEvent);
    }

    public void sendInventoryUpdatedEvent(final InventoryEvent inventoryEvent) {
        log.debug("Send inventory updated event with eventId: {}", inventoryEvent.eventId());
        kafkaTemplate.send(INVENTORY_UPDATED_TOPIC, inventoryEvent);
    }

    public void sendInventoryLowStockEvent(final InventoryEvent inventoryEvent) {
        log.debug("Send inventory low stock event with eventId: {}", inventoryEvent.eventId());
        kafkaTemplate.send(INVENTORY_LOW_STOCK_TOPIC, inventoryEvent);
    }

    public void sendInventorySoldOutEvent(final InventoryEvent inventoryEvent) {
        log.debug("Send inventory sold out event with eventId: {}", inventoryEvent.eventId());
        kafkaTemplate.send(INVENTORY_SOLD_OUT_TOPIC, inventoryEvent);
    }

}
