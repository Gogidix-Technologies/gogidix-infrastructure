package com.exalt.warehousing.inventoryservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.cloud.config.client.ConfigClientProperties;

/**
 * Test configuration for mocking external dependencies
 */
@TestConfiguration
public class TestConfiguration {
    
    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @MockBean
    private RedisTemplate<String, Object> redisTemplate;
    
    @MockBean
    private EurekaClientConfigBean eurekaClientConfigBean;
    
    @MockBean
    private ConfigClientProperties configClientProperties;
}
