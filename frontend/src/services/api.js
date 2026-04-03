// import axios from 'axios';

// // Producer API (port 8081)
// const PRODUCER_API = axios.create({
//   baseURL: 'http://localhost:8081/api',
//   timeout: 10000,
//   headers: { 'Content-Type': 'application/json' },
// });

// // Consumer API (port 8082)
// const CONSUMER_API = axios.create({
//   baseURL: 'http://localhost:8082/api',
//   timeout: 10000,
//   headers: { 'Content-Type': 'application/json' },
// });

// // Request interceptors
// PRODUCER_API.interceptors.request.use(
//   (config) => {
//     console.log(`[Producer] ${config.method?.toUpperCase()} ${config.url}`);
//     return config;
//   },
//   (error) => Promise.reject(error)
// );

// CONSUMER_API.interceptors.request.use(
//   (config) => {
//     console.log(`[Consumer] ${config.method?.toUpperCase()} ${config.url}`);
//     return config;
//   },
//   (error) => Promise.reject(error)
// );

// // Response interceptors
// PRODUCER_API.interceptors.response.use(
//   (response) => response.data,
//   (error) => {
//     const msg = error.response?.data?.message || error.message || 'Producer API error';
//     console.error('[Producer Error]', msg);
//     return Promise.reject(new Error(msg));
//   }
// );

// CONSUMER_API.interceptors.response.use(
//   (response) => response.data,
//   (error) => {
//     const msg = error.response?.data?.message || error.message || 'Consumer API error';
//     console.error('[Consumer Error]', msg);
//     return Promise.reject(new Error(msg));
//   }
// );

// // ============ BOOKING APIs ============
// export const createBooking = (bookingData) =>
//   PRODUCER_API.post('/bookings', bookingData);

// export const getAllBookings = () =>
//   CONSUMER_API.get('/bookings');

// export const getBookingById = (id) =>
//   CONSUMER_API.get(`/bookings/${id}`);

// export const getBookingCount = () =>
//   CONSUMER_API.get('/bookings/count');

// export const getBookingsByStatus = (status) =>
//   CONSUMER_API.get(`/bookings/status/${status}`);

// export const getBookingsByCustomer = (customerName) =>
//   CONSUMER_API.get(`/bookings/customer/${customerName}`);

// export const updateBookingStatus = (id, status) =>
//   CONSUMER_API.put(`/bookings/${id}/status?status=${status}`);

// export const approveBooking = (id) =>
//   CONSUMER_API.post(`/bookings/${id}/approve`);

// export const rejectBooking = (id, reason) =>
//   CONSUMER_API.post(`/bookings/${id}/reject`, { reason });

// // ============ DASHBOARD APIs ============
// export const getDashboardStats = () =>
//   CONSUMER_API.get('/bookings/dashboard');

// export const sendDashboardEvent = (eventData) =>
//   PRODUCER_API.post('/dashboard', eventData);

// export const sendDashboardUpdate = (params) =>
//   PRODUCER_API.post('/dashboard/update', null, { params });

// // ============ NOTIFICATION APIs ============
// export const sendNotification = (eventData) =>
//   PRODUCER_API.post('/notifications', eventData);

// export const sendBookingNotification = (params) =>
//   PRODUCER_API.post('/notifications/booking', null, { params });

// // ============ KAFKA INFO APIs ============
// export const getKafkaInfo = () =>
//   CONSUMER_API.get('/kafka/info');

// export const getKafkaTopics = () =>
//   CONSUMER_API.get('/kafka/topics');

// export const getKafkaConsumerGroups = () =>
//   CONSUMER_API.get('/kafka/consumer-groups');

// export const getKafkaOffsets = (topic) =>
//   CONSUMER_API.get(`/kafka/offsets/${topic}`);

// // export const getConsumerGroupOffsets = (groupId) =>
// //   CONSUMER_API.get(`/kafka/consumer-group/${groupId}/offsets`);

// // export const getAllOffsets = () =>
// //   CONSUMER_API.get('/kafka/all-offsets');

// // ============ PAYMENT APIs ============
// export const createPaymentIntent = (bookingEventId, customerName, customerEmail, amount, currency = 'inr', description = '') =>
//   CONSUMER_API.post('/payments/create-payment-intent', {
//     bookingEventId,
//     customerName,
//     customerEmail,
//     amount,
//     currency,
//     description
//   });

// export const confirmPayment = (paymentIntentId) =>
//   CONSUMER_API.post(`/payments/confirm/${paymentIntentId}`);

// export const cancelPayment = (paymentIntentId) =>
//   CONSUMER_API.post(`/payments/cancel/${paymentIntentId}`);

// export const refundPayment = (paymentIntentId, amount) =>
//   CONSUMER_API.post(`/payments/refund/${paymentIntentId}?amount=${amount}`);

// export const getPaymentStatus = (paymentIntentId) =>
//   CONSUMER_API.get(`/payments/status/${paymentIntentId}`);

