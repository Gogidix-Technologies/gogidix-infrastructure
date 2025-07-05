package com.exalt.warehousing.management.client;

import com.exalt.warehousing.management.dto.OrderItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

/**
 * Feign client for the Order Service
 */
@FeignClient(name = "order-service", fallbackFactory = OrderServiceClientFallbackFactory.class)
public interface OrderServiceClient {

    /**
     * Get all order items
     *
     * @return list of all order items
     */
    @GetMapping("/api/v1/orders/items")
    ResponseEntity<List<OrderItemDTO>> getAllOrderItems();

    /**
     * Get order items by order id
     *
     * @param orderId the order id
     * @return list of order items for the order
     */
    @GetMapping("/api/v1/orders/{orderId}/items")
    ResponseEntity<List<OrderItemDTO>> getOrderItemsByOrderId(@PathVariable UUID orderId);

    /**
     * Get order item by id
     *
     * @param itemId the item id
     * @return the order item
     */
    @GetMapping("/api/v1/orders/items/{itemId}")
    ResponseEntity<OrderItemDTO> getOrderItemById(@PathVariable UUID itemId);
} 
