# Kafka Real-time Event Streaming System

A **production-quality**, full-stack real-time application using Apache Kafka, Spring Boot (separate producer/consumer services), MySQL, and React + Vite with WebSocket live updates.

---

## Architecture

```
┌─────────────┐     ┌─────────────────┐     ┌─────────────────────────────────────┐
│  Frontend   │────▶│  Producer Svc   │────▶│          Kafka Cluster              │
│  (React +   │     │    (Port 8081)  │    │  booking (3p), dashboard (2p)      │
│   Vite)     │     └─────────────────┘    │  notification (3p)                 │
└─────────────┘                             └──────────────────┬──────────────────┘
       │ WebSocket                                                │
       ▼                                                          ▼
┌─────────────┐                   ┌─────────────────────────────────────────┐
│   Browser   │                   │         Consumer Service (8082)        │
│  (SockJS)   │                   │  booking │ analytics │ dashboard │    │
│             │                   │  group   │   group   │   group   │notif│
└─────────────┘                   └──────────┴────────────┴───────────┴─────┘
                                       │            │            │
                                       ▼            ▼            ▼
                                  ┌─────────┐ ┌─────────┐ ┌────────────┐
                                  │  MySQL  │ │ Kafka   │ │ WebSocket  │
                                  └─────────┘ │Producer│ └────────────┘
                                              └─────────┘
```

---

## Kafka Topics & Partitions

| Topic | Partitions | Replication Factor |
|-------|------------|-------------------|
| booking-events | 3 | 2 |
| dashboard-events | 2 | 2 |
| notification-events | 3 | 1 |

## Consumer Groups

| Service | Group ID | Purpose | Concurrency |
|---------|----------|---------|-------------|
| Booking Service | booking-consumer-group | Persists bookings to MySQL | 3 |
| Analytics Service | analytics-consumer-group | Calculates metrics, produces to dashboard | 2 |
| Dashboard Service | dashboard-consumer-group | Pushes real-time updates via WebSocket | 2 |
| Notification Service | notification-consumer-group | Handles alerts (simulated) | 3 |

---

## Project Structure

```
kafka-realtime/
├── producer-service/              ← Kafka Producer (Port 8081)
│   ├── src/main/java/com/realtime/producer/
│   │   ├── controller/            # BookingController, DashboardController, NotificationController
│   │   ├── dto/                   # BookingEvent, DashboardEvent, NotificationEvent
│   │   ├── service/               # BookingProducer, DashboardProducer, NotificationProducer
│   │   └── config/                # KafkaProducerConfig
│   └── src/main/resources/
│       └── application.yml
│
├── consumer-service/              ← Kafka Consumer (Port 8082)
│   ├── src/main/java/com/realtime/consumer/
│   │   ├── controller/            # BookingQueryController
│   │   ├── dto/                   # DTOs
│   │   ├── entity/                # Booking JPA entity
│   │   ├── repository/           # Spring Data JPA
│   │   ├── service/               # BookingConsumer, AnalyticsConsumer, DashboardConsumer, NotificationConsumer
│   │   └── config/                # KafkaConsumerConfig, WebSocketConfig
│   └── src/main/resources/
│       └── application.yml
│
├── frontend/                      ← React + Vite (Port 5173)
│   ├── src/
│   │   ├── services/api.js       # API client
│   │   ├── hooks/useWebSocket.js  # WebSocket hook (bookings, dashboard, notifications)
│   │   ├── pages/Dashboard.jsx   # Live dashboard
│   │   ├── pages/BookingForm.jsx  # Booking form
│   │   └── components/            # StatsCard, BookingTable, NotificationPanel
│   └── package.json
│
├── docker-compose-fullstack.yml   ← Full stack (optional)
├── README.md
└── .gitignore
```

---

## Quick Start — Local Development

### 1. Start MySQL
```bash
# On port 3306 with database: kafka_streaming_db
# username: root, password: Shiva@123
```

### 2. Start ZooKeeper
```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
```

### 3. Start Kafka Broker
```bash
bin/kafka-server-start.sh config/server.properties
```