// export const getStripePublishableKey = () =>
//   CONSUMER_API.get('/payments/publishable-key');

// // ============ HEALTH CHECKS ============
// export const producerHealthCheck = () =>
//   PRODUCER_API.get('/bookings/health');

// export const consumerHealthCheck = () =>
//   CONSUMER_API.get('/bookings/health');

// export const dashboardHealthCheck = () =>
//   PRODUCER_API.get('/dashboard/health');

// export const notificationHealthCheck = () =>
//   PRODUCER_API.get('/notifications/health');

// export { PRODUCER_API, CONSUMER_API };

// export const getConsumerGroupOffsets = (groupId) =>
//   CONSUMER_API.get(`/kafka/consumer-group/${groupId}/offsets`);

// // Get all topic offsets
// export const getAllOffsets = () =>
//   CONSUMER_API.get('/kafka/all-offsets');

// // Get specific topic details
// export const getTopicDetails = (topicName) =>
//   CONSUMER_API.get(`/kafka/topics/${topicName}`);



// import axios from 'axios';

// // Producer API (port 8081)
// const PRODUCER_API = axios.create({
//   baseURL: 'http://localhost:8081/api',
//   timeout: 10000,
//   headers: { 'Content-Type': 'application/json' },
// });

// // Consumer API (port 8082)
// const CONSUMER_API = axios.create({
//   baseURL: 'http://localhost:8082/api',
//   timeout: 10000,
//   headers: { 'Content-Type': 'application/json' },
// });

// // Request interceptors
// PRODUCER_API.interceptors.request.use(
//   (config) => {
//     console.log(`[Producer] ${config.method?.toUpperCase()} ${config.url}`);
//     return config;
//   },
//   (error) => Promise.reject(error)
// );

// CONSUMER_API.interceptors.request.use(
//   (config) => {
//     console.log(`[Consumer] ${config.method?.toUpperCase()} ${config.url}`);
//     return config;
//   },
//   (error) => Promise.reject(error)
// );

// // Response interceptors
// PRODUCER_API.interceptors.response.use(
//   (response) => response.data,
//   (error) => {
//     const msg = error.response?.data?.message || error.message || 'Producer API error';
//     console.error('[Producer Error]', msg);
//     return Promise.reject(new Error(msg));
//   }
// );

// CONSUMER_API.interceptors.response.use(
//   (response) => response.data,
//   (error) => {
//     const msg = error.response?.data?.message || error.message || 'Consumer API error';
//     console.error('[Consumer Error]', msg);
//     return Promise.reject(new Error(msg));
//   }
// );

// // ============ BOOKING APIs ============
// export const createBooking = (bookingData) =>
//   PRODUCER_API.post('/bookings', bookingData);

// // export const createBookingWithPayment = (bookingData) =>
// //   PRODUCER_API.post('/bookings/with-payment', bookingData);

// export const getAllBookings = () =>
//   CONSUMER_API.get('/bookings');

// export const getBookingById = (id) =>
//   CONSUMER_API.get(`/bookings/${id}`);

// export const getBookingCount = () =>
//   CONSUMER_API.get('/bookings/count');

// export const getBookingsByStatus = (status) =>
//   CONSUMER_API.get(`/bookings/status/${status}`);

// export const getBookingsByCustomer = (customerName) =>
//   CONSUMER_API.get(`/bookings/customer/${customerName}`);

// export const updateBookingStatus = (id, status) =>
//   CONSUMER_API.put(`/bookings/${id}/status?status=${status}`);

// export const approveBooking = (id) =>
//   CONSUMER_API.post(`/bookings/${id}/approve`);

// export const rejectBooking = (id, reason) =>
//   CONSUMER_API.post(`/bookings/${id}/reject`, { reason });

// // ============ PAYMENT APIs ============
// export const createPaymentIntent = (paymentData) =>
//   CONSUMER_API.post('/payments/create-payment-intent', paymentData);

// // export const confirmPayment = (paymentIntentId) =>
// //   CONSUMER_API.post(`/payments/confirm/${paymentIntentId}`);

// export const cancelPayment = (paymentIntentId) =>
//   CONSUMER_API.post(`/payments/cancel/${paymentIntentId}`);

// export const refundPayment = (paymentIntentId, amount) =>
//   CONSUMER_API.post(`/payments/refund/${paymentIntentId}?amount=${amount}`);

// export const getPaymentStatus = (paymentIntentId) =>
//   CONSUMER_API.get(`/payments/status/${paymentIntentId}`);

// // export const getStripePublishableKey = () =>
// //   CONSUMER_API.get('/payments/publishable-key');

// export const getPaymentHealth = () =>
//   CONSUMER_API.get('/payments/health');

// // ============ DASHBOARD APIs ============
// export const getDashboardStats = () =>
//   CONSUMER_API.get('/bookings/dashboard');

// export const sendDashboardEvent = (eventData) =>
//   PRODUCER_API.post('/dashboard', eventData);

