// // import { useState } from 'react'
// // import toast from 'react-hot-toast'
// // import { createBooking, createPaymentIntent, confirmPayment } from '../services/api'
// // import './BookingForm.css'

// // const SPORTS = ['Football', 'Cricket', 'Tennis', 'Basketball', 'Badminton', 'Swimming', 'Volleyball']
// // const VENUES = [
// //   'Gachibowli Stadium, Hyderabad',
// //   'Lal Bahadur Shastri Stadium, Hyderabad',
// //   'Rajiv Gandhi International Stadium',
// //   'YMCA Ground, Delhi',
// //   'Wankhede Stadium, Mumbai',
// //   'Eden Gardens, Kolkata',
// //   'Chinnaswamy Stadium, Bangalore',
// // ]

// // const INITIAL_FORM = {
// //   customerName: '',
// //   sportType: '',
// //   venue: '',
// //   slotDateTime: '',
// //   durationHours: 1,
// //   amount: '',
// //   message: '',
// // }

// // export default function BookingForm({ onSuccess }) {
// //   const [form, setForm] = useState(INITIAL_FORM)
// //   const [errors, setErrors] = useState({})
// //   const [submitting, setSubmitting] = useState(false)
// //   const [submitted, setSubmitted] = useState(null)

// //   const handleChange = (e) => {
// //     const { name, value } = e.target
// //     setForm(prev => ({ ...prev, [name]: value }))
// //     if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }))
// //   }

// //   const validate = () => {
// //     const newErrors = {}
// //     if (!form.customerName.trim()) newErrors.customerName = 'Customer name is required'
// //     if (!form.sportType) newErrors.sportType = 'Select a sport type'
// //     if (!form.venue) newErrors.venue = 'Select a venue'
// //     if (!form.slotDateTime) newErrors.slotDateTime = 'Select slot date & time'
// //     if (form.durationHours < 1 || form.durationHours > 12) newErrors.durationHours = 'Duration must be 1–12 hours'
// //     if (!form.amount || form.amount <= 0) newErrors.amount = 'Enter a valid amount'
// //     setErrors(newErrors)
// //     return Object.keys(newErrors).length === 0
// //   }

// //   const handleSubmit = async (e) => {
// //     e.preventDefault()
// //     if (!validate()) {
// //       toast.error('Please fix the errors before submitting')
// //       return
// //     }

// //     setSubmitting(true)

// //     const payload = {
// //       ...form,
// //       durationHours: Number(form.durationHours),
// //       amount: Number(form.amount),
// //       slotDateTime: form.slotDateTime.replace('T', ' ') + ':00',
// //     }

// //     try {
// //       const response = await createBooking(payload)
// //       setSubmitted({ ...response, ...payload })
// //       toast.success('✅ Booking created! Please complete payment.')
// //     } catch (err) {
// //       toast.error(err.message || 'Failed to create booking')
// //     } finally {
// //       setSubmitting(false)
// //     }
// //   }

// //   const handlePayment = async () => {
// //     if (!submitted) return
    
// //     setSubmitting(true)
// //     try {
// //       // Create payment intent
// //       const paymentData = await createPaymentIntent(
// //         submitted.eventId,
// //         submitted.customerName,
// //         submitted.customerName + '@example.com',
// //         submitted.amount
// //       )
      
// //       if (paymentData.clientSecret) {
// //         toast.success('Payment initialized!')
        
// //         // Redirect to success page
// //         window.location.href = `/?page=payment-success&bookingId=${submitted.eventId}&paymentIntentId=${paymentData.paymentIntentId}`
// //       }
// //     } catch (err) {
// //       toast.error(err.message || 'Payment failed')
// //       window.location.href = `/?page=payment-failure&bookingId=${submitted?.eventId}&reason=${encodeURIComponent(err.message)}`
// //     } finally {
// //       setSubmitting(false)
// //     }
// //   }

// //   const handleReset = () => {
// //     setForm(INITIAL_FORM)
// //     setErrors({})
// //     setSubmitted(null)
// //   }

// //   return (
// //     <div className="booking-form-page">
// //       <div className="page-header">
// //         <h1 className="page-title">New Booking</h1>
// //         <p className="page-subtitle">
// //           Form → Kafka Producer → Consumer → MySQL → Payment → Live Dashboard
// //         </p>
// //       </div>

// //       <div className="booking-form-layout">
// //         <div className="booking-form-card card">
// //           <div className="form-card-header">
// //             <span className="form-card-icon">📤</span>
// //             <div>
// //               <h2 className="form-card-title">Kafka Producer</h2>
// //               <p className="form-card-sub">Publishes to <code>booking-events</code> topic</p>
// //             </div>
// //           </div>

