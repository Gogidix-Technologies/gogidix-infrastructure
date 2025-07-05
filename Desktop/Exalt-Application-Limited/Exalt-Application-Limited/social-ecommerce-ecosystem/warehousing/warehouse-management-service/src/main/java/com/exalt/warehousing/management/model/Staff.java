package com.exalt.warehousing.management.model;

import com.exalt.warehousing.shared.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "warehouse")
public class Staff extends BaseEntity {
    
    @Column(nullable = false)
    private String employeeId;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String phone;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "warehouse_id")
    private String warehouseId;
    
    @Column(name = "zone_id")
    private String zoneId;
    
    @Column(name = "max_tasks_per_day")
    private Integer maxTasksPerDay;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "hire_date")
    private LocalDate hireDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StaffRole role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;
    
    @Column(nullable = false)
    private LocalDate joinDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StaffStatus status;
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
