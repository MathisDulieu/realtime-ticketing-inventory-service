package com.mathisdulieu.ticketing.inventory;

import com.mathisdulieu.ticketing.library.core.dto.inventory.InventoryEvent;
import com.mathisdulieu.ticketing.library.core.dto.reservation.ReservationCreatedEvent;
import com.mathisdulieu.ticketing.library.core.utils.UuidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private static final int LOW_STOCK_THRESHOLD = 10;

    private final InventoryRepository inventoryRepository;
    private final InventoryEventProducer inventoryEventProducer;
    private final UuidService uuidService;

    public void createInventory(final InventoryRequest inventoryRequest) {
        final Inventory inventory = Inventory.builder()
            .id(uuidService.generateUuid())
            .eventId(inventoryRequest.eventId())
            .totalTickets(inventoryRequest.totalTickets())
            .availableTickets(inventoryRequest.totalTickets())
            .build();

        inventoryRepository.save(inventory);
        log.info("Inventory created for eventId: {}", inventoryRequest.eventId());
    }

    public Inventory getInventory(final String eventId) {
        return inventoryRepository.findByEventId(eventId)
            .orElseThrow(() -> new RuntimeException("Inventory not found for eventId: " + eventId));
    }

    public void handleReservationCreated(final ReservationCreatedEvent event) {
        final Inventory inventory = inventoryRepository.findByEventId(event.eventId())
            .orElseThrow(() -> new RuntimeException("Inventory not found for eventId: " + event.eventId()));

        final InventoryEvent inventoryEvent = InventoryEvent.builder()
            .eventId(event.eventId())
            .build();

        if (inventory.availableTickets() <= 0) {
            log.warn("No tickets available for eventId: {}", event.eventId());
            inventoryEventProducer.sendReservationFailedEvent(inventoryEvent);
            return;
        }

        final Inventory updatedInventory = Inventory.builder()
            .id(inventory.id())
            .eventId(inventory.eventId())
            .totalTickets(inventory.totalTickets())
            .availableTickets(inventory.availableTickets() - 1)
            .build();

        inventoryRepository.save(updatedInventory);
        inventoryEventProducer.sendReservationConfirmedEvent(inventoryEvent);
        inventoryEventProducer.sendInventoryUpdatedEvent(inventoryEvent);

        if (updatedInventory.availableTickets() == 0) {
            inventoryEventProducer.sendInventorySoldOutEvent(inventoryEvent);
        } else if (updatedInventory.availableTickets() <= LOW_STOCK_THRESHOLD) {
            inventoryEventProducer.sendInventoryLowStockEvent(inventoryEvent);
        }

        log.info("Reservation confirmed for eventId: {}, remaining tickets: {}", event.eventId(), updatedInventory.availableTickets());
    }
}
