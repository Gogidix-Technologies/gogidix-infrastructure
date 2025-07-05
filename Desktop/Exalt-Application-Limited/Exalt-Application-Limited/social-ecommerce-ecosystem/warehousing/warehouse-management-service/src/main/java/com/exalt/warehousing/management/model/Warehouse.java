package com.exalt.warehousing.management.model;

import com.exalt.warehousing.shared.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "warehouses")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Warehouse extends BaseEntity {
    
    @Column(nullable = false)
    private String code;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String address;
    
    @Column(name = "address_line_1")
    private String addressLine1;
    
    @Column(name = "address_line_2")
    private String addressLine2;
    
    private String city;
    private String state;
    private String country;
    private String zipCode;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "square_footage")
    private Double squareFootage;
    
    @Column(nullable = false)
    private Double totalCapacity;
    
    @Column(nullable = false)
    private Double availableCapacity;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WarehouseStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WarehouseType type;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Location> locations = new HashSet<>();
    
    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Staff> staff = new HashSet<>();
}
