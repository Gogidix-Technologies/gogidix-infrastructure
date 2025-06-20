package com.exalt.warehousing.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class InventoryServiceApplicationTest {

    @Test
    void contextLoads() {
        // This test ensures the Spring context loads successfully
        assertTrue(true, "Application context should load");
    }
}
