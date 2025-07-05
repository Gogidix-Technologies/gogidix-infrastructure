package com.exalt.warehousing.inventory.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.UUID;

/**
 * Feign client for interacting with the Order Service
 */
@FeignClient(name = "order-service", url = "${integration.order-service.url}")
public interface OrderServiceClient {

    /**
     * Get order details by ID
     * @param orderId the order ID
     * @return order details
     */
    @GetMapping("/orders/{orderId}")
    ResponseEntity<Map<String, Object>> getOrderById(@PathVariable("orderId") UUID orderId);

    /**
     * Check if an order is in active state (can be processed)
     * @param orderId the order ID
     * @return true if order is active
     */
    @GetMapping("/orders/{orderId}/is-active")
    ResponseEntity<Boolean> isOrderActive(@PathVariable("orderId") UUID orderId);

    /**
     * Notify Order Service about inventory reservation status
     * @param orderId the order ID
     * @param status the reservation status (SUCCESS or FAILURE)
     * @param details additional details
     * @return confirmation response
     */
    @PostMapping("/orders/{orderId}/inventory-reservation")
    ResponseEntity<Map<String, Object>> updateInventoryReservation(
            @PathVariable("orderId") UUID orderId,
            @RequestBody Map<String, Object> status);

    /**
     * Notify Order Service about inventory allocation status
     * @param orderId the order ID
     * @param status the allocation status (SUCCESS or FAILURE)
     * @param details additional details
     * @return confirmation response
     */
    @PostMapping("/orders/{orderId}/inventory-allocation")
    ResponseEntity<Map<String, Object>> updateInventoryAllocation(
            @PathVariable("orderId") UUID orderId,
            @RequestBody Map<String, Object> status);
}
