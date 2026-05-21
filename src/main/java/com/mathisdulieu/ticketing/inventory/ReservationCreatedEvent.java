package com.mathisdulieu.ticketing.inventory;

import lombok.Builder;

@Builder
public record ReservationCreatedEvent(
    String eventId
) {}
