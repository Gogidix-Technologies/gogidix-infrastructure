package com.exalt.warehousing.management.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
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
 * Configuration for OpenAPI documentation
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${spring.application.name}")
    private String applicationName;
    
    @Value("${server.servlet.context-path}")
    private String contextPath;
    
    /**
     * Configure OpenAPI documentation
     * 
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI warehouseManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Warehouse Management API")
                        .description("API for managing warehouses, zones, locations, staff, and tasks")
                        .version("v0.1.0")
                        .contact(new Contact()
                                .name("Warehousing Domain Team")
                                .email("warehousing@example.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://example.com/licenses/proprietary")))
                .externalDocs(new ExternalDocumentation()
                        .description("Warehouse Management Documentation")
                        .url("https://example.com/docs/warehouse"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8086" + contextPath)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api-dev.example.com" + contextPath)
                                .description("Development Server"),
                        new Server()
                                .url("https://api.example.com" + contextPath)
                                .description("Production Server")));
    }
} 
