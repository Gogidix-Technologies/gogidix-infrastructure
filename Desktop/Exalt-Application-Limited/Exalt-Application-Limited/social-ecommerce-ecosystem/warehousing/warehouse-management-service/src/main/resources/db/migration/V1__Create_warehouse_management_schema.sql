-- V1__Create_warehouse_management_schema.sql

-- Warehouse Table
CREATE TABLE IF NOT EXISTS warehouse (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    country VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    phone VARCHAR(50),
    email VARCHAR(255),
    manager_id UUID,
    total_area_sqm DECIMAL(10, 2),
    available_area_sqm DECIMAL(10, 2),
    operating_hours JSONB,
    capabilities TEXT[],
    max_staff_capacity INTEGER,
    current_staff_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_warehouse_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'MAINTENANCE', 'CLOSED'))
);

-- Zone Table
CREATE TABLE IF NOT EXISTS zone (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    warehouse_id UUID NOT NULL REFERENCES warehouse(id) ON DELETE CASCADE,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    floor_number INTEGER,
    area_sqm DECIMAL(10, 2),
    temperature_controlled BOOLEAN DEFAULT FALSE,
    temperature_range_min DECIMAL(5, 2),
    temperature_range_max DECIMAL(5, 2),
    humidity_controlled BOOLEAN DEFAULT FALSE,
    humidity_range_min DECIMAL(5, 2),
    humidity_range_max DECIMAL(5, 2),
    max_weight_capacity_kg DECIMAL(10, 2),
    current_occupancy_percent DECIMAL(5, 2) DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_warehouse_zone_code UNIQUE (warehouse_id, code)
);

-- Location Table
CREATE TABLE IF NOT EXISTS location (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    zone_id UUID NOT NULL REFERENCES zone(id) ON DELETE CASCADE,
    code VARCHAR(50) NOT NULL,
    aisle VARCHAR(20),
    rack VARCHAR(20),
    shelf VARCHAR(20),
    bin VARCHAR(20),
    location_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    max_weight_kg DECIMAL(10, 2),
    max_volume_m3 DECIMAL(10, 3),
    current_weight_kg DECIMAL(10, 2) DEFAULT 0,
    current_volume_m3 DECIMAL(10, 3) DEFAULT 0,
    barcode VARCHAR(100),
    qr_code VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_zone_location_code UNIQUE (zone_id, code)
);

-- Staff Table
CREATE TABLE IF NOT EXISTS staff (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    warehouse_id UUID NOT NULL REFERENCES warehouse(id),
    employee_id VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50),
    role VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    department VARCHAR(100),
    shift VARCHAR(50),
    hire_date DATE NOT NULL,
    skills TEXT[],
    certifications JSONB,
    performance_rating DECIMAL(3, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_staff_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'ON_LEAVE', 'TERMINATED'))
);

-- Warehouse Task Table
CREATE TABLE IF NOT EXISTS warehouse_task (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_number VARCHAR(100) NOT NULL UNIQUE,
    warehouse_id UUID NOT NULL REFERENCES warehouse(id),
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(50) NOT NULL DEFAULT 'NORMAL',
    title VARCHAR(255) NOT NULL,
    description TEXT,
    reference_type VARCHAR(50),
    reference_id UUID,
    assigned_to UUID REFERENCES staff(id),
    location_id UUID REFERENCES location(id),
    due_date TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    estimated_duration_minutes INTEGER,
    actual_duration_minutes INTEGER,
    notes TEXT,
    created_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_task_status CHECK (status IN ('PENDING', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT chk_task_priority CHECK (priority IN ('LOW', 'NORMAL', 'HIGH', 'URGENT'))
);

-- Return Request Table (for warehouse management)
CREATE TABLE IF NOT EXISTS warehouse_return_request (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    return_number VARCHAR(100) NOT NULL UNIQUE,
    order_id VARCHAR(255) NOT NULL,
    warehouse_id UUID NOT NULL REFERENCES warehouse(id),
    customer_id VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    reason VARCHAR(50) NOT NULL,
    customer_comments TEXT,
    received_at TIMESTAMP,
    inspection_completed_at TIMESTAMP,
    processing_completed_at TIMESTAMP,
    quality_check_status VARCHAR(50),
    quality_check_notes TEXT,
    restocking_location_id UUID REFERENCES location(id),
    disposal_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Return Item Table (for warehouse management)
CREATE TABLE IF NOT EXISTS warehouse_return_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    return_request_id UUID NOT NULL REFERENCES warehouse_return_request(id) ON DELETE CASCADE,
    product_id VARCHAR(255) NOT NULL,
    sku VARCHAR(100) NOT NULL,
    quantity INTEGER NOT NULL,
    received_quantity INTEGER,
    condition VARCHAR(50),
    disposition VARCHAR(50),
    inspection_notes TEXT,
    restocking_quantity INTEGER DEFAULT 0,
    disposal_quantity INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_warehouse_status ON warehouse(status);
CREATE INDEX idx_zone_warehouse ON zone(warehouse_id);
CREATE INDEX idx_location_zone ON location(zone_id);
CREATE INDEX idx_location_status ON location(status);
CREATE INDEX idx_staff_warehouse ON staff(warehouse_id);
CREATE INDEX idx_staff_status ON staff(status);
CREATE INDEX idx_task_warehouse ON warehouse_task(warehouse_id);
CREATE INDEX idx_task_assigned ON warehouse_task(assigned_to);
CREATE INDEX idx_task_status ON warehouse_task(status);
CREATE INDEX idx_return_warehouse ON warehouse_return_request(warehouse_id);

-- Create updated_at trigger function (if not exists)
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Add triggers for updated_at
CREATE TRIGGER update_warehouse_updated_at BEFORE UPDATE ON warehouse FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_zone_updated_at BEFORE UPDATE ON zone FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_location_updated_at BEFORE UPDATE ON location FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_staff_updated_at BEFORE UPDATE ON staff FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_warehouse_task_updated_at BEFORE UPDATE ON warehouse_task FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_warehouse_return_request_updated_at BEFORE UPDATE ON warehouse_return_request FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
