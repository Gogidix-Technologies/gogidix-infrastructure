package com.exalt.warehousing.analytics.model;

/**
 * Access Level Enumeration
 * 
 * Represents different access levels for analytics reports and data
 */
public enum AccessLevel {
    PUBLIC("Public access - available to all users"),
    INTERNAL("Internal access - available to organization members"),
    RESTRICTED("Restricted access - requires specific permissions"),
    CONFIDENTIAL("Confidential access - limited to authorized personnel"),
    CLASSIFIED("Classified access - highest security level");

    private final String description;

    AccessLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get security level (1-5, where 5 is highest)
     */
    public int getSecurityLevel() {
        return switch (this) {
            case PUBLIC -> 1;
            case INTERNAL -> 2;
            case RESTRICTED -> 3;
            case CONFIDENTIAL -> 4;
            case CLASSIFIED -> 5;
        };
    }

    /**
     * Check if access level allows sharing
     */
    public boolean allowsSharing() {
        return this == PUBLIC || this == INTERNAL;
    }

    /**
     * Check if access level requires audit logging
     */
    public boolean requiresAuditLogging() {
        return this == RESTRICTED || this == CONFIDENTIAL || this == CLASSIFIED;
    }

    /**
     * Check if access level requires encryption
     */
    public boolean requiresEncryption() {
        return this == CONFIDENTIAL || this == CLASSIFIED;
    }

    /**
     * Get retention policy in days
     */
    public int getRetentionDays() {
        return switch (this) {
            case PUBLIC -> 90;
            case INTERNAL -> 365;
            case RESTRICTED -> 730; // 2 years
            case CONFIDENTIAL -> 2555; // 7 years
            case CLASSIFIED -> 3650; // 10 years
        };
    }

    /**
     * Get required roles for access
     */
    public String[] getRequiredRoles() {
        return switch (this) {
            case PUBLIC -> new String[]{"USER"};
            case INTERNAL -> new String[]{"EMPLOYEE"};
            case RESTRICTED -> new String[]{"ANALYST", "MANAGER"};
            case CONFIDENTIAL -> new String[]{"SENIOR_MANAGER", "DIRECTOR"};
            case CLASSIFIED -> new String[]{"EXECUTIVE", "ADMIN"};
        };
    }
}