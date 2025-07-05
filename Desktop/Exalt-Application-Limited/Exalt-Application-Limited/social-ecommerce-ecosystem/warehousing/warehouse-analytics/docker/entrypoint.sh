#!/bin/sh

# Warehouse Analytics Service Entrypoint Script
set -e

echo "Starting Warehouse Analytics Service..."
echo "Profile: ${SPRING_PROFILES_ACTIVE:-production}"
echo "Port: ${SERVER_PORT:-8083}"

# Wait for dependencies to be ready
echo "Checking dependencies..."

# Check PostgreSQL
if [ ! -z "$DB_HOST" ]; then
    echo "Waiting for PostgreSQL at $DB_HOST:${DB_PORT:-5432}..."
    timeout=60
    while ! nc -z "$DB_HOST" "${DB_PORT:-5432}" 2>/dev/null; do
        timeout=$((timeout - 1))
        if [ $timeout -eq 0 ]; then
            echo "ERROR: PostgreSQL is not available after 60 seconds"
            exit 1
        fi
        sleep 1
    done
    echo "PostgreSQL is ready"
fi

# Check Redis
if [ ! -z "$REDIS_HOST" ]; then
    echo "Waiting for Redis at $REDIS_HOST:${REDIS_PORT:-6379}..."
    timeout=30
    while ! nc -z "$REDIS_HOST" "${REDIS_PORT:-6379}" 2>/dev/null; do
        timeout=$((timeout - 1))
        if [ $timeout -eq 0 ]; then
            echo "WARNING: Redis is not available after 30 seconds, continuing..."
            break
        fi
        sleep 1
    done
    if nc -z "$REDIS_HOST" "${REDIS_PORT:-6379}" 2>/dev/null; then
        echo "Redis is ready"
    fi
fi

# Check Elasticsearch
if [ ! -z "$ELASTICSEARCH_HOST" ]; then
    echo "Waiting for Elasticsearch at $ELASTICSEARCH_HOST:${ELASTICSEARCH_PORT:-9200}..."
    timeout=30
    while ! nc -z "$ELASTICSEARCH_HOST" "${ELASTICSEARCH_PORT:-9200}" 2>/dev/null; do
        timeout=$((timeout - 1))
        if [ $timeout -eq 0 ]; then
            echo "WARNING: Elasticsearch is not available after 30 seconds, continuing..."
            break
        fi
        sleep 1
    done
    if nc -z "$ELASTICSEARCH_HOST" "${ELASTICSEARCH_PORT:-9200}" 2>/dev/null; then
        echo "Elasticsearch is ready"
    fi
fi

# Check Kafka
if [ ! -z "$KAFKA_SERVERS" ]; then
    echo "Checking Kafka connectivity..."
    # Extract first Kafka server for basic connectivity check
    KAFKA_HOST=$(echo "$KAFKA_SERVERS" | cut -d',' -f1 | cut -d':' -f1)
    KAFKA_PORT=$(echo "$KAFKA_SERVERS" | cut -d',' -f1 | cut -d':' -f2)
    timeout=30
    while ! nc -z "$KAFKA_HOST" "${KAFKA_PORT:-9092}" 2>/dev/null; do
        timeout=$((timeout - 1))
        if [ $timeout -eq 0 ]; then
            echo "WARNING: Kafka is not available after 30 seconds, continuing..."
            break
        fi
        sleep 1
    done
    if nc -z "$KAFKA_HOST" "${KAFKA_PORT:-9092}" 2>/dev/null; then
        echo "Kafka is ready"
    fi
fi

# Set up Java options
JAVA_OPTS="${JAVA_OPTS:-} -Djava.security.egd=file:/dev/./urandom"
JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-production}"
JAVA_OPTS="$JAVA_OPTS -Dserver.port=${SERVER_PORT:-8083}"

# Add JVM options for containerized environment
JAVA_OPTS="$JAVA_OPTS -XX:+UseContainerSupport"
JAVA_OPTS="$JAVA_OPTS -XX:MaxRAMPercentage=75.0"
JAVA_OPTS="$JAVA_OPTS -XX:+UseG1GC"
JAVA_OPTS="$JAVA_OPTS -XX:+UseStringDeduplication"

# Add debugging options if enabled
if [ "$DEBUG_MODE" = "true" ]; then
    echo "Debug mode enabled"
    JAVA_OPTS="$JAVA_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
fi

# Add JFR options if enabled
if [ "$JFR_ENABLED" = "true" ]; then
    echo "Java Flight Recorder enabled"
    JAVA_OPTS="$JAVA_OPTS -XX:+FlightRecorder"
    JAVA_OPTS="$JAVA_OPTS -XX:StartFlightRecording=duration=60s,filename=/app/logs/warehouse-analytics.jfr"
fi

# Export final JAVA_OPTS
export JAVA_OPTS

echo "Starting application with JAVA_OPTS: $JAVA_OPTS"

# Start the application
exec java $JAVA_OPTS -jar warehouse-analytics.jar