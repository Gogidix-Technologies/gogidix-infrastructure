package com.exalt.warehousing.management.service;

import com.exalt.warehousing.management.config.ShippingCarrierProperties;
import com.exalt.warehousing.management.dto.ShippingLabelDTO;
import com.exalt.warehousing.management.dto.ShippingRateDTO;
import com.exalt.warehousing.shared.dto.TrackingInfoDTO;
import com.exalt.warehousing.management.exception.ShippingIntegrationException;
import com.exalt.warehousing.management.model.ShippingCarrier;
import com.exalt.warehousing.management.model.ShippingMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for integrating with shipping carriers
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingCarrierIntegrationService {

    private final RestTemplate restTemplate;
    private final ShippingCarrierProperties carrierProperties;
    
    /**
     * Get shipping rates from multiple carriers
     *
     * @param originZipCode origin zip code
     * @param destinationZipCode destination zip code
     * @param weightLbs package weight in pounds
     * @param dimensionsInches package dimensions in inches (length, width, height)
     * @return list of shipping rates from different carriers
     */
    public List<ShippingRateDTO> getShippingRates(
            String originZipCode, 
            String destinationZipCode, 
            double weightLbs, 
            double[] dimensionsInches) {
        log.info("Getting shipping rates from {} to {}, weight: {} lbs, dimensions: {}x{}x{} inches",
                originZipCode, destinationZipCode, weightLbs, 
                dimensionsInches[0], dimensionsInches[1], dimensionsInches[2]);
        
        List<ShippingRateDTO> rates = new ArrayList<>();
        
        // Get rates from each configured carrier
        for (ShippingCarrier carrier : ShippingCarrier.values()) {
            try {
                List<ShippingRateDTO> carrierRates = getCarrierRates(
                        carrier, originZipCode, destinationZipCode, weightLbs, dimensionsInches);
                rates.addAll(carrierRates);
            } catch (Exception e) {
                log.error("Error getting rates from carrier {}: {}", carrier, e.getMessage());
            }
        }
        
        // Sort rates by price
        rates.sort(Comparator.comparing(ShippingRateDTO::getRate));
        
        return rates;
    }
    
    /**
     * Generate a shipping label
     *
     * @param carrier the shipping carrier
     * @param method the shipping method
     * @param originAddress origin address
     * @param destinationAddress destination address
     * @param weightLbs package weight in pounds
     * @param dimensionsInches package dimensions in inches (length, width, height)
     * @param packageContents description of package contents
     * @return the generated shipping label
     */
    public ShippingLabelDTO generateShippingLabel(
            ShippingCarrier carrier,
            ShippingMethod method,
            Map<String, String> originAddress,
            Map<String, String> destinationAddress,
            double weightLbs,
            double[] dimensionsInches,
            String packageContents) {
        log.info("Generating shipping label for carrier: {}, method: {}", carrier, method);
        
        // In a real implementation, this would call the carrier's API
        // For now, we'll create a placeholder shipping label
        
        // Generate tracking number
        String trackingNumber = generateTrackingNumber(carrier);
        
        // Calculate estimated delivery date
        LocalDateTime estimatedDelivery = calculateEstimatedDelivery(carrier, method, 
                originAddress.get("zipCode"), destinationAddress.get("zipCode"));
        
        // Build and return the shipping label DTO
        return ShippingLabelDTO.builder()
                .id(UUID.randomUUID())
                .carrier(carrier)
                .method(method)
                .trackingNumber(trackingNumber)
                .originAddress(originAddress)
                .destinationAddress(destinationAddress)
                .weightLbs(weightLbs)
                .dimensionsInches(dimensionsInches)
                .packageContents(packageContents)
                .labelUrl("https://shipping-api.example.com/labels/" + trackingNumber + ".pdf")
                .labelData("Base64EncodedLabelDataWouldBeHere".getBytes())
                .createdAt(LocalDateTime.now())
                .estimatedDelivery(estimatedDelivery)
                .build();
    }
    
    /**
     * Get tracking information for a shipment
     *
     * @param carrier the shipping carrier
     * @param trackingNumber the tracking number
     * @return the tracking information
     */
    public TrackingInfoDTO getTrackingInfo(ShippingCarrier carrier, String trackingNumber) {
        log.info("Getting tracking info for carrier: {}, tracking number: {}", carrier, trackingNumber);
        
        // In a real implementation, this would call the carrier's API
        // For now, we'll create placeholder tracking info
        
        // Generate random tracking events
        List<Map<String, Object>> events = generateTrackingEvents(trackingNumber);
        
        // Determine current status based on events
        String status = determineTrackingStatus(events);
        
        // Build and return the tracking info DTO – using logistics DTO structure
        return TrackingInfoDTO.builder()
                .carrier(carrier.name())
                .trackingNumber(trackingNumber)
                .status(status)
                .estimatedDelivery(LocalDateTime.now().plusDays(3))
                .build();
    }
    
    /**
     * Get shipping rates from a specific carrier
     *
     * @param carrier the shipping carrier
     * @param originZipCode origin zip code
     * @param destinationZipCode destination zip code
     * @param weightLbs package weight in pounds
     * @param dimensionsInches package dimensions in inches (length, width, height)
     * @return list of shipping rates from the carrier
     */
    private List<ShippingRateDTO> getCarrierRates(
            ShippingCarrier carrier,
            String originZipCode,
            String destinationZipCode,
            double weightLbs,
            double[] dimensionsInches) {
        // In a real implementation, this would call the carrier's API
        // For now, we'll create placeholder rates
        
        List<ShippingRateDTO> rates = new ArrayList<>();
        Random random = new Random();
        
        // Get carrier configuration
        String apiUrl = getCarrierApiUrl(carrier);
        String apiKey = getCarrierApiKey(carrier);
        
        // Log API request (would be actual API call in real implementation)
        log.info("Calling {} API for rates: {} -> {}, {} lbs", 
                carrier, originZipCode, destinationZipCode, weightLbs);
        
        // Generate rates for different shipping methods
        for (ShippingMethod method : getCarrierMethods(carrier)) {
            // Calculate base rate based on weight and distance
            double baseRate = calculateBaseRate(carrier, method, weightLbs);
            
            // Add distance factor (simplified calculation)
            int zipDiff = Math.abs(Integer.parseInt(originZipCode) - Integer.parseInt(destinationZipCode));
            double distanceFactor = zipDiff / 10000.0;
            
            // Calculate final rate with some randomization
            double rate = baseRate * (1 + distanceFactor) * (0.9 + random.nextDouble() * 0.2);
            
            // Calculate transit days
            int transitDays = calculateTransitDays(carrier, method, distanceFactor);
            
            // Build rate DTO
            ShippingRateDTO rateDto = ShippingRateDTO.builder()
                    .carrier(carrier)
                    .method(method)
                    .rate(Math.round(rate * 100) / 100.0) // Round to 2 decimal places
                    .currency("USD")
                    .transitDays(transitDays)
                    .guaranteedDelivery(method.isGuaranteed())
                    .build();
            
            rates.add(rateDto);
        }
        
        return rates;
    }
    
    /**
     * Get carrier API URL
     *
     * @param carrier the shipping carrier
     * @return the API URL
     */
    private String getCarrierApiUrl(ShippingCarrier carrier) {
        return switch (carrier) {
            case USPS -> carrierProperties.getUsps().getApiUrl();
            case UPS -> carrierProperties.getUps().getApiUrl();
            case FEDEX -> carrierProperties.getFedex().getApiUrl();
            case DHL -> carrierProperties.getDhl().getApiUrl();
        };
    }
    
    /**
     * Get carrier API key
     *
     * @param carrier the shipping carrier
     * @return the API key
     */
    private String getCarrierApiKey(ShippingCarrier carrier) {
        return switch (carrier) {
            case USPS -> carrierProperties.getUsps().getApiKey();
            case UPS -> carrierProperties.getUps().getApiKey();
            case FEDEX -> carrierProperties.getFedex().getApiKey();
            case DHL -> carrierProperties.getDhl().getApiKey();
        };
    }
    
    /**
     * Get available shipping methods for a carrier
     *
     * @param carrier the shipping carrier
     * @return list of available shipping methods
     */
    private List<ShippingMethod> getCarrierMethods(ShippingCarrier carrier) {
        return switch (carrier) {
            case USPS -> List.of(
                    ShippingMethod.USPS_FIRST_CLASS,
                    ShippingMethod.USPS_PRIORITY,
                    ShippingMethod.USPS_EXPRESS
            );
            case UPS -> List.of(
                    ShippingMethod.UPS_GROUND,
                    ShippingMethod.UPS_3_DAY_SELECT,
                    ShippingMethod.UPS_2ND_DAY_AIR,
                    ShippingMethod.UPS_NEXT_DAY_AIR
            );
            case FEDEX -> List.of(
                    ShippingMethod.FEDEX_GROUND,
                    ShippingMethod.FEDEX_2_DAY,
                    ShippingMethod.FEDEX_OVERNIGHT
            );
            case DHL -> List.of(
                    ShippingMethod.DHL_EXPRESS,
                    ShippingMethod.DHL_INTERNATIONAL
            );
        };
    }
    
    /**
     * Calculate base shipping rate
     *
     * @param carrier the shipping carrier
     * @param method the shipping method
     * @param weightLbs package weight in pounds
     * @return the base shipping rate
     */
    private double calculateBaseRate(ShippingCarrier carrier, ShippingMethod method, double weightLbs) {
        // Base rate depends on carrier and method
        double baseRate = switch (carrier) {
            case USPS -> switch (method) {
                case USPS_FIRST_CLASS -> 5.50;
                case USPS_PRIORITY -> 8.50;
                case USPS_EXPRESS -> 26.35;
                default -> 7.00;
            };
            case UPS -> switch (method) {
                case UPS_GROUND -> 9.50;
                case UPS_3_DAY_SELECT -> 15.25;
                case UPS_2ND_DAY_AIR -> 21.75;
                case UPS_NEXT_DAY_AIR -> 34.95;
                default -> 10.00;
            };
            case FEDEX -> switch (method) {
                case FEDEX_GROUND -> 9.75;
                case FEDEX_2_DAY -> 22.50;
                case FEDEX_OVERNIGHT -> 35.50;
                default -> 10.00;
            };
            case DHL -> switch (method) {
                case DHL_EXPRESS -> 28.50;
                case DHL_INTERNATIONAL -> 42.75;
                default -> 30.00;
            };
        };
        
        // Add weight factor
        double weightFactor = Math.max(1.0, weightLbs / 2.0);
        
        return baseRate * weightFactor;
    }
    
    /**
     * Calculate transit days
     *
     * @param carrier the shipping carrier
     * @param method the shipping method
     * @param distanceFactor distance factor
     * @return number of transit days
     */
    private int calculateTransitDays(ShippingCarrier carrier, ShippingMethod method, double distanceFactor) {
        // Base transit days depends on method
        int baseDays = switch (method) {
            case USPS_FIRST_CLASS -> 3;
            case USPS_PRIORITY -> 2;
            case USPS_EXPRESS -> 1;
            case UPS_GROUND -> 5;
            case UPS_3_DAY_SELECT -> 3;
            case UPS_2ND_DAY_AIR -> 2;
            case UPS_NEXT_DAY_AIR -> 1;
            case FEDEX_GROUND -> 5;
            case FEDEX_2_DAY -> 2;
            case FEDEX_OVERNIGHT -> 1;
            case DHL_EXPRESS -> 2;
            case DHL_INTERNATIONAL -> 5;
        };
        
        // Add distance factor
        int additionalDays = (int) Math.round(distanceFactor * 10);
        
        // Cap additional days based on method
        int maxAdditionalDays = switch (method) {
            case USPS_EXPRESS, UPS_NEXT_DAY_AIR, FEDEX_OVERNIGHT -> 0;
            case USPS_PRIORITY, UPS_2ND_DAY_AIR, FEDEX_2_DAY -> 1;
            case UPS_3_DAY_SELECT -> 2;
            default -> 3;
        };
        
        return baseDays + Math.min(additionalDays, maxAdditionalDays);
    }
    
    /**
     * Generate a tracking number for a carrier
     *
     * @param carrier the shipping carrier
     * @return the generated tracking number
     */
    private String generateTrackingNumber(ShippingCarrier carrier) {
        Random random = new Random();
        
        return switch (carrier) {
            case USPS -> "9400" + String.format("%012d", random.nextInt(999999999) + 1);
            case UPS -> "1Z" + String.format("%016d", random.nextInt(9999999) + 1);
            case FEDEX -> String.format("%012d", random.nextInt(999999999) + 1);
            case DHL -> String.format("%010d", random.nextInt(9999999) + 1);
        };
    }
    
    /**
     * Calculate estimated delivery date
     *
     * @param carrier the shipping carrier
     * @param method the shipping method
     * @param originZipCode origin zip code
     * @param destinationZipCode destination zip code
     * @return the estimated delivery date
     */
    private LocalDateTime calculateEstimatedDelivery(
            ShippingCarrier carrier, 
            ShippingMethod method,
            String originZipCode,
            String destinationZipCode) {
        // Calculate distance factor
        int zipDiff = Math.abs(Integer.parseInt(originZipCode) - Integer.parseInt(destinationZipCode));
        double distanceFactor = zipDiff / 10000.0;
        
        // Calculate transit days
        int transitDays = calculateTransitDays(carrier, method, distanceFactor);
        
        // Add processing time (1 day)
        return LocalDateTime.now().plusDays(transitDays + 1);
    }
    
    /**
     * Generate tracking events for a shipment
     *
     * @param trackingNumber the tracking number
     * @return list of tracking events
     */
    private List<Map<String, Object>> generateTrackingEvents(String trackingNumber) {
        List<Map<String, Object>> events = new ArrayList<>();
        
        // Create shipment created event
        LocalDateTime createdTime = LocalDateTime.now().minusDays(2);
        events.add(Map.of(
                "timestamp", createdTime,
                "status", "SHIPMENT_CREATED",
                "description", "Shipment information received",
                "location", "Shipper Location"
        ));
        
        // Create pickup event
        LocalDateTime pickupTime = createdTime.plusHours(3);
        events.add(Map.of(
                "timestamp", pickupTime,
                "status", "PICKUP_COMPLETE",
                "description", "Shipment picked up",
                "location", "Shipper Location"
        ));
        
        // Create in transit event
        LocalDateTime transitTime = pickupTime.plusHours(6);
        events.add(Map.of(
                "timestamp", transitTime,
                "status", "IN_TRANSIT",
                "description", "Shipment in transit",
                "location", "Origin Facility"
        ));
        
        // Create out for delivery event (if enough time has passed)
        if (transitTime.isBefore(LocalDateTime.now().minusHours(12))) {
            LocalDateTime outForDeliveryTime = LocalDateTime.now().minusHours(4);
            events.add(Map.of(
                    "timestamp", outForDeliveryTime,
                    "status", "OUT_FOR_DELIVERY",
                    "description", "Out for delivery",
                    "location", "Destination Facility"
            ));
            
            // Create delivered event (if enough time has passed)
            if (outForDeliveryTime.isBefore(LocalDateTime.now().minusHours(2))) {
                events.add(Map.of(
                        "timestamp", LocalDateTime.now().minusHours(1),
                        "status", "DELIVERED",
                        "description", "Delivered",
                        "location", "Destination Address"
                ));
            }
        }
        
        return events;
    }
    
    /**
     * Determine tracking status from events
     *
     * @param events list of tracking events
     * @return the current tracking status
     */
    private String determineTrackingStatus(List<Map<String, Object>> events) {
        // Get the most recent event
        Map<String, Object> latestEvent = events.get(events.size() - 1);
        
        // Return the status of the latest event
        return (String) latestEvent.get("status");
    }
} 
