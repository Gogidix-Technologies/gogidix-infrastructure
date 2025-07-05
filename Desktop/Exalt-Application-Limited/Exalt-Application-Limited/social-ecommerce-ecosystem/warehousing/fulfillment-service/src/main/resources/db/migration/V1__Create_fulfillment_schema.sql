-- V1__Create_fulfillment_schema.sql

-- Fulfillment Order Table
CREATE TABLE IF NOT EXISTS fulfillment_order (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id VARCHAR(255) NOT NULL UNIQUE,
    warehouse_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL DEFAULT 'NORMAL',
    source_system VARCHAR(100),
    customer_id VARCHAR(255),
    customer_name VARCHAR(255),
    shipping_address TEXT NOT NULL,
    shipping_method VARCHAR(50),
    special_instructions TEXT,
    total_items INTEGER NOT NULL DEFAULT 0,
    picked_items INTEGER NOT NULL DEFAULT 0,
    packed_items INTEGER NOT NULL DEFAULT 0,
    assigned_to UUID,
    picking_started_at TIMESTAMP,
    picking_completed_at TIMESTAMP,
    packing_started_at TIMESTAMP,
    packing_completed_at TIMESTAMP,
    shipped_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    cancellation_reason TEXT,
    notes TEXT,
    reassigned_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'ASSIGNED', 'PICKING', 'PICKED', 'PACKING', 'PACKED', 'READY_TO_SHIP', 'SHIPPED', 'CANCELLED')),
    CONSTRAINT chk_priority CHECK (priority IN ('LOW', 'NORMAL', 'HIGH', 'URGENT'))
);

-- Indexes for fulfillment_order
CREATE INDEX idx_fulfillment_order_status ON fulfillment_order(status);
CREATE INDEX idx_fulfillment_order_warehouse ON fulfillment_order(warehouse_id);
CREATE INDEX idx_fulfillment_order_assigned ON fulfillment_order(assigned_to);
CREATE INDEX idx_fulfillment_order_created ON fulfillment_order(created_at);

-- Fulfillment Order Item Table
CREATE TABLE IF NOT EXISTS fulfillment_order_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fulfillment_order_id UUID NOT NULL REFERENCES fulfillment_order(id) ON DELETE CASCADE,
    product_id VARCHAR(255) NOT NULL,
    sku VARCHAR(100) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    picked_quantity INTEGER NOT NULL DEFAULT 0,
    packed_quantity INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    location_code VARCHAR(50),
    bin_number VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_item_status CHECK (status IN ('PENDING', 'PICKING', 'PICKED', 'PACKING', 'PACKED', 'CANCELLED'))
);

-- Picking Task Table
CREATE TABLE IF NOT EXISTS picking_task (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fulfillment_order_id UUID NOT NULL REFERENCES fulfillment_order(id) ON DELETE CASCADE,
    task_number VARCHAR(100) NOT NULL UNIQUE,
    assigned_to UUID,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(50) NOT NULL DEFAULT 'NORMAL',
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    total_items INTEGER NOT NULL DEFAULT 0,
    picked_items INTEGER NOT NULL DEFAULT 0,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Packing Task Table
CREATE TABLE IF NOT EXISTS packing_task (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fulfillment_order_id UUID NOT NULL REFERENCES fulfillment_order(id) ON DELETE CASCADE,
    task_number VARCHAR(100) NOT NULL UNIQUE,
    assigned_to UUID,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(50) NOT NULL DEFAULT 'NORMAL',
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    total_items INTEGER NOT NULL DEFAULT 0,
    packed_items INTEGER NOT NULL DEFAULT 0,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Shipment Package Table
CREATE TABLE IF NOT EXISTS shipment_package (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fulfillment_order_id UUID NOT NULL REFERENCES fulfillment_order(id),
    package_number VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'CREATED',
    shipping_method VARCHAR(50) NOT NULL,
    carrier VARCHAR(100),
    tracking_number VARCHAR(255),
    weight_kg DECIMAL(10, 3),
    length_cm DECIMAL(10, 2),
    width_cm DECIMAL(10, 2),
    height_cm DECIMAL(10, 2),
    shipping_cost DECIMAL(19, 2),
    label_url TEXT,
    dispatched_at TIMESTAMP,
    delivered_at TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_shipment_status CHECK (status IN ('CREATED', 'LABELED', 'DISPATCHED', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED'))
);

-- Return Request Table
CREATE TABLE IF NOT EXISTS return_request (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    return_number VARCHAR(100) NOT NULL UNIQUE,
    order_id VARCHAR(255) NOT NULL,
    fulfillment_order_id UUID REFERENCES fulfillment_order(id),
    customer_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    reason VARCHAR(50) NOT NULL,
    customer_comments TEXT,
    return_label_url TEXT,
    received_at TIMESTAMP,
    processed_at TIMESTAMP,
    refund_amount DECIMAL(19, 2),
    restocking_fee DECIMAL(19, 2),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Return Item Table
CREATE TABLE IF NOT EXISTS return_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    return_request_id UUID NOT NULL REFERENCES return_request(id) ON DELETE CASCADE,
    product_id VARCHAR(255) NOT NULL,
    sku VARCHAR(100) NOT NULL,
    quantity INTEGER NOT NULL,
    condition VARCHAR(50),
    disposition VARCHAR(50),
    inspection_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
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
CREATE TRIGGER update_fulfillment_order_updated_at BEFORE UPDATE ON fulfillment_order FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_fulfillment_order_item_updated_at BEFORE UPDATE ON fulfillment_order_item FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_picking_task_updated_at BEFORE UPDATE ON picking_task FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_packing_task_updated_at BEFORE UPDATE ON packing_task FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_shipment_package_updated_at BEFORE UPDATE ON shipment_package FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_return_request_updated_at BEFORE UPDATE ON return_request FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
