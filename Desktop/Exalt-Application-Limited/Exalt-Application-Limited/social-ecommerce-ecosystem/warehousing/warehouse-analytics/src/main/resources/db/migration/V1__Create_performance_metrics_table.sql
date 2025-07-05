-- Create performance_metrics table
CREATE TABLE performance_metrics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    warehouse_id UUID NOT NULL,
    metric_type VARCHAR(100) NOT NULL,
    metric_category VARCHAR(50) NOT NULL,
    metric_name VARCHAR(255),
    description TEXT,
    value DECIMAL(19,4) NOT NULL,
    unit VARCHAR(50),
    performance_score DECIMAL(5,2),
    measurement_period VARCHAR(50),
    period_start TIMESTAMP,
    period_end TIMESTAMP,
    trend_direction VARCHAR(50),
    alert_level VARCHAR(20),
    is_alert BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    variance_percentage DECIMAL(5,2),
    data_source VARCHAR(100),
    recorded_by VARCHAR(255),
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by VARCHAR(255),
    last_updated_at TIMESTAMP,
    archive_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance
CREATE INDEX idx_performance_metrics_warehouse_id ON performance_metrics(warehouse_id);
CREATE INDEX idx_performance_metrics_type ON performance_metrics(metric_type);
CREATE INDEX idx_performance_metrics_category ON performance_metrics(metric_category);
CREATE INDEX idx_performance_metrics_recorded_at ON performance_metrics(recorded_at);
CREATE INDEX idx_performance_metrics_period_start ON performance_metrics(period_start);
CREATE INDEX idx_performance_metrics_alert ON performance_metrics(is_alert, alert_level);
CREATE INDEX idx_performance_metrics_active ON performance_metrics(is_active);
CREATE INDEX idx_performance_metrics_performance_score ON performance_metrics(performance_score);

-- Composite indexes for common queries
CREATE INDEX idx_performance_metrics_warehouse_type ON performance_metrics(warehouse_id, metric_type);
CREATE INDEX idx_performance_metrics_warehouse_category ON performance_metrics(warehouse_id, metric_category);
CREATE INDEX idx_performance_metrics_warehouse_recorded ON performance_metrics(warehouse_id, recorded_at);
CREATE INDEX idx_performance_metrics_type_recorded ON performance_metrics(metric_type, recorded_at);
CREATE INDEX idx_performance_metrics_warehouse_type_recorded ON performance_metrics(warehouse_id, metric_type, recorded_at);

-- Create trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_performance_metrics_updated_at 
    BEFORE UPDATE ON performance_metrics 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Add comments for documentation
COMMENT ON TABLE performance_metrics IS 'Table storing performance metrics for warehouse operations';
COMMENT ON COLUMN performance_metrics.warehouse_id IS 'UUID of the warehouse this metric belongs to';
COMMENT ON COLUMN performance_metrics.metric_type IS 'Type of metric (e.g., THROUGHPUT, ACCURACY, COST_PER_UNIT)';
COMMENT ON COLUMN performance_metrics.metric_category IS 'Category of metric (e.g., EFFICIENCY, QUALITY, COST)';
COMMENT ON COLUMN performance_metrics.value IS 'The actual metric value';
COMMENT ON COLUMN performance_metrics.performance_score IS 'Calculated performance score (0-100)';
COMMENT ON COLUMN performance_metrics.alert_level IS 'Alert severity level (INFO, LOW, MEDIUM, HIGH, CRITICAL)';
COMMENT ON COLUMN performance_metrics.is_alert IS 'Whether this metric triggered an alert';
COMMENT ON COLUMN performance_metrics.variance_percentage IS 'Percentage variance from expected value';
COMMENT ON COLUMN performance_metrics.trend_direction IS 'Direction of trend (IMPROVING, DECLINING, STABLE)';