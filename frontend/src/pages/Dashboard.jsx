// import { useState, useEffect, useCallback, useRef } from 'react'
// import toast from 'react-hot-toast'
// import { getAllBookings, getDashboardStats, updateBookingStatus } from '../services/api'
// import { useWebSocket } from '../hooks/useWebSocket'
// import StatsCard from '../components/StatsCard'
// import BookingTable from '../components/BookingTable'
// import ConnectionStatus from '../components/ConnectionStatus'
// import NotificationPanel from '../components/NotificationPanel'
// import './Dashboard.css'

// export default function Dashboard() {
//   const [bookings, setBookings] = useState([])
//   const [stats, setStats] = useState(null)
//   const [loading, setLoading] = useState(true)
//   const [newCount, setNewCount] = useState(0)
//   const newBookingIds = useRef(new Set())

//   const fetchData = useCallback(async () => {
//     try {
//       const [bookingsRes, statsRes] = await Promise.all([
//         getAllBookings(),
//         getDashboardStats(),
//       ])
//       setBookings(bookingsRes || [])
//       setStats(statsRes || null)
//     } catch (err) {
//       toast.error('Failed to load dashboard data')
//       console.error(err)
//     } finally {
//       setLoading(false)
//     }
//   }, [])

//   useEffect(() => {
//     fetchData()
//   }, [fetchData])

//   const handleLiveBooking = useCallback((data) => {
//     // Handle different message types
//     const booking = data.booking || data
    
//     if (booking.id) {
//       newBookingIds.current.add(booking.id)

//       setBookings(prev => {
//         const exists = prev.some(b => b.id === booking.id)
//         if (exists) return prev
//         return [booking, ...prev]
//       })

//       setNewCount(c => c + 1)

//       // Update stats
//       setStats(prev => {
//         if (!prev) return prev
//         return {
//           ...prev,
//           totalBookings: (prev.totalBookings || 0) + 1,
//           totalRevenue: (prev.totalRevenue || 0) + (booking.amount || 0),
//         }
//       })

//       toast.success(
//         `🎉 New ${booking.sportType} booking!\n${booking.customerName} → ₹${booking.amount}`,
//         { duration: 4000 }
//       )

//       setTimeout(() => { newBookingIds.current.delete(booking.id) }, 3000)
//     }
//   }, [])

//   const { isConnected, connectionStatus } = useWebSocket(handleLiveBooking)

//   const handleStatusChange = useCallback(async (bookingId, newStatus) => {
//     try {
//       await updateBookingStatus(bookingId, newStatus)
//       setBookings(prev => prev.map(b => 
//         b.id === bookingId ? { ...b, status: newStatus } : b
//       ))
//       toast.success(`Booking #${bookingId} status updated to ${newStatus}`)
//       fetchData() // Refresh stats
//     } catch (err) {
//       toast.error('Failed to update status')
//       console.error(err)
//     }
//   }, [fetchData])

//   if (loading) {
//     return (
//       <div className="dashboard-loading">
//         <div className="spinner" style={{ width: 36, height: 36 }} />
//         <p>Loading dashboard...</p>
//       </div>
//     )
//   }

//   return (
//     <div className="dashboard">
//       <div className="page-header dashboard-header">
//         <div>
//           <h1 className="page-title">Live Dashboard</h1>
//           <p className="page-subtitle">
//             Real-time sports ground bookings via Apache Kafka
//           </p>
//         </div>
//         <div className="header-right">
//           <ConnectionStatus status={connectionStatus} isConnected={isConnected} />
//           {newCount > 0 && (
//             <span className="new-count-badge">
//               +{newCount} new
//             </span>
//           )}
//           <button className="btn btn-secondary" onClick={fetchData}>
//             🔄 Refresh
//           </button>
//         </div>
//       </div>

