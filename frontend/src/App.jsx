// // import { useState } from 'react'
// // import { Toaster } from 'react-hot-toast'
// // import Dashboard from './pages/Dashboard'
// // import BookingForm from './pages/BookingForm'
// // import KafkaInfo from './components/KafkaInfo'
// // import PaymentSuccess from './pages/PaymentSuccess'
// // import PaymentFailure from './pages/PaymentFailure'
// // import './App.css'

// // const NAV_ITEMS = [
// //   { id: 'dashboard', label: 'Live Dashboard', icon: '📊' },
// //   { id: 'create',    label: 'New Booking',    icon: '➕' },
// //   { id: 'kafka',     label: 'Kafka Info',     icon: '⚡' },
// // ]

// // export default function App() {
// //   const [activePage, setActivePage] = useState('dashboard')

// //   const getPageParams = () => {
// //     const params = new URLSearchParams(window.location.search)
// //     return Object.fromEntries(params.entries())
// //   }

// //   const renderPage = () => {
// //     const { page, ...queryParams } = getPageParams()
    
// //     switch (page) {
// //       case 'payment-success':
// //         return <PaymentSuccess {...queryParams} />
// //       case 'payment-failure':
// //         return <PaymentFailure {...queryParams} />
// //       default:
// //         return (
// //           <>
// //             {activePage === 'dashboard' && <Dashboard />}
// //             {activePage === 'create' && (
// //               <BookingForm onSuccess={() => setActivePage('dashboard')} />
// //             )}
// //             {activePage === 'kafka' && <KafkaInfo />}
// //           </>
// //         )
// //     }
// //   }

// //   return (
// //     <div className="app-layout">
// //       {/* ── Sidebar ──────────────────────────────── */}
// //       <aside className="sidebar">
// //         <div className="sidebar-brand">
// //           <span className="brand-icon">⚡</span>
// //           <div className="brand-text">
// //             <span className="brand-title">KafkaStream</span>
// //             <span className="brand-sub">Real-time Booking</span>
// //           </div>
// //         </div>

// //         <nav className="sidebar-nav">
// //           {NAV_ITEMS.map(item => (
// //             <button
// //               key={item.id}
// //               className={`nav-item ${activePage === item.id ? 'active' : ''}`}
// //               onClick={() => setActivePage(item.id)}
// //             >
// //               <span className="nav-icon">{item.icon}</span>
// //               <span className="nav-label">{item.label}</span>
// //             </button>
// //           ))}
// //         </nav>

// //         <div className="sidebar-footer">
// //           <div className="tech-stack">
// //             <div className="tech-badge">
// //               <span className="tech-dot" style={{ background: '#ff6b35' }} />
// //               Apache Kafka
// //             </div>
// //             <div className="tech-badge">
// //               <span className="tech-dot" style={{ background: '#6db33f' }} />
// //               Spring Boot
// //             </div>
// //             <div className="tech-badge">
// //               <span className="tech-dot" style={{ background: '#61dafb' }} />
// //               React + Vite
// //             </div>
// //             <div className="tech-badge">
// //               <span className="tech-dot" style={{ background: '#00758f' }} />
// //               MySQL
// //             </div>
// //           </div>
// //         </div>
// //       </aside>

// //       {/* ── Main Content ─────────────────────────── */}
// //       <main className="main-content">
// //         {renderPage()}
// //       </main>

// //       {/* ── Toast Notifications ───────────────────── */}
// //       <Toaster
// //         position="top-right"
// //         toastOptions={{
// //           style: {
// //             background: '#1a2235',
// //             color: '#f1f5f9',
// //             border: '1px solid #2d3a50',
// //             borderRadius: '10px',
// //             fontSize: '0.875rem',
// //           },
// //           success: { iconTheme: { primary: '#10b981', secondary: '#fff' } },
// //           error:   { iconTheme: { primary: '#ef4444', secondary: '#fff' } },
// //         }}
// //       />
// //     </div>
// //   )
// // }



