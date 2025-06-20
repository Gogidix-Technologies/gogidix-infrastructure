package com.exalt.warehousing.analytics.repository.elasticsearch;

import com.exalt.warehousing.analytics.model.MetricCategory;
import com.exalt.warehousing.analytics.model.MetricType;
import com.exalt.warehousing.analytics.model.PerformanceMetric;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Elasticsearch Repository for Performance Metrics Search and Analytics
 */
@Repository
public interface PerformanceMetricSearchRepository extends ElasticsearchRepository<PerformanceMetric, UUID> {

    /**
     * Find metrics by warehouse ID
     */
    List<PerformanceMetric> findByWarehouseId(UUID warehouseId);

    /**
     * Find metrics by metric type
     */
    List<PerformanceMetric> findByMetricType(MetricType metricType);

    /**
     * Find metrics by category
     */
    List<PerformanceMetric> findByMetricCategory(MetricCategory metricCategory);

    /**
     * Search metrics by metric name
     */
    @Query("{\"match\": {\"metricName\": \"?0\"}}")
    List<PerformanceMetric> findByMetricNameContaining(String metricName);

    /**
     * Search metrics with fuzzy matching on metric name
     */
    @Query("{\"fuzzy\": {\"metricName\": {\"value\": \"?0\", \"fuzziness\": \"AUTO\"}}}")
    List<PerformanceMetric> findByMetricNameFuzzy(String metricName);

    /**
     * Find metrics within date range
     */
    @Query("{\"range\": {\"periodStart\": {\"gte\": \"?0\", \"lte\": \"?1\"}}}")
    List<PerformanceMetric> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find metrics with alerts
     */
    @Query("{\"term\": {\"isAlertTriggered\": true}}")
    List<PerformanceMetric> findAlertsTriggered();

    /**
     * Find metrics by performance score range
     */
    @Query("{\"range\": {\"performanceScore\": {\"gte\": ?0, \"lte\": ?1}}}")
    List<PerformanceMetric> findByPerformanceScoreRange(Double minScore, Double maxScore);

    /**
     * Advanced search with multiple criteria
     */
    @Query("{\n" +
           "  \"bool\": {\n" +
           "    \"must\": [\n" +
           "      {\"term\": {\"warehouseId\": \"?0\"}},\n" +
           "      {\"term\": {\"metricCategory\": \"?1\"}},\n" +
           "      {\"range\": {\"periodStart\": {\"gte\": \"?2\"}}}\n" +
           "    ]\n" +
           "  }\n" +
           "}")
    List<PerformanceMetric> findByWarehouseAndCategoryAndDateAfter(UUID warehouseId, 
                                                                  MetricCategory category, 
                                                                  LocalDateTime dateAfter);

    /**
     * Search metrics with high variance
     */
    @Query("{\"range\": {\"variancePercentage\": {\"gte\": ?0}}}")
    List<PerformanceMetric> findHighVarianceMetrics(Double minVariance);

    /**
     * Search declining metrics
     */
    @Query("{\"term\": {\"trendDirection\": \"DECLINING\"}}")
    List<PerformanceMetric> findDecliningMetrics();

    /**
     * Search improving metrics
     */
    @Query("{\"term\": {\"trendDirection\": \"IMPROVING\"}}")
    List<PerformanceMetric> findImprovingMetrics();

    /**
     * Full-text search across metric name and notes
     */
    @Query("{\n" +
           "  \"multi_match\": {\n" +
           "    \"query\": \"?0\",\n" +
           "    \"fields\": [\"metricName^2\", \"notes\"]\n" +
           "  }\n" +
           "}")
    List<PerformanceMetric> fullTextSearch(String searchText);

    /**
     * Find metrics by data source
     */
    @Query("{\"term\": {\"dataSource\": \"?0\"}}")
    List<PerformanceMetric> findByDataSource(String dataSource);

    /**
     * Aggregation query for average performance by category
     */
    @Query("{\n" +
           "  \"aggs\": {\n" +
           "    \"categories\": {\n" +
           "      \"terms\": {\"field\": \"metricCategory\"},\n" +
           "      \"aggs\": {\n" +
           "        \"avg_performance\": {\"avg\": {\"field\": \"performanceScore\"}}\n" +
           "      }\n" +
           "    }\n" +
           "  },\n" +
           "  \"size\": 0\n" +
           "}")
    Page<PerformanceMetric> getPerformanceByCategory(Pageable pageable);

    /**
     * Find critical alerts in time range
     */
    @Query("{\n" +
           "  \"bool\": {\n" +
           "    \"must\": [\n" +
           "      {\"term\": {\"alertLevel\": \"CRITICAL\"}},\n" +
           "      {\"term\": {\"isAlertTriggered\": true}},\n" +
           "      {\"range\": {\"createdAt\": {\"gte\": \"?0\"}}}\n" +
           "    ]\n" +
           "  }\n" +
           "}")
    List<PerformanceMetric> findRecentCriticalAlerts(LocalDateTime since);

