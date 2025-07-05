# PowerShell script to set up code analysis for Exalt-Application-Limited

Write-Host "🔍 Setting up Code Analysis & Security Scanning for Exalt-Application-Limited" -ForegroundColor Cyan
Write-Host "====================================================================" -ForegroundColor Cyan

# Organization Setup
$ORG_NAME = "Exalt-Application-Limited"
$BASE_DIR = Get-Location

Write-Host "📂 Looking for Maven projects to add SonarCloud plugin..." -ForegroundColor Green

# Find all pom.xml files
$POM_FILES = Get-ChildItem -Path . -Filter "pom.xml" -Recurse -ErrorAction SilentlyContinue | Where-Object { 
    $_.FullName -notlike "*node_modules*" -and $_.FullName -notlike "*target*" 
}

# Add SonarCloud Maven plugin to each pom.xml
foreach ($POM_FILE in $POM_FILES) {
    $SERVICE_DIR = Split-Path -Parent $POM_FILE.FullName
    $SERVICE_NAME = Split-Path -Leaf $SERVICE_DIR
    Write-Host "🔧 Adding SonarCloud plugin to $SERVICE_NAME" -ForegroundColor Yellow
    
    $XML = [xml](Get-Content $POM_FILE.FullName)
    
    # Check if build section exists
    $buildNode = $XML.project.build
    if ($null -eq $buildNode) {
        $buildNode = $XML.CreateElement("build")
        $XML.project.AppendChild($buildNode)
    }
    
    # Check if plugins section exists
    $pluginsNode = $buildNode.plugins
    if ($null -eq $pluginsNode) {
        $pluginsNode = $XML.CreateElement("plugins")
        $buildNode.AppendChild($pluginsNode)
    }
    
    # Check if SonarCloud plugin already exists
    $sonarPlugin = $pluginsNode.plugin | Where-Object { $_.groupId -eq "org.sonarsource.scanner.maven" }
    if ($null -eq $sonarPlugin) {
        $pluginNode = $XML.CreateElement("plugin")
        
        $groupId = $XML.CreateElement("groupId")
        $groupId.InnerText = "org.sonarsource.scanner.maven"
        $pluginNode.AppendChild($groupId)
        
        $artifactId = $XML.CreateElement("artifactId")
        $artifactId.InnerText = "sonar-maven-plugin"
        $pluginNode.AppendChild($artifactId)
        
        $version = $XML.CreateElement("version")
        $version.InnerText = "3.9.1.2184"
        $pluginNode.AppendChild($version)
        
        $pluginsNode.AppendChild($pluginNode)
    }
    
    $XML.Save($POM_FILE.FullName)
    
    # Create sonar-project.properties file if it doesn't exist
    $SONAR_PROPS_FILE = Join-Path -Path $SERVICE_DIR -ChildPath "sonar-project.properties"
    if (-Not (Test-Path $SONAR_PROPS_FILE)) {
        Write-Host "📄 Creating sonar-project.properties for $SERVICE_NAME" -ForegroundColor Yellow
        
        $SONAR_PROPS_CONTENT = @"
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
"@
        Set-Content -Path $SONAR_PROPS_FILE -Value $SONAR_PROPS_CONTENT
    }
}

# Find all package.json files for Node.js projects
Write-Host "📂 Looking for Node.js projects to add SonarCloud configuration..." -ForegroundColor Green
$PACKAGE_FILES = Get-ChildItem -Path . -Filter "package.json" -Recurse -ErrorAction SilentlyContinue | Where-Object { 
    $_.FullName -notlike "*node_modules*" 
}

foreach ($PACKAGE_FILE in $PACKAGE_FILES) {
    $SERVICE_DIR = Split-Path -Parent $PACKAGE_FILE.FullName
    $SERVICE_NAME = Split-Path -Leaf $SERVICE_DIR
    Write-Host "🔧 Adding SonarCloud configuration to $SERVICE_NAME" -ForegroundColor Yellow
    
    # Create sonar-project.properties file if it doesn't exist
    $SONAR_PROPS_FILE = Join-Path -Path $SERVICE_DIR -ChildPath "sonar-project.properties"
    if (-Not (Test-Path $SONAR_PROPS_FILE)) {
        Write-Host "📄 Creating sonar-project.properties for $SERVICE_NAME" -ForegroundColor Yellow
        
        $SONAR_PROPS_CONTENT = @"
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
"@
        Set-Content -Path $SONAR_PROPS_FILE -Value $SONAR_PROPS_CONTENT
    }
    
    # Add test coverage script to package.json if it doesn't exist
    $packageJson = Get-Content -Path $PACKAGE_FILE.FullName -Raw | ConvertFrom-Json
    if (-Not $packageJson.scripts.PSObject.Properties.Name.Contains("test:coverage")) {
        $packageJson.scripts | Add-Member -Name "test:coverage" -Value "jest --coverage" -MemberType NoteProperty
        $packageJson | ConvertTo-Json -Depth 10 | Set-Content -Path $PACKAGE_FILE.FullName
    }
}

Write-Host "✅ Code Analysis setup complete!" -ForegroundColor Green
Write-Host ""
Write-Host "⚠️ IMPORTANT: Before running the workflow, make sure to set these secrets in your GitHub repository:" -ForegroundColor Red
Write-Host "  • SONAR_TOKEN - Get this from SonarCloud after organization setup"
Write-Host "  • SNYK_TOKEN - Get this from Snyk after account setup"
Write-Host "  • DEEPSOURCE_DSN - Get this from DeepSource after repository setup"
Write-Host ""
Write-Host "📋 Next steps:" -ForegroundColor Cyan
Write-Host "  1. Create 'Exalt-Application-Limited' organization in SonarCloud"
Write-Host "  2. Link SonarCloud to your GitHub organization"
Write-Host "  3. Install the SonarCloud, Snyk, and DeepSource GitHub Apps"
Write-Host "  4. Add the required secrets to your GitHub repository"
Write-Host "  5. Push the changes to trigger the workflow"
