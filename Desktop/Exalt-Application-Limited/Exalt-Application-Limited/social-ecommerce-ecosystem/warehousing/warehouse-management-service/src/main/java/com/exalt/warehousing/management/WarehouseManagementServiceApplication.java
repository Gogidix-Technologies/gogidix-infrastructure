package com.exalt.warehousing.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
    "com.ecosystem.warehousing.management",
    "com.ecosystem.warehousing.task"
})
@EnableDiscoveryClient
@EnableFeignClients
@EnableKafka
@EnableAsync
@EnableScheduling
public class WarehouseManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseManagementServiceApplication.class, args);
    }
}
