package com.mathisdulieu.ticketing.inventory;

import com.mathisdulieu.ticketing.library.core.dto.InventoryEvent;
import com.mathisdulieu.ticketing.library.core.dto.ReservationCreatedEvent;
import com.mathisdulieu.ticketing.library.test.kafka.consumer.GenericTestKafkaConsumer;
import com.mathisdulieu.ticketing.library.test.mongo.config.MongoTestConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@EmbeddedKafka(partitions = 1,
    topics = {
        "json_realtime_reservation_created",
        "json_realtime_reservation_confirmed",
        "json_realtime_reservation_failed",
        "json_realtime_inventory_updated",
        "json_realtime_inventory_low_stock",
        "json_realtime_inventory_sold_out"
    }
)
@ActiveProfiles("test")
@Import({
    RealtimeTicketingInventoryServiceConfigurationTests.class,
    RealtimeTicketingInventoryServiceIntegrationTest.TestKafkaConsumer.class,
    MongoTestConfig.class
})
public class RealtimeTicketingInventoryServiceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private KafkaTemplate<String, ReservationCreatedEvent> kafkaTemplate;

    @Autowired
    private TestKafkaConsumer testKafkaConsumer;

    @BeforeEach
    void setup() {
        mongoTemplate.dropCollection("inventories");
        testKafkaConsumer.consumer.clearRecordsFromTopic("json_realtime_reservation_confirmed");
        testKafkaConsumer.consumer.clearRecordsFromTopic("json_realtime_reservation_failed");
        testKafkaConsumer.consumer.clearRecordsFromTopic("json_realtime_inventory_updated");
    }

    @Test
    void shouldCreateInventory() {
        // Arrange
        InventoryRequest inventoryRequest = InventoryRequest.builder()
            .eventId("event-id")
            .totalTickets(20)
            .build();

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/inventories", inventoryRequest, String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Inventory created");

        List<Inventory> savedInventories = mongoTemplate.findAll(Inventory.class);
        assertThat(savedInventories).hasSize(1);
        assertThat(savedInventories.getFirst().id()).isNotBlank();
        assertThat(savedInventories.getFirst().eventId()).isEqualTo("event-id");
        assertThat(savedInventories.getFirst().totalTickets()).isEqualTo(20);
        assertThat(savedInventories.getFirst().availableTickets()).isEqualTo(20);
    }

    @Test
    void shouldGetInventory() {
        // Arrange
        mongoTemplate.save("""
                {
                    "_id": "inventory-id",
                    "eventId": "event-id",
                    "totalTickets": 20,
                    "availableTickets": 15
                }
                """, "inventories");

        // Act
        ResponseEntity<Inventory> response = restTemplate.getForEntity("/api/v1/inventories/{eventId}", Inventory.class, "event-id");

        // Assert
        Inventory expectedInventory = Inventory.builder()
            .id("inventory-id")
            .eventId("event-id")
            .totalTickets(20)
            .availableTickets(15)
            .build();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(expectedInventory);
    }

    @Test
    void shouldCreateReservation() throws InterruptedException {
        // Arrange
        mongoTemplate.save("""
                {
                    "_id": "inventory-id",
                    "eventId": "event-id",
                    "totalTickets": 20,
                    "availableTickets": 15
                }
                """, "inventories");

        ReservationCreatedEvent reservationCreatedEvent = ReservationCreatedEvent.builder()
            .eventId("event-id")
            .build();

        // Act
        kafkaTemplate.send("json_realtime_reservation_created", reservationCreatedEvent);

        // Assert
        Inventory expectedInventory = Inventory.builder()
            .id("inventory-id")
            .eventId("event-id")
            .totalTickets(20)
            .availableTickets(14)
            .build();

        ConsumerRecord<String, InventoryEvent> confirmedRecord = testKafkaConsumer.consumer.pollRecord("json_realtime_reservation_confirmed", 5);
        assertThat(confirmedRecord.value().eventId()).isEqualTo("event-id");

        ConsumerRecord<String, InventoryEvent> updatedRecord = testKafkaConsumer.consumer.pollRecord("json_realtime_inventory_updated", 5);
        assertThat(updatedRecord.value().eventId()).isEqualTo("event-id");

        List<Inventory> updatedInventories = mongoTemplate.findAll(Inventory.class);
        assertThat(updatedInventories).hasSize(1);
        assertThat(updatedInventories.getFirst()).isEqualTo(expectedInventory);

        testKafkaConsumer.consumer.assertNoRecordReceived("json_realtime_reservation_failed", 3);
    }

    @Test
    void shouldSendReservationFailedEvent_whenNoTicketsAvailable() throws InterruptedException {
        // Arrange
        mongoTemplate.save("""
                {
                    "_id": "inventory-id",
                    "eventId": "event-id",
                    "totalTickets": 20,
                    "availableTickets": 0
                }
                """, "inventories");

        ReservationCreatedEvent reservationCreatedEvent = ReservationCreatedEvent.builder()
            .eventId("event-id")
            .build();

        // Act
        kafkaTemplate.send("json_realtime_reservation_created", reservationCreatedEvent);

        // Assert
        ConsumerRecord<String, InventoryEvent> failedRecord = testKafkaConsumer.consumer.pollRecord("json_realtime_reservation_failed", 5);
        assertThat(failedRecord).isNotNull();
        assertThat(failedRecord.value().eventId()).isEqualTo("event-id");

        testKafkaConsumer.consumer.assertNoRecordReceived("json_realtime_reservation_confirmed", 3);
        testKafkaConsumer.consumer.assertNoRecordReceived("json_realtime_inventory_updated", 3);
    }

    @TestComponent
    static class TestKafkaConsumer {
        final GenericTestKafkaConsumer<InventoryEvent> consumer = new GenericTestKafkaConsumer<>();

        @KafkaListener(
            topics = {
                "json_realtime_reservation_confirmed",
                "json_realtime_reservation_failed",
                "json_realtime_inventory_updated"
            },
            groupId = "test-group",
            containerFactory = "inventoryEventKafkaListenerContainerFactory"
        )
        public void consume(ConsumerRecord<String, InventoryEvent> record) {
            consumer.records.add(record);
        }
    }
}
