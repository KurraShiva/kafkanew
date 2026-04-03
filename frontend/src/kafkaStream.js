export const KAFKA_CONFIG = {
  server: {
    host: 'localhost',
    port: 9092,
    bootstrapServers: 'localhost:9092',
  },
  topics: [
    {
      name: 'booking-events',
      partitions: 3,
      replicationFactor: 1,
      description: 'Stores all booking events from producer',
    },
    {
      name: 'dashboard-events',
      partitions: 2,
      replicationFactor: 1,
      description: 'Stores analytics and dashboard updates',
    },
    {
      name: 'notification-events',
      partitions: 3,
      replicationFactor: 1,
      description: 'Stores notification and alert events',
    },
  ],
  consumerGroups: [
    {
      id: 'booking-consumer-group',
      topic: 'booking-events',
      description: 'Persists bookings to MySQL database',
      concurrency: 3,
    },
    {
      id: 'analytics-consumer-group',
      topic: 'booking-events',
      description: 'Calculates metrics and produces dashboard events',
      concurrency: 2,
    },
    {
      id: 'dashboard-consumer-group',
      topic: 'dashboard-events',
      description: 'Pushes real-time updates via WebSocket',
      concurrency: 2,
    },
    {
      id: 'notification-consumer-group',
      topic: 'notification-events',
      description: 'Handles alerts and notifications',
      concurrency: 3,
    },
  ],
  services: [
    {
      name: 'Producer Service',
      port: 8081,
      endpoint: '/api/bookings',
      description: 'Receives booking requests and publishes to Kafka',
    },
    {
      name: 'Consumer Service',
      port: 8082,
      endpoint: '/api/bookings',
      description: 'Consumes Kafka events, saves to MySQL, WebSocket updates',
    },
    {
      name: 'Frontend',
      port: 5173,
      endpoint: '/',
      description: 'React Vite application for booking form and dashboard',
    },
  ],
};

export default KAFKA_CONFIG;
