# Inventory Service

## Overview
The Inventory Service manages stock levels, warehouse locations, and inventory tracking for the social e-commerce ecosystem.

## Port Configuration
- Service Port: **8084**
- Management Port: **9084** (Actuator endpoints)

## Testing Strategy

### 1. Quick Development Test
```bash
# Run quick compilation and unit tests
./quick-test.bat
```

### 2. Full Production Test Suite
```bash
# Run complete test suite with all validations
./test-and-deploy.bat
```

### Test Suite Components
1. **Clean & Compile** - Ensures code compiles without errors
2. **Unit Tests** - Tests individual components in isolation
3. **Integration Tests** - Tests with real dependencies (DB, Kafka)
4. **Package Build** - Creates deployable JAR
5. **Test Reports** - Generates HTML test reports
6. **Code Coverage** - Jacoco coverage analysis
7. **Smoke Test** - Verifies service starts correctly

## Dependencies
- PostgreSQL (via Docker or local)
- Redis (for caching)
- Apache Kafka (for events)
- Eureka Server (service discovery)

## Local Development Setup

### Prerequisites
```bash
# Start required services
docker-compose up -d postgres redis kafka eureka
```

### Environment Variables
```properties
SPRING_PROFILES_ACTIVE=dev
DB_HOST=localhost
DB_PORT=5432
DB_NAME=inventory_db
REDIS_HOST=localhost
REDIS_PORT=6379
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

## API Endpoints

### Health Check
```
GET /actuator/health
```

### Core Endpoints
- `GET /api/v1/inventory/{productId}` - Get inventory for product
- `PUT /api/v1/inventory/{productId}` - Update inventory
- `POST /api/v1/inventory/reserve` - Reserve inventory for order
- `POST /api/v1/inventory/release` - Release reserved inventory

## Database Schema
The service uses Flyway for database migrations. Migrations are in:
```
src/main/resources/db/migration/
```

## Kafka Topics
- **Produces to:**
  - `inventory.updated`
  - `inventory.low-stock-alert`
  
- **Consumes from:**
  - `order.created`
  - `order.cancelled`

## Monitoring
- Metrics: `/actuator/metrics`
- Health: `/actuator/health`
- Info: `/actuator/info`

## Production Deployment

### Build for Production
```bash
mvn clean package -P prod
```

### Docker Build
```bash
docker build -t inventory-service:1.0.0 .
```

### Kubernetes Deployment
```bash
kubectl apply -f k8s/deployment.yaml
```

## Troubleshooting

### Common Issues
1. **Port Already in Use**: Check if another service is using port 8084
2. **Database Connection**: Verify PostgreSQL is running and accessible
3. **Kafka Connection**: Ensure Kafka brokers are available

### Logs Location
- Development: `logs/`
- Production: `/var/log/inventory-service/`

## Git Workflow
```bash
# After all tests pass
git add .
git commit -m "feat: inventory-service production ready"
git push origin feature/inventory-service
```
