package com.exalt.warehousing.management.service;

import com.exalt.warehousing.management.config.DataLakeProperties;
import com.exalt.warehousing.management.dto.etl.DataLakePayloadDTO;
import com.exalt.warehousing.management.dto.etl.EtlBatchDTO;
import com.exalt.warehousing.management.dto.etl.EtlResultDTO;
import com.exalt.warehousing.management.repository.LocationRepository;
import com.exalt.warehousing.management.repository.WarehouseRepository;
import com.exalt.warehousing.management.repository.WarehouseTaskRepository;
import com.exalt.warehousing.management.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j; - Replaced with explicit logger
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for ETL (Extract, Transform, Load) operations to the data lake
 */
@Service
@RequiredArgsConstructor
public class DataLakeEtlService {

    // Explicit logger instead of using Lombok's @Slf4j
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataLakeEtlService.class);

    private final RestTemplate dataLakeRestTemplate;
    private final DataLakeProperties dataLakeProperties;
    private final WarehouseRepository warehouseRepository;
    private final ZoneRepository zoneRepository;
    private final LocationRepository locationRepository;
    private final WarehouseTaskRepository warehouseTaskRepository;

    /**
     * Scheduled task to extract and send warehouse statistics to the data lake
     */
    @Scheduled(cron = "${warehouse.data-lake.warehouse-stats-cron:0 0 * * * *}")
    public void scheduleWarehouseStatisticsExtraction() {
        if (!dataLakeProperties.isEnabled()) {
            log.debug("Data lake integration is disabled. Skipping scheduled extraction.");
            return;
        }
        
        log.info("Starting scheduled warehouse statistics extraction for data lake");
        try {
            extractAndSendWarehouseStatistics();
            log.info("Completed scheduled warehouse statistics extraction");
        } catch (Exception e) {
            log.error("Error during scheduled warehouse statistics extraction", e);
        }
    }

    /**
     * Scheduled task to extract and send task completion data to the data lake
     */
    @Scheduled(cron = "${warehouse.data-lake.task-completion-cron:0 15 * * * *}")
    public void scheduleTaskCompletionExtraction() {
        if (!dataLakeProperties.isEnabled()) {
            return;
        }
        
        log.info("Starting scheduled task completion extraction for data lake");
        try {
            extractAndSendTaskCompletionData();
            log.info("Completed scheduled task completion extraction");
        } catch (Exception e) {
            log.error("Error during scheduled task completion extraction", e);
        }
    }

    /**
     * Extract and send warehouse statistics to the data lake
     * 
     * @return the ETL result with status information
     */
    public EtlResultDTO extractAndSendWarehouseStatistics() {
        log.info("Extracting warehouse statistics for data lake");
        
        // Get warehouse statistics
        long warehouseCount = warehouseRepository.count();
        long zoneCount = zoneRepository.count();
        long locationCount = locationRepository.count();
        
        // Create data payload
        Map<String, Object> data = new HashMap<>();
        data.put("warehouseCount", warehouseCount);
        data.put("zoneCount", zoneCount);
        data.put("locationCount", locationCount);
        data.put("extractionTime", LocalDateTime.now().toString());
        
        DataLakePayloadDTO payload = DataLakePayloadDTO.builder()
                .datasetName(dataLakeProperties.getDefaultDataset())
                .tableName("warehouse_statistics")
                .data(data)
                .build();
        
        // Send to data lake
        return sendToDataLake(payload);
    }

    /**
     * Extract and send task completion data to the data lake
     * 
     * @return the ETL result with status information
     */
    public EtlResultDTO extractAndSendTaskCompletionData() {
        log.info("Extracting task completion data for data lake");
        
        // Get task statistics (example implementation)
        long pendingTasks = warehouseTaskRepository.countByStatus("PENDING");
        long completedTasks = warehouseTaskRepository.countByStatus("COMPLETED");
        long inProgressTasks = warehouseTaskRepository.countByStatus("IN_PROGRESS");
        double avgCompletionTime = warehouseTaskRepository.calculateAverageCompletionTime();
        
        // Create data payload
        Map<String, Object> data = new HashMap<>();
        data.put("pendingTasks", pendingTasks);
        data.put("completedTasks", completedTasks);
        data.put("inProgressTasks", inProgressTasks);
        data.put("avgCompletionTimeMinutes", avgCompletionTime);
        data.put("extractionTime", LocalDateTime.now().toString());
        
        DataLakePayloadDTO payload = DataLakePayloadDTO.builder()
                .datasetName(dataLakeProperties.getDefaultDataset())
                .tableName("task_completion_metrics")
                .data(data)
                .build();
        
        // Send to data lake
        return sendToDataLake(payload);
    }

    /**
     * Send a batch of data to the data lake
     * 
     * @param batch the batch of data to send
     * @return the ETL result with batch processing information
     */
    public EtlResultDTO sendBatch(EtlBatchDTO batch) {
        log.info("Sending batch of {} records to data lake", batch.getRecords().size());
        
        DataLakePayloadDTO payload = DataLakePayloadDTO.builder()
                .datasetName(batch.getDatasetName())
                .tableName(batch.getTableName())
                .data(Map.of("records", batch.getRecords()))
                .batchId(UUID.randomUUID().toString())
                .build();
        
        return sendToDataLake(payload);
    }

    /**
     * Send data to the data lake with retry capability
     * 
     * @param payload the data payload to send
     * @return the ETL result with status information
     */
    @Retryable(
        value = {RestClientException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    private EtlResultDTO sendToDataLake(DataLakePayloadDTO payload) {
        if (!dataLakeProperties.isEnabled()) {
            log.warn("Data lake integration is disabled. Data will not be sent.");
            return EtlResultDTO.builder()
                    .success(false)
                    .message("Data lake integration is disabled")
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        try {
            String url = dataLakeProperties.getBaseUrl() + "/api/v1/ingest";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-Key", dataLakeProperties.getApiKey());
            
            HttpEntity<DataLakePayloadDTO> request = new HttpEntity<>(payload, headers);
            
            log.debug("Sending data to data lake: {}", url);
            ResponseEntity<EtlResultDTO> response = dataLakeRestTemplate.postForEntity(
                    url, request, EtlResultDTO.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Successfully sent data to data lake: {}", response.getBody().getBatchId());
                return response.getBody();
            } else {
                log.error("Failed to send data to data lake. Status: {}", response.getStatusCode());
                return EtlResultDTO.builder()
                        .success(false)
                        .message("Failed with status: " + response.getStatusCode())
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        } catch (Exception e) {
            log.error("Error sending data to data lake", e);
            throw new RestClientException("Failed to send data to data lake: " + e.getMessage(), e);
        }
    }
}
