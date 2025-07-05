# Node.js Services - Haulage Logistics Domain

This directory contains all **3 Node.js microservices** for the Haulage Logistics domain, built with **Node.js 18** and **Express.js**.

## 📁 Service Structure

### **Customer-Facing Services (2 services)**
```
├── customer-portal-service/        # Customer booking and tracking portal (React)
└── mobile-tracking-app/            # Mobile freight tracking application (React Native)
```

### **Integration Services (1 service)**
```
└── third-party-integration-service/ # External logistics APIs and webhooks
```

## 🏗️ Service Architecture

### **Standard Service Structure**
Each Node.js service follows this standardized structure:

```
service-name/
├── src/
│   ├── controllers/                 # Route handlers
│   ├── services/                    # Business logic
│   ├── models/                      # Data models (MongoDB/Mongoose)
│   ├── middleware/                  # Custom middleware
│   ├── routes/                      # Express routes
│   ├── utils/                       # Utility functions
│   ├── config/                      # Configuration files
│   ├── validators/                  # Input validation
│   └── app.js                       # Express application setup
├── tests/
│   ├── unit/                        # Unit tests
│   ├── integration/                 # Integration tests
│   └── fixtures/                    # Test data
├── public/                          # Static assets (for frontend services)
├── views/                           # Template files (if applicable)
├── Dockerfile                       # Container configuration
├── docker-compose.yml              # Local development setup
├── package.json                     # Dependencies and scripts
├── .env.template                    # Environment variables template
├── jest.config.js                   # Testing configuration
├── .eslintrc.js                     # ESLint configuration
└── README.md                        # Service-specific documentation
```

## 🛠️ Technology Stack

### **Core Technologies**
- **Node.js 18 LTS** - Latest LTS with modern JavaScript features
- **Express.js 4.18** - Fast, unopinionated web framework
- **MongoDB 7** - Document database for flexible data storage
- **Mongoose 8** - MongoDB object modeling
- **Redis 7** - Caching and session storage

### **Frontend Technologies**
- **React 18** - Customer portal UI
- **Next.js 14** - Full-stack React framework
- **React Native 0.73** - Mobile application
- **Material-UI 5** - Component library
- **Tailwind CSS** - Utility-first CSS framework

### **Authentication & Security**
- **JWT** - JSON Web Tokens for authentication
- **bcrypt** - Password hashing
- **helmet** - Security headers
- **cors** - Cross-Origin Resource Sharing
- **express-rate-limit** - Rate limiting

### **Validation & Documentation**
- **Joi** - Schema validation
- **Swagger/OpenAPI** - API documentation
- **express-validator** - Request validation middleware

### **Testing**
- **Jest** - Testing framework
- **Supertest** - HTTP assertion library
- **MongoDB Memory Server** - In-memory MongoDB for testing

### **Utilities & Productivity**
- **nodemon** - Development auto-restart
- **PM2** - Production process manager
- **Winston** - Logging library
- **Axios** - HTTP client

## 🔧 Development Setup

### **Prerequisites**
```bash
# Required software
Node.js 18 LTS
npm 8+ or yarn 1.22+
Docker & Docker Compose
MongoDB 7
Redis 7
```

### **Environment Setup**
```bash
# Clone the repository
cd haulage-logistics/node-services

# Install dependencies for all services
npm run install:all

# Or install for specific service
cd customer-portal-service
npm install

# Start infrastructure services
docker-compose -f ../docker-compose.yml up -d mongodb redis

# Start development server
npm run dev
```

### **Environment Variables**
Create `.env` file from `.env.template`:
```bash
cp .env.template .env
# Edit .env with your configuration
```

## 📊 Service Details

### **1. Customer Portal Service**
**Purpose**: Web-based customer portal for booking and tracking haulage services

**Technology Stack**:
- **Frontend**: React 18 + Next.js 14
- **Backend**: Node.js + Express.js
- **Database**: MongoDB (customer data, bookings)
- **Styling**: Material-UI + Tailwind CSS

**Key Features**:
- Customer registration and authentication
- Freight booking and quote requests
- Real-time shipment tracking
- Invoice and billing management
- Support ticket system
- Document upload and management

**API Endpoints**:
```
POST   /api/auth/login          # Customer authentication
POST   /api/bookings            # Create new booking
GET    /api/bookings/:id        # Get booking details
GET    /api/tracking/:id        # Track shipment
POST   /api/quotes              # Request freight quote
GET    /api/invoices            # Get customer invoices
```

### **2. Mobile Tracking App**
**Purpose**: React Native mobile application for freight tracking

**Technology Stack**:
- **Framework**: React Native 0.73
- **Navigation**: React Navigation 6
- **State Management**: Redux Toolkit
- **Maps**: React Native Maps
- **Push Notifications**: React Native Push Notification

**Key Features**:
- Real-time GPS tracking
- Push notifications for status updates
- Offline capability
- Barcode/QR code scanning
- Photo documentation
- Driver communication

**Platform Support**:
- iOS 13+
- Android 8+ (API level 26)

### **3. Third-Party Integration Service**
**Purpose**: Integration with external logistics APIs and webhook handling

**Technology Stack**:
- **Backend**: Node.js + Express.js
- **Queue**: Bull (Redis-based job queue)
- **HTTP Client**: Axios with retry logic
- **Database**: MongoDB (integration logs)

