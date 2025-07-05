-- V1__Create_inventory_schema.sql

-- Warehouse Location Table
CREATE TABLE IF NOT EXISTS warehouse_location (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    warehouse_type VARCHAR(50) NOT NULL,
    address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    country VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    phone VARCHAR(50),
    email VARCHAR(255),
    manager_name VARCHAR(255),
    total_capacity DECIMAL(19, 2),
    used_capacity DECIMAL(19, 2) DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    operating_hours JSONB,
    features TEXT[],
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Inventory Item Table
CREATE TABLE IF NOT EXISTS inventory_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    warehouse_id UUID NOT NULL REFERENCES warehouse_location(id),
    product_id VARCHAR(255) NOT NULL,
    sku VARCHAR(100) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    reserved_quantity INTEGER NOT NULL DEFAULT 0,
    available_quantity INTEGER NOT NULL DEFAULT 0,
    unit_cost DECIMAL(19, 2),
    status VARCHAR(50) NOT NULL,
    location_code VARCHAR(50),
    batch_number VARCHAR(100),
    expiry_date DATE,
    last_stock_check_date TIMESTAMP,
    reorder_point INTEGER,
    reorder_quantity INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_warehouse_product UNIQUE (warehouse_id, product_id),
    INDEX idx_sku (sku),
    INDEX idx_status (status),
    INDEX idx_warehouse_product (warehouse_id, product_id)
);

-- Inventory Reservation Table
CREATE TABLE IF NOT EXISTS inventory_reservation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reservation_number VARCHAR(100) NOT NULL UNIQUE,
    order_id VARCHAR(255) NOT NULL,
    warehouse_id UUID NOT NULL REFERENCES warehouse_location(id),
    status VARCHAR(50) NOT NULL,
    total_items INTEGER NOT NULL DEFAULT 0,
    expiry_time TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id),
    INDEX idx_status (status),
    INDEX idx_expiry (expiry_time)
);

-- Reservation Item Table
CREATE TABLE IF NOT EXISTS reservation_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reservation_id UUID NOT NULL REFERENCES inventory_reservation(id) ON DELETE CASCADE,
    inventory_item_id UUID NOT NULL REFERENCES inventory_item(id),
    quantity INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Inventory Transaction Table
CREATE TABLE IF NOT EXISTS inventory_transaction (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inventory_item_id UUID NOT NULL REFERENCES inventory_item(id),
    transaction_type VARCHAR(50) NOT NULL,
    quantity INTEGER NOT NULL,
    reference_number VARCHAR(255),
    reference_type VARCHAR(50),
    notes TEXT,
    performed_by VARCHAR(255),
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_item_date (inventory_item_id, transaction_date),
    INDEX idx_type (transaction_type),
    INDEX idx_reference (reference_number)
);

-- Inventory Movement Table
CREATE TABLE IF NOT EXISTS inventory_movement (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    from_warehouse_id UUID REFERENCES warehouse_location(id),
    to_warehouse_id UUID REFERENCES warehouse_location(id),
    product_id VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    movement_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    reference_number VARCHAR(255),
    notes TEXT,
    initiated_by VARCHAR(255),
    approved_by VARCHAR(255),
    shipped_date TIMESTAMP,
    received_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_product (product_id)
);

-- Inventory Allocation Table
CREATE TABLE IF NOT EXISTS inventory_allocation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id VARCHAR(255) NOT NULL,
    inventory_item_id UUID NOT NULL REFERENCES inventory_item(id),
    allocated_quantity INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    allocated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fulfillment_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id),
    INDEX idx_status (status)
);

-- Create updated_at trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Add triggers for updated_at
CREATE TRIGGER update_warehouse_location_updated_at BEFORE UPDATE ON warehouse_location FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_inventory_item_updated_at BEFORE UPDATE ON inventory_item FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_inventory_reservation_updated_at BEFORE UPDATE ON inventory_reservation FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_inventory_movement_updated_at BEFORE UPDATE ON inventory_movement FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_inventory_allocation_updated_at BEFORE UPDATE ON inventory_allocation FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
