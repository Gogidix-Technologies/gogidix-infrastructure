package com.exalt.warehousing.inventory.config;

import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Define tags for OpenAPI documentation to group API endpoints
 */
@Configuration
public class OpenApiTags {

    public static final String INVENTORY_ITEMS_TAG = "Inventory Items";
    public static final String WAREHOUSE_LOCATIONS_TAG = "Warehouse Locations";
    public static final String INVENTORY_RESERVATIONS_TAG = "Inventory Reservations";
    public static final String INVENTORY_TRANSACTIONS_TAG = "Inventory Transactions";
    public static final String INVENTORY_ALLOCATIONS_TAG = "Inventory Allocations";

    /**
     * Define tags for API documentation
     */
    @Bean
    public List<Tag> inventoryTags() {
        return List.of(
                new Tag()
                        .name(INVENTORY_ITEMS_TAG)
                        .description("Operations for managing inventory items"),
                new Tag()
                        .name(WAREHOUSE_LOCATIONS_TAG)
                        .description("Operations for managing warehouse locations"),
                new Tag()
                        .name(INVENTORY_RESERVATIONS_TAG)
                        .description("Operations for managing inventory reservations"),
                new Tag()
                        .name(INVENTORY_TRANSACTIONS_TAG)
                        .description("Operations for tracking inventory transactions"),
                new Tag()
                        .name(INVENTORY_ALLOCATIONS_TAG)
                        .description("Operations for managing inventory allocations in warehouse locations")
        );
    }
} 
