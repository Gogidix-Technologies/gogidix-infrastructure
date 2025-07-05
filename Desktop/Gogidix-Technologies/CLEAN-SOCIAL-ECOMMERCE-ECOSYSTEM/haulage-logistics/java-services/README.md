# Java Services - Haulage Logistics Domain

This directory contains all **15 Java microservices** for the Haulage Logistics domain, built with **Spring Boot 3.2** and **Java 17**.

## 📁 Service Structure

### **Core Logistics Services (5 services)**
```
├── haulage-core-service/           # Central logistics management
├── freight-management-service/     # Cargo and freight coordination
├── route-optimization-service/     # Heavy vehicle route planning
├── load-planning-service/          # Cargo optimization and weight distribution
└── transport-scheduling-service/   # Delivery scheduling and coordination
```

### **Fleet Management Services (4 services)**
```
├── fleet-management-service/       # Vehicle fleet oversight
├── driver-management-service/      # Professional driver coordination
├── vehicle-maintenance-service/    # Maintenance scheduling and tracking
└── fuel-management-service/        # Fuel optimization and cost tracking
```

### **Operations Services (3 services)**
```
├── dispatch-coordination-service/  # Central dispatch operations
├── cargo-tracking-service/         # Real-time freight tracking
└── warehouse-integration-service/  # Integration with warehousing domain
```

### **Business Services (3 services)**
```
├── billing-invoicing-service/      # Customer billing and payments
├── compliance-monitoring-service/  # Regulatory compliance tracking
└── analytics-reporting-service/    # Business intelligence and reporting
```

## 🏗️ Service Architecture

### **Standard Service Structure**
Each Java service follows this standardized structure:

```
service-name/
├── src/
│   ├── main/
│   │   ├── java/com/gogidix/haulage/[service]/
│   │   │   ├── controller/          # REST controllers
│   │   │   ├── service/             # Business logic
│   │   │   ├── repository/          # Data access layer
│   │   │   ├── entity/              # JPA entities
│   │   │   ├── dto/                 # Data transfer objects
│   │   │   ├── mapper/              # MapStruct mappers
│   │   │   ├── config/              # Configuration classes
│   │   │   ├── exception/           # Custom exceptions
│   │   │   └── Application.java     # Main application class
│   │   └── resources/
│   │       ├── application.yml      # Application configuration
│   │       ├── application-dev.yml  # Development config
│   │       ├── application-prod.yml # Production config
│   │       └── db/migration/        # Flyway migrations
│   └── test/
│       ├── java/                    # Unit and integration tests
│       └── resources/               # Test configurations
├── Dockerfile                       # Container configuration
├── docker-compose.yml              # Local development setup
├── pom.xml                         # Maven configuration
└── README.md                       # Service-specific documentation
```

## 🛠️ Technology Stack

### **Core Technologies**
- **Java 17** - LTS version with modern language features
- **Spring Boot 3.2.1** - Latest stable release
- **Spring Cloud 2023.0.0** - Microservices patterns
- **PostgreSQL 15** - Primary database
- **Flyway** - Database migrations

### **Spring Boot Starters**
- `spring-boot-starter-web` - REST APIs
- `spring-boot-starter-data-jpa` - Database access
- `spring-boot-starter-security` - Security framework
- `spring-boot-starter-validation` - Input validation
- `spring-boot-starter-actuator` - Health checks and metrics

### **Spring Cloud Components**
- `spring-cloud-starter-netflix-eureka-client` - Service discovery
- `spring-cloud-starter-config` - Configuration management
- `spring-cloud-starter-openfeign` - Inter-service communication

### **Messaging & Events**
- **Apache Kafka** - Event streaming
- `spring-kafka` - Kafka integration

### **Utilities & Productivity**
- **Lombok** - Boilerplate reduction
- **MapStruct** - Object mapping
- **SpringDoc OpenAPI** - API documentation

### **Testing**
- **JUnit 5** - Unit testing framework
- **Testcontainers** - Integration testing
- **Spring Boot Test** - Spring testing support

## 🔧 Development Setup

### **Prerequisites**
```bash
# Required software
Java 17 LTS
Maven 3.8+
Docker & Docker Compose
PostgreSQL 15
```

### **Environment Setup**
```bash
# Clone the repository
cd haulage-logistics/java-services

# Set environment variables
export JAVA_HOME=/path/to/java17
export MAVEN_HOME=/path/to/maven

# Start infrastructure services
docker-compose -f ../docker-compose.yml up -d postgres kafka redis

# Build all services
mvn clean install

# Run a specific service
cd haulage-core-service
mvn spring-boot:run
```

