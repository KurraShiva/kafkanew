#!/bin/bash

# Kafka Real-time Project - Development Startup Script
# Usage: ./start-dev.sh

set -e

echo "=========================================="
echo "Kafka Real-time Project - Dev Startup"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if Kafka is running
check_kafka() {
    echo -e "${YELLOW}Checking Kafka connection...${NC}"
    if kafka-broker-api-versions --bootstrap-server localhost:9092 > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Kafka is running on port 9092${NC}"
        return 0
    else
        echo -e "${RED}✗ Kafka is not running on port 9092${NC}"
        return 1
    fi
}

# Create topics
create_topics() {
    echo -e "${YELLOW}Creating Kafka topics...${NC}"
    
    kafka-topics.sh --create \
        --topic booking-events \
        --bootstrap-server localhost:9092 \
        --partitions 3 \
        --replication-factor 2 \
        --if-not-exists
        
    echo -e "${GREEN}✓ Topics created successfully${NC}"
}

# Build services
build_services() {
    echo -e "${YELLOW}Building services...${NC}"
    
    echo "Building producer-service..."
    cd producer-service
    mvn clean package -DskipTests
    cd ..
    
    echo "Building consumer-service..."
    cd consumer-service
    mvn clean package -DskipTests
    cd ..
    
    echo -e "${GREEN}✓ Services built successfully${NC}"
}

# Start services
start_services() {
    echo -e "${YELLOW}Starting services...${NC}"
    
    # Start MySQL if not running
    if ! pgrep -x "mysqld" > /dev/null; then
        echo "Starting MySQL..."
        # Add your MySQL startup command here
    fi
    
    # Start producer-service
    echo "Starting producer-service on port 8081..."
    cd producer-service
    nohup java -jar target/*.jar > logs/producer.log 2>&1 &
    cd ..
    
    # Start consumer-service
    echo "Starting consumer-service on port 8082..."
    cd consumer-service
    nohup java -jar target/*.jar > logs/consumer.log 2>&1 &
    cd ..
    
    # Start frontend
    echo "Starting frontend on port 5173..."
    cd frontend
    nohup npm run dev > logs/frontend.log 2>&1 &
    cd ..
    
    echo -e "${GREEN}✓ All services started${NC}"
}

# Main execution
main() {
    # Check if Kafka is running
    if ! check_kafka; then
        echo -e "${RED}Please start Kafka cluster first:${NC}"
        echo "  Option 1: docker-compose -f docker-compose-kafka-cluster.yml up -d"
        echo "  Option 2: Start Kafka brokers manually"
        exit 1
    fi
    
    # Create topics
    create_topics
    
    # Build services
    build_services
    
    # Create logs directory
    mkdir -p logs
    
    # Start services
    start_services
    
    echo -e "${GREEN}=========================================="
    echo "Services are starting..."
    echo "=========================================="
    echo "Producer API: http://localhost:8081/api"
    echo "Consumer API: http://localhost:8082/api"
    echo "Frontend:     http://localhost:5173"
    echo "Kafka UI:     http://localhost:8089"
    echo "==========================================${NC}"
}

main "$@"
