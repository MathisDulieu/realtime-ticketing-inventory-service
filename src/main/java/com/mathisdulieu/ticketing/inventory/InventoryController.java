package com.mathisdulieu.ticketing.inventory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    @Operation(summary = "Create inventory", description = "Creates a new inventory for an event")
    @ApiResponse(responseCode = "200", description = "Inventory created successfully")
    public ResponseEntity<String> createInventory(@RequestBody final InventoryRequest inventoryRequest) {
        log.debug("Received request: POST /api/v1/inventories");
        inventoryService.createInventory(inventoryRequest);
        return ResponseEntity.ok("Inventory created");
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Get inventory", description = "Returns the inventory for a given event")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<Inventory> getInventory(@PathVariable final String eventId) {
        log.debug("Received request: GET /api/v1/inventories/{}", eventId);
        return ResponseEntity.ok(inventoryService.getInventory(eventId));
    }
}