// //           <form onSubmit={handleSubmit} className="booking-form">
// //             <div className="form-row">
// //               <div className="form-group">
// //                 <label className="form-label">Customer Name *</label>
// //                 <input
// //                   className={`form-input ${errors.customerName ? 'error' : ''}`}
// //                   name="customerName"
// //                   value={form.customerName}
// //                   onChange={handleChange}
// //                   placeholder="e.g. Rahul Sharma"
// //                 />
// //                 {errors.customerName && <span className="form-error">{errors.customerName}</span>}
// //               </div>

// //               <div className="form-group">
// //                 <label className="form-label">Sport Type *</label>
// //                 <select
// //                   className={`form-select ${errors.sportType ? 'error' : ''}`}
// //                   name="sportType"
// //                   value={form.sportType}
// //                   onChange={handleChange}
// //                 >
// //                   <option value="">Select sport...</option>
// //                   {SPORTS.map(s => <option key={s} value={s}>{s}</option>)}
// //                 </select>
// //                 {errors.sportType && <span className="form-error">{errors.sportType}</span>}
// //               </div>
// //             </div>

// //             <div className="form-group">
// //               <label className="form-label">Venue *</label>
// //               <select
// //                 className={`form-select ${errors.venue ? 'error' : ''}`}
// //                 name="venue"
// //                 value={form.venue}
// //                 onChange={handleChange}
// //               >
// //                 <option value="">Select venue...</option>
// //                 {VENUES.map(v => <option key={v} value={v}>{v}</option>)}
// //               </select>
// //               {errors.venue && <span className="form-error">{errors.venue}</span>}
// //             </div>

// //             <div className="form-row">
// //               <div className="form-group">
// //                 <label className="form-label">Slot Date & Time *</label>
// //                 <input
// //                   type="datetime-local"
// //                   className={`form-input ${errors.slotDateTime ? 'error' : ''}`}
// //                   name="slotDateTime"
// //                   value={form.slotDateTime}
// //                   onChange={handleChange}
// //                 />
// //                 {errors.slotDateTime && <span className="form-error">{errors.slotDateTime}</span>}
// //               </div>

// //               <div className="form-group">
// //                 <label className="form-label">Duration (hours) *</label>
// //                 <input
// //                   type="number"
// //                   className={`form-input ${errors.durationHours ? 'error' : ''}`}
// //                   name="durationHours"
// //                   value={form.durationHours}
// //                   onChange={handleChange}
// //                   min="1" max="12"
// //                 />
// //                 {errors.durationHours && <span className="form-error">{errors.durationHours}</span>}
// //               </div>
// //             </div>

// //             <div className="form-group">
// //               <label className="form-label">Amount (₹) *</label>
// //               <input
// //                 type="number"
// //                 className={`form-input ${errors.amount ? 'error' : ''}`}
// //                 name="amount"
// //                 value={form.amount}
// //                 onChange={handleChange}
// //                 placeholder="e.g. 1500"
// //                 min="0"
// //               />
// //               {errors.amount && <span className="form-error">{errors.amount}</span>}
// //             </div>

// //             <div className="form-group">
// //               <label className="form-label">Message / Notes</label>
// //               <input
// //                 className="form-input"
// //                 name="message"
// //                 value={form.message}
// //                 onChange={handleChange}
// //                 placeholder="Any special requirements..."
// //               />
// //             </div>

// //             <div className="form-actions">
// //               <button type="button" className="btn btn-secondary" onClick={handleReset} disabled={submitting}>
// //                 Reset
// //               </button>
// //               <button type="submit" className="btn btn-primary" disabled={submitting}>
// //                 {submitting ? <><span className="spinner" /> Creating...</> : '📤 Create Booking'}
// //               </button>
// //             </div>
// //           </form>
// //         </div>