// export const sendDashboardUpdate = (params) =>
//   PRODUCER_API.post('/dashboard/update', null, { params });

// // ============ NOTIFICATION APIs ============
// export const sendNotification = (eventData) =>
//   PRODUCER_API.post('/notifications', eventData);

// export const sendBookingNotification = (params) =>
//   PRODUCER_API.post('/notifications/booking', null, { params });

// // ============ KAFKA INFO APIs ============
// export const getKafkaInfo = () =>
//   CONSUMER_API.get('/kafka/info');

// export const getKafkaTopics = () =>
//   CONSUMER_API.get('/kafka/topics');

// export const getKafkaConsumerGroups = () =>
//   CONSUMER_API.get('/kafka/consumer-groups');

// export const getKafkaOffsets = (topic) =>
//   CONSUMER_API.get(`/kafka/offsets/${topic}`);

// export const getConsumerGroupOffsets = (groupId) =>
//   CONSUMER_API.get(`/kafka/consumer-group/${groupId}/offsets`);

// export const getAllOffsets = () =>
//   CONSUMER_API.get('/kafka/all-offsets');

// // ============ HEALTH CHECKS ============
// export const producerHealthCheck = () =>
//   PRODUCER_API.get('/bookings/health');

// export const consumerHealthCheck = () =>
//   CONSUMER_API.get('/bookings/health');

// export const dashboardHealthCheck = () =>
//   PRODUCER_API.get('/dashboard/health');

// export const notificationHealthCheck = () =>
//   PRODUCER_API.get('/notifications/health');

// export { PRODUCER_API, CONSUMER_API };

// // Make sure these payment endpoints exist in your api.js

// // Create booking with payment (calls producer)
// export const createBookingWithPayment = (bookingData) =>
//   PRODUCER_API.post('/bookings/with-payment', bookingData);

// // Confirm payment after Stripe success
// export const confirmPayment = (paymentIntentId) =>
//   CONSUMER_API.post(`/payments/confirm/${paymentIntentId}`);

// // Get Stripe publishable key
// export const getStripePublishableKey = () =>
//   CONSUMER_API.get('/payments/publishable-key');

import axios from 'axios';

// Producer API (port 8081)
const PRODUCER_API = axios.create({
  baseURL: 'http://localhost:8081/api',
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' },
});

// Consumer API (port 8082)
const CONSUMER_API = axios.create({
  baseURL: 'http://localhost:8082/api',
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' },
});

// Response interceptors
PRODUCER_API.interceptors.response.use(
  (response) => response.data,
  (error) => {
    console.error('[Producer Error]', error.response?.status, error.response?.data);
    const msg = error.response?.data?.message || error.response?.data?.error || error.message;
    return Promise.reject(new Error(msg));
  }
);

CONSUMER_API.interceptors.response.use(
  (response) => response.data,
  (error) => {
    console.error('[Consumer Error]', error.response?.status, error.response?.data);
    const msg = error.response?.data?.message || error.response?.data?.error || error.message;
    return Promise.reject(new Error(msg));
  }
);

// ============ BOOKING APIs ============
export const createBookingWithPayment = (bookingData) =>
  PRODUCER_API.post('/bookings/with-payment', bookingData);

export const getAllBookings = () =>
  CONSUMER_API.get('/bookings');

export const updateBookingStatus = (id, status) =>
  CONSUMER_API.put(`/bookings/${id}/status?status=${status}`);

export const getDashboardStats = () =>
  CONSUMER_API.get('/bookings/dashboard');

// ============ PAYMENT APIs ============
export const confirmPayment = (paymentIntentId) =>
  CONSUMER_API.post(`/payments/confirm/${paymentIntentId}`);

export const getPaymentStatus = (paymentIntentId) =>
  CONSUMER_API.get(`/payments/status/${paymentIntentId}`);

export const getStripePublishableKey = () =>
  CONSUMER_API.get('/payments/publishable-key');

// ============ KAFKA APIs ============
export const getKafkaInfo = () =>
  CONSUMER_API.get('/kafka/info');

export const getKafkaTopics = () =>
  CONSUMER_API.get('/kafka/topics');

export const getKafkaConsumerGroups = () =>
  CONSUMER_API.get('/kafka/consumer-groups');

export const getKafkaOffsets = (topic) =>
  CONSUMER_API.get(`/kafka/offsets/${topic}`);

export const getConsumerGroupOffsets = (groupId) =>
  CONSUMER_API.get(`/kafka/consumer-group/${groupId}/offsets`);

export const getAllOffsets = () =>
  CONSUMER_API.get('/kafka/all-offsets');

// ============ HEALTH APIs ============
export const producerHealthCheck = () =>
  PRODUCER_API.get('/bookings/health');

export const consumerHealthCheck = () =>
  CONSUMER_API.get('/bookings/health');

export { PRODUCER_API, CONSUMER_API };