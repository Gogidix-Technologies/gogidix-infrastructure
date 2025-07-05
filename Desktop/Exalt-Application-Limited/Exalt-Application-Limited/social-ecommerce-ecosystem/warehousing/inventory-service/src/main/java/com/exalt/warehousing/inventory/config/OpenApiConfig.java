package com.exalt.warehousing.inventory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration for OpenAPI 3.0 documentation
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * Configure the OpenAPI documentation
     */
    @Bean
    public OpenAPI inventoryServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventory Service API")
                        .description("API for managing inventory in the warehousing domain of the Micro-Social-Ecommerce Ecosystem")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Warehousing Domain Team")
                                .email("warehousing@example.com")
                                .url("https://github.com/example/warehousing-domain"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://example.com/licenses/proprietary")))
                .servers(List.of(
                        new Server()
                                .url("/api/inventory")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://dev.example.com/api/inventory")
                                .description("Development Server"),
                        new Server()
                                .url("https://staging.example.com/api/inventory")
                                .description("Staging Server"),
                        new Server()
                                .url("https://api.example.com/api/inventory")
                                .description("Production Server")
                ));
    }
} 
