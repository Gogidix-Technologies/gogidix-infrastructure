#!/bin/sh

# Warehouse Analytics Service Health Check Script
set -e

# Configuration
HEALTH_URL="http://localhost:${SERVER_PORT:-8083}/warehouse-analytics/actuator/health"
TIMEOUT=10
MAX_RETRIES=3

echo "Performing health check..."

# Function to check service health
check_health() {
    local attempt=1
    while [ $attempt -le $MAX_RETRIES ]; do
        echo "Health check attempt $attempt/$MAX_RETRIES"
        
        # Use curl if available, otherwise use wget
        if command -v curl >/dev/null 2>&1; then
            response=$(curl -s -m $TIMEOUT -w "%{http_code}" -o /tmp/health_response "$HEALTH_URL" 2>/dev/null || echo "000")
        elif command -v wget >/dev/null 2>&1; then
            if wget -q -T $TIMEOUT -O /tmp/health_response "$HEALTH_URL" 2>/dev/null; then
                response="200"
            else
                response="000"
            fi
        else
            echo "ERROR: Neither curl nor wget is available for health check"
            return 1
        fi
        
        # Check HTTP status code
        if [ "$response" = "200" ]; then
            echo "Service is healthy (HTTP 200)"
            
            # Parse health response if available
            if [ -f /tmp/health_response ]; then
                if grep -q '"status":"UP"' /tmp/health_response 2>/dev/null; then
                    echo "Health status: UP"
                    rm -f /tmp/health_response
                    return 0
                elif grep -q '"status":"DOWN"' /tmp/health_response 2>/dev/null; then
                    echo "Health status: DOWN"
                    cat /tmp/health_response
                    rm -f /tmp/health_response
                    return 1
                else
                    echo "Health status: UNKNOWN"
                    cat /tmp/health_response
                    rm -f /tmp/health_response
                    return 1
                fi
            else
                echo "Health endpoint responded with 200"
                return 0
            fi
        else
            echo "Health check failed with HTTP status: $response"
        fi
        
        attempt=$((attempt + 1))
        if [ $attempt -le $MAX_RETRIES ]; then
            echo "Retrying in 2 seconds..."
            sleep 2
        fi
    done
    
    return 1
}

# Additional checks
check_disk_space() {
    # Check if log directory has enough space (at least 100MB)
    if [ -d "/app/logs" ]; then
        available=$(df /app/logs 2>/dev/null | tail -1 | awk '{print $4}' || echo "0")
        if [ "$available" -lt 102400 ]; then  # 100MB in KB
            echo "WARNING: Low disk space in logs directory: ${available}KB available"
        fi
    fi
}

check_memory() {
    # Check if system has enough free memory
    if [ -f "/proc/meminfo" ]; then
        available=$(grep MemAvailable /proc/meminfo 2>/dev/null | awk '{print $2}' || echo "0")
        if [ "$available" -lt 102400 ]; then  # 100MB in KB
            echo "WARNING: Low available memory: ${available}KB"
        fi
    fi
}

check_java_process() {
    # Check if Java process is running
    if ! pgrep -f "warehouse-analytics.jar" >/dev/null 2>&1; then
        echo "ERROR: Java process not found"
        return 1
    fi
    echo "Java process is running"
    return 0
}

# Perform all checks
echo "=== Warehouse Analytics Service Health Check ==="

# Basic Java process check
if ! check_java_process; then
    echo "FAIL: Java process check failed"
    exit 1
fi

# HTTP health endpoint check
if ! check_health; then
    echo "FAIL: HTTP health check failed"
    exit 1
fi

# Additional system checks (non-fatal)
check_disk_space
check_memory

echo "=== Health Check Completed Successfully ==="
exit 0