package com.mathisdulieu.ticketing.inventory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Realtime Ticketing — Inventory Service")
                .description("Handles ticket stock, seat availability and overbooking prevention")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Mathis Dulieu")
                    .url("https://github.com/MathisDulieu")))
            .servers(List.of(
                new Server().url("http://localhost:8082").description("Local"),
                new Server().url("https://inventory.yourdomain.com").description("Production")
            ));
    }

}
