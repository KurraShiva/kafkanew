// import { useNavigate } from 'react-router-dom'
// import './PaymentResult.css'

// export default function PaymentFailure() {
//   const navigate = useNavigate()
//   const params = new URLSearchParams(window.location.search)
//   const bookingId = params.get('bookingId')
//   const reason = params.get('reason') || 'Payment was cancelled or failed'

//   return (
//     <div className="payment-result-page">
//       <div className="payment-result-card failure">
//         <div className="result-icon">❌</div>
//         <h1>Payment Failed</h1>
//         <p className="result-message">{decodeURIComponent(reason)}</p>
        
//         <div className="result-details">
//           {bookingId && (
//             <div className="detail-row">
//               <span className="detail-label">Booking ID:</span>
//               <span className="detail-value">#{bookingId}</span>
//             </div>
//           )}
//           <div className="detail-row">
//             <span className="detail-label">Status:</span>
//             <span className="detail-value status-failure">CANCELLED</span>
//           </div>
//         </div>

//         <div className="result-actions">
//           <button className="btn btn-primary" onClick={() => navigate('/?page=dashboard')}>
//             Go to Dashboard
//           </button>
//           <button className="btn btn-secondary" onClick={() => navigate('/?page=create')}>
//             Try Again
//           </button>
//         </div>
//       </div>
//     </div>
//   )
// }



import './PaymentResult.css'

export default function PaymentFailure() {
  const params = new URLSearchParams(window.location.search)
  const bookingId = params.get('bookingId')
  const reason = params.get('reason') || 'Payment was cancelled or failed'

  const goToDashboard = () => { window.location.href = '/?page=dashboard' }
  const tryAgain = () => { window.location.href = '/?page=create' }

  return (
    <div className="payment-result-page">
      <div className="payment-result-card failure">
        <div className="result-icon">❌</div>
        <h1>Payment Failed</h1>
        <p className="result-message">{decodeURIComponent(reason)}</p>
        <div className="result-details">
          {bookingId && (<div className="detail-row"><span className="detail-label">Booking ID:</span><span className="detail-value">#{bookingId}</span></div>)}
          <div className="detail-row"><span className="detail-label">Status:</span><span className="detail-value status-failure">CANCELLED</span></div>
        </div>
        <div className="result-actions">
          <button className="btn btn-primary" onClick={goToDashboard}>Go to Dashboard</button>
          <button className="btn btn-secondary" onClick={tryAgain}>Try Again</button>
        </div>
      </div>
    </div>
  )
}