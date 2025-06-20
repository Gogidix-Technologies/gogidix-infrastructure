package com.exalt.warehousing.management.client;

import com.exalt.warehousing.management.dto.OrderItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Fallback factory for the OrderServiceClient
 */
@Component
public class OrderServiceClientFallbackFactory implements FallbackFactory<OrderServiceClient> {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceClientFallbackFactory.class);

    @Override
    public OrderServiceClient create(Throwable cause) {
        return new OrderServiceClient() {
            @Override
            public ResponseEntity<List<OrderItemDTO>> getAllOrderItems() {
                log.error("Fallback for getAllOrderItems: {}", cause.getMessage());
                return ResponseEntity.ok(Collections.emptyList());
            }

            @Override
            public ResponseEntity<List<OrderItemDTO>> getOrderItemsByOrderId(UUID orderId) {
                log.error("Fallback for getOrderItemsByOrderId: {}", cause.getMessage());
                return ResponseEntity.ok(Collections.emptyList());
            }

            @Override
            public ResponseEntity<OrderItemDTO> getOrderItemById(UUID itemId) {
                log.error("Fallback for getOrderItemById: {}", cause.getMessage());
                return ResponseEntity.ok(null);
            }
        };
    }
}