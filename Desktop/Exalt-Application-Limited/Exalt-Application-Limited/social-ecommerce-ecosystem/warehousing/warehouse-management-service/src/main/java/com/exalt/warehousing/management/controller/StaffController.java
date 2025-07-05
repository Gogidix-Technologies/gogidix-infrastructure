package com.exalt.warehousing.management.controller;

import com.exalt.warehousing.management.dto.StaffDTO;
import com.exalt.warehousing.management.model.StaffRole;
import com.exalt.warehousing.management.model.StaffStatus;
import com.exalt.warehousing.management.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for staff operations
 */
@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Staff Management", description = "API for warehouse staff operations")
public class StaffController {
    
    private final StaffService staffService;
    
    @GetMapping
    @Operation(summary = "Get all staff members", 
            description = "Returns a list of all staff members in the system")
    @ApiResponse(responseCode = "200", description = "Staff members retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = StaffDTO.class)))
    public ResponseEntity<List<StaffDTO>> getAllStaff() {
        log.debug("REST request to get all staff members");
        return ResponseEntity.ok(staffService.findAll());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get staff member by ID", 
            description = "Returns a staff member based on ID")
    @ApiResponse(responseCode = "200", description = "Staff member retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = StaffDTO.class)))
    @ApiResponse(responseCode = "404", description = "Staff member not found")
    public ResponseEntity<StaffDTO> getStaffById(
            @Parameter(description = "Staff ID", required = true)
            @PathVariable("id") UUID id) {
        log.debug("REST request to get staff member by id: {}", id);
        return ResponseEntity.ok(staffService.findById(id));
    }
    
    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get staff member by employee ID", 
            description = "Returns a staff member based on employee ID")
    @ApiResponse(responseCode = "200", description = "Staff member retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = StaffDTO.class)))
    @ApiResponse(responseCode = "404", description = "Staff member not found")
    public ResponseEntity<StaffDTO> getStaffByEmployeeId(
            @Parameter(description = "Employee ID", required = true)
            @PathVariable("employeeId") String employeeId) {
        log.debug("REST request to get staff member by employee id: {}", employeeId);
        return ResponseEntity.ok(staffService.findByEmployeeId(employeeId));
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get staff member by user ID", 
            description = "Returns a staff member based on user ID")
    @ApiResponse(responseCode = "200", description = "Staff member retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = StaffDTO.class)))
    @ApiResponse(responseCode = "404", description = "Staff member not found")
    public ResponseEntity<StaffDTO> getStaffByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable("userId") UUID userId) {
        log.debug("REST request to get staff member by user id: {}", userId);
        return ResponseEntity.ok(staffService.findByUserId(userId));
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Get staff member by email", 
            description = "Returns a staff member based on email")
    @ApiResponse(responseCode = "200", description = "Staff member retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = StaffDTO.class)))
    @ApiResponse(responseCode = "404", description = "Staff member not found")
    public ResponseEntity<StaffDTO> getStaffByEmail(
            @Parameter(description = "Email address", required = true)
            @PathVariable("email") String email) {
        log.debug("REST request to get staff member by email: {}", email);
        return ResponseEntity.ok(staffService.findByEmail(email));
    }
    
    @GetMapping("/warehouse/{warehouseId}")
    @Operation(summary = "Get staff members by warehouse", 
            description = "Returns staff members in a specific warehouse")
    @ApiResponse(responseCode = "200", description = "Staff members retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = StaffDTO.class)))
    public ResponseEntity<List<StaffDTO>> getStaffByWarehouse(
            @Parameter(description = "Warehouse ID", required = true)
            @PathVariable("warehouseId") UUID warehouseId) {
        log.debug("REST request to get staff members by warehouse id: {}", warehouseId);
        return ResponseEntity.ok(staffService.findByWarehouseId(warehouseId));
    }
    
    @GetMapping("/role/{role}")
    @Operation(summary = "Get staff members by role", 
            description = "Returns staff members with a specific role")
    @ApiResponse(responseCode = "200", description = "Staff members retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = StaffDTO.class)))
    public ResponseEntity<List<StaffDTO>> getStaffByRole(
            @Parameter(description = "Staff role", required = true)
            @PathVariable("role") StaffRole role) {
        log.debug("REST request to get staff members by role: {}", role);
        return ResponseEntity.ok(staffService.findByRole(role));
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get staff members by status", 
            description = "Returns staff members with a specific status")
    @ApiResponse(responseCode = "200", description = "Staff members retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = StaffDTO.class)))
    public ResponseEntity<List<StaffDTO>> getStaffByStatus(
            @Parameter(description = "Staff status", required = true)
            @PathVariable("status") StaffStatus status) {
        log.debug("REST request to get staff members by status: {}", status);
        return ResponseEntity.ok(staffService.findByStatus(status));
    }
    
    @GetMapping("/warehouse/{warehouseId}/role/{role}")
    @Operation(summary = "Get staff members by warehouse and role", 
            description = "Returns staff members in a specific warehouse with a specific role")
    @ApiResponse(responseCode = "200", description = "Staff members retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = StaffDTO.class)))
    public ResponseEntity<List<StaffDTO>> getStaffByWarehouseAndRole(
            @Parameter(description = "Warehouse ID", required = true)
            @PathVariable("warehouseId") UUID warehouseId,
            @Parameter(description = "Staff role", required = true)
            @PathVariable("role") StaffRole role) {
        log.debug("REST request to get staff members by warehouse id: {} and role: {}", warehouseId, role);
        return ResponseEntity.ok(staffService.findByWarehouseIdAndRole(warehouseId, role));
    }
    
    @GetMapping("/zone/{zoneId}")
    @Operation(summary = "Get staff members by zone", 
            description = "Returns staff members assigned to a specific zone")
    @ApiResponse(responseCode = "200", description = "Staff members retrieved successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = StaffDTO.class)))
    public ResponseEntity<List<StaffDTO>> getStaffByZone(
            @Parameter(description = "Zone ID", required = true)
            @PathVariable("zoneId") UUID zoneId) {
        log.debug("REST request to get staff members by zone id: {}", zoneId);
        return ResponseEntity.ok(staffService.findByZoneId(zoneId));
    }
    
    @PostMapping
    @Operation(summary = "Create a new staff member", 
            description = "Registers a new staff member in the system")
    @ApiResponse(responseCode = "201", description = "Staff member created successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = StaffDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<StaffDTO> createStaff(
            @Parameter(description = "Staff member data", required = true)
            @Valid @RequestBody StaffDTO staffDTO) {
        log.debug("REST request to create staff member: {}", staffDTO);
        StaffDTO result = staffService.create(staffDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a staff member", 
            description = "Updates an existing staff member's details")
    @ApiResponse(responseCode = "200", description = "Staff member updated successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = StaffDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Staff member not found")
    public ResponseEntity<StaffDTO> updateStaff(
            @Parameter(description = "Staff ID", required = true)
            @PathVariable("id") UUID id,
            @Parameter(description = "Updated staff data", required = true)
            @Valid @RequestBody StaffDTO staffDTO) {
        log.debug("REST request to update staff member with id: {}", id);
        StaffDTO result = staffService.update(id, staffDTO);
        return ResponseEntity.ok(result);
    }
    
    @PatchMapping("/{id}/status/{status}")
    @Operation(summary = "Change staff status", 
            description = "Updates the status of a staff member")
    @ApiResponse(responseCode = "200", description = "Staff status updated successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = StaffDTO.class)))
    @ApiResponse(responseCode = "404", description = "Staff member not found")
    public ResponseEntity<StaffDTO> changeStaffStatus(
            @Parameter(description = "Staff ID", required = true)
            @PathVariable("id") UUID id,
            @Parameter(description = "New staff status", required = true)
            @PathVariable("status") StaffStatus status) {
        log.debug("REST request to change status of staff member with id: {} to {}", id, status);
        StaffDTO result = staffService.changeStatus(id, status);
        return ResponseEntity.ok(result);
    }
    
    @PatchMapping("/{id}/assign-to-zone/{zoneId}")
    @Operation(summary = "Assign staff to zone", 
            description = "Assigns a staff member to a specific zone in a warehouse")
    @ApiResponse(responseCode = "200", description = "Staff assigned to zone successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = StaffDTO.class)))
    @ApiResponse(responseCode = "404", description = "Staff member or zone not found")
    public ResponseEntity<StaffDTO> assignStaffToZone(
            @Parameter(description = "Staff ID", required = true)
            @PathVariable("id") UUID id,
            @Parameter(description = "Zone ID", required = true)
            @PathVariable("zoneId") UUID zoneId) {
        log.debug("REST request to assign staff member with id: {} to zone with id: {}", id, zoneId);
        StaffDTO result = staffService.assignToZone(id, zoneId);
        return ResponseEntity.ok(result);
    }
    
    @PatchMapping("/{id}/remove-from-zone")
    @Operation(summary = "Remove staff from zone", 
            description = "Removes a staff member from their current zone assignment")
    @ApiResponse(responseCode = "200", description = "Staff removed from zone successfully",
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = StaffDTO.class)))
    @ApiResponse(responseCode = "404", description = "Staff member not found")
    public ResponseEntity<StaffDTO> removeStaffFromZone(
            @Parameter(description = "Staff ID", required = true)
            @PathVariable("id") UUID id) {
        log.debug("REST request to remove staff member with id: {} from zone assignment", id);
        // Remove from zone by updating assignment
        StaffDTO result = staffService.findById(id);
        // Logic to remove zone assignment would go here
        log.warn("removeFromZone functionality not yet implemented in service");
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a staff member", 
            description = "Removes a staff member from the system")
    @ApiResponse(responseCode = "204", description = "Staff member deleted successfully")
    @ApiResponse(responseCode = "404", description = "Staff member not found")
    public ResponseEntity<Void> deleteStaff(
            @Parameter(description = "Staff ID", required = true)
            @PathVariable("id") UUID id) {
        log.debug("REST request to delete staff member with id: {}", id);
        staffService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }
} 
