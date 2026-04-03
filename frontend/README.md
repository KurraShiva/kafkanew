# Frontend - React + Vite Real-time Booking Dashboard

## Overview

This is the frontend module of the Kafka Real-time Event Streaming System. It provides a user interface for making venue/sport bookings with real-time payment processing and live updates via WebSocket.

---

## Tech Stack

- **Framework**: React 18 + Vite
- **Routing**: React Router v6
- **Styling**: CSS Modules / Plain CSS
- **Payment**: Stripe Elements (react-stripe-js)
- **Real-time**: WebSocket (SockJS + STOMP) with @stomp/stompjs
- **HTTP Client**: Axios

---

## Complete Flow

### 1. Booking Flow

```
┌─────────────┐     ┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  User fills │────▶│  Submit        │────▶│  Create Payment │────▶│  Stripe         │
│  Booking     │     │  Form          │     │  Intent         │     │  Checkout       │
│  Form        │     │  (POST /api/   │     │  (POST /api/    │     │  (Card Element) │
│             │     │   bookings)     │     │   payments/     │     │                 │
└─────────────┘     └─────────────────┘     │   create-       │     └────────┬────────┘
                                            │   payment-intent│              │
                                            └─────────────────┘              │
                                                         │                    │
                                                         ▼                    ▼
┌─────────────┐     ┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  WebSocket  │◀────│  Kafka          │◀────│  Consumer       │◀────│  Stripe         │
│  Updates    │     │  Events         │     │  Service        │     │  Webhook        │
│  (Real-time)│     │  (payment-      │     │  (Payment       │     │  Confirmation   │
│             │     │   events)       │     │   Confirmed)    │     │                 │
└─────────────┘     └─────────────────┘     └─────────────────┘     └─────────────────┘
```

### 2. Data Flow Details

1. **User Submission** → `BookingForm.jsx` sends POST to Producer Service (`/api/bookings`)
2. **Kafka Producer** → Producer Service publishes to `booking-events` topic
3. **Kafka Consumer** → Consumer Service (booking group) consumes and persists to MySQL
4. **Analytics Consumer** → Analytics consumer processes and publishes to `dashboard-events`
5. **Dashboard Consumer** → Dashboard consumer pushes to WebSocket `/topic/bookings`
6. **Frontend Updates** → `useWebSocket.js` receives and updates UI in real-time

### 3. Payment Flow

```
┌─────────────┐     ┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  User       │────▶│  Backend        │────▶│  Stripe API    │────▶│  Payment        │
│  Enters     │     │  Creates        │     │  Creates       │     │  Intent         │
│  Card        │     │  PaymentIntent │     │  PaymentIntent │     │  (clientSecret) │
│  Details     │     │                 │     │                │     │                 │
└─────────────┘     └─────────────────┘     └─────────────────┘     └────────┬────────┘
                                                                            │
                                                                            ▼
┌─────────────┐     ┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  User       │────▶│  Backend        │────▶│  Kafka          │────▶│  Frontend       │
│  Confirms   │     │  Confirms       │     │  Publishes      │     │  Updates        │
│  Payment    │     │  Payment        │     │  Payment Event  │     │  (WebSocket)    │
│             │     │  (POST /api/    │     │                 │     │                 │
│             │     │   payments/     │     │                 │     │                 │
│             │     │   confirm/{id}) │     │                 │     │                 │
└─────────────┘     └─────────────────┘     └─────────────────┘     └─────────────────┘
```

---

## Project Structure

```
frontend/
├── src/
│   ├── App.jsx                 # Main app with routes
│   ├── main.jsx               # Entry point
│   ├── services/
│   │   └── api.js             # Axios API client
│   ├── hooks/
│   │   └── useWebSocket.js    # WebSocket hook for real-time updates
│   ├── pages/
│   │   ├── Dashboard.jsx      # Main dashboard with live data
│   │   ├── BookingForm.jsx    # Booking form with Stripe payment
│   │   ├── PaymentSuccess.jsx # Payment success page
│   │   └── PaymentFailure.jsx # Payment failure page
│   └── components/
│       ├── BookingTable.jsx       # Real-time bookings table
│       ├── StatsCard.jsx          # Statistics cards
│       ├── NotificationPanel.jsx  # Real-time notifications
│       ├── KafkaInfo.jsx          # Kafka cluster info display
│       ├── ConnectionStatus.jsx   # WebSocket connection status
│       └── PaymentHistory.jsx    # Payment history table
├── index.html
├── package.json
└── vite.config.js
```

---

## API Endpoints

### Producer Service (Port 8081)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/bookings` | Create new booking |
| GET | `/api/bookings` | Get all bookings |
| GET | `/api/dashboard/stats` | Get dashboard statistics |
| POST | `/api/notifications/send` | Send notification |

### Consumer Service (Port 8082)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/bookings` | Query all bookings |
| GET | `/api/bookings/{id}` | Get booking by ID |
| POST | `/api/payments/create-payment-intent` | Create Stripe payment intent |
| POST | `/api/payments/confirm/{paymentIntentId}` | Confirm payment |
| GET | `/api/payments/publishable-key` | Get Stripe publishable key |

---

## WebSocket Topics

| Topic | Description | Updates |
|-------|-------------|---------|
| `/topic/bookings` | Real-time booking updates | New bookings, status changes |
| `/topic/dashboard` | Dashboard statistics | Live metrics, counters |
| `/topic/notifications` | User notifications | Alerts, messages |
| `/topic/payments` | Payment events | Payment intent created/confirmed |

---

## Running the Frontend

### Prerequisites
- Node.js 18+
- Producer Service running on port 8081
- Consumer Service running on port 8082

### Installation
```bash
cd frontend
npm install
```

### Development
```bash
npm run dev
```
Runs on `http://localhost:5173`

### Production Build
```bash
npm run build
```

---

## Environment Variables

Create `.env` file in frontend root:

```env
VITE_API_BASE_URL=http://localhost:8081
VITE_WS_URL=ws://localhost:8082/ws
VITE_STRIPE_PUBLISHABLE_KEY=pk_test_xxx
```

---

## Key Components

### BookingForm.jsx
- Collects user booking details (name, email, sport type, venue, slot time, duration, amount)
- Integrates Stripe Elements for card payment
- Handles payment intent creation and confirmation
- Shows loading states and error handling

### useWebSocket.js
- Manages WebSocket connection with STOMP protocol
- Subscribes to multiple topics (`/topic/bookings`, `/topic/dashboard`, `/topic/notifications`, `/topic/payments`)
- Provides connection status and auto-reconnect logic
- Returns latest messages for each topic

### Dashboard.jsx
- Displays real-time statistics (total bookings, confirmed, pending, revenue)
- Shows live booking table with auto-updating
- Displays notification panel with alerts
- Shows Kafka cluster info and connection status