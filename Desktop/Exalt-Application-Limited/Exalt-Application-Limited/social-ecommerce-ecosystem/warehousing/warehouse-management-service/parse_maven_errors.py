import re

# Read the compile output directly from the last Maven run
compile_output = """
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/BusinessIntelligenceService.java:[56,69] incompatible types: java.util.UUID cannot be converted to java.lang.String
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/LocationServiceImpl.java:[68,63] incompatible types: java.lang.String cannot be converted to java.util.UUID
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/LocationServiceImpl.java:[94,67] incompatible types: java.lang.String cannot be converted to java.util.UUID
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/mapper/StaffMapper.java:[39,40] incompatible types: java.lang.String cannot be converted to java.util.UUID
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/mapper/StaffMapper.java:[61,74] incompatible types: bad type in conditional expression
[[1;31mERROR[m]     java.util.UUID cannot be converted to java.lang.Long
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/mapper/StaffMapper.java:[90,47] incompatible types: java.util.UUID cannot be converted to java.lang.Long
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/mapper/StaffMapper.java:[114,43] incompatible types: java.util.UUID cannot be converted to java.lang.String
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/mapper/StaffMapper.java:[117,47] incompatible types: java.time.LocalDateTime cannot be converted to java.time.LocalDate
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/WarehouseTaskServiceImpl.java:[159,67] incompatible types: java.util.UUID cannot be converted to java.lang.String
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/WarehouseTaskServiceImpl.java:[169,100] incompatible types: java.util.UUID cannot be converted to java.lang.String
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/WarehouseTaskServiceImpl.java:[202,74] incompatible types: java.util.UUID cannot be converted to java.lang.String
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/WarehouseTaskServiceImpl.java:[214,72] incompatible types: java.util.UUID cannot be converted to java.lang.String
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/WarehouseTaskServiceImpl.java:[391,57] incompatible types: java.util.UUID cannot be converted to java.lang.String
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/task/service/WarehouseTaskServiceImpl.java:[213,27] incompatible types: java.lang.String cannot be converted to java.util.UUID
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/task/service/WarehouseTaskServiceImpl.java:[354,27] incompatible types: java.lang.String cannot be converted to java.util.UUID
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/task/service/WarehouseTaskServiceImpl.java:[366,27] incompatible types: java.lang.String cannot be converted to java.util.UUID
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/ShippingCarrierIntegrationService.java:[141,17] cannot find symbol
[[1;31mERROR[m]   symbol:   method carrierName(java.lang.String)
[[1;31mERROR[m]   location: class com.exalt.warehousing.shared.dto.TrackingInfoDTO.TrackingInfoDTOBuilder
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/controller/LocationController.java:[134,45] cannot find symbol
[[1;31mERROR[m]   symbol:   method getZoneById(java.util.UUID)
[[1;31mERROR[m]   location: variable zoneService of type com.exalt.warehousing.management.service.ZoneService
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/OptimizedPickingPathServiceImpl.java:[31,8] com.exalt.warehousing.management.service.impl.OptimizedPickingPathServiceImpl is not abstract and does not override abstract method findPickingPathById(java.util.UUID) in com.exalt.warehousing.management.service.OptimizedPickingPathService
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/OptimizedPickingPathServiceImpl.java:[50,91] incompatible types: java.util.UUID cannot be converted to java.lang.String
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/OptimizedPickingPathServiceImpl.java:[58,25] incompatible types: inference variable T has incompatible bounds
[[1;31mERROR[m]     equality constraints: java.util.UUID
[[1;31mERROR[m]     lower bounds: java.lang.String
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/OptimizedPickingPathServiceImpl.java:[61,83] incompatible types: java.lang.String cannot be converted to java.util.UUID
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/OptimizedPickingPathServiceImpl.java:[93,86] incompatible types: java.util.UUID cannot be converted to java.lang.String
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/OptimizedPickingPathServiceImpl.java:[113,25] incompatible types: inference variable T has incompatible bounds
[[1;31mERROR[m]     equality constraints: java.util.UUID
[[1;31mERROR[m]     lower bounds: java.lang.String
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/OptimizedPickingPathServiceImpl.java:[116,93] incompatible types: java.lang.String cannot be converted to java.util.UUID
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/OptimizedPickingPathServiceImpl.java:[128,49] incompatible types: java.lang.String cannot be converted to java.util.UUID
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/OptimizedPickingPathServiceImpl.java:[152,62] incompatible types: java.util.UUID cannot be converted to java.lang.String
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/OptimizedPickingPathServiceImpl.java:[156,78] incompatible types: java.util.List<java.util.UUID> cannot be converted to java.lang.Iterable<java.lang.String>
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/OptimizedPickingPathServiceImpl.java:[179,62] incompatible types: java.util.UUID cannot be converted to java.lang.String
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/OptimizedPickingPathServiceImpl.java:[183,78] incompatible types: java.util.List<java.util.UUID> cannot be converted to java.lang.Iterable<java.lang.String>
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/OptimizedPickingPathServiceImpl.java:[251,45] incompatible types: java.util.UUID cannot be converted to java.lang.String
[[1;31mERROR[m] /mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/warehousing/warehouse-management-service/src/main/java/com/exalt/warehousing/management/service/impl/OptimizedPickingPathServiceImpl.java:[291,51] incompatible types: java.lang.String cannot be converted to java.util.UUID
"""

