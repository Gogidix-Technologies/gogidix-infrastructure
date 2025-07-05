
echo %YELLOW%Step 3: Run Integration Tests%NC%
echo ==========================================
call "%MAVEN_HOME%\bin\mvn.cmd" test -Dtest="*IT" > "%LOG_DIR%\integration-tests.log" 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo %RED%[FAILED] Integration tests failed!%NC%
    echo Check logs at: %LOG_DIR%\integration-tests.log
    pause
    exit /b 1
)
echo %GREEN%[PASSED] Integration tests successful%NC%
echo.

echo %YELLOW%Step 4: Build JAR Package%NC%
echo ==========================================
call "%MAVEN_HOME%\bin\mvn.cmd" package -DskipTests > "%LOG_DIR%\package.log" 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo %RED%[FAILED] Package build failed!%NC%
    echo Check logs at: %LOG_DIR%\package.log
    pause
    exit /b 1
)
echo %GREEN%[PASSED] JAR package built successfully%NC%
echo.

echo %YELLOW%Step 5: Generate Test Reports%NC%
echo ==========================================
call "%MAVEN_HOME%\bin\mvn.cmd" surefire-report:report > "%LOG_DIR%\reports.log" 2>&1
echo %GREEN%[DONE] Test reports generated in target/site%NC%
echo.

echo %YELLOW%Step 6: Check Code Coverage%NC%
echo ==========================================
call "%MAVEN_HOME%\bin\mvn.cmd" jacoco:report > "%LOG_DIR%\coverage.log" 2>&1
echo %GREEN%[DONE] Coverage report generated%NC%
echo.

echo %YELLOW%Step 7: Verify Service Startup%NC%
echo ==========================================
echo Starting service for smoke test...
start /B java -jar target\%SERVICE_NAME%-1.0.0.jar --server.port=%SERVICE_PORT% > "%LOG_DIR%\startup.log" 2>&1

REM Wait for service to start
echo Waiting for service to start...
timeout /t 30 /nobreak > nul

REM Check if service is running
curl -s http://localhost:%SERVICE_PORT%/actuator/health > "%TEST_RESULTS_DIR%\health-check.json"
if %ERRORLEVEL% NEQ 0 (
    echo %RED%[FAILED] Service failed to start!%NC%
    taskkill /F /FI "WINDOWTITLE eq java*" > nul 2>&1
    pause
    exit /b 1
)
echo %GREEN%[PASSED] Service started successfully%NC%

REM Stop the service
echo Stopping service...
taskkill /F /FI "WINDOWTITLE eq java*" > nul 2>&1
echo.

echo ==========================================
echo %GREEN%ALL TESTS PASSED!%NC%
echo ==========================================
echo.
echo Test Summary:
echo - Compilation: PASSED
echo - Unit Tests: PASSED
echo - Integration Tests: PASSED
echo - Build: PASSED
echo - Smoke Test: PASSED
echo.
echo Artifacts:
echo - JAR: target\%SERVICE_NAME%-1.0.0.jar
echo - Test Reports: target\site\surefire-report.html
echo - Coverage: target\site\jacoco\index.html
echo - Logs: %LOG_DIR%\
echo.
echo %YELLOW%Ready for Production Deployment!%NC%
echo.
echo Next steps:
echo 1. Review test reports
echo 2. Commit changes: git add . && git commit -m "feat: %SERVICE_NAME% tested and production ready"
echo 3. Push to repository: git push origin main
echo.
pause
