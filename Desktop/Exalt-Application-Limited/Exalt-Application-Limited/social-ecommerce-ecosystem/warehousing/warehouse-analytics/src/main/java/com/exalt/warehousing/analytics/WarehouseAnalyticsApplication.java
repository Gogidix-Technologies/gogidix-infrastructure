package com.exalt.warehousing.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Warehouse Analytics Service Application
 * 
 * Advanced analytics microservice for warehouse operations providing:
 * - Real-time KPI monitoring and dashboard reporting
 * - Operational performance analysis and optimization recommendations
 * - Predictive analytics for inventory management and demand forecasting
 * - Cross-domain data aggregation from multiple warehouse systems
 * - Advanced search and filtering capabilities with Elasticsearch
 * - Machine learning-powered insights for operational efficiency
 * - Automated alert generation for performance anomalies
 * - Historical trend analysis and comparative reporting
 * - Multi-dimensional analytics across time, location, and categories
 * - Integration with BI tools and external reporting systems
 */
@SpringBootApplication
@EnableFeignClients
@EnableKafka
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@EnableCaching
@EnableJpaRepositories(basePackages = "com.ecosystem.warehousing.analytics.repository.jpa")
@EnableElasticsearchRepositories(basePackages = "com.ecosystem.warehousing.analytics.repository.elasticsearch")
public class WarehouseAnalyticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseAnalyticsApplication.class, args);
    }
}