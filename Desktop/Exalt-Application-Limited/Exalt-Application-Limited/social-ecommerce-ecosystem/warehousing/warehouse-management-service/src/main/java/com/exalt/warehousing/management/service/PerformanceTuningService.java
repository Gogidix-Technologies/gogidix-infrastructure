package com.exalt.warehousing.management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for performance tuning and load testing analysis
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceTuningService {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    
    // Store performance metrics
    private final Map<String, ConcurrentHashMap<String, AtomicLong>> methodExecutionTimes = new ConcurrentHashMap<>();
    private final Map<String, ConcurrentHashMap<String, AtomicLong>> methodCallCounts = new ConcurrentHashMap<>();
    
    /**
     * Record method execution time for performance analysis
     * 
     * @param className the class name
     * @param methodName the method name
     * @param executionTimeMs the execution time in milliseconds
     */
    public void recordMethodExecutionTime(String className, String methodName, long executionTimeMs) {
        methodExecutionTimes.computeIfAbsent(className, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(methodName, k -> new AtomicLong(0))
                .addAndGet(executionTimeMs);
        
        methodCallCounts.computeIfAbsent(className, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(methodName, k -> new AtomicLong(0))
                .incrementAndGet();
        
        // Log slow method executions
        if (executionTimeMs > 1000) {
            log.warn("Slow method execution detected: {}.{} took {}ms", className, methodName, executionTimeMs);
        }
    }
    
    /**
     * Get database connection pool statistics
     * 
     * @return map of connection pool statistics
     */
    public Map<String, Object> getConnectionPoolStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // This assumes HikariCP as the connection pool implementation
            // The actual implementation may vary depending on the connection pool used
            // For a complete implementation, check the specific connection pool's JMX metrics
            
            int activeConnections = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.PROCESSLIST WHERE COMMAND != 'Sleep'", 
                    Integer.class);
            
            stats.put("activeConnections", activeConnections);
            stats.put("timestamp", System.currentTimeMillis());
            
            // Add more connection pool metrics as needed
            
        } catch (Exception e) {
            log.error("Error retrieving connection pool statistics", e);
            stats.put("error", e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * Get slow query statistics from the database
     * 
     * @return list of slow queries with execution statistics
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSlowQueryStats() {
        // This query assumes a recent MySQL/MariaDB implementation
        // For other databases, the query would need to be adjusted
        try {
            return jdbcTemplate.queryForList(
                    "SELECT id, db, query, exec_count, total_latency, avg_latency, max_latency " +
                    "FROM performance_schema.events_statements_summary_by_digest " +
                    "WHERE avg_latency > 100000 " + // 100ms
                    "ORDER BY avg_latency DESC LIMIT 10");
        } catch (Exception e) {
            log.error("Error retrieving slow query statistics", e);
            return List.of(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get performance metrics for all recorded method executions
     * 
     * @return map of performance metrics by class and method
     */
    public Map<String, Object> getMethodPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        for (Map.Entry<String, ConcurrentHashMap<String, AtomicLong>> classEntry : methodExecutionTimes.entrySet()) {
            String className = classEntry.getKey();
            Map<String, Object> classMethods = new HashMap<>();
            
            for (Map.Entry<String, AtomicLong> methodEntry : classEntry.getValue().entrySet()) {
                String methodName = methodEntry.getKey();
                long totalTime = methodEntry.getValue().get();
                long callCount = methodCallCounts.get(className).get(methodName).get();
                
                Map<String, Object> methodMetrics = new HashMap<>();
                methodMetrics.put("totalTimeMs", totalTime);
                methodMetrics.put("callCount", callCount);
                methodMetrics.put("averageTimeMs", callCount > 0 ? totalTime / callCount : 0);
                
                classMethods.put(methodName, methodMetrics);
            }
            
            metrics.put(className, classMethods);
        }
        
        return metrics;
    }
    
    /**
     * Reset all performance metrics
     */
    public void resetPerformanceMetrics() {
        methodExecutionTimes.clear();
        methodCallCounts.clear();
        log.info("Performance metrics have been reset");
    }
    
    /**
     * Log performance statistics periodically
     */
    @Scheduled(fixedRateString = "${warehouse.performance.stats-log-interval-ms:3600000}")
    public void logPerformanceStatistics() {
        log.info("Performance statistics summary:");
        
        // Log method performance metrics
        Map<String, Object> metrics = getMethodPerformanceMetrics();
        for (Map.Entry<String, Object> classEntry : metrics.entrySet()) {
            String className = classEntry.getKey();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> methods = (Map<String, Object>) classEntry.getValue();
            
            for (Map.Entry<String, Object> methodEntry : methods.entrySet()) {
                String methodName = methodEntry.getKey();
                
                @SuppressWarnings("unchecked")
                Map<String, Object> methodMetrics = (Map<String, Object>) methodEntry.getValue();
                
                long totalTime = (long) methodMetrics.get("totalTimeMs");
                long callCount = (long) methodMetrics.get("callCount");
                long avgTime = (long) methodMetrics.get("averageTimeMs");
                
                log.info("{}.{}: {} calls, {} ms total, {} ms avg", 
                        className, methodName, callCount, totalTime, avgTime);
            }
        }
        
        // Log connection pool statistics
        Map<String, Object> poolStats = getConnectionPoolStats();
        log.info("Connection pool stats: {}", poolStats);
        
        // Log slow query statistics
        List<Map<String, Object>> slowQueries = getSlowQueryStats();
        log.info("Slow queries (top 10): {}", slowQueries);
    }
    
    /**
     * Analyze database indexes and suggest improvements
     * 
     * @return map of index analysis results
     */
    @Transactional(readOnly = true)
    public Map<String, Object> analyzeIndexes() {
        Map<String, Object> results = new HashMap<>();
        
        try {
            // This query is specific to MySQL/MariaDB and will need adjustment for other databases
            List<Map<String, Object>> missingIndexes = jdbcTemplate.queryForList(
                    "SELECT * FROM sys.schema_tables_with_full_table_scans " +
                    "ORDER BY rows_full_scanned DESC LIMIT 10");
            
            results.put("tablesWithFullScans", missingIndexes);
            
            List<Map<String, Object>> unusedIndexes = jdbcTemplate.queryForList(
                    "SELECT * FROM sys.schema_unused_indexes");
            
            results.put("unusedIndexes", unusedIndexes);
            
        } catch (Exception e) {
            log.error("Error analyzing database indexes", e);
            results.put("error", e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Run simulated load test for performance analysis
     * 
     * @param concurrentUsers number of concurrent users to simulate
     * @param durationSeconds duration of the test in seconds
     * @return load test results
     */
    public Map<String, Object> runSimulatedLoadTest(int concurrentUsers, int durationSeconds) {
        log.info("Starting simulated load test with {} concurrent users for {} seconds",
                concurrentUsers, durationSeconds);
        
        Map<String, Object> results = new HashMap<>();
        results.put("startTime", System.currentTimeMillis());
        results.put("concurrentUsers", concurrentUsers);
        results.put("durationSeconds", durationSeconds);
        
        // Reset metrics before test
        resetPerformanceMetrics();
        
        // In a real implementation, this would launch multiple threads
        // to simulate concurrent user activity against the APIs
        // For demonstration purposes, we'll just log the intent
        
        log.info("Load test simulation completed (this is a placeholder implementation)");
        
        // Add test results
        results.put("endTime", System.currentTimeMillis());
        results.put("metrics", getMethodPerformanceMetrics());
        results.put("connectionPoolStats", getConnectionPoolStats());
        results.put("slowQueries", getSlowQueryStats());
        
        return results;
    }
} 
