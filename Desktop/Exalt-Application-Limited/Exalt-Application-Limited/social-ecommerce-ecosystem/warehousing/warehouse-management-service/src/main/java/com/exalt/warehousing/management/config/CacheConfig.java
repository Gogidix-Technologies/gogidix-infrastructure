package com.exalt.warehousing.management.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Configuration for caching in the warehouse management service
 * Uses Caffeine cache for high performance in-memory caching
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String INVENTORY_ITEMS_CACHE = "inventoryItems";
    public static final String ORDER_ITEMS_CACHE = "orderItems";
    public static final String WAREHOUSE_INVENTORY_CACHE = "warehouseInventory";
    public static final String ORDER_ITEMS_BY_ORDER_CACHE = "orderItemsByOrder";
    public static final String LOCATION_CACHE = "locations";

    /**
     * Configure cache manager with Caffeine for high performance
     * 
     * @return the cache manager
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Set default cache configuration
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .recordStats());
        
        // Set the cache names
        cacheManager.setCacheNames(Arrays.asList(
                INVENTORY_ITEMS_CACHE,
                ORDER_ITEMS_CACHE,
                WAREHOUSE_INVENTORY_CACHE,
                ORDER_ITEMS_BY_ORDER_CACHE,
                LOCATION_CACHE
        ));
        
        return cacheManager;
    }
    
    /**
     * Simple cache manager for testing and development
     * 
     * @return the simple cache manager
     */
    @Bean
    public CacheManager simpleCacheManager() {
        return new ConcurrentMapCacheManager(
                INVENTORY_ITEMS_CACHE,
                ORDER_ITEMS_CACHE,
                WAREHOUSE_INVENTORY_CACHE,
                ORDER_ITEMS_BY_ORDER_CACHE,
                LOCATION_CACHE
        );
    }
} 
