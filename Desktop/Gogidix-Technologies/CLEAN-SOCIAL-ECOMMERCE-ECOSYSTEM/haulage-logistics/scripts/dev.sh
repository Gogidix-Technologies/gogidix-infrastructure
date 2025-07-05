#!/bin/bash

# Development startup script for Haulage Logistics Domain
# This script starts all services for local development

set -e

# Script configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
LOG_DIR="$PROJECT_ROOT/logs"
PID_DIR="$PROJECT_ROOT/.pids"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging function
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}"
}

info() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')] INFO: $1${NC}"
}

# Create necessary directories
mkdir -p "$LOG_DIR" "$PID_DIR"

# Function to check if required tools are installed
check_prerequisites() {
    log "Checking prerequisites..."
    
    local missing_tools=()
    
    # Check Java
    if ! command -v java &> /dev/null; then
        missing_tools+=("java")
    else
        java_version=$(java -version 2>&1 | head -n 1)
        info "Found Java: $java_version"
    fi
    
    # Check Maven
    if ! command -v mvn &> /dev/null; then
        missing_tools+=("maven")
    else
        mvn_version=$(mvn -version | head -n 1)
        info "Found Maven: $mvn_version"
    fi
    
    # Check Node.js
    if ! command -v node &> /dev/null; then
        missing_tools+=("node")
    else
        node_version=$(node --version)
        info "Found Node.js: $node_version"
    fi
    
    # Check npm
    if ! command -v npm &> /dev/null; then
        missing_tools+=("npm")
    else
        npm_version=$(npm --version)
        info "Found npm: $npm_version"
    fi
    
    # Check Docker
    if ! command -v docker &> /dev/null; then
        missing_tools+=("docker")
    else
        docker_version=$(docker --version)
        info "Found Docker: $docker_version"
    fi
    
    # Check Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        missing_tools+=("docker-compose")
    else
        compose_version=$(docker-compose --version)
        info "Found Docker Compose: $compose_version"
    fi
    
    if [ ${#missing_tools[@]} -ne 0 ]; then
        error "Missing required tools: ${missing_tools[*]}"
        error "Please install the missing tools and try again."
        exit 1
    fi
    
    log "All prerequisites satisfied!"
}

# Function to check and start infrastructure services
start_infrastructure() {
    log "Starting infrastructure services..."
    
    cd "$PROJECT_ROOT"
    
    # Check if docker-compose.yml exists
    if [ ! -f "docker-compose.yml" ]; then
        error "docker-compose.yml not found in $PROJECT_ROOT"
        exit 1
    fi
    
    # Start infrastructure services (PostgreSQL, MongoDB, Redis, Kafka)
    log "Starting database and message queue services..."
    docker-compose up -d postgres mongodb redis kafka zookeeper
    
    # Wait for services to be ready
    log "Waiting for infrastructure services to be ready..."
    sleep 10
    
    # Check PostgreSQL
    info "Checking PostgreSQL connection..."
    max_attempts=30
    attempt=1
    while [ $attempt -le $max_attempts ]; do
        if docker-compose exec -T postgres pg_isready -U haulage_user -d haulage_db &> /dev/null; then
            log "PostgreSQL is ready!"
            break
        fi
        if [ $attempt -eq $max_attempts ]; then
            error "PostgreSQL failed to start after $max_attempts attempts"
            exit 1
        fi
        info "Waiting for PostgreSQL... (attempt $attempt/$max_attempts)"
        sleep 2
        ((attempt++))
    done
    
    # Check MongoDB
    info "Checking MongoDB connection..."
    attempt=1
    while [ $attempt -le $max_attempts ]; do
        if docker-compose exec -T mongodb mongosh --eval "db.adminCommand('ping')" &> /dev/null; then
            log "MongoDB is ready!"
            break
        fi
        if [ $attempt -eq $max_attempts ]; then
            error "MongoDB failed to start after $max_attempts attempts"
            exit 1
        fi
        info "Waiting for MongoDB... (attempt $attempt/$max_attempts)"
        sleep 2
        ((attempt++))
    done
    
    # Check Redis
    info "Checking Redis connection..."
    attempt=1
    while [ $attempt -le $max_attempts ]; do
        if docker-compose exec -T redis redis-cli ping | grep -q "PONG"; then
            log "Redis is ready!"
            break
        fi
        if [ $attempt -eq $max_attempts ]; then
            error "Redis failed to start after $max_attempts attempts"
            exit 1
        fi
        info "Waiting for Redis... (attempt $attempt/$max_attempts)"
        sleep 2
        ((attempt++))
    done
    
    log "All infrastructure services are ready!"
}

# Function to start Java services
start_java_services() {
    log "Starting Java services..."
    
    local java_services=(
        "haulage-booking-service:8081"
        "fleet-management-service:8082"
        "route-optimization-service:8083"
        "pricing-engine-service:8084"
        "cargo-management-service:8085"
        "driver-management-service:8086"
        "maintenance-scheduling-service:8087"
        "fuel-monitoring-service:8088"
        "insurance-claims-service:8089"
        "compliance-monitoring-service:8090"
        "contract-management-service:8091"
        "invoice-billing-service:8092"
        "vehicle-tracking-service:8093"
        "load-planning-service:8094"
        "customer-portal-api:8095"
    )
    
    cd "$PROJECT_ROOT/java-services"
    
    # Build all Java services
    log "Building Java services..."
    if ! mvn clean compile -T 1C; then
        error "Failed to build Java services"
        exit 1
    fi
    
    # Start each service
    for service_config in "${java_services[@]}"; do
        IFS=':' read -r service_name port <<< "$service_config"
        
        log "Starting $service_name on port $port..."
        
        # Set service-specific environment variables
        export SERVER_PORT=$port
        export SPRING_PROFILES_ACTIVE=development
        export DATABASE_URL="jdbc:postgresql://localhost:5432/haulage_db"
        export DATABASE_USERNAME="haulage_user"
        export DATABASE_PASSWORD="haulage_password"
        export REDIS_URL="redis://localhost:6379"
        export KAFKA_BOOTSTRAP_SERVERS="localhost:9092"
        
        # Start service in background
        cd "$service_name" 2>/dev/null || {
            warn "Service directory $service_name not found, skipping..."
            continue
        }
        
        mvn spring-boot:run \
            -Dspring-boot.run.jvmArguments="-Xmx512m -Xms256m" \
            > "$LOG_DIR/${service_name}.log" 2>&1 &
        
        local pid=$!
        echo $pid > "$PID_DIR/${service_name}.pid"
        
        info "Started $service_name (PID: $pid)"
        cd ..
        
        # Brief pause between service starts
        sleep 2
    done
    
    log "All Java services started!"
}

# Function to start Node.js services
start_node_services() {
    log "Starting Node.js services..."
    
    local node_services=(
        "haulage-analytics-service:3001"
        "real-time-notification-service:3002"
        "iot-integration-service:3003"
        "mobile-api-gateway:3004"
    )
    
    cd "$PROJECT_ROOT/node-services"
    
    # Install dependencies for all Node.js services
    log "Installing Node.js dependencies..."
    for service_config in "${node_services[@]}"; do
        IFS=':' read -r service_name port <<< "$service_config"
        
        if [ -d "$service_name" ]; then
            cd "$service_name"
            if [ -f "package.json" ]; then
                log "Installing dependencies for $service_name..."
                npm install --silent
            fi
            cd ..
        fi
    done
    
    # Start each Node.js service
    for service_config in "${node_services[@]}"; do
        IFS=':' read -r service_name port <<< "$service_config"
        
        log "Starting $service_name on port $port..."
        
        cd "$service_name" 2>/dev/null || {
            warn "Service directory $service_name not found, skipping..."
            continue
        }
        
        # Set service-specific environment variables
        export PORT=$port
        export NODE_ENV=development
        export DATABASE_URL="postgresql://haulage_user:haulage_password@localhost:5432/haulage_db"
        export MONGODB_URL="mongodb://localhost:27017/haulage_analytics"
        export REDIS_URL="redis://localhost:6379"
        export KAFKA_BROKERS="localhost:9092"
        
        # Start service in background
        if [ -f "package.json" ]; then
            npm run dev > "$LOG_DIR/${service_name}.log" 2>&1 &
            local pid=$!
            echo $pid > "$PID_DIR/${service_name}.pid"
            info "Started $service_name (PID: $pid)"
        else
            warn "No package.json found for $service_name, skipping..."
        fi
        
        cd ..
        
        # Brief pause between service starts
        sleep 2
    done
    
    log "All Node.js services started!"
}

# Function to start frontend applications
start_frontend_apps() {
    log "Starting frontend applications..."
    
    local frontend_apps=(
        "customer-portal:3010"
        "fleet-dashboard:3011"
        "admin-panel:3012"
        "mobile-app:3013"
    )
    
    cd "$PROJECT_ROOT/frontend-apps"
    
    # Install dependencies and start each frontend app
    for app_config in "${frontend_apps[@]}"; do
        IFS=':' read -r app_name port <<< "$app_config"
        
        log "Starting $app_name on port $port..."
        
        cd "$app_name" 2>/dev/null || {
            warn "Frontend app directory $app_name not found, skipping..."
            continue
        }
        
        # Install dependencies if package.json exists
        if [ -f "package.json" ]; then
            log "Installing dependencies for $app_name..."
            npm install --silent
            
            # Set environment variables
            export PORT=$port
            export REACT_APP_API_URL="http://localhost:8080/api"
            export REACT_APP_WS_URL="ws://localhost:8080/ws"
            
            # Start frontend app
            npm start > "$LOG_DIR/${app_name}.log" 2>&1 &
            local pid=$!
            echo $pid > "$PID_DIR/${app_name}.pid"
            info "Started $app_name (PID: $pid)"
        else
            warn "No package.json found for $app_name, skipping..."
        fi
        
        cd ..
        
        # Brief pause between app starts
        sleep 3
    done
    
    log "All frontend applications started!"
}

# Function to display service status
show_status() {
    log "Service Status Summary:"
    echo ""
    
    # Infrastructure services
    info "Infrastructure Services:"
    docker-compose ps postgres mongodb redis kafka zookeeper | tail -n +2
    echo ""
    
    # Java services
    info "Java Services:"
    for pid_file in "$PID_DIR"/*.pid; do
        if [ -f "$pid_file" ]; then
            service_name=$(basename "$pid_file" .pid)
            pid=$(cat "$pid_file")
            if kill -0 "$pid" 2>/dev/null; then
                echo "  ✓ $service_name (PID: $pid)"
            else
                echo "  ✗ $service_name (not running)"
            fi
        fi
    done
    echo ""
    
    # Service URLs
    info "Service URLs:"
    echo "  Customer Portal: http://localhost:3010"
    echo "  Fleet Dashboard: http://localhost:3011"
    echo "  Admin Panel: http://localhost:3012"
    echo "  Mobile App: http://localhost:3013"
    echo "  API Documentation: http://localhost:8080/swagger-ui.html"
    echo "  Booking Service: http://localhost:8081"
    echo "  Analytics Service: http://localhost:3001"
    echo ""
    
    info "Logs can be found in: $LOG_DIR"
    info "To stop all services, run: ./scripts/stop.sh"
}

# Function to cleanup on exit
cleanup() {
    log "Development environment stopped."
}

# Trap cleanup function on script exit
trap cleanup EXIT

# Main execution
main() {
    log "Starting Haulage Logistics Development Environment..."
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --skip-infrastructure)
                SKIP_INFRASTRUCTURE=true
                shift
                ;;
            --skip-java)
                SKIP_JAVA=true
                shift
                ;;
            --skip-node)
                SKIP_NODE=true
                shift
                ;;
            --skip-frontend)
                SKIP_FRONTEND=true
                shift
                ;;
            --help|-h)
                echo "Usage: $0 [OPTIONS]"
                echo ""
                echo "Options:"
                echo "  --skip-infrastructure  Skip starting infrastructure services"
                echo "  --skip-java           Skip starting Java services"
                echo "  --skip-node           Skip starting Node.js services"
                echo "  --skip-frontend       Skip starting frontend applications"
                echo "  --help, -h            Show this help message"
                exit 0
                ;;
            *)
                error "Unknown option: $1"
                exit 1
                ;;
        esac
    done
    
    # Check prerequisites
    check_prerequisites
    
    # Start services based on options
    if [ "$SKIP_INFRASTRUCTURE" != "true" ]; then
        start_infrastructure
    else
        info "Skipping infrastructure services (use --skip-infrastructure)"
    fi
    
    if [ "$SKIP_JAVA" != "true" ]; then
        start_java_services
    else
        info "Skipping Java services (use --skip-java)"
    fi
    
    if [ "$SKIP_NODE" != "true" ]; then
        start_node_services
    else
        info "Skipping Node.js services (use --skip-node)"
    fi
    
    if [ "$SKIP_FRONTEND" != "true" ]; then
        start_frontend_apps
    else
        info "Skipping frontend applications (use --skip-frontend)"
    fi
    
    # Wait a moment for services to fully start
    log "Waiting for services to fully initialize..."
    sleep 10
    
    # Show status
    show_status
    
    log "Development environment is ready!"
    log "Press Ctrl+C to stop all services"
    
    # Keep script running
    while true; do
        sleep 60
        # Optional: Add health checks here
    done
}

# Run main function
main "$@"