# Setup Documentation

This section contains all documentation related to setting up and configuring the Haulage Logistics Domain for development and production environments.

## 📋 Quick Reference

| Topic | Document | Description |
|-------|----------|-------------|
| **Development Setup** | [development.md](development.md) | Complete local development environment setup |
| **Contributing Guide** | [contributing.md](contributing.md) | Code standards and contribution process |
| **Testing Guide** | [testing.md](testing.md) | Testing strategies and procedures |

## 🚀 Getting Started

### For New Developers
1. **Read the development setup guide**: [development.md](development.md)
2. **Follow the contributing guidelines**: [contributing.md](contributing.md)
3. **Run the setup script**: `./scripts/setup.sh`
4. **Start development**: `./scripts/dev.sh`

### For DevOps Engineers
1. **Review the development setup**: [development.md](development.md)
2. **Check deployment documentation**: [../operations/deployment.md](../operations/deployment.md)
3. **Review Kubernetes manifests**: [../../k8s/](../../k8s/)
4. **Understand testing procedures**: [testing.md](testing.md)

## 📚 Setup Topics

### Development Environment
- **Local Setup**: Java 17, Maven, Node.js 18, Docker environment configuration
- **IDE Configuration**: Recommended IntelliJ IDEA and VSCode settings
- **Development Tools**: Spring Boot DevTools, hot reloading, debugging setup
- **Database Setup**: PostgreSQL, MongoDB, Redis local development instances

### Code Quality
- **Java Standards**: Spring Boot best practices, clean code principles
- **JavaScript/TypeScript**: ESLint, Prettier, and code formatting rules
- **Testing Standards**: JUnit 5, Jest, integration testing approaches
- **Git Hooks**: Pre-commit hooks for quality checks and automated formatting

### Environment Configuration
- **Environment Variables**: Required and optional variables for all services
- **API Configuration**: External service integration and API keys
- **Feature Flags**: Development and testing feature toggles
- **Security Settings**: Development security considerations and JWT configuration

### Service Dependencies
- **Infrastructure Services**: PostgreSQL, MongoDB, Redis, Kafka, Zookeeper
- **External APIs**: Google Maps, weather services, fuel price APIs
- **Monitoring Stack**: Prometheus, Grafana, ELK stack for development
- **Testing Tools**: TestContainers, WireMock, integration test utilities

## 🛠️ Prerequisites

### System Requirements
- **Java**: OpenJDK 17 LTS or later
- **Maven**: Version 3.8 or later
- **Node.js**: Version 18 LTS or later
- **Docker**: Version 20.10 or later with Docker Compose
- **Git**: Version 2.20 or later
- **Operating System**: Windows 10+, macOS 10.15+, or Linux (Ubuntu 18.04+)

### Hardware Requirements
- **RAM**: Minimum 8GB, recommended 16GB (for running all services)
- **Storage**: At least 10GB free space for dependencies and data
- **Network**: Stable internet connection for package downloads and external APIs
- **CPU**: Multi-core processor recommended for parallel service execution

### Optional Tools
- **IntelliJ IDEA**: Recommended IDE for Java development
- **VSCode**: Recommended for Node.js and frontend development
- **Postman**: For API testing and development
- **Docker Desktop**: For easier container management
- **Kubernetes CLI (kubectl)**: For container orchestration testing

## 🔧 Quick Setup Commands

```bash
# Clone the repository
git clone <repository-url>
cd haulage-logistics

# Run automated setup
./scripts/setup.sh

# Start development environment
./scripts/dev.sh

# Alternative manual setup
# Start infrastructure
docker-compose up -d

# Build Java services
mvn clean install

# Install Node.js dependencies
cd node-services && npm install
cd ../frontend-apps && npm install

# Start individual services
mvn spring-boot:run  # For Java services
npm run dev          # For Node.js services
npm start           # For frontend apps
```

## 📖 Detailed Documentation

### [Development Environment Setup](development.md)
Comprehensive guide to setting up your local development environment including:
- System requirements and installation procedures
- Infrastructure service configuration (Docker Compose)
- Java service development with Spring Boot and Maven
- Node.js service development with Express and npm
- Frontend development with React and modern tooling
- Database configuration and migration procedures
- External API integration and testing
- Debugging and profiling tools setup

