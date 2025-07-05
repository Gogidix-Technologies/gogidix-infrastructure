package com.exalt.warehousing.management.service.impl;

import com.exalt.warehousing.management.dto.LocationDTO;
import com.exalt.warehousing.management.dto.PickingPathDTO;
import com.exalt.warehousing.management.dto.PickingPathDTO.PickItemDTO;
import com.exalt.warehousing.management.exception.ResourceNotFoundException;
import com.exalt.warehousing.management.mapper.LocationMapper;
import com.exalt.warehousing.management.model.Location;
import com.exalt.warehousing.management.model.Zone;
import com.exalt.warehousing.management.repository.InventoryItemRepository;
import com.exalt.warehousing.management.repository.LocationRepository;
import com.exalt.warehousing.management.repository.OrderItemRepository;
import com.exalt.warehousing.management.repository.ZoneRepository;
import com.exalt.warehousing.management.service.OptimizedPickingPathService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the OptimizedPickingPathService interface
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OptimizedPickingPathServiceImpl implements OptimizedPickingPathService {

    private final LocationRepository locationRepository;
    private final ZoneRepository zoneRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final LocationMapper locationMapper;
    
    // Average walking speed in meters per minute
    private static final double AVERAGE_WALKING_SPEED = 50.0;
    
    // Average pick time per item in minutes
    private static final double AVERAGE_PICK_TIME_PER_ITEM = 0.5;

    @Override
    public PickingPathDTO generateOptimizedPickingPath(UUID warehouseId, List<UUID> pickItems) {
        log.debug("Generating optimized picking path for warehouse {} with {} items", warehouseId, pickItems.size());
        
        // Get the default starting location (e.g., packing station)
        Location startLocation = locationRepository.findDefaultStartLocationByWarehouseId(warehouseId.toString())
                .orElseThrow(() -> new ResourceNotFoundException("Default start location not found for warehouse: " + warehouseId));
        
        // Get locations for all the items
        Map<UUID, Location> itemLocationMap = getItemLocations(pickItems);
        List<String> locationIds = itemLocationMap.values().stream()
                .map(Location::getId)
                .distinct()
                .collect(Collectors.toList());
        
        // Calculate the shortest path between locations
        List<LocationDTO> shortestPath = calculateShortestPath((UUID.fromString(startLocation.getId())), locationIds.stream().map(UUID::fromString).collect(Collectors.toList()));
        
        // Create pick items
        List<PickItemDTO> pickItemDTOs = createPickItems(pickItems, itemLocationMap, shortestPath);
        
        // Calculate metrics
        double totalDistance = calculateTotalDistance(shortestPath);
        double estimatedTime = calculateEstimatedTime(totalDistance, pickItemDTOs.size());
        
        // Build and return the result
        return PickingPathDTO.builder()
                .id(UUID.randomUUID())
                .warehouseId(warehouseId)
                .startLocation(locationMapper.toDTO(startLocation))
                .endLocation(locationMapper.toDTO(startLocation)) // End at the same location
                .locations(shortestPath)
                .pickItems(pickItemDTOs)
                .totalDistance(totalDistance)
                .estimatedTimeMinutes(estimatedTime)
                .algorithm("nearest-neighbor")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    public PickingPathDTO generateZoneOptimizedPickingPath(UUID zoneId, List<UUID> pickItems) {
        log.debug("Generating zone optimized picking path for zone {} with {} items", zoneId, pickItems.size());
        
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + zoneId));
        
        // Get the default starting location for the zone
        Location startLocation = locationRepository.findDefaultStartLocationByZoneId(zoneId.toString())
                .orElseThrow(() -> new ResourceNotFoundException("Default start location not found for zone: " + zoneId));
        
        // Get locations for all the items
        Map<UUID, Location> itemLocationMap = getItemLocations(pickItems);
        
        // Filter items that are in the requested zone
        List<UUID> zoneItems = itemLocationMap.entrySet().stream()
                .filter(entry -> zoneId.equals(entry.getValue().getZoneId()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        if (zoneItems.isEmpty()) {
            throw new IllegalArgumentException("No items found in the specified zone");
        }
        
        Map<UUID, Location> zoneItemLocationMap = getItemLocations(zoneItems);
        List<String> locationIds = zoneItemLocationMap.values().stream()
                .map(Location::getId)
                .distinct()
                .collect(Collectors.toList());
        
        // Calculate the shortest path within the zone
        List<LocationDTO> shortestPath = calculateZoneShortestPath(zone, (UUID.fromString(startLocation.getId())), locationIds.stream().map(UUID::fromString).collect(Collectors.toList()), "nearest-neighbor");
        
        // Create pick items
        List<PickItemDTO> pickItemDTOs = createPickItems(zoneItems, zoneItemLocationMap, shortestPath);
        
        // Calculate metrics
        double totalDistance = calculateTotalDistance(shortestPath);
        double estimatedTime = calculateEstimatedTime(totalDistance, pickItemDTOs.size());
        
        // Build and return the result
        return PickingPathDTO.builder()
                .id(UUID.randomUUID())
                .warehouseId((UUID.fromString(String.valueOf(zone.getWarehouseId()))))
                .zoneId(zoneId)
                .startLocation(locationMapper.toDTO(startLocation))
                .endLocation(locationMapper.toDTO(startLocation)) // End at the same location
                .locations(shortestPath)
                .pickItems(pickItemDTOs)
                .totalDistance(totalDistance)
                .estimatedTimeMinutes(estimatedTime)
                .algorithm("nearest-neighbor")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    public List<LocationDTO> calculateShortestPath(UUID startLocationId, List<UUID> destinationLocationIds) {
        log.debug("Calculating shortest path from location {} to {} destinations", 
                startLocationId, destinationLocationIds.size());
        
        if (destinationLocationIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Get all locations
        List<Location> allLocations = new ArrayList<>();
        Location startLocation = locationRepository.findById(startLocationId.toString())
                .orElseThrow(() -> new ResourceNotFoundException("Start location not found with id: " + startLocationId));
        allLocations.add(startLocation);
        
        List<Location> destinationLocations = locationRepository.findAllById(destinationLocationIds.stream().map(UUID::toString).collect(Collectors.toList()));
        if (destinationLocations.size() != destinationLocationIds.size()) {
            log.warn("Not all destination locations were found. Requested: {}, Found: {}", 
                    destinationLocationIds.size(), destinationLocations.size());
        }
        allLocations.addAll(destinationLocations);
        
        // Use nearest neighbor algorithm to find the shortest path
        return nearestNeighborAlgorithm(locationMapper.toDTO(startLocation), 
                destinationLocations.stream().map(locationMapper::toDTO).collect(Collectors.toList()));
    }

    @Override
    public List<LocationDTO> calculateZoneShortestPath(Zone zone, UUID startLocationId, 
            List<UUID> destinationLocationIds, String algorithm) {
        log.debug("Calculating shortest path in zone {} using algorithm {}", zone.getId(), algorithm);
        
        if (destinationLocationIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Get all locations
        List<Location> allLocations = new ArrayList<>();
        Location startLocation = locationRepository.findById(startLocationId.toString())
                .orElseThrow(() -> new ResourceNotFoundException("Start location not found with id: " + startLocationId));
        allLocations.add(startLocation);
        
        List<Location> destinationLocations = locationRepository.findAllById(destinationLocationIds.stream().map(UUID::toString).collect(Collectors.toList()));
        if (destinationLocations.size() != destinationLocationIds.size()) {
            log.warn("Not all destination locations were found. Requested: {}, Found: {}", 
                    destinationLocationIds.size(), destinationLocations.size());
        }
        
        // Filter locations to only include those in the specified zone
        destinationLocations = destinationLocations.stream()
                .filter(location -> zone.getId().equals(location.getZoneId()))
                .collect(Collectors.toList());
        
        if (destinationLocations.isEmpty()) {
            throw new IllegalArgumentException("No destination locations found in the specified zone");
        }
        
        // Apply the appropriate algorithm
        switch (algorithm.toLowerCase()) {
            case "nearest-neighbor":
            default:
                return nearestNeighborAlgorithm(locationMapper.toDTO(startLocation), 
                        destinationLocations.stream().map(locationMapper::toDTO).collect(Collectors.toList()));
        }
    }

    @Override
    public List<PickingPathDTO> optimizeBatchPicking(UUID warehouseId, List<UUID> orderIds) {
        log.debug("Optimizing batch picking for warehouse {} with {} orders", warehouseId, orderIds.size());
        
        // This is a simplified implementation that creates a separate picking path for each order
        // A more advanced implementation would group orders by zone and optimize across orders
        
        List<PickingPathDTO> pickingPaths = new ArrayList<>();
        
        for (UUID orderId : orderIds) {
            // Get items for this order
            List<UUID> orderItems = orderItemRepository.findItemIdsByOrderId(orderId);
            
            if (!orderItems.isEmpty()) {
                PickingPathDTO path = generateOptimizedPickingPath(warehouseId, orderItems);
                pickingPaths.add(path);
            }
        }
        
        return pickingPaths;
    }

    @Override
    public double getPickingTimeEstimate(UUID pickingPathId) {
        // In a real implementation, this would likely retrieve a saved picking path from the database
        // For now, we'll return a placeholder value
        log.debug("Getting picking time estimate for picking path {}", pickingPathId);
        return 10.0; // 10 minutes placeholder
    }
    
    /**
     * Get locations for the specified items
     *
     * @param itemIds the item IDs
     * @return a map of item ID to location
     */
    private Map<UUID, Location> getItemLocations(List<UUID> itemIds) {
        Map<UUID, Location> itemLocationMap = new HashMap<>();
        
        for (UUID itemId : itemIds) {
            // In a real implementation, this would query the inventory repository to get the location
            // For this simplified version, we'll assume a direct mapping is available
            UUID locationId = inventoryItemRepository.findLocationIdByItemId(itemId);
            if (locationId != null) {
                locationRepository.findById(locationId.toString()).ifPresent(location -> {
                    itemLocationMap.put(itemId, location);
                });
            }
        }
        
        return itemLocationMap;
    }
    
    /**
     * Create pick items for the specified items
     *
     * @param itemIds the item IDs
     * @param itemLocationMap the map of item ID to location
     * @param shortestPath the shortest path
     * @return list of pick items
     */
    private List<PickItemDTO> createPickItems(List<UUID> itemIds, Map<UUID, Location> itemLocationMap, 
            List<LocationDTO> shortestPath) {
        // Create a map of location ID to sequence in the shortest path
        Map<UUID, Integer> locationSequenceMap = new HashMap<>();
        for (int i = 0; i < shortestPath.size(); i++) {
            locationSequenceMap.put(shortestPath.get(i).getId(), i);
        }
        
        // Create pick items with sequence
        List<PickItemDTO> pickItems = new ArrayList<>();
        for (UUID itemId : itemIds) {
            Location location = itemLocationMap.get(itemId);
            if (location != null) {
                // Get item details from inventory
                UUID productId = inventoryItemRepository.findProductIdByItemId(itemId);
                String sku = inventoryItemRepository.findSkuByItemId(itemId);
                String productName = inventoryItemRepository.findProductNameByItemId(itemId);
                Integer quantity = inventoryItemRepository.findQuantityByItemId(itemId);
                UUID orderId = orderItemRepository.findOrderIdByItemId(itemId);
                
                // Create pick item
                PickItemDTO pickItem = PickItemDTO.builder()
                        .itemId(itemId)
                        .locationId((UUID.fromString(location.getId())))
                        .productId(productId)
                        .sku(sku)
                        .productName(productName)
                        .quantity(quantity != null ? quantity : 1)
                        .orderId(orderId)
                        .pickSequence(locationSequenceMap.getOrDefault(location.getId(), 0))
                        .build();
                
                pickItems.add(pickItem);
            }
        }
        
        // Sort pick items by pick sequence
        pickItems.sort(Comparator.comparingInt(PickItemDTO::getPickSequence));
        
        return pickItems;
    }
    
    @Override
    public PickingPathDTO findPickingPathById(UUID pickingPathId) {
        log.debug("Finding picking path with id: {}", pickingPathId);
        
        // This is a placeholder implementation since we don't have a PickingPath entity
        // In a real implementation, you would have a PickingPathRepository
        throw new UnsupportedOperationException("PickingPath persistence not implemented yet. Use generate methods instead.");
    }
    
    /**
     * Calculate the total distance for a path
     *
     * @param path the path
     * @return the total distance in meters
     */
    private double calculateTotalDistance(List<LocationDTO> path) {
        double totalDistance = 0.0;
        
        for (int i = 0; i < path.size() - 1; i++) {
            LocationDTO current = path.get(i);
            LocationDTO next = path.get(i + 1);
            totalDistance += calculateDistance(current, next);
        }
        
        return totalDistance;
    }
    
    /**
     * Calculate the distance between two locations
     *
     * @param location1 the first location
     * @param location2 the second location
     * @return the distance in meters
     */
    private double calculateDistance(LocationDTO location1, LocationDTO location2) {
        // In a real implementation, this would use the warehouse layout to calculate actual distances
        // For this simplified version, we'll use a Euclidean distance based on aisle/rack/level/position
        
        // Parse location coordinates
        int aisle1 = parseNumeric(location1.getAisle());
        int rack1 = parseNumeric(location1.getRack());
        int level1 = parseNumeric(location1.getLevel());
        int position1 = parseNumeric(location1.getPosition());
        
        int aisle2 = parseNumeric(location2.getAisle());
        int rack2 = parseNumeric(location2.getRack());
        int level2 = parseNumeric(location2.getLevel());
        int position2 = parseNumeric(location2.getPosition());
        
        // Calculate distance using Euclidean distance formula
        double aisleDistance = Math.abs(aisle2 - aisle1) * 3.0; // Assume 3 meters between aisles
        double rackDistance = Math.abs(rack2 - rack1) * 1.5;    // Assume 1.5 meters between racks
        double levelDistance = Math.abs(level2 - level1) * 2.0; // Assume 2 meters between levels
        double positionDistance = Math.abs(position2 - position1) * 0.5; // Assume 0.5 meters between positions
        
        return Math.sqrt(Math.pow(aisleDistance, 2) + Math.pow(rackDistance, 2) + 
                Math.pow(levelDistance, 2) + Math.pow(positionDistance, 2));
    }
    
    /**
     * Parse a numeric value from a string
     *
     * @param value the string value
     * @return the numeric value
     */
    private int parseNumeric(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        
        try {
            // Extract numeric part from the string (e.g., "A12" -> 12)
            StringBuilder sb = new StringBuilder();
            for (char c : value.toCharArray()) {
                if (Character.isDigit(c)) {
                    sb.append(c);
                }
            }
            
            if (sb.length() > 0) {
                return Integer.parseInt(sb.toString());
            }
            
            // If no numeric part, use the first character as an ordinal value
            return Character.getNumericValue(value.charAt(0));
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Calculate the estimated time to complete a picking path
     *
     * @param distance the total distance in meters
     * @param numberOfItems the number of items to pick
     * @return the estimated time in minutes
     */
    private double calculateEstimatedTime(double distance, int numberOfItems) {
        // Time = walking time + picking time
        double walkingTime = distance / AVERAGE_WALKING_SPEED;
        double pickingTime = numberOfItems * AVERAGE_PICK_TIME_PER_ITEM;
        
        return walkingTime + pickingTime;
    }
    
    /**
     * Implement the nearest neighbor algorithm for path finding
     *
     * @param start the starting location
     * @param destinations the destination locations
     * @return the shortest path
     */
    private List<LocationDTO> nearestNeighborAlgorithm(LocationDTO start, List<LocationDTO> destinations) {
        List<LocationDTO> path = new ArrayList<>();
        Set<UUID> visited = new HashSet<>();
        
        // Add the start location to the path
        path.add(start);
        visited.add(start.getId());
        
        LocationDTO current = start;
        
        // While there are unvisited destinations
        while (visited.size() <= destinations.size()) {
            LocationDTO nearest = null;
            double nearestDistance = Double.MAX_VALUE;
            
            // Find the nearest unvisited location
            for (LocationDTO location : destinations) {
                if (!visited.contains(location.getId())) {
                    double distance = calculateDistance(current, location);
                    if (distance < nearestDistance) {
                        nearest = location;
                        nearestDistance = distance;
                    }
                }
            }
            
            // If all destinations have been visited, break
            if (nearest == null) {
                break;
            }
            
            // Add the nearest location to the path
            path.add(nearest);
            visited.add(nearest.getId());
            current = nearest;
        }
        
        return path;
    }
} 
