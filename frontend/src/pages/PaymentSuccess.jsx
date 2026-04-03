import { useEffect, useState } from 'react'
import { getPaymentStatus } from '../services/api'
import './PaymentResult.css'

export default function PaymentSuccess() {
  const [loading, setLoading] = useState(true)
  const [paymentData, setPaymentData] = useState(null)

  useEffect(() => {
    const params = new URLSearchParams(window.location.search)
    const paymentIntentId = params.get('paymentIntentId')

    const fetchPaymentStatus = async () => {
      if (paymentIntentId) {
        try {
          const status = await getPaymentStatus(paymentIntentId)
          setPaymentData(status)
        } catch (err) {
          console.error('Failed to fetch payment status:', err)
        } finally {
          setLoading(false)
        }
      } else {
        setLoading(false)
      }
    }

    fetchPaymentStatus()
  }, [])

  const goToDashboard = () => { window.location.href = '/?page=dashboard' }
  const makeAnotherBooking = () => { window.location.href = '/?page=create' }

  const params = new URLSearchParams(window.location.search)
  const bookingId = params.get('bookingId')

  if (loading) {
    return (
      <div className="payment-result-page">
        <div className="payment-result-card"><div className="spinner" style={{ margin: '0 auto' }} /><p style={{ marginTop: '1rem' }}>Processing payment...</p></div>
      </div>
    )
  }

  return (
    <div className="payment-result-page">
      <div className="payment-result-card success">
        <div className="result-icon">✅</div>
        <h1>Payment Successful!</h1>
        <p className="result-message">Your booking has been confirmed successfully.</p>
        <div className="result-details">
          <div className="detail-row"><span className="detail-label">Booking ID:</span><span className="detail-value">#{bookingId}</span></div>
          {paymentData && (<><div className="detail-row"><span className="detail-label">Amount Paid:</span><span className="detail-value">₹{paymentData.amount}</span></div>
          <div className="detail-row"><span className="detail-label">Payment Status:</span><span className="detail-value status-success">{paymentData.status}</span></div></>)}
        </div>
        <div className="result-actions">
          <button className="btn btn-primary" onClick={goToDashboard}>Go to Dashboard</button>
          <button className="btn btn-secondary" onClick={makeAnotherBooking}>Make Another Booking</button>
        </div>
      </div>
    </div>
  )
}