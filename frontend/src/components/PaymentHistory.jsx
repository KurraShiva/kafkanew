// src/components/PaymentHistory.jsx
import { useState, useEffect } from 'react'
import { getAllBookings } from '../services/api'
import './PaymentHistory.css'

export default function PaymentHistory() {
  const [payments, setPayments] = useState([])
  const [loading, setLoading] = useState(true)
  const [filter, setFilter] = useState('all')

  useEffect(() => {
    fetchPayments()
  }, [])

  const fetchPayments = async () => {
    try {
      const bookings = await getAllBookings()
      const paymentData = bookings.filter(b => b.paymentIntentId).map(b => ({
        id: b.id,
        eventId: b.eventId,
        customerName: b.customerName,
        amount: b.amount,
        status: b.paymentStatus || b.status,
        paymentIntentId: b.paymentIntentId,
        createdAt: b.createdAt,
        sportType: b.sportType
      }))
      setPayments(paymentData)
    } catch (err) {
      console.error('Failed to fetch payments:', err)
    } finally {
      setLoading(false)
    }
  }

  const getStatusBadge = (status) => {
    const statusMap = {
      'PAID': 'badge-success',
      'CONFIRMED': 'badge-success',
      'PENDING': 'badge-warning',
      'PENDING_PAYMENT': 'badge-warning',
      'FAILED': 'badge-danger',
      'PAYMENT_FAILED': 'badge-danger',
      'REFUNDED': 'badge-info',
      'CANCELLED': 'badge-secondary'
    }
    return statusMap[status] || 'badge-secondary'
  }

  const filteredPayments = payments.filter(p => {
    if (filter === 'all') return true
    if (filter === 'success') return p.status === 'PAID' || p.status === 'CONFIRMED'
    if (filter === 'pending') return p.status === 'PENDING' || p.status === 'PENDING_PAYMENT'
    if (filter === 'failed') return p.status === 'FAILED' || p.status === 'PAYMENT_FAILED'
    return true
  })

  if (loading) {
    return <div className="payment-history-loading"><div className="spinner"></div> Loading payments...</div>
  }

  return (
    <div className="payment-history">
      <div className="payment-history-header">
        <h3>💳 Payment History</h3>
        <div className="filter-buttons">
          <button className={`filter-btn ${filter === 'all' ? 'active' : ''}`} onClick={() => setFilter('all')}>All</button>
          <button className={`filter-btn ${filter === 'success' ? 'active' : ''}`} onClick={() => setFilter('success')}>Success</button>
          <button className={`filter-btn ${filter === 'pending' ? 'active' : ''}`} onClick={() => setFilter('pending')}>Pending</button>
          <button className={`filter-btn ${filter === 'failed' ? 'active' : ''}`} onClick={() => setFilter('failed')}>Failed</button>
        </div>
      </div>

      <div className="payments-table-wrapper">
        <table className="payments-table">
          <thead>
            <tr>
              <th>Payment ID</th>
              <th>Customer</th>
              <th>Sport</th>
              <th>Amount</th>
              <th>Status</th>
              <th>Payment Intent</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            {filteredPayments.map(payment => (
              <tr key={payment.id}>
                <td><span className="payment-id">#{payment.id}</span></td>
                <td>{payment.customerName}</td>
                <td>{payment.sportType}</td>
                <td className="amount">₹{payment.amount?.toLocaleString()}</td>
                <td><span className={`badge ${getStatusBadge(payment.status)}`}>{payment.status}</span></td>
                <td className="payment-intent">{payment.paymentIntentId?.slice(0, 20)}...</td>
                <td>{new Date(payment.createdAt).toLocaleDateString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}