// //         <div className="form-right-panel">
// //           {submitted && (
// //             <div className="submitted-card card animate-fade-in-up">
// //               <div className="submitted-header">
// //                 <span>✅</span>
// //                 <div>
// //                   <h3>Booking Created!</h3>
// //                   <p>Complete payment to confirm</p>
// //                 </div>
// //               </div>
// //               <div className="submitted-body">
// //                 <div className="submitted-row">
// //                   <span>Customer</span><strong>{submitted.customerName}</strong>
// //                 </div>
// //                 <div className="submitted-row">
// //                   <span>Sport</span><strong>{submitted.sportType}</strong>
// //                 </div>
// //                 <div className="submitted-row">
// //                   <span>Venue</span><strong>{submitted.venue}</strong>
// //                 </div>
// //                 <div className="submitted-row">
// //                   <span>Amount</span><strong>₹{submitted.amount}</strong>
// //                 </div>
// //               </div>
// //               <button
// //                 className="btn btn-primary"
// //                 style={{ width: '100%', marginTop: '1rem' }}
// //                 onClick={handlePayment}
// //                 disabled={submitting}
// //               >
// //                 {submitting ? <><span className="spinner" /> Processing...</> : '💳 Pay Now with Stripe'}
// //               </button>
// //             </div>
// //           )}

// //           <div className="kafka-info-card card">
// //             <h3 className="kafka-info-title">⚡ What happens next?</h3>
// //             <ol className="kafka-steps">
// //               <li><span className="step-num">1</span><div><strong>Kafka Producer</strong><p>Message to <code>booking-events</code></p></div></li>
// //               <li><span className="step-num">2</span><div><strong>Kafka Consumer</strong><p>Picks up from Kafka</p></div></li>
// //               <li><span className="step-num">3</span><div><strong>MySQL Save</strong><p>Booking saved with PENDING status</p></div></li>
// //               <li><span className="step-num">4</span><div><strong>Stripe Payment</strong><p>Complete payment to confirm</p></div></li>
// //               <li><span className="step-num">5</span><div><strong>WebSocket Push</strong><p>Live update to dashboard</p></div></li>
// //             </ol>
// //           </div>
// //         </div>
// //       </div>
// //     </div>
// //   )
// // }




// import { useState, useEffect } from 'react'
// import toast from 'react-hot-toast'
// import { createBookingWithPayment, getStripePublishableKey } from '../services/api'
// import './BookingForm.css'

// // Load Stripe.js dynamically
// const loadStripe = (publishableKey) => {
//   return new Promise((resolve, reject) => {
//     if (window.Stripe) {
//       resolve(window.Stripe(publishableKey))
//       return
//     }
    
//     const script = document.createElement('script')
//     script.src = 'https://js.stripe.com/v3/'
//     script.async = true
//     script.onload = () => {
//       resolve(window.Stripe(publishableKey))
//     }
//     script.onerror = reject
//     document.body.appendChild(script)
//   })
// }

// const SPORTS = ['Football', 'Cricket', 'Tennis', 'Basketball', 'Badminton', 'Swimming', 'Volleyball']
// const VENUES = [
//   'Gachibowli Stadium, Hyderabad',
//   'Lal Bahadur Shastri Stadium, Hyderabad',
//   'Rajiv Gandhi International Stadium',
//   'YMCA Ground, Delhi',
//   'Wankhede Stadium, Mumbai',
//   'Eden Gardens, Kolkata',
//   'Chinnaswamy Stadium, Bangalore',
// ]

// const INITIAL_FORM = {
//   customerName: '',
//   customerEmail: '',
//   sportType: '',
//   venue: '',
//   slotDateTime: '',
//   durationHours: 1,
//   amount: '',
//   message: '',
// }

// export default function BookingForm({ onSuccess }) {
//   const [form, setForm] = useState(INITIAL_FORM)
//   const [errors, setErrors] = useState({})
//   const [submitting, setSubmitting] = useState(false)
//   const [stripe, setStripe] = useState(null)
//   const [publishableKey, setPublishableKey] = useState('')

//   // Load Stripe publishable key
//   useEffect(() => {
//     const loadStripeKey = async () => {
//       try {
//         const response = await getStripePublishableKey()
//         setPublishableKey(response.publishableKey)
//         const stripeInstance = await loadStripe(response.publishableKey)
//         setStripe(stripeInstance)
//       } catch (err) {
//         console.error('Failed to load Stripe:', err)
//         toast.error('Failed to load payment system')
//       }
//     }
//     loadStripeKey()
//   }, [])

//   const handleChange = (e) => {
//     const { name, value } = e.target
//     setForm(prev => ({ ...prev, [name]: value }))
//     if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }))
//   }

