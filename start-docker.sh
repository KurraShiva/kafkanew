#!/bin/bash

# Kafka Real-time Project - Docker Compose Startup Script
# Usage: ./start-docker.sh

set -e

echo "=========================================="
echo "Kafka Real-time Project - Docker Setup"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Build services first
build_services() {
    echo -e "${YELLOW}Building Docker images...${NC}"
    
    echo "Building producer-service..."
    cd producer-service
    mvn clean package -DskipTests
    cd ..
    
    echo "Building consumer-service..."
    cd consumer-service
    mvn clean package -DskipTests
    cd ..
    
    echo -e "${GREEN}✓ Maven builds complete${NC}"
}

# Start all services
start_services() {
    echo -e "${YELLOW}Starting all services with Docker Compose...${NC}"
    docker-compose -f docker-compose-fullstack.yml up -d
    echo -e "${GREEN}✓ Services started${NC}"
}

# Wait for services to be healthy
wait_for_services() {
    echo -e "${YELLOW}Waiting for services to be healthy...${NC}"
    
    echo "Checking Kafka..."
    until docker exec kafka-b0 kafka-broker-api-versions --bootstrap-server localhost:9092 > /dev/null 2>&1; do
        echo "Waiting for Kafka..."
        sleep 5
    done
    echo -e "${GREEN}✓ Kafka is ready${NC}"
    
    echo "Checking MySQL..."
    until docker exec kafka-mysql mysqladmin ping -h localhost --silent; do
        echo "Waiting for MySQL..."
        sleep 5
    done
    echo -e "${GREEN}✓ MySQL is ready${NC}"
    
    echo "Creating topics..."
    docker exec kafka-b0 kafka-topics --create \
        --topic booking-events \
        --bootstrap-server localhost:9092 \
        --partitions 3 \
        --replication-factor 2 \
        --if-not-exists || true
    echo -e "${GREEN}✓ Topics created${NC}"
}

# Stop services
stop_services() {
    echo -e "${YELLOW}Stopping all services...${NC}"
    docker-compose -f docker-compose-fullstack.yml down
    echo -e "${GREEN}✓ Services stopped${NC}"
}

# Show logs
show_logs() {
    echo -e "${YELLOW}Showing logs...${NC}"
    docker-compose -f docker-compose-fullstack.yml logs -f
}

# Show status
show_status() {
    echo -e "${YELLOW}Service status:${NC}"
    docker-compose -f docker-compose-fullstack.yml ps
}

# Main execution
case "${1:-start}" in
    start)
        build_services
        start_services
        wait_for_services
        show_status
        echo -e "${GREEN}=========================================="
        echo "All services are running!"
        echo "=========================================="
        echo "Producer API: http://localhost:8081/api"
        echo "Consumer API: http://localhost:8082/api"
        echo "Frontend:     http://localhost:5173"
        echo "Kafka UI:     http://localhost:8089"
        echo "MySQL:        localhost:3306"
        echo "==========================================${NC}"
        ;;
    stop)
        stop_services
        ;;
    restart)
        stop_services
        start
        ;;
    logs)
        show_logs
        ;;
    status)
        show_status
        ;;
    build)
        build_services
        ;;
    *)
        echo "Usage: $0 {start|stop|restart|logs|status|build}"
        exit 1
        ;;
esac