// import { useState, useEffect } from 'react'
// import { Toaster } from 'react-hot-toast'
// import Dashboard from './pages/Dashboard'
// import BookingForm from './pages/BookingForm'
// import KafkaInfo from './components/KafkaInfo'
// import PaymentSuccess from './pages/PaymentSuccess'
// import PaymentFailure from './pages/PaymentFailure'
// import './App.css'

// const NAV_ITEMS = [
//   { id: 'dashboard', label: 'Live Dashboard', icon: '📊' },
//   { id: 'create', label: 'New Booking', icon: '➕' },
//   { id: 'kafka', label: 'Kafka Info', icon: '⚡' },
// ]

// export default function App() {
//   const [activePage, setActivePage] = useState('dashboard')
//   const [currentPage, setCurrentPage] = useState('dashboard')

//   useEffect(() => {
//     const params = new URLSearchParams(window.location.search)
//     const page = params.get('page')
//     if (page === 'payment-success') {
//       setCurrentPage('payment-success')
//     } else if (page === 'payment-failure') {
//       setCurrentPage('payment-failure')
//     } else {
//       setCurrentPage(activePage)
//     }
//   }, [activePage, window.location.search])

//   const renderPage = () => {
//     const params = new URLSearchParams(window.location.search)
    
//     if (currentPage === 'payment-success') {
//       return <PaymentSuccess />
//     }
//     if (currentPage === 'payment-failure') {
//       return <PaymentFailure />
//     }
    
//     switch (activePage) {
//       case 'dashboard':
//         return <Dashboard />
//       case 'create':
//         return <BookingForm onSuccess={() => setActivePage('dashboard')} />
//       case 'kafka':
//         return <KafkaInfo />
//       default:
//         return <Dashboard />
//     }
//   }

//   return (
//     <div className="app-layout">
//       <aside className="sidebar">
//         <div className="sidebar-brand">
//           <span className="brand-icon">⚡</span>
//           <div className="brand-text">
//             <span className="brand-title">KafkaStream</span>
//             <span className="brand-sub">Real-time Booking</span>
//           </div>
//         </div>

//         <nav className="sidebar-nav">
//           {NAV_ITEMS.map(item => (
//             <button
//               key={item.id}
//               className={`nav-item ${activePage === item.id ? 'active' : ''}`}
//               onClick={() => {
//                 setActivePage(item.id)
//                 setCurrentPage(item.id)
//                 window.history.pushState({}, '', `/?page=${item.id}`)
//               }}
//             >
//               <span className="nav-icon">{item.icon}</span>
//               <span className="nav-label">{item.label}</span>
//             </button>
//           ))}
//         </nav>

//         <div className="sidebar-footer">
//           <div className="tech-stack">
//             <div className="tech-badge">
//               <span className="tech-dot" style={{ background: '#ff6b35' }} />
//               Apache Kafka
//             </div>
//             <div className="tech-badge">
//               <span className="tech-dot" style={{ background: '#6db33f' }} />
//               Spring Boot
//             </div>
//             <div className="tech-badge">
//               <span className="tech-dot" style={{ background: '#61dafb' }} />
//               React + Vite
//             </div>
//             <div className="tech-badge">
//               <span className="tech-dot" style={{ background: '#00758f' }} />
//               MySQL
//             </div>
//           </div>
//         </div>
//       </aside>

//       <main className="main-content">
//         {renderPage()}
//       </main>

//       <Toaster
//         position="top-right"
//         toastOptions={{
//           style: {
//             background: '#1a2235',
//             color: '#f1f5f9',
//             border: '1px solid #2d3a50',
//             borderRadius: '10px',
//           },
//           success: { iconTheme: { primary: '#10b981', secondary: '#fff' } },
//           error: { iconTheme: { primary: '#ef4444', secondary: '#fff' } },
//         }}
//       />
//     </div>
//   )
// }


