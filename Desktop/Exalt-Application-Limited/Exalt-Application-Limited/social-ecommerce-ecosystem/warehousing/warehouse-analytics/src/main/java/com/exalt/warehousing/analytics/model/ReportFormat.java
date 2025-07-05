package com.exalt.warehousing.analytics.model;

/**
 * Report Format Enumeration
 * 
 * Represents different output formats for analytics reports
 */
public enum ReportFormat {
    PDF("Portable Document Format"),
    EXCEL("Microsoft Excel Spreadsheet"),
    CSV("Comma Separated Values"),
    JSON("JavaScript Object Notation"),
    XML("Extensible Markup Language"),
    HTML("HyperText Markup Language"),
    POWERPOINT("Microsoft PowerPoint Presentation");

    private final String description;

    ReportFormat(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get MIME type for the format
     */
    public String getMimeType() {
        return switch (this) {
            case PDF -> "application/pdf";
            case EXCEL -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case CSV -> "text/csv";
            case JSON -> "application/json";
            case XML -> "application/xml";
            case HTML -> "text/html";
            case POWERPOINT -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        };
    }

    /**
     * Get file extension
     */
    public String getFileExtension() {
        return switch (this) {
            case PDF -> ".pdf";
            case EXCEL -> ".xlsx";
            case CSV -> ".csv";
            case JSON -> ".json";
            case XML -> ".xml";
            case HTML -> ".html";
            case POWERPOINT -> ".pptx";
        };
    }

    /**
     * Check if format supports charts and visualizations
     */
    public boolean supportsVisualizations() {
        return this == PDF || this == EXCEL || this == HTML || this == POWERPOINT;
    }

    /**
     * Check if format is suitable for data exchange
     */
    public boolean isSuitableForDataExchange() {
        return this == CSV || this == JSON || this == XML || this == EXCEL;
    }

    /**
     * Check if format is suitable for presentations
     */
    public boolean isSuitableForPresentation() {
        return this == PDF || this == POWERPOINT || this == HTML;
    }

    /**
     * Get typical file size multiplier compared to raw data
     */
    public double getFileSizeMultiplier() {
        return switch (this) {
            case CSV -> 1.2;
            case JSON -> 1.5;
            case XML -> 2.0;
            case EXCEL -> 1.8;
            case HTML -> 1.3;
            case PDF -> 1.1;
            case POWERPOINT -> 2.5;
        };
    }
}