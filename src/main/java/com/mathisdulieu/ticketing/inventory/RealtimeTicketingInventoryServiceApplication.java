package com.mathisdulieu.ticketing.inventory;

import com.mathisdulieu.ticketing.inventory.config.CorsProperties;
import com.mathisdulieu.ticketing.inventory.config.KafkaProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Date;
import java.util.TimeZone;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableConfigurationProperties({
    CorsProperties.class,
    KafkaProperties.class
})
public class RealtimeTicketingInventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealtimeTicketingInventoryServiceApplication.class, args);
    }

    @PostConstruct
    void steUtcTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        log.info("RealtimeTicketingInventoryServiceApplication running in UTC timezone at : {}", new Date());
    }

}