// import { useEffect, useState } from 'react'
// import { BrowserRouter, Routes, Route, useNavigate, useLocation } from 'react-router-dom'
// import { Toaster } from 'react-hot-toast'
// import Dashboard from './pages/Dashboard'
// import BookingForm from './pages/BookingForm'
// import KafkaInfo from './components/KafkaInfo'
// import PaymentSuccess from './pages/PaymentSuccess'
// import PaymentFailure from './pages/PaymentFailure'
// import './App.css'

// const NAV_ITEMS = [
//   { id: 'dashboard', label: 'Live Dashboard', icon: '📊' },
//   { id: 'create', label: 'New Booking', icon: '➕' },
//   { id: 'kafka', label: 'Kafka Info', icon: '⚡' },
// ]

// // This component handles the main app layout with navigation
// function AppLayout() {
//   const [activePage, setActivePage] = useState('dashboard')
//   const location = useLocation()
//   const navigate = useNavigate()

//   useEffect(() => {
//     // Check if we're on a payment page
//     if (location.pathname === '/payment-success') {
//       return
//     }
//     if (location.pathname === '/payment-failure') {
//       return
//     }
//     // Update active page based on URL param
//     const params = new URLSearchParams(location.search)
//     const page = params.get('page')
//     if (page && ['dashboard', 'create', 'kafka'].includes(page)) {
//       setActivePage(page)
//     }
//   }, [location])

//   const renderPage = () => {
//     switch (activePage) {
//       case 'dashboard':
//         return <Dashboard />
//       case 'create':
//         return <BookingForm onSuccess={() => navigate('/?page=dashboard')} />
//       case 'kafka':
//         return <KafkaInfo />
//       default:
//         return <Dashboard />
//     }
//   }

//   return (
//     <div className="app-layout">
//       <aside className="sidebar">
//         <div className="sidebar-brand">
//           <span className="brand-icon">⚡</span>
//           <div className="brand-text">
//             <span className="brand-title">KafkaStream</span>
//             <span className="brand-sub">Real-time Booking</span>
//           </div>
//         </div>

//         <nav className="sidebar-nav">
//           {NAV_ITEMS.map(item => (
//             <button
//               key={item.id}
//               className={`nav-item ${activePage === item.id ? 'active' : ''}`}
//               onClick={() => {
//                 setActivePage(item.id)
//                 navigate(`/?page=${item.id}`)
//               }}
//             >
//               <span className="nav-icon">{item.icon}</span>
//               <span className="nav-label">{item.label}</span>
//             </button>
//           ))}
//         </nav>

//         <div className="sidebar-footer">
//           <div className="tech-stack">
//             <div className="tech-badge">
//               <span className="tech-dot" style={{ background: '#ff6b35' }} />
//               Apache Kafka
//             </div>
//             <div className="tech-badge">
//               <span className="tech-dot" style={{ background: '#6db33f' }} />
//               Spring Boot
//             </div>
//             <div className="tech-badge">
//               <span className="tech-dot" style={{ background: '#61dafb' }} />
//               React + Vite
//             </div>
//             <div className="tech-badge">
//               <span className="tech-dot" style={{ background: '#00758f' }} />
//               MySQL
//             </div>
//           </div>
//         </div>
//       </aside>

//       <main className="main-content">
//         {renderPage()}
//       </main>
//     </div>
//   )
// }

// // Main App component with Router
// export default function App() {
//   return (
//     <BrowserRouter>
//       <Routes>
//         <Route path="/" element={<AppLayout />} />
//         <Route path="/payment-success" element={<PaymentSuccess />} />
//         <Route path="/payment-failure" element={<PaymentFailure />} />
//       </Routes>
//       <Toaster
//         position="top-right"
//         toastOptions={{
//           style: {
//             background: '#1a2235',
//             color: '#f1f5f9',
//             border: '1px solid #2d3a50',
//             borderRadius: '10px',
//           },
//           success: { iconTheme: { primary: '#10b981', secondary: '#fff' } },
//           error: { iconTheme: { primary: '#ef4444', secondary: '#fff' } },
//         }}
//       />
//     </BrowserRouter>
//   )
// }


