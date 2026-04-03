#!/bin/bash

# Kafka Home Directory
KAFKA_HOME="/home/shiva/ApacheKafka/kafka_2.13-3.9.1"
KAFKA_BIN="$KAFKA_HOME/bin"
KAFKA_CONFIG="$KAFKA_HOME/config"

# Topic Name (as configured in application.yml)
TOPIC_NAME="booking-events"
BOOTSTRAP_SERVER="localhost:9092"
BOOTSTRAP_SERVERS="localhost:9092,localhost:9093"

# ============================================
# SINGLE BROKER COMMANDS
# ============================================

# Start Zookeeper
$KAFKA_BIN/zookeeper-server-start.sh $KAFKA_CONFIG/zookeeper.properties

# Start Kafka Server
$KAFKA_BIN/kafka-server-start.sh $KAFKA_CONFIG/server.properties

# Stop Kafka Server
$KAFKA_BIN/kafka-server-stop.sh

# Stop Zookeeper
$KAFKA_BIN/zookeeper-server-stop.sh

# ============================================
# MULTI-BROKER SETUP (Already Done)
# ============================================

# Copy server.properties for broker 1 and 2
# cp $KAFKA_CONFIG/server.properties $KAFKA_CONFIG/server-1.properties
# cp $KAFKA_CONFIG/server.properties $KAFKA_CONFIG/server-2.properties

# Edit configs:
# server-1.properties: broker.id=1, listeners=PLAINTEXT://:9092, log.dirs=/tmp/kafka-logs-1
# server-2.properties: broker.id=2, listeners=PLAINTEXT://:9093, log.dirs=/tmp/kafka-logs-2

# Start both brokers:
# $KAFKA_BIN/kafka-server-start.sh $KAFKA_CONFIG/server-1.properties
# $KAFKA_BIN/kafka-server-start.sh $KAFKA_CONFIG/server-2.properties

# ============================================
# TOPIC COMMANDS
# ============================================

# Create topic with replication-factor 2
$KAFKA_BIN/kafka-topics.sh --create \
  --topic $TOPIC_NAME \
  --bootstrap-server $BOOTSTRAP_SERVER \
  --partitions 3 \
  --replication-factor 2

# List all topics
$KAFKA_BIN/kafka-topics.sh --list --bootstrap-server $BOOTSTRAP_SERVER

# Describe topic (shows partition info & replication)
$KAFKA_BIN/kafka-topics.sh --describe \
  --topic $TOPIC_NAME \
  --bootstrap-server $BOOTSTRAP_SERVER

# Delete topic
$KAFKA_BIN/kafka-topics.sh --delete \
  --topic $TOPIC_NAME \
  --bootstrap-server $BOOTSTRAP_SERVER

# ============================================
# PRODUCER & CONSUMER
# ============================================

# Start Console Producer
$KAFKA_BIN/kafka-console-producer.sh \
  --topic $TOPIC_NAME \
  --bootstrap-server $BOOTSTRAP_SERVER

# Start Console Consumer (from beginning)
$KAFKA_BIN/kafka-console-consumer.sh \
  --topic $TOPIC_NAME \
  --from-beginning \
  --bootstrap-server $BOOTSTRAP_SERVER

# Start Console Consumer with group
$KAFKA_BIN/kafka-console-consumer.sh \
  --topic $TOPIC_NAME \
  --group booking-consumer-group \
  --bootstrap-server $BOOTSTRAP_SERVER

# ============================================
# CONSUMER GROUPS
# ============================================

# List all consumer groups
$KAFKA_BIN/kafka-consumer-groups.sh --list --bootstrap-server $BOOTSTRAP_SERVER

# Describe consumer group (show offsets & lag)
$KAFKA_BIN/kafka-consumer-groups.sh \
  --group booking-consumer-group \
  --describe \
  --bootstrap-server $BOOTSTRAP_SERVER

# ============================================
# CLEANUP
# ============================================

# Stop all brokers and zookeeper
$KAFKA_BIN/kafka-server-stop.sh
$KAFKA_BIN/zookeeper-server-stop.sh

# Clean up logs and data
rm -rf $KAFKA_HOME/logs
rm -rf /tmp/kafka-logs
rm -rf /tmp/kafka-logs-1
rm -rf /tmp/kafka-logs-2
rm -rf /tmp/zookeeper
rm -rf /tmp/kafka-*
