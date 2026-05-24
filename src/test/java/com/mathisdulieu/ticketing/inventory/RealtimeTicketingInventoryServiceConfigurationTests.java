package com.mathisdulieu.ticketing.inventory;

import com.mathisdulieu.ticketing.library.core.dto.InventoryEvent;
import com.mathisdulieu.ticketing.library.core.dto.ReservationCreatedEvent;
import com.mathisdulieu.ticketing.library.core.utils.UuidService;
import com.mathisdulieu.ticketing.library.test.kafka.config.KafkaConsumerTestConfig;
import com.mathisdulieu.ticketing.library.test.kafka.config.KafkaProducerTestConfig;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@TestConfiguration
public class RealtimeTicketingInventoryServiceConfigurationTests {

    @Bean
    public MBeanExporter exporter() {
        return Mockito.mock(MBeanExporter.class);
    }

    @Bean
    public UuidService uuidService() {
        return new UuidService();
    }

    @Bean
    @ConditionalOnProperty("spring.embedded.kafka.brokers")
    public KafkaTemplate<String, ReservationCreatedEvent> reservationCreatedEventKafkaTemplate(Environment environment) {
        return KafkaProducerTestConfig.kafkaTemplate(environment.getProperty("spring.kafka.bootstrap-servers"));
    }

    @Bean
    @ConditionalOnProperty("spring.embedded.kafka.brokers")
    public ConcurrentKafkaListenerContainerFactory<String, InventoryEvent> inventoryEventKafkaListenerContainerFactory(Environment environment) {
        ConsumerFactory<String, InventoryEvent> consumerFactory = KafkaConsumerTestConfig.consumerFactory(environment.getProperty("spring.embedded.kafka.brokers"), InventoryEvent.class);
        return KafkaConsumerTestConfig.listenerContainerFactory(consumerFactory);
    }
}