//   const validate = () => {
//     const newErrors = {}
//     if (!form.customerName.trim()) newErrors.customerName = 'Customer name is required'
//     if (!form.customerEmail.trim()) newErrors.customerEmail = 'Email is required'
//     if (!/\S+@\S+\.\S+/.test(form.customerEmail)) newErrors.customerEmail = 'Invalid email format'
//     if (!form.sportType) newErrors.sportType = 'Select a sport type'
//     if (!form.venue) newErrors.venue = 'Select a venue'
//     if (!form.slotDateTime) newErrors.slotDateTime = 'Select slot date & time'
//     if (form.durationHours < 1 || form.durationHours > 12) newErrors.durationHours = 'Duration must be 1–12 hours'
//     if (!form.amount || form.amount <= 0) newErrors.amount = 'Enter a valid amount'
//     setErrors(newErrors)
//     return Object.keys(newErrors).length === 0
//   }

//   const handleSubmit = async (e) => {
//     e.preventDefault()
//     if (!validate()) {
//       toast.error('Please fix the errors before submitting')
//       return
//     }

//     if (!stripe) {
//       toast.error('Payment system is loading. Please wait...')
//       return
//     }

//     setSubmitting(true)

//     const payload = {
//       customerName: form.customerName,
//       customerEmail: form.customerEmail,
//       sportType: form.sportType,
//       venue: form.venue,
//       slotDateTime: form.slotDateTime.replace('T', ' ') + ':00',
//       durationHours: Number(form.durationHours),
//       amount: Number(form.amount),
//       message: form.message,
//     }

//     try {
//       // Create booking and payment intent
//       const response = await createBookingWithPayment(payload)
      
//       if (response.clientSecret) {
//         // Confirm payment with Stripe
//         const { error, paymentIntent } = await stripe.confirmCardPayment(response.clientSecret, {
//           payment_method: {
//             card: {
//               // This would come from Stripe Elements in a real implementation
//               // For demo, we'll simulate a successful payment
//               token: 'tok_visa'
//             },
//             billing_details: {
//               name: form.customerName,
//               email: form.customerEmail,
//             },
//           },
//         })

//         if (error) {
//           toast.error(error.message || 'Payment failed')
//           window.location.href = `/?page=payment-failure&bookingId=${response.eventId}&reason=${encodeURIComponent(error.message)}`
//         } else if (paymentIntent && paymentIntent.status === 'succeeded') {
//           // Confirm payment with backend
//           await confirmPayment(paymentIntent.id)
//           toast.success('Payment successful! Booking confirmed.')
//           window.location.href = `/?page=payment-success&bookingId=${response.eventId}&paymentIntentId=${paymentIntent.id}`
//         }
//       } else {
//         toast.error('Failed to initialize payment')
//       }
//     } catch (err) {
//       toast.error(err.message || 'Failed to create booking')
//       window.location.href = `/?page=payment-failure&reason=${encodeURIComponent(err.message)}`
//     } finally {
//       setSubmitting(false)
//     }
//   }

//   const handleReset = () => {
//     setForm(INITIAL_FORM)
//     setErrors({})
//   }

//   return (
//     <div className="booking-form-page">
//       <div className="page-header">
//         <h1 className="page-title">New Booking with Payment</h1>
//         <p className="page-subtitle">
//           Create booking and pay securely with Stripe
//         </p>
//       </div>

//       <div className="booking-form-layout">
//         <div className="booking-form-card card">
//           <div className="form-card-header">
//             <span className="form-card-icon">💳</span>
//             <div>
//               <h2 className="form-card-title">Booking & Payment</h2>
//               <p className="form-card-sub">Secure payment powered by Stripe</p>
//             </div>
//           </div>

//           <form onSubmit={handleSubmit} className="booking-form">
//             <div className="form-row">
//               <div className="form-group">
//                 <label className="form-label">Customer Name *</label>
//                 <input
//                   className={`form-input ${errors.customerName ? 'error' : ''}`}
//                   name="customerName"
//                   value={form.customerName}
//                   onChange={handleChange}
//                   placeholder="e.g. Rahul Sharma"
//                 />
//                 {errors.customerName && <span className="form-error">{errors.customerName}</span>}
//               </div>

//               <div className="form-group">
//                 <label className="form-label">Email *</label>
//                 <input
//                   type="email"
//                   className={`form-input ${errors.customerEmail ? 'error' : ''}`}
//                   name="customerEmail"
//                   value={form.customerEmail}
//                   onChange={handleChange}
//                   placeholder="rahul@example.com"
//                 />
//                 {errors.customerEmail && <span className="form-error">{errors.customerEmail}</span>}
//               </div>
//             </div>

//             <div className="form-row">
//               <div className="form-group">
//                 <label className="form-label">Sport Type *</label>
//                 <select
//                   className={`form-select ${errors.sportType ? 'error' : ''}`}
//                   name="sportType"
//                   value={form.sportType}
//                   onChange={handleChange}
//                 >
//                   <option value="">Select sport...</option>
//                   {SPORTS.map(s => <option key={s} value={s}>{s}</option>)}
//                 </select>
//                 {errors.sportType && <span className="form-error">{errors.sportType}</span>}
//               </div>

