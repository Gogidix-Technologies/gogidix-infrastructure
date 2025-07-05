# Warehouse Management Service

## Overview
The Warehouse Management Service is the core service for managing warehouse operations, including warehouse locations, zones, staff, tasks, and integration with inventory and fulfillment services.

## Features
- Warehouse location and zone management
- Staff management and assignment
- Task creation and tracking
- Return processing and quality assessment
- Shipping carrier integration
- QR code generation for locations and tasks
- Real-time analytics and dashboards
- Mobile API support for staff apps

## Technology Stack
- Java 17
- Spring Boot 3.1.5
- PostgreSQL
- Redis (for caching)
- Apache Kafka (for event streaming)
- Spring Cloud (for microservices integration)

## Getting Started

### Prerequisites
- Java 17
- Maven 3.6+
- Docker & Docker Compose
- PostgreSQL 15+
- Redis 7+

### Running Locally

1. **Clone the repository**
   ```bash
   cd warehousing/warehouse-management-service
   ```

2. **Start dependencies using Docker Compose**
   ```bash
   docker-compose up -d warehouse-db warehouse-redis
   ```

3. **Run the service**
   ```bash
   mvn spring-boot:run
   ```

The service will start on port 8086 by default.

### API Documentation
Once the service is running, access the API documentation at:
- Swagger UI: http://localhost:8086/swagger-ui.html
- OpenAPI Spec: http://localhost:8086/v3/api-docs

## API Endpoints

### Warehouse Management
- `GET /api/v1/warehouses` - List warehouses
- `POST /api/v1/warehouses` - Create warehouse
- `GET /api/v1/warehouses/{id}` - Get warehouse details
- `PUT /api/v1/warehouses/{id}` - Update warehouse
- `DELETE /api/v1/warehouses/{id}` - Delete warehouse

### Zone Management
- `GET /api/v1/warehouses/{warehouseId}/zones` - List zones
- `POST /api/v1/warehouses/{warehouseId}/zones` - Create zone
- `GET /api/v1/zones/{id}` - Get zone details
- `PUT /api/v1/zones/{id}` - Update zone

### Staff Management
- `GET /api/v1/staff` - List staff
- `POST /api/v1/staff` - Create staff
- `GET /api/v1/staff/{id}` - Get staff details
- `PUT /api/v1/staff/{id}` - Update staff
- `POST /api/v1/staff/{id}/assign` - Assign staff to task

### Task Management
- `GET /api/v1/tasks` - List tasks
- `POST /api/v1/tasks` - Create task
- `GET /api/v1/tasks/{id}` - Get task details
- `PUT /api/v1/tasks/{id}/status` - Update task status
- `POST /api/v1/tasks/bulk-assign` - Bulk assign tasks

### Returns Management
- `POST /api/v1/returns` - Create return request
- `GET /api/v1/returns/{id}` - Get return details
- `PUT /api/v1/returns/{id}/quality-assessment` - Submit quality assessment
- `POST /api/v1/returns/{id}/label` - Generate return label

### Mobile API
- `GET /api/v1/mobile/tasks` - Get assigned tasks for staff
- `PUT /api/v1/mobile/tasks/{id}/complete` - Complete task
- `POST /api/v1/mobile/scan` - Scan QR code

## Events Published
- `warehouse.created` - When warehouse is created
- `warehouse.updated` - When warehouse is updated
- `zone.created` - When zone is created
- `task.created` - When task is created
- `task.assigned` - When task is assigned
- `task.completed` - When task is completed
- `return.created` - When return is created
- `return.assessed` - When return quality is assessed

## Integration Points
- **Inventory Service** - For stock levels and locations
- **Fulfillment Service** - For order processing tasks
- **Order Service** - For order information
- **Shipping Carriers** - For label generation and tracking

## Building for Production
```bash
mvn clean package
docker build -t warehouse-management-service:latest .
```

## Testing
```bash
mvn test
```