//       <div className="stats-grid">
//         <StatsCard
//           label="Total Bookings"
//           value={stats?.totalBookings ?? 0}
//           icon="🎯"
//           color="blue"
//         />
//         <StatsCard
//           label="Confirmed"
//           value={stats?.confirmedBookings ?? 0}
//           icon="✅"
//           color="green"
//         />
//         <StatsCard
//           label="Pending"
//           value={stats?.pendingBookings ?? 0}
//           icon="⏳"
//           color="orange"
//         />
//         <StatsCard
//           label="Revenue"
//           value={`₹${(stats?.totalRevenue ?? 0).toLocaleString('en-IN')}`}
//           icon="💰"
//           color="purple"
//         />
//       </div>

//       <div className="table-section">
//         <div className="section-header">
//           <h3 className="section-title">
//             Live Bookings
//             <span className="live-indicator">
//               <span className={`live-dot ${isConnected ? '' : 'disconnected'}`} />
//               {isConnected ? 'LIVE' : 'OFFLINE'}
//             </span>
//           </h3>
//           <span className="record-count">{bookings.length} records</span>
//         </div>
//         <BookingTable 
//           bookings={bookings} 
//           newBookingIds={newBookingIds.current} 
//           onStatusChange={handleStatusChange} 
//         />
//       </div>

//       <NotificationPanel />
//     </div>
//   )
// }


import { useState, useEffect, useCallback, useRef } from 'react'
import toast from 'react-hot-toast'
import { getAllBookings, getDashboardStats, updateBookingStatus } from '../services/api'
import { useWebSocket } from '../hooks/useWebSocket'
import StatsCard from '../components/StatsCard'
import BookingTable from '../components/BookingTable'
import ConnectionStatus from '../components/ConnectionStatus'
import NotificationPanel from '../components/NotificationPanel'
import './Dashboard.css'