//               <div className="form-group">
//                 <label className="form-label">Venue *</label>
//                 <select
//                   className={`form-select ${errors.venue ? 'error' : ''}`}
//                   name="venue"
//                   value={form.venue}
//                   onChange={handleChange}
//                 >
//                   <option value="">Select venue...</option>
//                   {VENUES.map(v => <option key={v} value={v}>{v}</option>)}
//                 </select>
//                 {errors.venue && <span className="form-error">{errors.venue}</span>}
//               </div>
//             </div>

//             <div className="form-row">
//               <div className="form-group">
//                 <label className="form-label">Slot Date & Time *</label>
//                 <input
//                   type="datetime-local"
//                   className={`form-input ${errors.slotDateTime ? 'error' : ''}`}
//                   name="slotDateTime"
//                   value={form.slotDateTime}
//                   onChange={handleChange}
//                 />
//                 {errors.slotDateTime && <span className="form-error">{errors.slotDateTime}</span>}
//               </div>

//               <div className="form-group">
//                 <label className="form-label">Duration (hours) *</label>
//                 <input
//                   type="number"
//                   className={`form-input ${errors.durationHours ? 'error' : ''}`}
//                   name="durationHours"
//                   value={form.durationHours}
//                   onChange={handleChange}
//                   min="1" max="12"
//                 />
//                 {errors.durationHours && <span className="form-error">{errors.durationHours}</span>}
//               </div>
//             </div>

//             <div className="form-group">
//               <label className="form-label">Amount (₹) *</label>
//               <input
//                 type="number"
//                 className={`form-input ${errors.amount ? 'error' : ''}`}
//                 name="amount"
//                 value={form.amount}
//                 onChange={handleChange}
//                 placeholder="e.g. 1500"
//                 min="0"
//               />
//               {errors.amount && <span className="form-error">{errors.amount}</span>}
//             </div>

//             <div className="form-group">
//               <label className="form-label">Message / Notes</label>
//               <input
//                 className="form-input"
//                 name="message"
//                 value={form.message}
//                 onChange={handleChange}
//                 placeholder="Any special requirements..."
//               />
//             </div>

//             <div className="payment-info">
//               <div className="payment-badge">
//                 <span className="payment-icon">🔒</span>
//                 <span>Secure payment powered by Stripe</span>
//               </div>
//             </div>

//             <div className="form-actions">
//               <button type="button" className="btn btn-secondary" onClick={handleReset} disabled={submitting}>
//                 Reset
//               </button>
//               <button type="submit" className="btn btn-primary" disabled={submitting || !stripe}>
//                 {submitting ? <><span className="spinner" /> Processing...</> : `💳 Pay ₹${form.amount || 0}`}
//               </button>
//             </div>
//           </form>
//         </div>

//         <div className="form-right-panel">
//           <div className="payment-summary-card card">
//             <h3 className="payment-summary-title">💳 Payment Summary</h3>
//             <div className="payment-details">
//               <div className="payment-row">
//                 <span>Booking Amount</span>
//                 <strong>₹{form.amount || 0}</strong>
//               </div>
//               <div className="payment-row">
//                 <span>GST (18%)</span>
//                 <strong>₹{((form.amount || 0) * 0.18).toFixed(2)}</strong>
//               </div>
//               <div className="payment-row total">
//                 <span>Total Payable</span>
//                 <strong>₹{((form.amount || 0) * 1.18).toFixed(2)}</strong>
//               </div>
//             </div>
//           </div>

//           <div className="kafka-info-card card">
//             <h3 className="kafka-info-title">⚡ Payment Flow</h3>
//             <ol className="kafka-steps">
//               <li><span className="step-num">1</span><div><strong>Create Booking</strong><p>Send to Kafka</p></div></li>
//               <li><span className="step-num">2</span><div><strong>Create Payment Intent</strong><p>Stripe API</p></div></li>
//               <li><span className="step-num">3</span><div><strong>Confirm Payment</strong><p>Stripe.js</p></div></li>
//               <li><span className="step-num">4</span><div><strong>Update Status</strong><p>CONFIRMED</p></div></li>
//               <li><span className="step-num">5</span><div><strong>WebSocket Push</strong><p>Live update</p></div></li>
//             </ol>
//           </div>
//         </div>
//       </div>
//     </div>
//   )
// }


