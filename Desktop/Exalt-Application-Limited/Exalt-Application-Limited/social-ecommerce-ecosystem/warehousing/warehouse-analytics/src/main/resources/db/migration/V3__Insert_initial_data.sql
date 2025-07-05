-- Insert initial benchmark data for common metrics
INSERT INTO metric_benchmarks (metric_type, metric_category, industry_benchmark, company_benchmark, regional_benchmark, tier_benchmark, benchmark_period, effective_from, created_by) VALUES

-- Efficiency Metrics
('THROUGHPUT', 'EFFICIENCY', 85.0, 90.0, 82.0, 88.0, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),
('STORAGE_UTILIZATION', 'EFFICIENCY', 78.0, 82.0, 75.0, 80.0, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),
('STAFF_UTILIZATION', 'EFFICIENCY', 75.0, 80.0, 72.0, 78.0, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),
('EQUIPMENT_UTILIZATION', 'EFFICIENCY', 70.0, 75.0, 68.0, 73.0, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),

-- Accuracy Metrics
('ORDER_ACCURACY', 'ACCURACY', 99.5, 99.8, 99.2, 99.6, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),
('INVENTORY_ACCURACY', 'ACCURACY', 98.5, 99.0, 98.0, 98.7, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),
('PICKING_ACCURACY', 'ACCURACY', 99.0, 99.5, 98.5, 99.2, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),
('SHIPPING_ACCURACY', 'ACCURACY', 98.8, 99.3, 98.5, 99.0, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),

-- Speed Metrics
('ORDER_PROCESSING_TIME', 'SPEED', 24.0, 18.0, 28.0, 20.0, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),
('PICKING_TIME_PER_ITEM', 'SPEED', 120.0, 90.0, 140.0, 105.0, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),
('SHIPPING_TIME', 'SPEED', 48.0, 36.0, 52.0, 42.0, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),
('CYCLE_TIME', 'SPEED', 72.0, 60.0, 80.0, 66.0, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),

-- Cost Metrics
('COST_PER_ORDER', 'COST', 12.50, 10.00, 14.00, 11.25, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),
('LABOR_COST_PER_HOUR', 'COST', 18.00, 16.50, 19.50, 17.25, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),
('STORAGE_COST_PER_UNIT', 'COST', 2.25, 2.00, 2.50, 2.10, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),
('TRANSPORTATION_COST', 'COST', 8.75, 7.50, 9.25, 8.00, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),

-- Quality Metrics
('DAMAGE_RATE', 'QUALITY', 2.5, 1.5, 3.0, 2.0, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),
('RETURN_RATE', 'QUALITY', 5.0, 3.5, 6.0, 4.2, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),
('CUSTOMER_SATISFACTION', 'QUALITY', 85.0, 90.0, 82.0, 87.5, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),

-- Safety Metrics
('INCIDENT_RATE', 'SAFETY', 2.3, 1.2, 2.8, 1.8, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),
('SAFETY_SCORE', 'SAFETY', 92.0, 96.0, 90.0, 94.0, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),

-- Sustainability Metrics
('ENERGY_EFFICIENCY', 'SUSTAINABILITY', 75.0, 82.0, 72.0, 78.5, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),
('WASTE_REDUCTION', 'SUSTAINABILITY', 15.0, 25.0, 12.0, 20.0, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),

-- Technology Metrics
('SYSTEM_UPTIME', 'TECHNOLOGY', 99.0, 99.5, 98.5, 99.2, 'MONTHLY', CURRENT_TIMESTAMP, 'system'),
('DATA_ACCURACY', 'TECHNOLOGY', 97.0, 98.5, 96.0, 97.8, 'MONTHLY', CURRENT_TIMESTAMP, 'system');

-- Insert default alert configurations for critical metrics
INSERT INTO alert_configurations (warehouse_id, metric_type, metric_category, min_threshold, max_threshold, warning_threshold, critical_threshold, notification_emails, created_by) VALUES

