package com.mathisdulieu.ticketing.inventory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@TestConfiguration
public class RealtimeTicketingInventoryServiceConfigurationTests {

    @Bean
    public MBeanExporter exporter() {
        return Mockito.mock(MBeanExporter.class);
    }

    @Bean(destroyMethod = "shutdown")
    public MongoServer mongoServer() {
        MongoServer mongoServer = new MongoServer(new MemoryBackend());
        mongoServer.bind();
        return mongoServer;
    }

    @Primary
    @Bean(destroyMethod = "close")
    public MongoClient mongoClient(MongoServer mongoServer) {
        return MongoClients.create("mongodb://" + mongoServer.getLocalAddress().getHostName() + ":" + mongoServer.getLocalAddress().getPort());
    }

    @Primary
    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "inventory-service-test");
    }

    @Bean
    @ConditionalOnProperty("spring.embedded.kafka.brokers")
    public ProducerFactory<String, ReservationCreatedEvent> testProducerFactory(Environment environment) {
        return new DefaultKafkaProducerFactory<>(Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        ));
    }

    @Bean
    @ConditionalOnProperty("spring.embedded.kafka.brokers")
    public KafkaTemplate<String, ReservationCreatedEvent> reservationCreatedEventKafkaTemplate(
        ProducerFactory<String, ReservationCreatedEvent> testProducerFactory) {
        return new KafkaTemplate<>(testProducerFactory);
    }

    @Bean
    @ConditionalOnProperty("spring.embedded.kafka.brokers")
    public ConsumerFactory<String, InventoryEvent> inventoryEventConsumerFactory(Environment environment) {
        JsonDeserializer<InventoryEvent> deserializer = new JsonDeserializer<>(InventoryEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.embedded.kafka.brokers"),
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false
        ), new StringDeserializer(), deserializer);
    }

    @Bean
    @ConditionalOnProperty("spring.embedded.kafka.brokers")
    public ConcurrentKafkaListenerContainerFactory<String, InventoryEvent> inventoryEventKafkaListenerContainerFactory(
        ConsumerFactory<String, InventoryEvent> inventoryEventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, InventoryEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(inventoryEventConsumerFactory);
        return factory;
    }
}
