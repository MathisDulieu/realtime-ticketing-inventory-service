package com.mathisdulieu.ticketing.inventory;

import lombok.Builder;

@Builder
public record InventoryRequest(
    String eventId,
    int totalTickets
) {}
