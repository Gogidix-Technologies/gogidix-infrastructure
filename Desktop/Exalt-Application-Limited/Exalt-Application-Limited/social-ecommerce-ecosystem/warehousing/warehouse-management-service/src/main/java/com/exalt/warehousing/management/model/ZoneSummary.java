package com.exalt.warehousing.management.model;

import java.util.UUID;

/**
 * Interface for warehouse zone summary information
 * including metrics about locations within the zone
 */
public interface ZoneSummary {

    /**
     * Get the zone ID
     *
     * @return the zone ID
     */
    UUID getZoneId();

    /**
     * Get the zone name
     *
     * @return the zone name
     */
    String getName();

    /**
     * Get the zone type
     *
     * @return the zone type
     */
    ZoneType getType();

    /**
     * Get the zone code
     *
     * @return the zone code
     */
    String getCode();

    /**
     * Get the total number of locations in the zone
     *
     * @return the total location count
     */
    int getTotalLocations();

    /**
     * Get the number of available locations in the zone
     *
     * @return the available location count
     */
    int getAvailableLocations();

    /**
     * Get the number of occupied locations in the zone
     *
     * @return the occupied location count
     */
    int getOccupiedLocations();
} 
