@echo off
REM test-and-deploy.bat - Complete testing and deployment script for fulfillment-service
echo ==========================================
echo FULFILLMENT SERVICE - COMPLETE TEST SUITE
echo ==========================================
echo Started at: %date% %time%
echo.

REM Set variables
set SERVICE_NAME=fulfillment-service
set SERVICE_PORT=8086
set BASE_DIR=%~dp0
set TEST_RESULTS_DIR=%BASE_DIR%test-results
set LOG_DIR=%BASE_DIR%logs

REM Create directories
if not exist "%TEST_RESULTS_DIR%" mkdir "%TEST_RESULTS_DIR%"
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

REM Colors for output (Windows 10+)
set GREEN=[92m
set RED=[91m
set YELLOW=[93m
set NC=[0m

echo %YELLOW%Step 1: Clean and Compile%NC%
echo ==========================================
call mvn clean compile > "%LOG_DIR%\compile.log" 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo %RED%[FAILED] Compilation failed!%NC%
    echo Check logs at: %LOG_DIR%\compile.log
    pause
    exit /b 1
)
echo %GREEN%[PASSED] Compilation successful%NC%
echo.

echo %YELLOW%Step 2: Run Unit Tests%NC%
echo ==========================================
call mvn test -Dtest="*Test" > "%LOG_DIR%\unit-tests.log" 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo %RED%[FAILED] Unit tests failed!%NC%
    echo Check logs at: %LOG_DIR%\unit-tests.log
    pause
    exit /b 1
)
echo %GREEN%[PASSED] Unit tests successful%NC%
echo.