    /**
     * Search metrics by tags
     */
    @Query("{\n" +
           "  \"nested\": {\n" +
           "    \"path\": \"tags\",\n" +
           "    \"query\": {\n" +
           "      \"bool\": {\n" +
           "        \"must\": [\n" +
           "          {\"match\": {\"tags.key\": \"?0\"}},\n" +
           "          {\"match\": {\"tags.value\": \"?1\"}}\n" +
           "        ]\n" +
           "      }\n" +
           "    }\n" +
           "  }\n" +
           "}")
    List<PerformanceMetric> findByTag(String tagKey, String tagValue);

    /**
     * Find outliers using statistical analysis
     */
    @Query("{\n" +
           "  \"bool\": {\n" +
           "    \"should\": [\n" +
           "      {\"range\": {\"performanceScore\": {\"lt\": ?0}}},\n" +
           "      {\"range\": {\"performanceScore\": {\"gt\": ?1}}},\n" +
           "      {\"range\": {\"variancePercentage\": {\"gte\": ?2}}}\n" +
           "    ],\n" +
           "    \"minimum_should_match\": 1\n" +
           "  }\n" +
           "}")
    List<PerformanceMetric> findOutliers(Double lowPerformanceThreshold, 
                                       Double highPerformanceThreshold, 
                                       Double highVarianceThreshold);

    /**
     * Time-based aggregation for trending
     */
    @Query("{\n" +
           "  \"aggs\": {\n" +
           "    \"metrics_over_time\": {\n" +
           "      \"date_histogram\": {\n" +
           "        \"field\": \"periodStart\",\n" +
           "        \"calendar_interval\": \"?1\"\n" +
           "      },\n" +
           "      \"aggs\": {\n" +
           "        \"avg_value\": {\"avg\": {\"field\": \"metricValue\"}},\n" +
           "        \"avg_performance\": {\"avg\": {\"field\": \"performanceScore\"}}\n" +
           "      }\n" +
           "    }\n" +
           "  },\n" +
           "  \"query\": {\n" +
           "    \"bool\": {\n" +
           "      \"must\": [\n" +
           "        {\"term\": {\"warehouseId\": \"?0\"}},\n" +
           "        {\"range\": {\"periodStart\": {\"gte\": \"?2\"}}}\n" +
           "      ]\n" +
           "    }\n" +
           "  },\n" +
           "  \"size\": 0\n" +
           "}")
    Page<PerformanceMetric> getMetricsTrend(UUID warehouseId, String interval, LocalDateTime fromDate, Pageable pageable);

    /**
     * Find similar metrics based on metric characteristics
     */
    @Query("{\n" +
           "  \"more_like_this\": {\n" +
           "    \"fields\": [\"metricName\", \"metricType\", \"metricCategory\"],\n" +
           "    \"like\": [\n" +
           "      {\n" +
           "        \"_index\": \"warehouse_performance_metrics\",\n" +
           "        \"_id\": \"?0\"\n" +
           "      }\n" +
           "    ],\n" +
           "    \"min_term_freq\": 1,\n" +
           "    \"max_query_terms\": 10\n" +
           "  }\n" +
           "}")
    List<PerformanceMetric> findSimilarMetrics(UUID metricId);

    /**
     * Geospatial search (if warehouse locations are indexed)
     */
    @Query("{\n" +
           "  \"geo_distance\": {\n" +
           "    \"distance\": \"?2\",\n" +
           "    \"location\": {\n" +
           "      \"lat\": ?0,\n" +
           "      \"lon\": ?1\n" +
           "    }\n" +
           "  }\n" +
           "}")
    List<PerformanceMetric> findMetricsNearLocation(Double latitude, Double longitude, String distance);

    /**
     * Find metrics by description containing text
     */
    List<PerformanceMetric> findByDescriptionContaining(String searchText);

    /**
     * Complex search with multiple filters
     */
    @Query("{\n" +
           "  \"bool\": {\n" +
           "    \"must\": [\n" +
           "      {\"bool\": {\"should\": [" +
           "        {\"match\": {\"description\": \"?0\"}}," +
           "        {\"match\": {\"metricName\": \"?0\"}}" +
           "      ]}}," +
           "      {\"term\": {\"warehouseId\": \"?1\"}}," +
           "      {\"term\": {\"metricType\": \"?2\"}}," +
           "      {\"term\": {\"metricCategory\": \"?3\"}}," +
           "      {\"range\": {\"recordedAt\": {\"gte\": \"?4\", \"lte\": \"?5\"}}}" +
           "    ]\n" +
           "  }\n" +
           "}")
    Page<PerformanceMetric> findWithFilters(String searchText,
                                          UUID warehouseId,
                                          MetricType metricType,
                                          MetricCategory category,
                                          LocalDateTime startDate,
                                          LocalDateTime endDate,
                                          Pageable pageable);
}