import { useState, useEffect } from 'react'
import { Toaster } from 'react-hot-toast'
import Dashboard from './pages/Dashboard'
import BookingForm from './pages/BookingForm'
import KafkaInfo from './components/KafkaInfo'
import PaymentSuccess from './pages/PaymentSuccess'
import PaymentFailure from './pages/PaymentFailure'
import './App.css'

const NAV_ITEMS = [
  { id: 'dashboard', label: 'Live Dashboard', icon: '📊' },
  { id: 'create', label: 'New Booking', icon: '➕' },
  { id: 'kafka', label: 'Kafka Info', icon: '⚡' },
]

export default function App() {
  const [activePage, setActivePage] = useState('dashboard')
  const [currentPage, setCurrentPage] = useState('dashboard')

  useEffect(() => {
    const handleUrlChange = () => {
      const params = new URLSearchParams(window.location.search)
      const page = params.get('page')
      
      if (page === 'payment-success') {
        setCurrentPage('payment-success')
      } else if (page === 'payment-failure') {
        setCurrentPage('payment-failure')
      } else {
        setCurrentPage(activePage)
      }
    }

    window.addEventListener('popstate', handleUrlChange)
    handleUrlChange()

    return () => {
      window.removeEventListener('popstate', handleUrlChange)
    }
  }, [activePage])

  const navigateTo = (page) => {
    setActivePage(page)
    setCurrentPage(page)
    window.history.pushState({}, '', `/?page=${page}`)
  }

  const renderPage = () => {
    if (currentPage === 'payment-success') {
      return <PaymentSuccess />
    }
    if (currentPage === 'payment-failure') {
      return <PaymentFailure />
    }
    
    switch (activePage) {
      case 'dashboard':
        return <Dashboard />
      case 'create':
        return <BookingForm onSuccess={() => navigateTo('dashboard')} />
      case 'kafka':
        return <KafkaInfo />
      default:
        return <Dashboard />
    }
  }

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <div className="sidebar-brand">
          <span className="brand-icon">⚡</span>
          <div className="brand-text">
            <span className="brand-title">KafkaStream</span>
            <span className="brand-sub">Real-time Booking</span>
          </div>
        </div>

        <nav className="sidebar-nav">
          {NAV_ITEMS.map(item => (
            <button
              key={item.id}
              className={`nav-item ${activePage === item.id ? 'active' : ''}`}
              onClick={() => navigateTo(item.id)}
            >
              <span className="nav-icon">{item.icon}</span>
              <span className="nav-label">{item.label}</span>
            </button>
          ))}
        </nav>

        <div className="sidebar-footer">
          <div className="tech-stack">
            <div className="tech-badge"><span className="tech-dot" style={{ background: '#ff6b35' }} />Kafka</div>
            <div className="tech-badge"><span className="tech-dot" style={{ background: '#6db33f' }} />Spring Boot</div>
            <div className="tech-badge"><span className="tech-dot" style={{ background: '#61dafb' }} />React</div>
            <div className="tech-badge"><span className="tech-dot" style={{ background: '#635bff' }} />Stripe</div>
          </div>
        </div>
      </aside>

      <main className="main-content">
        {renderPage()}
      </main>

      <Toaster
        position="top-right"
        toastOptions={{
          style: { background: '#1a2235', color: '#f1f5f9', border: '1px solid #2d3a50' },
          success: { iconTheme: { primary: '#10b981', secondary: '#fff' } },
          error: { iconTheme: { primary: '#ef4444', secondary: '#fff' } },
        }}
      />
    </div>
  )
}