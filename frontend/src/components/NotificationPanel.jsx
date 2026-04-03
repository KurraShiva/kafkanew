// import { useState, useEffect } from 'react'

// const PRIORITY_COLORS = {
//   HIGH: 'bg-red-100 border-red-500 text-red-800',
//   MEDIUM: 'bg-yellow-100 border-yellow-500 text-yellow-800',
//   LOW: 'bg-green-100 border-green-500 text-green-800',
// }

// export default function NotificationPanel() {
//   const [notifications, setNotifications] = useState([])
//   const [showPanel, setShowPanel] = useState(true)

//   useEffect(() => {
//     const handleNotification = (event) => {
//       const notification = event.detail
//       setNotifications(prev => [notification, ...prev].slice(0, 50))
//     }

//     window.addEventListener('kafka-notification', handleNotification)
//     return () => window.removeEventListener('kafka-notification', handleNotification)
//   }, [])

//   const clearNotifications = () => setNotifications([])

//   if (!showPanel) {
//     return (
//       <button 
//         className="fixed bottom-4 right-4 bg-blue-500 text-white p-3 rounded-full shadow-lg hover:bg-blue-600 transition"
//         onClick={() => setShowPanel(true)}
//       >
//         🔔
//       </button>
//     )
//   }

//   return (
//     <div className="fixed bottom-4 right-4 w-80 max-h-96 bg-white rounded-lg shadow-2xl border border-gray-200 overflow-hidden">
//       <div className="bg-gray-100 px-4 py-2 flex justify-between items-center border-b">
//         <h3 className="font-semibold text-gray-700">🔔 Notifications</h3>
//         <div className="flex gap-2">
//           <button 
//             onClick={clearNotifications}
//             className="text-xs text-gray-500 hover:text-gray-700"
//           >
//             Clear
//           </button>
//           <button 
//             onClick={() => setShowPanel(false)}
//             className="text-gray-500 hover:text-gray-700"
//           >
//             ✕
//           </button>
//         </div>
//       </div>
      
//       <div className="overflow-y-auto max-h-72">
//         {notifications.length === 0 ? (
//           <div className="p-4 text-center text-gray-500 text-sm">
//             No notifications yet
//           </div>
//         ) : (
//           notifications.map((notif, idx) => (
//             <div 
//               key={idx} 
//               className={`p-3 border-b border-gray-100 text-sm ${PRIORITY_COLORS[notif.priority] || 'bg-gray-50'}`}
//             >
//               <div className="font-medium">{notif.notificationType}</div>
//               <div className="text-xs mt-1">{notif.message}</div>
//               <div className="text-xs text-gray-500 mt-1">
//                 {notif.timestamp ? new Date(notif.timestamp).toLocaleTimeString() : 'Just now'}
//               </div>
//             </div>
//           ))
//         )}
//       </div>
//     </div>
//   )
// }


import { useState, useEffect } from 'react'

const PRIORITY_COLORS = {
  HIGH: 'rgba(239,68,68,0.1)',
  MEDIUM: 'rgba(245,158,11,0.1)',
  LOW: 'rgba(16,185,129,0.1)',
}

export default function NotificationPanel() {
  const [notifications, setNotifications] = useState([])
  const [showPanel, setShowPanel] = useState(true)

  useEffect(() => {
    const handleNotification = (event) => {
      const notification = event.detail
      setNotifications(prev => [notification, ...prev].slice(0, 50))
    }

    window.addEventListener('kafka-notification', handleNotification)
    return () => window.removeEventListener('kafka-notification', handleNotification)
  }, [])

  const clearNotifications = () => setNotifications([])

  if (!showPanel) {
    return (
      <button 
        className="btn btn-primary"
        style={{ position: 'fixed', bottom: '1rem', right: '1rem', borderRadius: '99px' }}
        onClick={() => setShowPanel(true)}
      >
        🔔 {notifications.length}
      </button>
    )
  }

  return (
    <div className="card" style={{ position: 'fixed', bottom: '1rem', right: '1rem', width: '320px', maxHeight: '400px', overflow: 'hidden', zIndex: 1000 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem', paddingBottom: '0.5rem', borderBottom: '1px solid var(--border)' }}>
        <h3 style={{ fontSize: '0.9rem', fontWeight: 700 }}>🔔 Notifications</h3>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button onClick={clearNotifications} className="btn btn-secondary" style={{ padding: '2px 8px', fontSize: '0.7rem' }}>Clear</button>
          <button onClick={() => setShowPanel(false)} className="btn btn-secondary" style={{ padding: '2px 8px', fontSize: '0.7rem' }}>✕</button>
        </div>
      </div>
      
      <div style={{ overflowY: 'auto', maxHeight: '320px' }}>
        {notifications.length === 0 ? (
          <p style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '1rem' }}>No notifications</p>
        ) : (
          notifications.map((notif, idx) => (
            <div 
              key={idx} 
              style={{ 
                padding: '0.75rem', 
                borderBottom: '1px solid var(--border)',
                background: PRIORITY_COLORS[notif.priority] || 'transparent',
                borderRadius: '6px',
                marginBottom: '0.5rem'
              }}
            >
              <div style={{ fontWeight: 600, fontSize: '0.8rem' }}>{notif.notificationType}</div>
              <div style={{ fontSize: '0.7rem', color: 'var(--text-secondary)', marginTop: '4px' }}>{notif.message}</div>
              <div style={{ fontSize: '0.65rem', color: 'var(--text-muted)', marginTop: '4px' }}>
                {notif.timestamp ? new Date(notif.timestamp).toLocaleTimeString() : 'Just now'}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  )
}