package com.exalt.warehousing.fulfillment.dto.inventory;

import java.util.List;
import java.util.UUID;

/**
 * DTO for checking inventory availability
 */
public class InventoryCheckRequest {

    /**
     * ID of the order for which inventory is being checked
     */
    private UUID orderId;

    /**
     * Delivery address for warehouse selection
     */
    private DeliveryAddressDTO deliveryAddress;

    /**
     * List of items to check inventory for
     */
    private List<InventoryItemDTO> items;

    // Constructors
    public InventoryCheckRequest() {
        // Default constructor
    }

    public InventoryCheckRequest(UUID orderId, DeliveryAddressDTO deliveryAddress, List<InventoryItemDTO> items) {
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
        this.items = items;
    }

    // Getters
    public UUID getOrderId() {
        return orderId;
    }

    public DeliveryAddressDTO getDeliveryAddress() {
        return deliveryAddress;
    }

    public List<InventoryItemDTO> getItems() {
        return items;
    }

    // Setters
    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public void setDeliveryAddress(DeliveryAddressDTO deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setItems(List<InventoryItemDTO> items) {
        this.items = items;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID orderId;
        private DeliveryAddressDTO deliveryAddress;
        private List<InventoryItemDTO> items;

        public Builder orderId(UUID orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder deliveryAddress(DeliveryAddressDTO deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
            return this;
        }

        public Builder items(List<InventoryItemDTO> items) {
            this.items = items;
            return this;
        }

        public InventoryCheckRequest build() {
            return new InventoryCheckRequest(orderId, deliveryAddress, items);
        }
    }

    /**
     * DTO for delivery address
     */
    public static class DeliveryAddressDTO {
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String stateProvince;
        private String postalCode;
        private String country;

        // Constructors
        public DeliveryAddressDTO() {
            // Default constructor
        }

        public DeliveryAddressDTO(String addressLine1, String addressLine2, String city, String stateProvince, String postalCode, String country) {
            this.addressLine1 = addressLine1;
            this.addressLine2 = addressLine2;
            this.city = city;
            this.stateProvince = stateProvince;
            this.postalCode = postalCode;
            this.country = country;
        }

        // Getters
        public String getAddressLine1() {
            return addressLine1;
        }

        public String getAddressLine2() {
            return addressLine2;
        }

        public String getCity() {
            return city;
        }

        public String getStateProvince() {
            return stateProvince;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public String getCountry() {
            return country;
        }

        // Setters
        public void setAddressLine1(String addressLine1) {
            this.addressLine1 = addressLine1;
        }

        public void setAddressLine2(String addressLine2) {
            this.addressLine2 = addressLine2;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public void setStateProvince(String stateProvince) {
            this.stateProvince = stateProvince;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        // Builder pattern
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String addressLine1;
            private String addressLine2;
            private String city;
            private String stateProvince;
            private String postalCode;
            private String country;

            public Builder addressLine1(String addressLine1) {
                this.addressLine1 = addressLine1;
                return this;
            }

            public Builder addressLine2(String addressLine2) {
                this.addressLine2 = addressLine2;
                return this;
            }

            public Builder city(String city) {
                this.city = city;
                return this;
            }

            public Builder stateProvince(String stateProvince) {
                this.stateProvince = stateProvince;
                return this;
            }

            public Builder postalCode(String postalCode) {
                this.postalCode = postalCode;
                return this;
            }

            public Builder country(String country) {
                this.country = country;
                return this;
            }

            public DeliveryAddressDTO build() {
                return new DeliveryAddressDTO(addressLine1, addressLine2, city, stateProvince, postalCode, country);
            }
        }
    }

    /**
     * DTO for inventory item
     */
    public static class InventoryItemDTO {
        private UUID productId;
        private String sku;
        private int quantity;

        // Constructors
        public InventoryItemDTO() {
            // Default constructor
        }

        public InventoryItemDTO(UUID productId, String sku, int quantity) {
            this.productId = productId;
            this.sku = sku;
            this.quantity = quantity;
        }

        // Getters
        public UUID getProductId() {
            return productId;
        }

        public String getSku() {
            return sku;
        }

        public int getQuantity() {
            return quantity;
        }

        // Setters
        public void setProductId(UUID productId) {
            this.productId = productId;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        // Builder pattern
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private UUID productId;
            private String sku;
            private int quantity;

            public Builder productId(UUID productId) {
                this.productId = productId;
                return this;
            }

            public Builder sku(String sku) {
                this.sku = sku;
                return this;
            }

            public Builder quantity(int quantity) {
                this.quantity = quantity;
                return this;
            }

            public InventoryItemDTO build() {
                return new InventoryItemDTO(productId, sku, quantity);
            }
        }
    }
}