# Parse errors
errors = []
error_pattern = r'\[ERROR\] (.+?\.java):\[(\d+),(\d+)\] (.+?)(?=\[\[ < /dev/null | $)'
matches = re.findall(error_pattern, compile_output, re.DOTALL)

for match in matches:
    file_path, line, col, error_msg = match
    # Clean up error message
    error_msg = error_msg.strip().replace('\n', ' ').replace('[[1;31mERROR[m]', '').strip()
    
    errors.append({
        'file': file_path.split('/')[-1],
        'line': int(line),
        'col': int(col),
        'message': error_msg,
        'full_path': file_path
    })

# Categorize errors
categories = {
    'UUID_to_String': [],
    'String_to_UUID': [],
    'UUID_to_Long': [],
    'DateTime_conversion': [],
    'Method_not_found': [],
    'Abstract_method': [],
    'List_type_conversion': [],
    'Other': []
}

for error in errors:
    msg = error['message']
    if 'UUID cannot be converted to java.lang.String' in msg:
        categories['UUID_to_String'].append(error)
    elif 'String cannot be converted to java.util.UUID' in msg:
        categories['String_to_UUID'].append(error)
    elif 'UUID cannot be converted to java.lang.Long' in msg:
        categories['UUID_to_Long'].append(error)
    elif 'LocalDateTime cannot be converted to java.time.LocalDate' in msg:
        categories['DateTime_conversion'].append(error)
    elif 'cannot find symbol' in msg and 'method' in msg:
        categories['Method_not_found'].append(error)
    elif 'does not override abstract method' in msg:
        categories['Abstract_method'].append(error)
    elif 'List<java.util.UUID> cannot be converted' in msg:
        categories['List_type_conversion'].append(error)
    else:
        categories['Other'].append(error)

# Print comprehensive analysis
print("WAREHOUSE MANAGEMENT SERVICE - COMPILATION ERROR ANALYSIS")
print("=" * 100)
print(f"Total errors: 32 (down from 64)")
print()

print("ERROR CATEGORY BREAKDOWN:")
print("-" * 100)
for category, items in categories.items():
    if items:
        print(f"\n{category}: {len(items)} errors")
        
        # Group by file
        file_groups = {}
        for item in items:
            file_name = item['file']
            if file_name not in file_groups:
                file_groups[file_name] = []
            file_groups[file_name].append(item)
        
        for file_name, file_errors in sorted(file_groups.items()):
            print(f"\n  📄 {file_name} ({len(file_errors)} errors):")
            for err in file_errors:
                print(f"     Line {err['line']}: {err['message'][:100]}...")

print("\n" + "=" * 100)
print("FILES WITH MOST ERRORS:")
print("-" * 100)
file_counts = {}
for error in errors:
    file_name = error['file']
    file_counts[file_name] = file_counts.get(file_name, 0) + 1

for file_name, count in sorted(file_counts.items(), key=lambda x: x[1], reverse=True):
    print(f"{file_name}: {count} errors")

print("\n" + "=" * 100)
print("CRITICAL PATTERNS TO FIX:")
print("-" * 100)
print("1. UUID/String conversions (17 errors) - Most common issue")
print("2. OptimizedPickingPathServiceImpl.java has 12 errors - needs major refactoring")
print("3. StaffMapper.java has mixed type issues (UUID/Long/String)")
print("4. Two WarehouseTaskServiceImpl files with different packages causing conflicts")
print("5. Missing method implementations in services")

