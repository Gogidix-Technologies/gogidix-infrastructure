#!/bin/bash

echo "🔍 Setting up Code Analysis & Security Scanning for Exalt-Application-Limited"
echo "===================================================================="

# Organization Setup
ORG_NAME="Exalt-Application-Limited"
BASE_DIR=$(pwd)

echo "📂 Looking for Maven projects to add SonarCloud plugin..."

# Find all pom.xml files
POM_FILES=$(find . -name "pom.xml" -not -path "*/node_modules/*" -not -path "*/target/*")

# Add SonarCloud Maven plugin to each pom.xml
for POM_FILE in $POM_FILES; do
    SERVICE_DIR=$(dirname "$POM_FILE")
    SERVICE_NAME=$(basename "$SERVICE_DIR")
    echo "🔧 Adding SonarCloud plugin to $SERVICE_NAME"
    
    # Check if the build plugins section exists
    if grep -q "<build>" "$POM_FILE"; then
        if grep -q "<plugins>" "$POM_FILE"; then
            # Check if SonarCloud plugin already exists
            if ! grep -q "sonar-maven-plugin" "$POM_FILE"; then
                # Add plugin to existing plugins section
                sed -i '/<plugins>/a \
                <plugin>\
                    <groupId>org.sonarsource.scanner.maven</groupId>\
                    <artifactId>sonar-maven-plugin</artifactId>\
                    <version>3.9.1.2184</version>\
                </plugin>' "$POM_FILE"
            fi
        else
            # Add plugins section with SonarCloud plugin
            sed -i '/<build>/a \
            <plugins>\
                <plugin>\
                    <groupId>org.sonarsource.scanner.maven</groupId>\
                    <artifactId>sonar-maven-plugin</artifactId>\
                    <version>3.9.1.2184</version>\
                </plugin>\
            </plugins>' "$POM_FILE"
        fi
    else
        # Add build section with plugins and SonarCloud plugin
        sed -i '/<\/project>/i \
        <build>\
            <plugins>\
                <plugin>\
                    <groupId>org.sonarsource.scanner.maven</groupId>\
                    <artifactId>sonar-maven-plugin</artifactId>\
                    <version>3.9.1.2184</version>\
                </plugin>\
            </plugins>\
        </build>' "$POM_FILE"
    fi
    
    # Create sonar-project.properties file if it doesn't exist
    if [ ! -f "$SERVICE_DIR/sonar-project.properties" ]; then
        echo "📄 Creating sonar-project.properties for $SERVICE_NAME"
        cat > "$SERVICE_DIR/sonar-project.properties" << EOF
# SonarCloud configuration
sonar.projectKey=${ORG_NAME}_${SERVICE_NAME}
sonar.organization=exalt-application-limited

# Service information
sonar.projectName=${SERVICE_NAME}
sonar.projectVersion=1.0

# Path is relative to the sonar-project.properties file
sonar.sources=src/main
sonar.tests=src/test
sonar.exclusions=node_modules/**,**/*.spec.ts,**/*.test.js,**/*.test.ts,**/*.test.jsx,**/*.test.tsx

# Encoding of the source code
sonar.sourceEncoding=UTF-8

# Java specific settings
sonar.java.binaries=target/classes
sonar.java.test.binaries=target/test-classes
sonar.java.libraries=target/dependency/*.jar
sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
EOF
    fi
done

# Find all package.json files for Node.js projects
echo "📂 Looking for Node.js projects to add SonarCloud configuration..."
PACKAGE_FILES=$(find . -name "package.json" -not -path "*/node_modules/*")

for PACKAGE_FILE in $PACKAGE_FILES; do
    SERVICE_DIR=$(dirname "$PACKAGE_FILE")
    SERVICE_NAME=$(basename "$SERVICE_DIR")
    echo "🔧 Adding SonarCloud configuration to $SERVICE_NAME"
    
    # Create sonar-project.properties file if it doesn't exist
    if [ ! -f "$SERVICE_DIR/sonar-project.properties" ]; then
        echo "📄 Creating sonar-project.properties for $SERVICE_NAME"
        cat > "$SERVICE_DIR/sonar-project.properties" << EOF
# SonarCloud configuration
sonar.projectKey=${ORG_NAME}_${SERVICE_NAME}
sonar.organization=exalt-application-limited

# Service information
sonar.projectName=${SERVICE_NAME}
sonar.projectVersion=1.0

# Path is relative to the sonar-project.properties file
sonar.sources=src
sonar.tests=test,__tests__
sonar.exclusions=node_modules/**,**/*.spec.ts,**/*.test.js,**/*.test.ts,**/*.test.jsx,**/*.test.tsx

# Encoding of the source code
sonar.sourceEncoding=UTF-8

# JavaScript/TypeScript settings
sonar.javascript.lcov.reportPaths=coverage/lcov.info
sonar.typescript.lcov.reportPaths=coverage/lcov.info
EOF
    fi
    
    # Add test coverage script to package.json if it doesn't exist
    if ! grep -q '"test:coverage"' "$PACKAGE_FILE"; then
        # Use temp file to modify json
        TMP_FILE=$(mktemp)
        jq '.scripts["test:coverage"] = "jest --coverage"' "$PACKAGE_FILE" > "$TMP_FILE"
        mv "$TMP_FILE" "$PACKAGE_FILE"
    fi
done

echo "✅ Code Analysis setup complete!"
echo ""
echo "⚠️ IMPORTANT: Before running the workflow, make sure to set these secrets in your GitHub repository:"
echo "  • SONAR_TOKEN - Get this from SonarCloud after organization setup"
echo "  • SNYK_TOKEN - Get this from Snyk after account setup"
echo "  • DEEPSOURCE_DSN - Get this from DeepSource after repository setup"
echo ""
echo "📋 Next steps:"
echo "  1. Create 'Exalt-Application-Limited' organization in SonarCloud"
echo "  2. Link SonarCloud to your GitHub organization"
echo "  3. Install the SonarCloud, Snyk, and DeepSource GitHub Apps"
echo "  4. Add the required secrets to your GitHub repository"
echo "  5. Push the changes to trigger the workflow"
