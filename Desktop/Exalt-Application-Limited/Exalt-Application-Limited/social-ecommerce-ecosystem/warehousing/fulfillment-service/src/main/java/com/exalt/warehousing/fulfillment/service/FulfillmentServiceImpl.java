package com.exalt.warehousing.fulfillment.service;

import com.exalt.warehousing.fulfillment.dto.FulfillmentRequest;
import com.exalt.warehousing.fulfillment.dto.FulfillmentResult;
import org.springframework.stereotype.Service;

@Service
public class FulfillmentServiceImpl implements FulfillmentService {
    
    @Override
    public FulfillmentResult processFulfillment(FulfillmentRequest request) {
        return new FulfillmentResult(true, "Processed successfully");
    }
    
    @Override
    public void updateFulfillmentStatus(String fulfillmentId, String status) {
        // Implementation will be added in next iteration
    }
}
