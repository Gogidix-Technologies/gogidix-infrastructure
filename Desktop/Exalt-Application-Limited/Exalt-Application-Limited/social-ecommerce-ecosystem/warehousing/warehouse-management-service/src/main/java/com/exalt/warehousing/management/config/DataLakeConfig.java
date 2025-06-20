package com.exalt.warehousing.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for data lake integration
 * Provides beans and settings for ETL processes and data warehousing
 */
@Configuration
@EnableScheduling
public class DataLakeConfig {

    /**
     * RestTemplate bean for API calls to data lake services
     */
    @Bean
    public RestTemplate dataLakeRestTemplate() {
        return new RestTemplate();
    }
    
    /**
     * Configuration properties for data lake connection
     */
    @Bean
    public DataLakeProperties dataLakeProperties() {
        return new DataLakeProperties();
    }
} 
