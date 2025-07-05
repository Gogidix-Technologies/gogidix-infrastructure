package com.exalt.warehousing.management.dto;

import com.exalt.warehousing.management.model.StaffRole;
import com.exalt.warehousing.management.model.StaffStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Staff entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffDTO {
    
    private UUID id;
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;
    
    @NotBlank(message = "Employee ID is required")
    @Pattern(regexp = "^[A-Z0-9]{3,10}$", message = "Employee ID must be 3-10 uppercase alphanumeric characters")
    private String employeeId;
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @Pattern(regexp = "^\\+?[0-9\\s\\-()]{8,20}$", message = "Invalid phone number format")
    private String phoneNumber;
    
    @NotNull(message = "Role is required")
    private StaffRole role;
    
    @NotNull(message = "Status is required")
    private StaffStatus status;
    
    private UUID zoneId;
    
    private Integer maxTasksPerDay;
    
    private String notes;
    
    private LocalDateTime hireDate;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 