export default function Dashboard() {
  const [bookings, setBookings] = useState([])
  const [stats, setStats] = useState(null)
  const [loading, setLoading] = useState(true)
  const [newCount, setNewCount] = useState(0)
  const newBookingIds = useRef(new Set())

  const fetchData = useCallback(async () => {
    try {
      const [bookingsRes, statsRes] = await Promise.all([
        getAllBookings(),
        getDashboardStats(),
      ])
      setBookings(bookingsRes || [])
      setStats(statsRes || null)
    } catch (err) {
      toast.error('Failed to load dashboard data')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchData()
  }, [fetchData])

  const handleLiveBooking = useCallback((data) => {
    const booking = data.booking || data
    
    if (booking.id) {
      newBookingIds.current.add(booking.id)

      setBookings(prev => {
        const exists = prev.some(b => b.id === booking.id)
        if (exists) return prev
        return [booking, ...prev]
      })

      setNewCount(c => c + 1)

      setStats(prev => {
        if (!prev) return prev
        const newStats = { ...prev }
        newStats.totalBookings = (prev.totalBookings || 0) + 1
        
        if (booking.status === 'CONFIRMED') {
          newStats.confirmedBookings = (prev.confirmedBookings || 0) + 1
          newStats.totalRevenue = (prev.totalRevenue || 0) + (booking.amount || 0)
        } else if (booking.status === 'PENDING_PAYMENT' || booking.status === 'PENDING') {
          newStats.pendingBookings = (prev.pendingBookings || 0) + 1
        } else if (booking.status === 'CANCELLED') {
          newStats.cancelledBookings = (prev.cancelledBookings || 0) + 1
        }
        
        return newStats
      })

      toast.success(
        `📢 New ${booking.sportType} booking from ${booking.customerName}`,
        { duration: 4000 }
      )

      setTimeout(() => { newBookingIds.current.delete(booking.id) }, 3000)
    }
  }, [])

  const { isConnected, connectionStatus } = useWebSocket(handleLiveBooking)

  const handleStatusChange = useCallback(async (bookingId, newStatus) => {
    try {
      await updateBookingStatus(bookingId, newStatus)
      setBookings(prev => prev.map(b => 
        b.id === bookingId ? { ...b, status: newStatus } : b
      ))
      
      // Update stats based on status change
      setStats(prev => {
        if (!prev) return prev
        const newStats = { ...prev }
        const oldBooking = bookings.find(b => b.id === bookingId)
        
        if (oldBooking) {
          // Remove from old status count
          if (oldBooking.status === 'CONFIRMED') {
            newStats.confirmedBookings = Math.max(0, (prev.confirmedBookings || 0) - 1)
            newStats.totalRevenue = Math.max(0, (prev.totalRevenue || 0) - (oldBooking.amount || 0))
          } else if (oldBooking.status === 'PENDING_PAYMENT' || oldBooking.status === 'PENDING') {
            newStats.pendingBookings = Math.max(0, (prev.pendingBookings || 0) - 1)
          } else if (oldBooking.status === 'CANCELLED') {
            newStats.cancelledBookings = Math.max(0, (prev.cancelledBookings || 0) - 1)
          }
          
          // Add to new status count
          if (newStatus === 'CONFIRMED') {
            newStats.confirmedBookings = (prev.confirmedBookings || 0) + 1
            newStats.totalRevenue = (prev.totalRevenue || 0) + (oldBooking.amount || 0)
          } else if (newStatus === 'PENDING_PAYMENT' || newStatus === 'PENDING') {
            newStats.pendingBookings = (prev.pendingBookings || 0) + 1
          } else if (newStatus === 'CANCELLED') {
            newStats.cancelledBookings = (prev.cancelledBookings || 0) + 1
          }
        }
        
        return newStats
      })
      
      toast.success(`Booking #${bookingId} status updated to ${newStatus}`)
      fetchData()
    } catch (err) {
      toast.error('Failed to update status')
      console.error(err)
    }
  }, [bookings, fetchData])

  if (loading) {
    return (
      <div className="dashboard-loading">
        <div className="spinner" style={{ width: 36, height: 36 }} />
        <p>Loading dashboard...</p>
      </div>
    )
  }

  return (
    <div className="dashboard">
      <div className="page-header dashboard-header">
        <div>
          <h1 className="page-title">Live Dashboard</h1>
          <p className="page-subtitle">
            Real-time sports ground bookings with Stripe payment integration
          </p>
        </div>
        <div className="header-right">
          <ConnectionStatus status={connectionStatus} isConnected={isConnected} />
          {newCount > 0 && (
            <span className="new-count-badge">
              +{newCount} new
            </span>
          )}
          <button className="btn btn-secondary" onClick={fetchData}>
            🔄 Refresh
          </button>
        </div>
      </div>

      <div className="stats-grid">
        <StatsCard
          label="Total Bookings"
          value={stats?.totalBookings ?? 0}
          icon="🎯"
          color="blue"
        />
        <StatsCard
          label="Confirmed"
          value={stats?.confirmedBookings ?? 0}
          icon="✅"
          color="green"
        />
        <StatsCard
          label="Pending Payment"
          value={stats?.pendingBookings ?? 0}
          icon="⏳"
          color="orange"
        />
        <StatsCard
          label="Cancelled"
          value={stats?.cancelledBookings ?? 0}
          icon="❌"
          color="red"
        />
        <StatsCard
          label="Revenue"
          value={`₹${(stats?.totalRevenue ?? 0).toLocaleString('en-IN')}`}
          icon="💰"
          color="purple"
        />
      </div>

      <div className="table-section">
        <div className="section-header">
          <h3 className="section-title">
            Live Bookings
            <span className="live-indicator">
              <span className={`live-dot ${isConnected ? '' : 'disconnected'}`} />
              {isConnected ? 'LIVE' : 'OFFLINE'}
            </span>
          </h3>
          <span className="record-count">{bookings.length} records</span>
        </div>
        <BookingTable 
          bookings={bookings} 
          newBookingIds={newBookingIds.current} 
          onStatusChange={handleStatusChange} 
        />
      </div>

      <NotificationPanel />
    </div>
  )
}