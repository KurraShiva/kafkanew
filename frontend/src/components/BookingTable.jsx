import { format } from 'date-fns'
import { useState } from 'react'
import toast from 'react-hot-toast'
import { updateBookingStatus } from '../services/api'
import './BookingTable.css'

const SPORT_ICONS = {
  Football: '⚽', Cricket: '🏏', Tennis: '🎾',
  Basketball: '🏀', Badminton: '🏸', Swimming: '🏊',
  Volleyball: '🏐', default: '🏟️'
}



// const STATUS_OPTIONS = ['PENDING', 'CONFIRMED', 'CANCELLED']
const STATUS_OPTIONS = ['PENDING_PAYMENT', 'PENDING', 'CONFIRMED', 'CANCELLED', 'PAYMENT_FAILED']

function StatusBadge({ status, bookingId, onStatusChange }) {
  const [isOpen, setIsOpen] = useState(false)
  const [updating, setUpdating] = useState(false)

const getBadgeClass = () => {
    switch (status) {
      case 'CONFIRMED': return 'badge-green'
      case 'PENDING_PAYMENT': 
      case 'PENDING': return 'badge-orange'
      case 'CANCELLED': return 'badge-red'
      case 'PAYMENT_FAILED': return 'badge-red'
      default: return 'badge-blue'
    }
  }

    const getStatusIcon = () => {
    switch (status) {
      case 'CONFIRMED': return '✅'
      case 'PENDING_PAYMENT': 
      case 'PENDING': return '⏳'
      case 'CANCELLED': return '❌'
      case 'PAYMENT_FAILED': return '💔'
      default: return '📋'
    }
  }

  const handleChange = async (newStatus) => {
    if (newStatus === status) {
      setIsOpen(false)
      return
    }
    setUpdating(true)
    try {
      await onStatusChange(bookingId, newStatus)
      toast.success(`Status updated to ${newStatus}`)
    } catch (err) {
      toast.error('Failed to update status')
    } finally {
      setUpdating(false)
      setIsOpen(false)
    }
  }

  return (
    <div className="status-dropdown">
      <button 
        className={`badge ${getBadgeClass()} status-btn`}
        onClick={() => setIsOpen(!isOpen)}
        disabled={updating}
      >
        {updating ? '⏳' : status === 'CONFIRMED' ? '✅' : status === 'PENDING' ? '⏳' : '❌'} {status}
        <span className="dropdown-arrow">▼</span>
      </button>
      {isOpen && (
        <div className="status-menu">
          {STATUS_OPTIONS.map(opt => (
            <button
              key={opt}
              className={`status-option ${opt === status ? 'active' : ''}`}
              onClick={() => handleChange(opt)}
            >
              {opt === 'CONFIRMED' ? '✅' : opt === 'PENDING' ? '⏳' : '❌'} {opt}
            </button>
          ))}
        </div>
      )}
    </div>
  )
}

function formatDateTime(dt) {
  if (!dt) return '—'
  try {
    return format(new Date(dt.replace(' ', 'T')), 'dd MMM, hh:mm a')
  } catch {
    return dt
  }
}

function formatAmount(amount) {
  if (!amount && amount !== 0) return '—'
  return `₹${Number(amount).toLocaleString('en-IN')}`
}

export default function BookingTable({ bookings = [], newBookingIds = new Set(), onStatusChange }) {
  if (!bookings.length) {
    return (
      <div className="table-empty">
        <span className="empty-icon">📭</span>
        <p>No bookings yet.</p>
        <p className="empty-sub">Submit a booking to see live updates here.</p>
      </div>
    )
  }

  return (
    <div className="table-wrapper">
      <table className="booking-table">
        <thead>
          <tr>
            <th>#ID</th>
            <th>Customer</th>
            <th>Sport</th>
            <th>Venue</th>
            <th>Slot</th>
            <th>Duration</th>
            <th>Amount</th>
            <th>Status</th>
            <th>Created</th>
          </tr>
        </thead>
        <tbody>
          {bookings.map((b) => (
            <tr
              key={b.id}
              className={newBookingIds.has(b.id) ? 'new-row-highlight' : ''}
            >
              <td><span className="id-badge">#{b.id}</span></td>
              <td><span className="customer-name">{b.customerName}</span></td>
              <td><span className="sport-cell">{SPORT_ICONS[b.sportType] || '🏟️'} {b.sportType}</span></td>
              <td><span className="venue-cell" title={b.venue}>{b.venue?.split(',')[0]}</span></td>
              <td>{formatDateTime(b.slotDateTime)}</td>
              <td>{b.durationHours ? <span className="duration-badge">{b.durationHours}h</span> : '—'}</td>
              <td><span className="amount-cell">{formatAmount(b.amount)}</span></td>
              <td><StatusBadge status={b.status} bookingId={b.id} onStatusChange={onStatusChange} /></td>
              <td className="created-at">{formatDateTime(b.createdAt)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}