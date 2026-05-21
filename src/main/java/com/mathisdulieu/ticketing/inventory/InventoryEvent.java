package com.mathisdulieu.ticketing.inventory;

import lombok.Builder;

@Builder
public record InventoryEvent(
    String eventId
) {}
