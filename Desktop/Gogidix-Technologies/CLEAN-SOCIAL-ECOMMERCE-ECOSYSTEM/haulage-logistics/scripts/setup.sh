#!/bin/bash

# Setup script for Haulage Logistics Domain
# This script initializes the development environment

set -e

# Script configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging functions
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

# Function to check system requirements
check_system_requirements() {
    log "Checking system requirements..."
    
    # Check operating system
    OS="$(uname -s)"
    case "${OS}" in
        Linux*)     
            info "Operating System: Linux"
            ;;
        Darwin*)    
            info "Operating System: macOS"
            ;;
        CYGWIN*|MINGW32*|MSYS*|MINGW*)
            info "Operating System: Windows"
            ;;
        *)          
            warn "Unknown operating system: ${OS}"
            ;;
    esac
    
    # Check available memory
    if command -v free &> /dev/null; then
        total_mem=$(free -m | awk '/^Mem:/{print $2}')
        if [ "$total_mem" -lt 8192 ]; then
            warn "Less than 8GB RAM detected (${total_mem}MB). Performance may be affected."
        else
            info "Available memory: ${total_mem}MB"
        fi
    fi
    
    # Check available disk space
    available_space=$(df "$PROJECT_ROOT" | awk 'NR==2{print $4}')
    if [ "$available_space" -lt 10485760 ]; then  # 10GB in KB
        warn "Less than 10GB disk space available. Consider freeing up space."
    else
        info "Available disk space: $(df -h "$PROJECT_ROOT" | awk 'NR==2{print $4}')"
    fi
}