**Key Integrations**:
- **Google Maps API** - Route optimization and geocoding
- **Weather APIs** - Weather-based route planning
- **Fuel Price APIs** - Real-time fuel cost data
- **Government APIs** - Permit and compliance checking
- **Telematics APIs** - Vehicle tracking data
- **Payment Gateways** - Stripe, PayPal integration

**API Endpoints**:
```
POST   /api/webhooks/gps        # GPS tracking webhooks
POST   /api/webhooks/payment    # Payment status webhooks
GET    /api/routes/optimize     # Route optimization
GET    /api/weather/:location   # Weather data
POST   /api/compliance/check    # Compliance verification
```

## 🚀 Build & Deployment

### **Development Commands**
```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Run tests
npm test

# Run tests in watch mode
npm run test:watch

# Run linting
npm run lint

# Fix linting issues
npm run lint:fix

# Build for production
npm run build

# Start production server
npm start
```

### **Docker Commands**
```bash
# Build Docker image
docker build -t haulage/[service-name]:latest .

# Run container
docker run -p 3000:3000 haulage/[service-name]:latest

# Docker Compose for development
docker-compose up -d
```

### **Environment Profiles**
- **development** - Local development with hot reload
- **test** - Testing environment with in-memory databases
- **staging** - Pre-production environment
- **production** - Production environment with optimizations

## 📋 Service Implementation Status

| Service | Status | Port | Database | Frontend | Documentation |
|---------|--------|------|----------|----------|---------------|
| customer-portal-service | 🟡 Planned | 3001 | MongoDB | React + Next.js | ⏳ TODO |
| mobile-tracking-app | 🟡 Planned | N/A | AsyncStorage | React Native | ⏳ TODO |
| third-party-integration-service | 🟡 Planned | 3002 | MongoDB | N/A | ⏳ TODO |

## 🔍 Code Quality & Standards

### **Code Style**
```bash
# ESLint configuration
extends: ['eslint:recommended', 'airbnb-base']

# Prettier integration
"prettier/prettier": ["error"]

# Custom rules
"no-console": "warn"
"prefer-const": "error"
```

### **Testing Standards**
- **Unit Tests**: 80%+ code coverage
- **Integration Tests**: All API endpoints
- **E2E Tests**: Critical user journeys
- **Performance Tests**: Load testing with Artillery

### **Security Standards**
- **OWASP** compliance
- **Input validation** on all endpoints
- **Rate limiting** for API protection
- **CORS** configuration
- **Helmet** security headers

## 📚 API Documentation

### **OpenAPI Specifications**
Each service includes:
- Complete OpenAPI 3.0 specification
- Swagger UI at `/api-docs`
- Postman collections for testing
- Request/response examples

### **Authentication**
```javascript
// JWT Authentication example
Authorization: Bearer <jwt_token>

// API Key authentication (for third-party integrations)
X-API-Key: <api_key>
```

## 🧪 Testing Strategy

### **Unit Testing**
```javascript
// Example Jest test
describe('BookingService', () => {
  it('should create a new booking', async () => {
    const booking = await BookingService.create(mockBookingData);
    expect(booking).toHaveProperty('id');
    expect(booking.status).toBe('pending');
  });
});
```

### **Integration Testing**
```javascript
// Example Supertest integration test
describe('POST /api/bookings', () => {
  it('should create a booking and return 201', async () => {
    const response = await request(app)
      .post('/api/bookings')
      .send(mockBookingData)
      .expect(201);
    
    expect(response.body).toHaveProperty('id');
  });
});
```

## 🚀 Deployment Guide

### **Production Deployment**
```bash
# Using PM2
pm2 start ecosystem.config.js --env production

# Using Docker
docker-compose -f docker-compose.prod.yml up -d

# Health check
curl http://localhost:3000/health
```

### **Environment Variables (Production)**
```bash
NODE_ENV=production
PORT=3000
MONGODB_URI=mongodb://production-host:27017/haulage
REDIS_URL=redis://production-host:6379
JWT_SECRET=production-jwt-secret
```

## 🔗 Integration Points

### **Java Services Integration**
```javascript
// Example Feign client integration
const haulageCore = axios.create({
  baseURL: process.env.HAULAGE_CORE_URL,
  headers: {
    'Authorization': `Bearer ${jwt}`,
    'Content-Type': 'application/json'
  }
});

// Create booking in Java service
const booking = await haulageCore.post('/api/bookings', bookingData);
```

### **Message Queue Integration**
```javascript
// Kafka producer example
const kafka = new KafkaJS.Kafka({
  brokers: [process.env.KAFKA_BROKERS]
});

await producer.send({
  topic: 'booking-created',
  messages: [{ value: JSON.stringify(booking) }]
});
```

## 🚀 Next Steps

1. **Service Implementation**: Start with `customer-portal-service`
2. **Database Design**: MongoDB schema design
3. **API Design**: OpenAPI specification
4. **Frontend Development**: React components and pages
5. **Testing Setup**: Jest and testing infrastructure
6. **CI/CD Pipeline**: GitHub Actions integration

---

*Last Updated: 2025-07-02*  
*Total Services: 3 Node.js microservices*  
*Architecture Status: Ready for implementation*