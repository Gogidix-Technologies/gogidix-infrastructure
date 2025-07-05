package com.exalt.warehousing.fulfillment.client.impl;

import com.exalt.warehousing.fulfillment.client.InventoryClient;
import com.exalt.warehousing.fulfillment.dto.inventory.InventoryAllocationRequest;
import com.exalt.warehousing.fulfillment.dto.inventory.InventoryAllocationResponse;
import com.exalt.warehousing.fulfillment.dto.inventory.InventoryCheckRequest;
import com.exalt.warehousing.fulfillment.dto.inventory.InventoryCheckResponse;
import com.exalt.warehousing.fulfillment.dto.inventory.InventoryReleaseRequest;
import com.exalt.warehousing.fulfillment.dto.inventory.InventoryReleaseResponse;
import com.exalt.warehousing.fulfillment.dto.inventory.InventoryReservationRequest;
import com.exalt.warehousing.fulfillment.dto.inventory.InventoryReservationResponse;
import com.exalt.warehousing.fulfillment.exception.ServiceIntegrationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

/**
 * Implementation of InventoryClient using RestTemplate
 */
@Component
@Slf4j
public class InventoryClientImpl implements InventoryClient {

    private final RestTemplate restTemplate;
    private final String inventoryServiceBaseUrl;

    public InventoryClientImpl(
            RestTemplate restTemplate,
            @Value("${services.inventory.baseUrl}") String inventoryServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.inventoryServiceBaseUrl = inventoryServiceBaseUrl;
    }

    @Override
    public InventoryCheckResponse checkInventoryAvailability(InventoryCheckRequest request) {
        log.debug("Checking inventory availability: {}", request);
        try {
            String url = inventoryServiceBaseUrl + "/api/v1/inventory/check";
            ResponseEntity<InventoryCheckResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    InventoryCheckResponse.class);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error checking inventory availability", e);
            throw new ServiceIntegrationException("Failed to check inventory availability", e);
        }
    }

    @Override
    public InventoryReservationResponse reserveInventory(InventoryReservationRequest request) {
        log.debug("Reserving inventory: {}", request);
        try {
            String url = inventoryServiceBaseUrl + "/api/v1/inventory/reserve";
            ResponseEntity<InventoryReservationResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    InventoryReservationResponse.class);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error reserving inventory", e);
            throw new ServiceIntegrationException("Failed to reserve inventory", e);
        }
    }

    @Override
    public InventoryAllocationResponse allocateInventory(InventoryAllocationRequest request) {
        log.debug("Allocating inventory: {}", request);
        try {
            String url = inventoryServiceBaseUrl + "/api/v1/inventory/allocate";
            ResponseEntity<InventoryAllocationResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    InventoryAllocationResponse.class);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error allocating inventory", e);
            throw new ServiceIntegrationException("Failed to allocate inventory", e);
        }
    }

    @Override
    public boolean releaseInventory(InventoryReleaseRequest request) {
        log.debug("Releasing inventory: {}", request);
        try {
            String url = inventoryServiceBaseUrl + "/api/v1/inventory/release";
            ResponseEntity<InventoryReleaseResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    InventoryReleaseResponse.class);
            InventoryReleaseResponse result = response.getBody();
            return result != null && result.isSuccess();
        } catch (RestClientException e) {
            log.error("Error releasing inventory", e);
            throw new ServiceIntegrationException("Failed to release inventory", e);
        }
    }

    // Note: getProductInventory method removed as it's not part of the interface

    @Override
    public UUID getRecommendedWarehouse(InventoryCheckRequest request) {
        log.debug("Getting recommended warehouse for request: {}", request);
        try {
            String url = inventoryServiceBaseUrl + "/api/v1/inventory/recommend-warehouse";
            ResponseEntity<UUID> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    UUID.class);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error getting recommended warehouse", e);
            throw new ServiceIntegrationException("Failed to get recommended warehouse", e);
        }
    }

    @Override
    public InventoryCheckResponse checkWarehouseInventory(UUID warehouseId, InventoryCheckRequest request) {
        log.debug("Checking inventory in warehouse {}: {}", warehouseId, request);
        try {
            String url = UriComponentsBuilder.fromUriString(inventoryServiceBaseUrl)
                    .path("/api/v1/inventory/warehouses/{warehouseId}/check")
                    .buildAndExpand(warehouseId)
                    .toUriString();
            
            ResponseEntity<InventoryCheckResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    InventoryCheckResponse.class);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error checking warehouse inventory", e);
            throw new ServiceIntegrationException("Failed to check warehouse inventory", e);
        }
    }
} 
