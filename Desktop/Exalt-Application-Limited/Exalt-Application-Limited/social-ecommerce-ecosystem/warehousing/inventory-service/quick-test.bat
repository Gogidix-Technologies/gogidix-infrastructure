@echo off
REM quick-test.bat - Quick test script for development
echo ==========================================
echo INVENTORY SERVICE - QUICK TEST
echo ==========================================

REM Set JAVA_HOME to use OpenJDK 17
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot
set MAVEN_HOME=C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\apache-maven-3.9.9
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

echo Using Java: %JAVA_HOME%
echo Using Maven: %MAVEN_HOME%
echo.

echo Running quick compilation and unit tests...
echo.

call "%MAVEN_HOME%\bin\mvn.cmd" clean test
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [FAILED] Tests failed!
    pause
    exit /b 1
)

echo.
echo [SUCCESS] All tests passed!
echo.
echo Run test-and-deploy.bat for full production testing
pause