# Function to check and install prerequisites
check_prerequisites() {
    log "Checking and installing prerequisites..."
    
    local missing_tools=()
    local install_commands=()
    
    # Check Java
    if ! command -v java &> /dev/null; then
        missing_tools+=("Java 17")
        case "${OS}" in
            Linux*)
                install_commands+=("sudo apt-get update && sudo apt-get install openjdk-17-jdk")
                ;;
            Darwin*)
                install_commands+=("brew install openjdk@17")
                ;;
            *)
                install_commands+=("Please install Java 17 from https://adoptium.net/")
                ;;
        esac
    else
        java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
        info "Found Java: $java_version"
        
        # Check if Java version is 17 or higher
        java_major_version=$(echo "$java_version" | cut -d'.' -f1)
        if [ "$java_major_version" -lt 17 ]; then
            warn "Java version $java_version is less than required version 17"
            missing_tools+=("Java 17")
        fi
    fi
    
    # Check Maven
    if ! command -v mvn &> /dev/null; then
        missing_tools+=("Maven")
        case "${OS}" in
            Linux*)
                install_commands+=("sudo apt-get install maven")
                ;;
            Darwin*)
                install_commands+=("brew install maven")
                ;;
            *)
                install_commands+=("Please install Maven from https://maven.apache.org/download.cgi")
                ;;
        esac
    else
        mvn_version=$(mvn -version | head -n 1)
        info "Found Maven: $mvn_version"
    fi
    
    # Check Node.js
    if ! command -v node &> /dev/null; then
        missing_tools+=("Node.js 18+")
        case "${OS}" in
            Linux*)
                install_commands+=("curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash - && sudo apt-get install -y nodejs")
                ;;
            Darwin*)
                install_commands+=("brew install node@18")
                ;;
            *)
                install_commands+=("Please install Node.js 18+ from https://nodejs.org/")
                ;;
        esac
    else
        node_version=$(node --version)
        info "Found Node.js: $node_version"
        
        # Check if Node.js version is 18 or higher
        node_major_version=$(echo "$node_version" | cut -d'v' -f2 | cut -d'.' -f1)
        if [ "$node_major_version" -lt 18 ]; then
            warn "Node.js version $node_version is less than required version 18"
            missing_tools+=("Node.js 18+")
        fi
    fi
    
    # Check npm
    if ! command -v npm &> /dev/null; then
        missing_tools+=("npm")
        install_commands+=("npm is usually installed with Node.js")
    else
        npm_version=$(npm --version)
        info "Found npm: $npm_version"
    fi
    
    # Check Docker
    if ! command -v docker &> /dev/null; then
        missing_tools+=("Docker")
        case "${OS}" in
            Linux*)
                install_commands+=("curl -fsSL https://get.docker.com -o get-docker.sh && sh get-docker.sh")
                ;;
            Darwin*)
                install_commands+=("brew install --cask docker")
                ;;
            *)
                install_commands+=("Please install Docker from https://www.docker.com/products/docker-desktop")
                ;;
        esac
    else
        docker_version=$(docker --version)
        info "Found Docker: $docker_version"
        
        # Check if Docker daemon is running
        if ! docker info &> /dev/null; then
            warn "Docker daemon is not running. Please start Docker."
        fi
    fi
    
    # Check Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        missing_tools+=("Docker Compose")
        case "${OS}" in
            Linux*)
                install_commands+=("sudo curl -L \"https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)\" -o /usr/local/bin/docker-compose && sudo chmod +x /usr/local/bin/docker-compose")
                ;;
            Darwin*)
                install_commands+=("Docker Compose is included with Docker Desktop")
                ;;
            *)
                install_commands+=("Please install Docker Compose from https://docs.docker.com/compose/install/")
                ;;
        esac
    else
        compose_version=$(docker-compose --version)
        info "Found Docker Compose: $compose_version"
    fi
    
    # Check Git
    if ! command -v git &> /dev/null; then
        missing_tools+=("Git")
        case "${OS}" in
            Linux*)
                install_commands+=("sudo apt-get install git")
                ;;
            Darwin*)
                install_commands+=("brew install git")
                ;;
            *)
                install_commands+=("Please install Git from https://git-scm.com/downloads")
                ;;
        esac
    else
        git_version=$(git --version)
        info "Found Git: $git_version"
    fi
    
    # Display missing tools and installation commands
    if [ ${#missing_tools[@]} -ne 0 ]; then
        error "Missing required tools:"
        for i in "${!missing_tools[@]}"; do
            echo "  - ${missing_tools[$i]}"
            echo "    Install with: ${install_commands[$i]}"
        done
        echo ""
        error "Please install the missing tools and run this script again."
        exit 1
    fi
    
    log "All prerequisites are satisfied!"
}

# Function to create project structure
create_project_structure() {
    log "Creating project structure..."
    
    cd "$PROJECT_ROOT"
    
    # Create main directories
    local directories=(
        "java-services"
        "node-services"
        "frontend-apps"
        "shared"
        "config"
        "logs"
        "data"
        ".pids"
        "docs/architecture"
        "docs/operations"
        "docs/setup"
        "docs/api"
        "tests/integration"
        "tests/e2e"
        "scripts/deployment"
        "scripts/maintenance"
    )
    
    for dir in "${directories[@]}"; do
        if [ ! -d "$dir" ]; then
            mkdir -p "$dir"
            info "Created directory: $dir"
        fi
    done
    
    # Create Java service directories
    local java_services=(
        "haulage-booking-service"
        "fleet-management-service"
        "route-optimization-service"
        "pricing-engine-service"
        "cargo-management-service"
        "driver-management-service"
        "maintenance-scheduling-service"
        "fuel-monitoring-service"
        "insurance-claims-service"
        "compliance-monitoring-service"
        "contract-management-service"
        "invoice-billing-service"
        "vehicle-tracking-service"
        "load-planning-service"
        "customer-portal-api"
    )
    
    cd java-services
    for service in "${java_services[@]}"; do
        if [ ! -d "$service" ]; then
            mkdir -p "$service/src/main/java/com/exalt/haulage/${service//-/}"
            mkdir -p "$service/src/main/resources"
            mkdir -p "$service/src/test/java"
            info "Created Java service: $service"
        fi
    done
    cd ..
    
    # Create Node.js service directories
    local node_services=(
        "haulage-analytics-service"
        "real-time-notification-service"
        "iot-integration-service"
        "mobile-api-gateway"
    )
    
    cd node-services
    for service in "${node_services[@]}"; do
        if [ ! -d "$service" ]; then
            mkdir -p "$service/src"
            mkdir -p "$service/tests"
            info "Created Node.js service: $service"
        fi
    done
    cd ..
    
    # Create frontend app directories
    local frontend_apps=(
        "customer-portal"
        "fleet-dashboard"
        "admin-panel"
        "mobile-app"
    )
    
    cd frontend-apps
    for app in "${frontend_apps[@]}"; do
        if [ ! -d "$app" ]; then
            mkdir -p "$app/src/components"
            mkdir -p "$app/src/pages"
            mkdir -p "$app/public"
            info "Created frontend app: $app"
        fi
    done
    cd ..
    
    log "Project structure created successfully!"
}

# Function to create configuration files
create_configuration_files() {
    log "Creating configuration files..."
    
    cd "$PROJECT_ROOT"
    
    # Create .env.template
    if [ ! -f ".env.template" ]; then
        cat > .env.template << EOF
# Haulage Logistics Environment Configuration Template
# Copy this file to .env and update with your specific values

# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/haulage_db
DATABASE_USERNAME=haulage_user
DATABASE_PASSWORD=haulage_password

# MongoDB Configuration (for analytics)
MONGODB_URL=mongodb://localhost:27017/haulage_analytics
MONGODB_USERNAME=haulage_analytics_user
MONGODB_PASSWORD=haulage_analytics_password

# Redis Configuration
REDIS_URL=redis://localhost:6379
REDIS_PASSWORD=

# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_GROUP_ID=haulage-logistics

# JWT Configuration
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=86400000

# External API Keys
GOOGLE_MAPS_API_KEY=your-google-maps-api-key
WEATHER_API_KEY=your-weather-api-key
FUEL_PRICE_API_KEY=your-fuel-price-api-key

# AWS Configuration (for production)
AWS_REGION=us-east-1
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
S3_BUCKET_NAME=haulage-logistics-bucket

# Notification Configuration
TWILIO_ACCOUNT_SID=your-twilio-sid
TWILIO_AUTH_TOKEN=your-twilio-token
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-email-password

# Development Configuration
LOG_LEVEL=debug
SPRING_PROFILES_ACTIVE=development
NODE_ENV=development
EOF
        info "Created .env.template"
    fi
    
    # Create .env for development (if it doesn't exist)
    if [ ! -f ".env" ]; then
        cp .env.template .env
        info "Created .env from template"
        warn "Please update .env with your specific configuration values"
    fi
    
    # Create .gitignore
    if [ ! -f ".gitignore" ]; then
        cat > .gitignore << EOF
# Environment files
.env
.env.local
.env.production

# Logs
logs/
*.log
npm-debug.log*
yarn-debug.log*
yarn-error.log*

# Runtime data
pids
*.pid
*.seed
*.pid.lock
.pids/

# Coverage directory used by tools like istanbul
coverage/
*.lcov

# nyc test coverage
.nyc_output

# Dependency directories
node_modules/
jspm_packages/

# Optional npm cache directory
.npm

# Optional REPL history
.node_repl_history

# Output of 'npm pack'
*.tgz

# Yarn Integrity file
.yarn-integrity

# Java
target/
*.jar
*.class
.mvn/wrapper/maven-wrapper.jar

# IDE
.idea/
.vscode/
*.swp
*.swo
*~

# OS
.DS_Store
Thumbs.db

# Docker
.docker/

# Temporary files
tmp/
temp/

# Build outputs
build/
dist/

# Database
data/
EOF
        info "Created .gitignore"
    fi
    
    log "Configuration files created successfully!"
}

# Function to initialize databases
initialize_databases() {
    log "Initializing databases..."
    
    cd "$PROJECT_ROOT"
    
    # Start infrastructure services
    if [ -f "docker-compose.yml" ]; then
        log "Starting database services..."
        docker-compose up -d postgres mongodb redis
        
        # Wait for databases to be ready
        log "Waiting for databases to be ready..."
        sleep 15
        
        # Initialize PostgreSQL database
        log "Initializing PostgreSQL database..."
        docker-compose exec -T postgres psql -U postgres -c "
            CREATE DATABASE haulage_db;
            CREATE USER haulage_user WITH PASSWORD 'haulage_password';
            GRANT ALL PRIVILEGES ON DATABASE haulage_db TO haulage_user;
        " || warn "PostgreSQL database may already exist"
        
        # Initialize MongoDB database
        log "Initializing MongoDB database..."
        docker-compose exec -T mongodb mongosh haulage_analytics --eval "
            db.createUser({
                user: 'haulage_analytics_user',
                pwd: 'haulage_analytics_password',
                roles: [{role: 'readWrite', db: 'haulage_analytics'}]
            })
        " || warn "MongoDB user may already exist"
        
        log "Databases initialized successfully!"
    else
        warn "docker-compose.yml not found. Skipping database initialization."
    fi
}

# Function to install dependencies
install_dependencies() {
    log "Installing dependencies..."
    
    cd "$PROJECT_ROOT"
    
    # Install Java dependencies
    if [ -d "java-services" ] && [ -f "pom.xml" ]; then
        log "Installing Java dependencies..."
        mvn clean install -DskipTests
    fi
    
    # Install Node.js dependencies for each service
    if [ -d "node-services" ]; then
        cd node-services
        for service_dir in */; do
            if [ -f "${service_dir}package.json" ]; then
                log "Installing dependencies for ${service_dir%/}..."
                cd "$service_dir"
                npm install
                cd ..
            fi
        done
        cd ..
    fi
    
    # Install frontend dependencies
    if [ -d "frontend-apps" ]; then
        cd frontend-apps
        for app_dir in */; do
            if [ -f "${app_dir}package.json" ]; then
                log "Installing dependencies for ${app_dir%/}..."
                cd "$app_dir"
                npm install
                cd ..
            fi
        done
        cd ..
    fi
    
    log "Dependencies installed successfully!"
}

# Function to create additional scripts
create_additional_scripts() {
    log "Creating additional utility scripts..."
    
    cd "$PROJECT_ROOT/scripts"
    
    # Create stop script
    if [ ! -f "stop.sh" ]; then
        cat > stop.sh << 'EOF'
#!/bin/bash

# Stop all Haulage Logistics services

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
PID_DIR="$PROJECT_ROOT/.pids"

echo "Stopping all Haulage Logistics services..."

# Stop all services using PID files
if [ -d "$PID_DIR" ]; then
    for pid_file in "$PID_DIR"/*.pid; do
        if [ -f "$pid_file" ]; then
            service_name=$(basename "$pid_file" .pid)
            pid=$(cat "$pid_file")
            if kill -0 "$pid" 2>/dev/null; then
                echo "Stopping $service_name (PID: $pid)..."
                kill "$pid"
                rm "$pid_file"
            else
                echo "Service $service_name was not running"
                rm "$pid_file"
            fi
        fi
    done
fi

# Stop Docker services
cd "$PROJECT_ROOT"
if [ -f "docker-compose.yml" ]; then
    echo "Stopping infrastructure services..."
    docker-compose down
fi

echo "All services stopped!"
EOF
        chmod +x stop.sh
        info "Created stop.sh script"
    fi
    
    # Create test script
    if [ ! -f "test.sh" ]; then
        cat > test.sh << 'EOF'
#!/bin/bash

# Run all tests for Haulage Logistics services

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "Running Haulage Logistics tests..."

cd "$PROJECT_ROOT"

# Run Java tests
if [ -f "pom.xml" ]; then
    echo "Running Java tests..."
    mvn test
fi

# Run Node.js tests
if [ -d "node-services" ]; then
    cd node-services
    for service_dir in */; do
        if [ -f "${service_dir}package.json" ]; then
            echo "Running tests for ${service_dir%/}..."
            cd "$service_dir"
            npm test || true
            cd ..
        fi
    done
    cd ..
fi

# Run frontend tests
if [ -d "frontend-apps" ]; then
    cd frontend-apps
    for app_dir in */; do
        if [ -f "${app_dir}package.json" ]; then
            echo "Running tests for ${app_dir%/}..."
            cd "$app_dir"
            npm test -- --coverage --watchAll=false || true
            cd ..
        fi
    done
    cd ..
fi

echo "All tests completed!"
EOF
        chmod +x test.sh
        info "Created test.sh script"
    fi
    
    # Create build script
    if [ ! -f "build.sh" ]; then
        cat > build.sh << 'EOF'
#!/bin/bash

# Build all Haulage Logistics services and applications

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "Building Haulage Logistics services..."

cd "$PROJECT_ROOT"

# Build Java services
if [ -f "pom.xml" ]; then
    echo "Building Java services..."
    mvn clean package -DskipTests
fi

# Build Node.js services
if [ -d "node-services" ]; then
    cd node-services
    for service_dir in */; do
        if [ -f "${service_dir}package.json" ]; then
            echo "Building ${service_dir%/}..."
            cd "$service_dir"
            npm run build || true
            cd ..
        fi
    done
    cd ..
fi

# Build frontend applications
if [ -d "frontend-apps" ]; then
    cd frontend-apps
    for app_dir in */; do
        if [ -f "${app_dir}package.json" ]; then
            echo "Building ${app_dir%/}..."
            cd "$app_dir"
            npm run build || true
            cd ..
        fi
    done
    cd ..
fi

echo "Build completed!"
EOF
        chmod +x build.sh
        info "Created build.sh script"
    fi
    
    log "Additional scripts created successfully!"
}

# Function to display setup summary
show_setup_summary() {
    log "Setup Summary:"
    echo ""
    
    info "Project Structure:"
    echo "  ✓ Java services directory created"
    echo "  ✓ Node.js services directory created"
    echo "  ✓ Frontend applications directory created"
    echo "  ✓ Configuration files created"
    echo "  ✓ Utility scripts created"
    echo ""
    
    info "Services to be implemented:"
    echo "  Java Services (15):"
    echo "    - haulage-booking-service"
    echo "    - fleet-management-service"
    echo "    - route-optimization-service"
    echo "    - pricing-engine-service"
    echo "    - cargo-management-service"
    echo "    - driver-management-service"
    echo "    - maintenance-scheduling-service"
    echo "    - fuel-monitoring-service"
    echo "    - insurance-claims-service"
    echo "    - compliance-monitoring-service"
    echo "    - contract-management-service"
    echo "    - invoice-billing-service"
    echo "    - vehicle-tracking-service"
    echo "    - load-planning-service"
    echo "    - customer-portal-api"
    echo ""
    echo "  Node.js Services (4):"
    echo "    - haulage-analytics-service"
    echo "    - real-time-notification-service"
    echo "    - iot-integration-service"
    echo "    - mobile-api-gateway"
    echo ""
    echo "  Frontend Applications (4):"
    echo "    - customer-portal"
    echo "    - fleet-dashboard"
    echo "    - admin-panel"
    echo "    - mobile-app"
    echo ""
    
    info "Next Steps:"
    echo "  1. Update .env file with your configuration"
    echo "  2. Run './scripts/dev.sh' to start development environment"
    echo "  3. Access API documentation at: http://localhost:8080/swagger-ui.html"
    echo "  4. Access frontend applications at: http://localhost:3010-3013"
    echo ""
    
    info "Useful Commands:"
    echo "  Start development: ./scripts/dev.sh"
    echo "  Stop all services: ./scripts/stop.sh"
    echo "  Run tests: ./scripts/test.sh"
    echo "  Build all: ./scripts/build.sh"
    echo ""
    
    log "Haulage Logistics setup completed successfully!"
    log "Ready for development!"
}

# Main execution
main() {
    log "Starting Haulage Logistics Domain Setup..."
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --skip-deps)
                SKIP_DEPS=true
                shift
                ;;
            --skip-db)
                SKIP_DB=true
                shift
                ;;
            --help|-h)
                echo "Usage: $0 [OPTIONS]"
                echo ""
                echo "Options:"
                echo "  --skip-deps       Skip dependency installation"
                echo "  --skip-db         Skip database initialization"
                echo "  --help, -h        Show this help message"
                exit 0
                ;;
            *)
                error "Unknown option: $1"
                exit 1
                ;;
        esac
    done
    
    # Run setup steps
    check_system_requirements
    check_prerequisites
    create_project_structure
    create_configuration_files
    create_additional_scripts
    
    if [ "$SKIP_DEPS" != "true" ]; then
        install_dependencies
    else
        info "Skipping dependency installation (--skip-deps)"
    fi
    
    if [ "$SKIP_DB" != "true" ]; then
        initialize_databases
    else
        info "Skipping database initialization (--skip-db)"
    fi
    
    show_setup_summary
}

# Run main function
main "$@"