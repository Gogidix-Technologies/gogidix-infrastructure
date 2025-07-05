-- Create analytics_reports table
CREATE TABLE analytics_reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    report_type VARCHAR(50) NOT NULL,
    report_format VARCHAR(20) NOT NULL,
    report_status VARCHAR(20) NOT NULL DEFAULT 'GENERATING',
    access_level VARCHAR(20) NOT NULL DEFAULT 'INTERNAL',
    warehouse_id UUID,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    generated_by VARCHAR(255) NOT NULL,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    content_data BYTEA,
    file_path VARCHAR(500),
    file_size BIGINT,
    download_count INTEGER DEFAULT 0,
    last_accessed_at TIMESTAMP,
    last_accessed_by VARCHAR(255),
    error_message TEXT,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for analytics_reports
CREATE INDEX idx_analytics_reports_type ON analytics_reports(report_type);
CREATE INDEX idx_analytics_reports_status ON analytics_reports(report_status);
CREATE INDEX idx_analytics_reports_warehouse ON analytics_reports(warehouse_id);
CREATE INDEX idx_analytics_reports_generated_by ON analytics_reports(generated_by);
CREATE INDEX idx_analytics_reports_generated_at ON analytics_reports(generated_at);
CREATE INDEX idx_analytics_reports_date_range ON analytics_reports(start_date, end_date);

-- Create trigger for updated_at
CREATE TRIGGER update_analytics_reports_updated_at 
    BEFORE UPDATE ON analytics_reports 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Create alert_configurations table
CREATE TABLE alert_configurations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    warehouse_id UUID NOT NULL,
    metric_type VARCHAR(100) NOT NULL,
    metric_category VARCHAR(50),
    min_threshold DECIMAL(19,4),
    max_threshold DECIMAL(19,4),
    warning_threshold DECIMAL(19,4),
    critical_threshold DECIMAL(19,4),
    is_enabled BOOLEAN DEFAULT TRUE,
    notification_emails TEXT[],
    notification_webhooks TEXT[],
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(warehouse_id, metric_type)
);

-- Create indexes for alert_configurations
CREATE INDEX idx_alert_configs_warehouse ON alert_configurations(warehouse_id);
CREATE INDEX idx_alert_configs_metric_type ON alert_configurations(metric_type);
CREATE INDEX idx_alert_configs_enabled ON alert_configurations(is_enabled);

-- Create trigger for alert_configurations updated_at
CREATE TRIGGER update_alert_configurations_updated_at 
    BEFORE UPDATE ON alert_configurations 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Create metric_benchmarks table for comparative analysis
CREATE TABLE metric_benchmarks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    metric_type VARCHAR(100) NOT NULL,
    metric_category VARCHAR(50) NOT NULL,
    industry_benchmark DECIMAL(19,4),
    company_benchmark DECIMAL(19,4),
    regional_benchmark DECIMAL(19,4),
    tier_benchmark DECIMAL(19,4),
    benchmark_period VARCHAR(50),
    effective_from TIMESTAMP NOT NULL,
    effective_to TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for metric_benchmarks
CREATE INDEX idx_metric_benchmarks_type ON metric_benchmarks(metric_type);
CREATE INDEX idx_metric_benchmarks_category ON metric_benchmarks(metric_category);
CREATE INDEX idx_metric_benchmarks_effective ON metric_benchmarks(effective_from, effective_to);

-- Create trigger for metric_benchmarks updated_at
CREATE TRIGGER update_metric_benchmarks_updated_at 
    BEFORE UPDATE ON metric_benchmarks 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Add comments
COMMENT ON TABLE analytics_reports IS 'Table storing analytics report metadata and content';
COMMENT ON TABLE alert_configurations IS 'Table storing alert threshold configurations for metrics';
COMMENT ON TABLE metric_benchmarks IS 'Table storing benchmark values for comparative analysis';

COMMENT ON COLUMN analytics_reports.report_type IS 'Type of report (PERFORMANCE, TREND, COMPARISON, EXECUTIVE)';
COMMENT ON COLUMN analytics_reports.report_format IS 'Output format (PDF, CSV, EXCEL, JSON)';
COMMENT ON COLUMN analytics_reports.report_status IS 'Status (GENERATING, COMPLETED, FAILED, SCHEDULED)';
COMMENT ON COLUMN analytics_reports.access_level IS 'Access level (PUBLIC, INTERNAL, RESTRICTED, CONFIDENTIAL)';

COMMENT ON COLUMN alert_configurations.min_threshold IS 'Minimum acceptable value';
COMMENT ON COLUMN alert_configurations.max_threshold IS 'Maximum acceptable value';
COMMENT ON COLUMN alert_configurations.warning_threshold IS 'Threshold for warning alerts';
COMMENT ON COLUMN alert_configurations.critical_threshold IS 'Threshold for critical alerts';