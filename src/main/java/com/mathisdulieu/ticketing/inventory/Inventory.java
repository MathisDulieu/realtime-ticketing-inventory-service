package com.mathisdulieu.ticketing.inventory;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Document(collection = "inventories")
public record Inventory(
    @Id String id,
    String eventId,
    int totalTickets,
    int availableTickets
) {}