-- These are global defaults (warehouse_id = NULL for system-wide defaults)
('00000000-0000-0000-0000-000000000000', 'ORDER_ACCURACY', 'ACCURACY', 95.0, 100.0, 98.0, 95.0, ARRAY['alerts@ecosystem.com'], 'system'),
('00000000-0000-0000-0000-000000000000', 'INVENTORY_ACCURACY', 'ACCURACY', 95.0, 100.0, 97.0, 95.0, ARRAY['alerts@ecosystem.com'], 'system'),
('00000000-0000-0000-0000-000000000000', 'THROUGHPUT', 'EFFICIENCY', 70.0, 100.0, 80.0, 70.0, ARRAY['alerts@ecosystem.com'], 'system'),
('00000000-0000-0000-0000-000000000000', 'STORAGE_UTILIZATION', 'EFFICIENCY', 60.0, 95.0, 70.0, 60.0, ARRAY['alerts@ecosystem.com'], 'system'),
('00000000-0000-0000-0000-000000000000', 'INCIDENT_RATE', 'SAFETY', 0.0, 5.0, 2.0, 3.5, ARRAY['safety@ecosystem.com'], 'system'),
('00000000-0000-0000-0000-000000000000', 'SYSTEM_UPTIME', 'TECHNOLOGY', 95.0, 100.0, 98.0, 95.0, ARRAY['tech@ecosystem.com'], 'system'),
('00000000-0000-0000-0000-000000000000', 'DAMAGE_RATE', 'QUALITY', 0.0, 10.0, 3.0, 5.0, ARRAY['quality@ecosystem.com'], 'system'),
('00000000-0000-0000-0000-000000000000', 'COST_PER_ORDER', 'COST', 5.0, 25.0, 15.0, 20.0, ARRAY['finance@ecosystem.com'], 'system');

-- Create a view for current active benchmarks
CREATE VIEW current_benchmarks AS
SELECT 
    metric_type,
    metric_category,
    industry_benchmark,
    company_benchmark,
    regional_benchmark,
    tier_benchmark,
    benchmark_period,
    effective_from
FROM metric_benchmarks 
WHERE (effective_to IS NULL OR effective_to > CURRENT_TIMESTAMP)
AND effective_from <= CURRENT_TIMESTAMP;

-- Create a view for metric performance vs benchmarks
CREATE VIEW metric_performance_analysis AS
SELECT 
    pm.warehouse_id,
    pm.metric_type,
    pm.metric_category,
    pm.value,
    pm.performance_score,
    pm.recorded_at,
    cb.industry_benchmark,
    cb.company_benchmark,
    cb.regional_benchmark,
    cb.tier_benchmark,
    CASE 
        WHEN pm.value >= cb.company_benchmark THEN 'ABOVE_COMPANY'
        WHEN pm.value >= cb.industry_benchmark THEN 'ABOVE_INDUSTRY'
        WHEN pm.value >= cb.regional_benchmark THEN 'ABOVE_REGIONAL'
        ELSE 'BELOW_BENCHMARKS'
    END as benchmark_performance,
    ROUND(((pm.value - cb.company_benchmark) / cb.company_benchmark * 100)::numeric, 2) as variance_from_company_benchmark
FROM performance_metrics pm
LEFT JOIN current_benchmarks cb ON pm.metric_type = cb.metric_type
WHERE pm.is_active = true;

-- Create function to calculate warehouse efficiency score
CREATE OR REPLACE FUNCTION calculate_warehouse_efficiency_score(
    p_warehouse_id UUID,
    p_start_date TIMESTAMP DEFAULT (CURRENT_TIMESTAMP - INTERVAL '30 days'),
    p_end_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) RETURNS DECIMAL(5,2) AS $$
DECLARE
    efficiency_score DECIMAL(5,2);
BEGIN
    SELECT 
        ROUND(
            AVG(
                CASE pm.metric_category
                    WHEN 'EFFICIENCY' THEN pm.performance_score * 0.30
                    WHEN 'ACCURACY' THEN pm.performance_score * 0.25
                    WHEN 'SPEED' THEN pm.performance_score * 0.25
                    WHEN 'COST' THEN pm.performance_score * 0.20
                    ELSE pm.performance_score * 0.10
                END
            )::numeric, 2
        )
    INTO efficiency_score
    FROM performance_metrics pm
    WHERE pm.warehouse_id = p_warehouse_id
    AND pm.recorded_at BETWEEN p_start_date AND p_end_date
    AND pm.is_active = true
    AND pm.performance_score IS NOT NULL;
    
    RETURN COALESCE(efficiency_score, 0.00);
END;
$$ LANGUAGE plpgsql;

-- Add comment for the function
COMMENT ON FUNCTION calculate_warehouse_efficiency_score IS 'Calculates weighted efficiency score for a warehouse over a specified period';

-- Create an index on the performance analysis view's commonly queried columns
CREATE INDEX idx_performance_metrics_analysis ON performance_metrics(warehouse_id, metric_type, is_active, recorded_at) 
WHERE is_active = true;