package com.exalt.warehousing.management.service;

import com.exalt.warehousing.management.model.LocationStatus;
import com.exalt.warehousing.management.model.TaskStatus;
import com.exalt.warehousing.management.model.WarehouseTask;
import com.exalt.warehousing.management.repository.LocationRepository;
import com.exalt.warehousing.management.repository.WarehouseRepository;
import com.exalt.warehousing.management.repository.WarehouseTaskRepository;
import com.exalt.warehousing.management.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for business intelligence reporting and analytics
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessIntelligenceService {

    private final WarehouseRepository warehouseRepository;
    private final ZoneRepository zoneRepository;
    private final LocationRepository locationRepository;
    private final WarehouseTaskRepository warehouseTaskRepository;
    private final ReferenceDataSyncService referenceDataSyncService;
    
    /**
     * Get warehouse efficiency metrics
     *
     * @param warehouseId the warehouse ID
     * @return map of efficiency metrics
     */
    @Cacheable(value = "biCache", key = "'warehouseEfficiency-' + #warehouseId")
    @Transactional(readOnly = true)
    public Map<String, Object> getWarehouseEfficiencyMetrics(UUID warehouseId) {
        log.info("Calculating efficiency metrics for warehouse: {}", warehouseId);
        
        // Get warehouse tasks for the last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        
        Map<String, Object> metrics = new HashMap<>();
        
        // Calculate task completion metrics
        double avgTaskCompletionTime = warehouseTaskRepository.calculateAverageCompletionTimeByWarehouse(warehouseId);
        metrics.put("averageTaskCompletionTimeMinutes", avgTaskCompletionTime);
        
        // Calculate zone utilization
        long totalLocations = locationRepository.countByWarehouseId(warehouseId.toString());
        long activeLocations = locationRepository.countByWarehouseIdAndStatus(warehouseId.toString(), LocationStatus.ACTIVE);
        double zoneUtilization = totalLocations > 0 ? (double) activeLocations / totalLocations * 100 : 0;
        metrics.put("zoneUtilizationPercentage", zoneUtilization);
        
        // Calculate order processing efficiency (if data available)
        // This would require integration with the order service
        // For now, we'll use placeholder values
        metrics.put("orderProcessingTimeMinutes", 45.5);
        metrics.put("pickingAccuracyPercentage", 98.7);
        
        // Calculate warehouse capacity metrics
        metrics.put("totalStorageLocations", totalLocations);
        metrics.put("occupiedStorageLocations", activeLocations);
        metrics.put("availableStorageLocations", totalLocations - activeLocations);
        metrics.put("storageUtilizationPercentage", zoneUtilization);
        
        metrics.put("warehouseId", warehouseId);
        metrics.put("reportTimestamp", LocalDateTime.now());
        
        return metrics;
    }
    
    /**
     * Get staff performance data for dashboard
     *
     * @param warehouseId the warehouse ID
     * @return map of staff performance metrics
     */
    @Cacheable(value = "biCache", key = "'staffPerformance-' + #warehouseId")
    @Transactional(readOnly = true)
    public Map<String, Object> getStaffPerformanceData(UUID warehouseId) {
        log.info("Retrieving staff performance data for warehouse: {}", warehouseId);
        
        Map<String, Object> performanceData = new HashMap<>();
        
        // Placeholder for staff performance metrics
        // In a real implementation, we would retrieve this from the StaffRepository and task history
        
        List<Map<String, Object>> staffMetrics = new ArrayList<>();
        
        // Sample staff metrics
        Map<String, Object> staff1 = new HashMap<>();
        staff1.put("staffId", UUID.randomUUID());
        staff1.put("name", "John Smith");
        staff1.put("tasksCompleted", 145);
        staff1.put("averageCompletionTimeMinutes", 22.5);
        staff1.put("pickingAccuracy", 99.2);
        staffMetrics.add(staff1);
        
        Map<String, Object> staff2 = new HashMap<>();
        staff2.put("staffId", UUID.randomUUID());
        staff2.put("name", "Jane Doe");
        staff2.put("tasksCompleted", 162);
        staff2.put("averageCompletionTimeMinutes", 20.1);
        staff2.put("pickingAccuracy", 99.8);
        staffMetrics.add(staff2);
        
        // Add more staff metrics as needed
        
        performanceData.put("staffMetrics", staffMetrics);
        performanceData.put("warehouseId", warehouseId);
        performanceData.put("reportTimestamp", LocalDateTime.now());
        
        return performanceData;
    }
    
    /**
     * Get inventory turnover metrics
     *
     * @param warehouseId the warehouse ID
     * @param timeRangeInDays time range in days
     * @return map of inventory turnover metrics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getInventoryTurnoverMetrics(UUID warehouseId, Integer timeRangeInDays) {
        log.info("Calculating inventory turnover metrics for warehouse: {} over {} days", 
                warehouseId, timeRangeInDays);
        
        Map<String, Object> turnoverMetrics = new HashMap<>();
        
        // Calculate inventory turnover rate
        // This would require integration with the inventory service
        // For now, we'll use placeholder values
        turnoverMetrics.put("inventoryTurnoverRate", 4.2);
        turnoverMetrics.put("averageDaysInInventory", 21.3);
        
        // Top moving products
        List<Map<String, Object>> topProducts = new ArrayList<>();
        
        // Sample product data
        Map<String, Object> product1 = new HashMap<>();
        product1.put("productId", UUID.randomUUID());
        product1.put("name", "Smartphone X");
        product1.put("sku", "SPX-1000");
        product1.put("turnoverRate", 12.5);
        product1.put("averageDaysInInventory", 7.2);
        topProducts.add(product1);
        
        Map<String, Object> product2 = new HashMap<>();
        product2.put("productId", UUID.randomUUID());
        product2.put("name", "Laptop Pro");
        product2.put("sku", "LTP-2000");
        product2.put("turnoverRate", 8.3);
        product2.put("averageDaysInInventory", 11.5);
        topProducts.add(product2);
        
        // Add more product data as needed
        
        turnoverMetrics.put("topMovingProducts", topProducts);
        turnoverMetrics.put("slowestMovingProducts", generateSlowMovingProducts());
        
        turnoverMetrics.put("warehouseId", warehouseId);
        turnoverMetrics.put("timeRangeInDays", timeRangeInDays);
        turnoverMetrics.put("reportTimestamp", LocalDateTime.now());
        
        return turnoverMetrics;
    }
    
    /**
     * Get order fulfillment analytics
     *
     * @param warehouseId the warehouse ID
     * @param timeRangeInDays time range in days
     * @return map of order fulfillment analytics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getOrderFulfillmentAnalytics(UUID warehouseId, Integer timeRangeInDays) {
        log.info("Retrieving order fulfillment analytics for warehouse: {} over {} days", 
                warehouseId, timeRangeInDays);
        
        Map<String, Object> fulfillmentAnalytics = new HashMap<>();
        
        // Calculate order fulfillment metrics
        // This would require integration with the order service
        // For now, we'll use placeholder values
        fulfillmentAnalytics.put("totalOrdersProcessed", 1245);
        fulfillmentAnalytics.put("averageFulfillmentTimeHours", 6.2);
        fulfillmentAnalytics.put("onTimeDeliveryPercentage", 94.8);
        fulfillmentAnalytics.put("orderAccuracyPercentage", 99.2);
        
        // Order volume by day
        Map<String, Integer> orderVolumeByDay = new HashMap<>();
        // Sample data for the last 7 days
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 7; i++) {
            String day = now.minus(i, ChronoUnit.DAYS).toLocalDate().toString();
            // Random order count between 150 and 250
            int orderCount = 150 + new Random().nextInt(100);
            orderVolumeByDay.put(day, orderCount);
        }
        fulfillmentAnalytics.put("orderVolumeByDay", orderVolumeByDay);
        
        // Peak hours for order processing
        Map<String, Integer> peakHours = new HashMap<>();
        // Sample data for 24 hours
        for (int hour = 0; hour < 24; hour++) {
            // Random order count with a peak in the afternoon
            int peak = hour >= 12 && hour <= 18 ? 30 : 10;
            int orderCount = peak + new Random().nextInt(20);
            peakHours.put(String.valueOf(hour), orderCount);
        }
        fulfillmentAnalytics.put("peakHourOrderVolume", peakHours);
        
        fulfillmentAnalytics.put("warehouseId", warehouseId);
        fulfillmentAnalytics.put("timeRangeInDays", timeRangeInDays);
        fulfillmentAnalytics.put("reportTimestamp", LocalDateTime.now());
        
        return fulfillmentAnalytics;
    }
    
    /**
     * Generate data for slow-moving products (helper method)
     *
     * @return list of slow-moving product data
     */
    private List<Map<String, Object>> generateSlowMovingProducts() {
        List<Map<String, Object>> slowProducts = new ArrayList<>();
        
        // Sample slow-moving product data
        Map<String, Object> product1 = new HashMap<>();
        product1.put("productId", UUID.randomUUID());
        product1.put("name", "Legacy Tablet");
        product1.put("sku", "LGT-500");
        product1.put("turnoverRate", 0.8);
        product1.put("daysInInventory", 120.5);
        slowProducts.add(product1);
        
        Map<String, Object> product2 = new HashMap<>();
        product2.put("productId", UUID.randomUUID());
        product2.put("name", "Wired Headphones");
        product2.put("sku", "WHP-100");
        product2.put("turnoverRate", 1.2);
        product2.put("daysInInventory", 95.2);
        slowProducts.add(product2);
        
        // Add more product data as needed
        
        return slowProducts;
    }
} 