### 4. Create Topics
```bash
# booking-events (3 partitions, rf=2)
bin/kafka-topics.sh --create \
  --topic booking-events \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 2

# dashboard-events (2 partitions, rf=2)
bin/kafka-topics.sh --create \
  --topic dashboard-events \
  --bootstrap-server localhost:9092 \
  --partitions 2 \
  --replication-factor 2

# notification-events (3 partitions, rf=1)
bin/kafka-topics.sh --create \
  --topic notification-events \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

# Verify
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```

### 5. Build & Run Services

```bash
# Producer Service
cd producer-service
mvn clean install
mvn spring-boot:run

# Consumer Service (new terminal)
cd consumer-service
mvn clean install
mvn spring-boot:run

# Frontend
cd frontend
npm install
npm run dev
```

---

## API Reference

### Producer Service (Port 8081)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/bookings | Create booking (async) |
| POST | /api/bookings/sync | Create booking (sync) |
| POST | /api/dashboard | Send dashboard event |
| POST | /api/dashboard/update | Send dashboard update |
| POST | /api/notifications | Send notification |
| GET | /api/bookings/health | Health check |
| GET | /api/dashboard/health | Health check |
| GET | /api/notifications/health | Health check |

### Consumer Service (Port 8082)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/bookings | Get all bookings |
| GET | /api/bookings/{id} | Get by ID |
| GET | /api/bookings/status/{status} | Get by status |
| GET | /api/bookings/customer/{name} | Get by customer |
| GET | /api/bookings/count | Get count |
| GET | /api/bookings/dashboard | Get dashboard stats |
| GET | /api/bookings/health | Health check |

### WebSocket Topics

| Topic | Description |
|-------|-------------|
| /topic/bookings | Live booking updates |
| /topic/dashboard | Real-time dashboard analytics |
| /topic/notifications | Alert notifications |

---

## Functional Flow

```
1. User submits booking from React Frontend
         │
         ▼
2. Producer → Kafka "booking-events" topic
         │
         ├─────────────────────┬─────────────────────┐
         ▼                     ▼                     ▼
3. BookingConsumer    AnalyticsConsumer      NotificationConsumer
   (MySQL)            (Calculate metrics)     (Log alerts)
         │                     │                     │
         │                    ▼                     │
         │            Kafka "dashboard-events"    │
         │                     │                     │
         │                     ▼                     │
         │            DashboardConsumer            │
         │            (WebSocket /topic/dashboard) │
         │                     │                     │
         └─────────────────────┴─────────────────────┘
                              │
                              ▼
4. React Frontend receives real-time updates
```

---

## Sample Request

```bash
curl -X POST http://localhost:8081/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "sportType": "Football",
    "venue": "Gachibowli Stadium, Hyderabad",
    "slotDateTime": "2026-04-01 10:00:00",
    "durationHours": 2,
    "amount": 1500,
    "message": "Need changing room"
  }'
```

---

## Key Kafka Features

| Feature | Configuration |
|---------|---------------|
| Partitions (booking) | 3 |
| Partitions (dashboard) | 2 |
| Partitions (notification) | 3 |
| Replication Factor | 2 (booking, dashboard), 1 (notification) |
| Consumer Groups | 4 (booking, analytics, dashboard, notification) |
| Idempotence | enable.idempotence=true |
| Acknowledgment | acks=all |
| Acknowledgment Mode | MANUAL_IMMEDIATE |

---

## Technology Stack

- **Backend**: Spring Boot 3.x, Spring Kafka, Spring WebSocket
- **Frontend**: React 18, Vite 5, SockJS, STOMP.js, react-hot-toast
- **Message Broker**: Apache Kafka (single broker)
- **Database**: MySQL 8
- **Build**: Maven, npm

---

## Troubleshooting

**Kafka connection refused?**
```bash
# Check Zookeeper first, then Kafka broker
# Verify bootstrap-servers: localhost:9092
```

**Consumer not receiving?**
```bash
# Verify topics exist
kafka-topics.sh --describe --bootstrap-server localhost:9092
```

**WebSocket not connecting?**
```bash
# Check consumer-service is running on port 8082
# Verify WebSocket endpoint: ws://localhost:8082/ws
```
