# Fulfillment Service

## Overview
The Fulfillment Service manages the complete order fulfillment process, from order receipt through picking, packing, and shipping within the warehousing ecosystem.

## Features
- Order fulfillment workflow management
- Picking and packing task assignment
- Shipment creation and tracking
- Integration with inventory service
- Return processing
- Multi-carrier shipping support

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
   cd warehousing/fulfillment-service
   ```

2. **Start dependencies using Docker Compose**
   ```bash
   docker-compose up -d fulfillment-db fulfillment-redis
   ```

3. **Run the service**
   ```bash
   mvn spring-boot:run
   ```

The service will start on port 8085 by default.

### API Documentation
Once the service is running, access the API documentation at:
- Swagger UI: http://localhost:8085/swagger-ui.html
- OpenAPI Spec: http://localhost:8085/v3/api-docs

## Configuration
Key configuration properties:
- `SERVER_PORT`: Service port (default: 8085)
- `DATABASE_URL`: PostgreSQL connection URL
- `REDIS_HOST`: Redis host
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka brokers
- `INVENTORY_SERVICE_URL`: Inventory service URL

## API Endpoints

### Fulfillment Orders
- `POST /api/v1/fulfillment/orders` - Create fulfillment order
- `GET /api/v1/fulfillment/orders/{id}` - Get fulfillment order
- `PUT /api/v1/fulfillment/orders/{id}/status` - Update order status
- `POST /api/v1/fulfillment/orders/{id}/assign` - Assign to warehouse

### Picking Tasks
- `GET /api/v1/fulfillment/picking/tasks` - List picking tasks
- `PUT /api/v1/fulfillment/picking/tasks/{id}/start` - Start picking
- `PUT /api/v1/fulfillment/picking/tasks/{id}/complete` - Complete picking

### Packing Tasks
- `GET /api/v1/fulfillment/packing/tasks` - List packing tasks
- `PUT /api/v1/fulfillment/packing/tasks/{id}/start` - Start packing
- `PUT /api/v1/fulfillment/packing/tasks/{id}/complete` - Complete packing

### Shipments
- `POST /api/v1/fulfillment/shipments` - Create shipment
- `GET /api/v1/fulfillment/shipments/{id}` - Get shipment
- `POST /api/v1/fulfillment/shipments/{id}/label` - Generate shipping label
- `PUT /api/v1/fulfillment/shipments/{id}/dispatch` - Dispatch shipment

## Events Published
- `fulfillment.order.created` - When order is created
- `fulfillment.order.assigned` - When order is assigned to warehouse
- `fulfillment.picking.completed` - When picking is completed
- `fulfillment.packing.completed` - When packing is completed
- `fulfillment.shipment.created` - When shipment is created
- `fulfillment.shipment.dispatched` - When shipment is dispatched

## Building for Production
```bash
mvn clean package
docker build -t fulfillment-service:latest .
```

## Testing
```bash
mvn test
```