import { useState, useEffect } from 'react'
import { Elements, CardElement, useStripe, useElements } from '@stripe/react-stripe-js'
import { loadStripe } from '@stripe/stripe-js'
import toast from 'react-hot-toast'
import { createBookingWithPayment, getStripePublishableKey, confirmPayment } from '../services/api'
import './BookingForm.css'

const SPORTS = ['Football', 'Cricket', 'Tennis', 'Basketball', 'Badminton', 'Swimming', 'Volleyball']
const VENUES = [
  'Gachibowli Stadium, Hyderabad',
  'Lal Bahadur Shastri Stadium, Hyderabad',
  'Rajiv Gandhi International Stadium',
  'YMCA Ground, Delhi',
  'Wankhede Stadium, Mumbai',
  'Eden Gardens, Kolkata',
  'Chinnaswamy Stadium, Bangalore',
]

const INITIAL_FORM = {
  customerName: '',
  customerEmail: '',
  sportType: '',
  venue: '',
  slotDateTime: '',
  durationHours: 1,
  amount: '',
  message: '',
}

// Stripe Payment Form Component
function PaymentForm({ bookingData, onSuccess, onError }) {
  const stripe = useStripe()
  const elements = useElements()
  const [processing, setProcessing] = useState(false)
  const [clientSecret, setClientSecret] = useState(null)
  const [paymentIntentId, setPaymentIntentId] = useState(null)
  const [bookingId, setBookingId] = useState(null)

  useEffect(() => {
    const initializePayment = async () => {
      try {
        setProcessing(true)
        
        const payload = {
          customerName: bookingData.customerName,
          customerEmail: bookingData.customerEmail,
          sportType: bookingData.sportType,
          venue: bookingData.venue,
          slotDateTime: bookingData.slotDateTime.replace('T', ' ') + ':00',
          durationHours: Number(bookingData.durationHours),
          amount: Number(bookingData.amount),
          message: bookingData.message,
        }

        console.log('Sending booking request:', payload)
        const response = await createBookingWithPayment(payload)
        console.log('Booking response:', response)
        
        if (response.clientSecret) {
          setClientSecret(response.clientSecret)
          setPaymentIntentId(response.paymentIntentId)
          setBookingId(response.eventId)
          toast.success('Booking created! Please complete payment.')
        } else {
          toast.error('Failed to initialize payment')
          onError?.('Payment initialization failed')
        }
      } catch (err) {
        console.error('Payment initialization error:', err)
        toast.error(err.message || 'Failed to initialize payment')
        onError?.(err.message)
      } finally {
        setProcessing(false)
      }
    }

    initializePayment()
  }, [bookingData])

  const handleSubmit = async (event) => {
    event.preventDefault()
    
    if (!stripe || !elements) {
      toast.error('Stripe not loaded')
      return
    }

    if (!clientSecret) {
      toast.error('Payment not initialized')
      return
    }

    setProcessing(true)

    const cardElement = elements.getElement(CardElement)

    try {
      const { error, paymentIntent } = await stripe.confirmCardPayment(clientSecret, {
        payment_method: {
          card: cardElement,
          billing_details: {
            name: bookingData.customerName,
            email: bookingData.customerEmail,
          },
        },
      })

      if (error) {
        console.error('Payment error:', error)
        toast.error(error.message || 'Payment failed')
        onError?.(error.message)
        window.location.href = `/?page=payment-failure&bookingId=${bookingId}&reason=${encodeURIComponent(error.message)}`
      } else if (paymentIntent && paymentIntent.status === 'succeeded') {
        toast.success('Payment successful! Confirming booking...')
        
        await confirmPayment(paymentIntent.id)
        
        toast.success('Booking confirmed successfully!')
        window.location.href = `/?page=payment-success&bookingId=${bookingId}&paymentIntentId=${paymentIntent.id}`
        onSuccess?.()
      }
    } catch (err) {
      console.error('Payment confirmation error:', err)
      toast.error('Payment processing failed')
      onError?.(err.message)
    } finally {
      setProcessing(false)
    }
  }

  const cardElementOptions = {
    style: {
      base: {
        fontSize: '16px',
        color: '#f1f5f9',
        fontFamily: 'Inter, system-ui, sans-serif',
        '::placeholder': { color: '#64748b' },
        backgroundColor: '#1e293b',
      },
      invalid: { color: '#ef4444', iconColor: '#ef4444' },
      complete: { color: '#10b981' },
    },
    hidePostalCode: true,
  }

  if (processing && !clientSecret) {
    return (
      <div className="payment-loading">
        <div className="spinner"></div>
        <p>Creating your booking...</p>
      </div>
    )
  }

  return (
    <form onSubmit={handleSubmit} className="stripe-payment-form">
      <div className="payment-details-summary">
        <h4>Payment Summary</h4>
        <div className="summary-row">
          <span>Booking Amount:</span>
          <strong>₹{bookingData.amount}</strong>
        </div>
        <div className="summary-row">
          <span>GST (18%):</span>
          <strong>₹{((bookingData.amount || 0) * 0.18).toFixed(2)}</strong>
        </div>
        <div className="summary-row total">
          <span>Total:</span>
          <strong>₹{((bookingData.amount || 0) * 1.18).toFixed(2)}</strong>
        </div>
      </div>

      <div className="card-element-wrapper">
        <label className="form-label">Card Details</label>
        <div className="card-element-container">
          <CardElement options={cardElementOptions} />
        </div>
        <p className="card-hint">Test card: 4242 4242 4242 4242 | Any future date | Any CVC</p>
      </div>

      <button 
        type="submit" 
        className="btn btn-primary pay-btn" 
        disabled={!stripe || processing || !clientSecret}
      >
        {processing ? (
          <>
            <span className="spinner"></span>
            Processing Payment...
          </>
        ) : (
          `Pay ₹${((bookingData.amount || 0) * 1.18).toFixed(2)}`
        )}
      </button>
    </form>
  )
}

