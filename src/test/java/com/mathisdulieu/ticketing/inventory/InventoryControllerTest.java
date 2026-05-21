package com.mathisdulieu.ticketing.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryService inventoryService;

    @Test
    void shouldCreateInventory() throws Exception {
        // Arrange

        // Act
        ResultActions resultActions = mockMvc.perform(post("/api/v1/inventories")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "eventId": "event-id",
                    "totalTickets": 15
                }
                """));

        // Assert
        InventoryRequest expectedInventoryRequest = InventoryRequest.builder()
            .eventId("event-id")
            .totalTickets(15)
            .build();

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(content().string("Inventory created"));
        verify(inventoryService).createInventory(expectedInventoryRequest);
    }

    @Test
    void shouldGetInventory() throws Exception {
        // Arrange
        Inventory inventory = Inventory.builder()
            .id("inventory-id")
            .eventId("event-id")
            .totalTickets(15)
            .availableTickets(10)
            .build();

        when(inventoryService.getInventory("event-id")).thenReturn(inventory);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/api/v1/inventories/{eventId}", "event-id"));

        // Assert
        String expectedInventory = """
            {
                "id": "inventory-id",
                "eventId": "event-id",
                "totalTickets": 15,
                "availableTickets": 10
            }
            """;

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(content().json(expectedInventory));
    }

}