### [Contributing Guidelines](contributing.md)
Guidelines for contributing to the Haulage Logistics project including:
- Code style and formatting standards for Java and JavaScript
- Git workflow and branch naming conventions
- Pull request process and code review procedures
- Testing requirements and coverage expectations
- Documentation standards and maintenance
- Issue reporting and feature request procedures
- Integration with existing Gogidix ecosystem standards

### [Testing Procedures](testing.md)
Comprehensive testing guide covering:
- Unit testing with JUnit 5 (Java) and Jest (Node.js)
- Integration testing with TestContainers and test databases
- End-to-end testing with Selenium and Playwright
- Performance testing and load testing procedures
- Security testing and vulnerability assessment
- API testing with Postman and automated test suites
- Continuous integration and automated testing pipelines

## ⚠️ Common Setup Issues

### Java Development Issues
```bash
# Check Java version
java -version

# If version is below 17, update Java
# macOS: brew install openjdk@17
# Ubuntu: sudo apt install openjdk-17-jdk
# Windows: Download from https://adoptium.net/

# Set JAVA_HOME environment variable
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```

### Node.js Development Issues
```bash
# Check Node.js version
node --version

# If version is below 18, update Node.js
# Using nvm (recommended):
nvm install 18
nvm use 18

# Or download from https://nodejs.org/
```

### Docker Issues
```bash
# Check Docker status
docker info

# Start Docker daemon (Linux)
sudo systemctl start docker

# Check Docker Compose
docker-compose --version

# If Docker Compose not found:
# Linux: sudo apt install docker-compose
# macOS: brew install docker-compose
```

### Database Connection Issues
```bash
# Check if databases are running
docker-compose ps

# Restart database services
docker-compose restart postgres mongodb redis

# Check database logs
docker-compose logs postgres
docker-compose logs mongodb
```

### Permission Issues (Unix/Linux/macOS)
```bash
# Make scripts executable
chmod +x scripts/*.sh

# Fix Docker permissions (Linux)
sudo usermod -aG docker $USER
# Log out and back in after running this command

# Fix npm permissions
npm config set prefix ~/.npm-global
export PATH=~/.npm-global/bin:$PATH
```

## 🔍 Troubleshooting

### Build Failures
1. **Clear caches**: `mvn clean`, `npm cache clean --force`
2. **Check Java version**: Ensure Java 17+ is installed and set as default
3. **Check Node.js version**: Ensure Node.js 18+ is installed
4. **Check dependencies**: Ensure all required tools are installed
5. **Check environment variables**: Verify all required environment variables are set

### Service Startup Issues
1. **Port conflicts**: Check if ports 8080-8095, 3000-3013 are available
2. **Database connectivity**: Verify databases are running and accessible
3. **Environment configuration**: Check `.env` file exists and is properly configured
4. **Memory issues**: Ensure sufficient RAM is available (8GB+ recommended)

### API Integration Issues
1. **API keys**: Verify all external API keys are configured in `.env`
2. **Network connectivity**: Check internet connection for external API access
3. **Rate limiting**: Be aware of API rate limits during development
4. **Authentication**: Ensure JWT secrets and authentication configuration is correct

## 📞 Getting Help

### Resources
- **Documentation**: Check other docs in this directory
- **Scripts**: Use `./scripts/setup.sh --help` for script options
- **Logs**: Check service logs in `logs/` directory for detailed error messages
- **API Docs**: Access OpenAPI documentation at `/api-docs` when services are running

### Support Channels
- **Technical Issues**: Create GitHub issues with detailed error information
- **Development Questions**: Ask in team development channels
- **Setup Problems**: Contact DevOps team for infrastructure-related issues
- **Architecture Questions**: Reach out to technical architecture team

### Useful Resources
- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **Node.js Documentation**: https://nodejs.org/en/docs/
- **Docker Documentation**: https://docs.docker.com/
- **Maven Documentation**: https://maven.apache.org/guides/
- **React Documentation**: https://react.dev/

---

*Last Updated: 2025-07-02*  
*For the latest setup information, always check the main project README.md*