// Main Booking Form Component
export default function BookingForm({ onSuccess }) {
  const [form, setForm] = useState(INITIAL_FORM)
  const [errors, setErrors] = useState({})
  const [showPayment, setShowPayment] = useState(false)
  const [stripePromise, setStripePromise] = useState(null)

  useEffect(() => {
    const loadStripeKey = async () => {
      try {
        const response = await getStripePublishableKey()
        console.log('Stripe publishable key loaded')
        const stripe = loadStripe(response.publishableKey)
        setStripePromise(stripe)
      } catch (err) {
        console.error('Failed to load Stripe:', err)
        toast.error('Failed to load payment system')
      }
    }
    loadStripeKey()
  }, [])

  const handleChange = (e) => {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
    if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }))
  }

  const validate = () => {
    const newErrors = {}
    if (!form.customerName.trim()) newErrors.customerName = 'Customer name is required'
    if (!form.customerEmail.trim()) newErrors.customerEmail = 'Email is required'
    if (!/\S+@\S+\.\S+/.test(form.customerEmail)) newErrors.customerEmail = 'Invalid email format'
    if (!form.sportType) newErrors.sportType = 'Select a sport type'
    if (!form.venue) newErrors.venue = 'Select a venue'
    if (!form.slotDateTime) newErrors.slotDateTime = 'Select slot date & time'
    if (form.durationHours < 1 || form.durationHours > 12) newErrors.durationHours = 'Duration must be 1–12 hours'
    if (!form.amount || form.amount <= 0) newErrors.amount = 'Enter a valid amount'
    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleContinueToPayment = (e) => {
    e.preventDefault()
    if (validate()) {
      setShowPayment(true)
    } else {
      toast.error('Please fix the errors before continuing')
    }
  }

  const handleBackToForm = () => {
    setShowPayment(false)
  }

  if (showPayment) {
    return (
      <div className="booking-form-page">
        <div className="page-header">
          <h1 className="page-title">Complete Payment</h1>
          <p className="page-subtitle">Secure payment powered by Stripe</p>
        </div>

        <div className="payment-layout">
          <div className="payment-form-card card">
            <div className="payment-header">
              <button className="back-btn" onClick={handleBackToForm}>
                ← Back to Booking Details
              </button>
              <h2>Payment Information</h2>
            </div>
            
            {stripePromise ? (
              <Elements stripe={stripePromise}>
                <PaymentForm 
                  bookingData={form} 
                  onSuccess={onSuccess}
                  onError={handleBackToForm}
                />
              </Elements>
            ) : (
              <div className="payment-loading">
                <div className="spinner"></div>
                <p>Loading payment system...</p>
              </div>
            )}
          </div>

          <div className="booking-summary-card card">
            <h3>Booking Summary</h3>
            <div className="summary-details">
              <div className="summary-item"><span>Customer:</span><strong>{form.customerName}</strong></div>
              <div className="summary-item"><span>Email:</span><strong>{form.customerEmail}</strong></div>
              <div className="summary-item"><span>Sport:</span><strong>{form.sportType}</strong></div>
              <div className="summary-item"><span>Venue:</span><strong>{form.venue}</strong></div>
              <div className="summary-item"><span>Date:</span><strong>{new Date(form.slotDateTime).toLocaleString()}</strong></div>
              <div className="summary-item"><span>Duration:</span><strong>{form.durationHours} hour(s)</strong></div>
            </div>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="booking-form-page">
      <div className="page-header">
        <h1 className="page-title">New Booking</h1>
        <p className="page-subtitle">Create booking and pay securely with Stripe</p>
      </div>

      <div className="booking-form-layout">
        <div className="booking-form-card card">
          <div className="form-card-header">
            <span className="form-card-icon">📝</span>
            <div>
              <h2 className="form-card-title">Booking Details</h2>
              <p className="form-card-sub">Enter your booking information</p>
            </div>
          </div>

          <form onSubmit={handleContinueToPayment} className="booking-form">
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Customer Name *</label>
                <input className={`form-input ${errors.customerName ? 'error' : ''}`} name="customerName" value={form.customerName} onChange={handleChange} placeholder="e.g. Rahul Sharma" />
                {errors.customerName && <span className="form-error">{errors.customerName}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">Email *</label>
                <input type="email" className={`form-input ${errors.customerEmail ? 'error' : ''}`} name="customerEmail" value={form.customerEmail} onChange={handleChange} placeholder="rahul@example.com" />
                {errors.customerEmail && <span className="form-error">{errors.customerEmail}</span>}
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Sport Type *</label>
                <select className={`form-select ${errors.sportType ? 'error' : ''}`} name="sportType" value={form.sportType} onChange={handleChange}>
                  <option value="">Select sport...</option>
                  {SPORTS.map(s => <option key={s} value={s}>{s}</option>)}
                </select>
                {errors.sportType && <span className="form-error">{errors.sportType}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">Venue *</label>
                <select className={`form-select ${errors.venue ? 'error' : ''}`} name="venue" value={form.venue} onChange={handleChange}>
                  <option value="">Select venue...</option>
                  {VENUES.map(v => <option key={v} value={v}>{v}</option>)}
                </select>
                {errors.venue && <span className="form-error">{errors.venue}</span>}
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Slot Date & Time *</label>
                <input type="datetime-local" className={`form-input ${errors.slotDateTime ? 'error' : ''}`} name="slotDateTime" value={form.slotDateTime} onChange={handleChange} />
                {errors.slotDateTime && <span className="form-error">{errors.slotDateTime}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">Duration (hours) *</label>
                <input type="number" className={`form-input ${errors.durationHours ? 'error' : ''}`} name="durationHours" value={form.durationHours} onChange={handleChange} min="1" max="12" />
                {errors.durationHours && <span className="form-error">{errors.durationHours}</span>}
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Amount (₹) *</label>
              <input type="number" className={`form-input ${errors.amount ? 'error' : ''}`} name="amount" value={form.amount} onChange={handleChange} placeholder="e.g. 1500" min="0" />
              {errors.amount && <span className="form-error">{errors.amount}</span>}
            </div>

            <div className="form-group">
              <label className="form-label">Message / Notes</label>
              <input className="form-input" name="message" value={form.message} onChange={handleChange} placeholder="Any special requirements..." />
            </div>

            <div className="form-actions">
              <button type="button" className="btn btn-secondary" onClick={() => setForm(INITIAL_FORM)}>Reset</button>
              <button type="submit" className="btn btn-primary">Continue to Payment →</button>
            </div>
          </form>
        </div>

        <div className="form-right-panel">
          <div className="payment-info-card card">
            <h3 className="payment-info-title">🔒 Secure Payment</h3>
            <p>Your payment will be processed securely through Stripe.</p>
            <div className="payment-icons"><span>💳 Visa</span><span>💳 Mastercard</span><span>💳 Amex</span><span>💳 RuPay</span></div>
          </div>
          <div className="kafka-info-card card">
            <h3 className="kafka-info-title">⚡ Payment Flow</h3>
            <ol className="kafka-steps">
              <li><span className="step-num">1</span><div><strong>Enter Details</strong><p>Fill booking information</p></div></li>
              <li><span className="step-num">2</span><div><strong>Enter Card Details</strong><p>Secure Stripe form</p></div></li>
              <li><span className="step-num">3</span><div><strong>Pay</strong><p>One-click payment</p></div></li>
              <li><span className="step-num">4</span><div><strong>Confirmation</strong><p>Booking confirmed instantly</p></div></li>
            </ol>
          </div>
        </div>
      </div>
    </div>
  )
}