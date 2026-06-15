package com.mathisdulieu.ticketing.inventory;

import com.mathisdulieu.ticketing.library.core.utils.UuidService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jmx.export.MBeanExporter;

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

}