### **Database Setup**
```bash
# Connect to PostgreSQL
psql -h localhost -p 5433 -U haulage_user -d haulage_logistics

# Run migrations (automatic on startup)
# Or manually: mvn flyway:migrate
```

## 📊 Service Dependencies

### **Inter-Service Communication**
```yaml
# Core service dependencies
haulage-core-service:
  - No dependencies (foundational service)

freight-management-service:
  - haulage-core-service
  - route-optimization-service

fleet-management-service:
  - haulage-core-service
  - driver-management-service

# Integration dependencies
warehouse-integration-service:
  - warehousing-domain/warehouse-management-service
  - warehousing-domain/inventory-service
```

### **External Dependencies**
```yaml
# Shared infrastructure services
auth-service: Authentication and authorization
notification-service: SMS/Email notifications
payment-processing-service: Billing and payments
geo-location-service: Location tracking
analytics-engine: Business reporting
file-storage-service: Document management
```

## 🚀 Build & Deployment

### **Maven Build Commands**
```bash
# Build all services
mvn clean install

# Skip tests (for faster builds)
mvn clean install -DskipTests

# Build specific service
cd [service-name]
mvn clean package

# Run tests
mvn test

# Integration tests
mvn verify
```

### **Docker Commands**
```bash
# Build Docker image
mvn spring-boot:build-image

# Or using Dockerfile
docker build -t haulage/[service-name]:latest .

# Run container
docker run -p 8080:8080 haulage/[service-name]:latest
```

### **Profiles**
- **dev** - Local development (H2 database, relaxed security)
- **test** - Integration testing (TestContainers)
- **prod** - Production (PostgreSQL, full security)

## 📋 Service Implementation Status

| Service | Status | Port | Database Schema | Documentation |
|---------|--------|------|----------------|---------------|
| haulage-core-service | 🟡 Planned | 8081 | `core` | ⏳ TODO |
| freight-management-service | 🟡 Planned | 8082 | `freight` | ⏳ TODO |
| route-optimization-service | 🟡 Planned | 8083 | `routes` | ⏳ TODO |
| load-planning-service | 🟡 Planned | 8084 | `loads` | ⏳ TODO |
| transport-scheduling-service | 🟡 Planned | 8085 | `scheduling` | ⏳ TODO |
| fleet-management-service | 🟡 Planned | 8086 | `fleet` | ⏳ TODO |
| driver-management-service | 🟡 Planned | 8087 | `drivers` | ⏳ TODO |
| vehicle-maintenance-service | 🟡 Planned | 8088 | `maintenance` | ⏳ TODO |
| fuel-management-service | 🟡 Planned | 8089 | `fuel` | ⏳ TODO |
| dispatch-coordination-service | 🟡 Planned | 8090 | `dispatch` | ⏳ TODO |
| cargo-tracking-service | 🟡 Planned | 8091 | `tracking` | ⏳ TODO |
| warehouse-integration-service | 🟡 Planned | 8092 | `integration` | ⏳ TODO |
| billing-invoicing-service | 🟡 Planned | 8093 | `billing` | ⏳ TODO |
| compliance-monitoring-service | 🟡 Planned | 8094 | `compliance` | ⏳ TODO |
| analytics-reporting-service | 🟡 Planned | 8095 | `analytics` | ⏳ TODO |

## 🔍 Code Quality & Standards

### **Code Style**
- **Checkstyle** configuration for consistent formatting
- **PMD** for code quality analysis
- **SpotBugs** for bug detection
- **SonarQube** integration for comprehensive analysis

### **Testing Standards**
- **Unit Tests**: 80%+ code coverage
- **Integration Tests**: All REST endpoints
- **Contract Tests**: Inter-service communication
- **Performance Tests**: Load testing with JMeter

### **Security Standards**
- **OWASP** compliance
- **JWT** authentication
- **HTTPS** in production
- **Input validation** on all endpoints
- **SQL injection** prevention

## 📚 Documentation

### **API Documentation**
- **OpenAPI 3.0** specifications
- **Swagger UI** available at `/swagger-ui.html`
- **Postman collections** for testing

### **Service Documentation**
Each service includes:
- `README.md` with service overview
- API documentation
- Database schema documentation
- Deployment instructions

## 🚀 Next Steps

1. **Service Implementation**: Start with `haulage-core-service`
2. **Database Design**: Create Flyway migrations
3. **API Design**: Define OpenAPI specifications
4. **Testing Strategy**: Set up testing frameworks
5. **CI/CD Pipeline**: GitHub Actions integration

---

*Last Updated: 2025-07-02*  
*Total Services: 15 Java microservices*  
*Architecture Status: Ready for implementation*