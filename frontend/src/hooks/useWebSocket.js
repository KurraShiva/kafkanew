// import { useEffect, useRef, useState, useCallback } from 'react'
// import { Client } from '@stomp/stompjs'
// import SockJS from 'sockjs-client'

// export const useWebSocket = (onBooking, onDashboard, onNotification) => {
//   const clientRef    = useRef(null)
//   const [isConnected, setIsConnected]         = useState(false)
//   const [connectionStatus, setConnectionStatus] = useState('DISCONNECTED')

//   const connect = useCallback(() => {
//     if (clientRef.current?.active) return

//     setConnectionStatus('CONNECTING')

//     const client = new Client({
//       webSocketFactory: () => new SockJS('http://localhost:8082/ws'),
//       reconnectDelay: 5000,

//       onConnect: () => {
//         setIsConnected(true)
//         setConnectionStatus('CONNECTED')
//         console.log('🔌 WebSocket CONNECTED to /ws')

//         client.subscribe('/topic/bookings', (message) => {
//           try {
//             const booking = JSON.parse(message.body)
//             console.log('📡 Live booking received:', booking)
//             if (onBooking) onBooking(booking)
//           } catch (err) {
//             console.error('Failed to parse booking message:', err)
//           }
//         })

//         client.subscribe('/topic/dashboard', (message) => {
//           try {
//             const dashboard = JSON.parse(message.body)
//             console.log('📊 Dashboard update received:', dashboard)
//             if (onDashboard) onDashboard(dashboard)
//           } catch (err) {
//             console.error('Failed to parse dashboard message:', err)
//           }
//         })

//         client.subscribe('/topic/notifications', (message) => {
//           try {
//             const notification = JSON.parse(message.body)
//             console.log('🔔 Notification received:', notification)
//             window.dispatchEvent(new CustomEvent('kafka-notification', { detail: notification }))
//             if (onNotification) onNotification(notification)
//           } catch (err) {
//             console.error('Failed to parse notification message:', err)
//           }
//         })
//       },

//       onDisconnect: () => {
//         setIsConnected(false)
//         setConnectionStatus('DISCONNECTED')
//         console.log('🔌 WebSocket DISCONNECTED')
//       },

//       onStompError: (frame) => {
//         setIsConnected(false)
//         setConnectionStatus('ERROR')
//         console.error('STOMP error:', frame.headers?.message)
//       },

//       onWebSocketClose: () => {
//         setIsConnected(false)
//         setConnectionStatus('RECONNECTING')
//         console.log('🔄 WebSocket closed — will reconnect in 5s...')
//       },
//     })

//     client.activate()
//     clientRef.current = client
//   }, [onBooking, onDashboard, onNotification])

//   const disconnect = useCallback(() => {
//     if (clientRef.current?.active) {
//       clientRef.current.deactivate()
//       setIsConnected(false)
//       setConnectionStatus('DISCONNECTED')
//     }
//   }, [])

//   useEffect(() => {
//     connect()
//     return () => { disconnect() }
//   }, [connect])

//   return { isConnected, connectionStatus, disconnect }
// }



import { useEffect, useRef, useState, useCallback } from 'react'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

if (typeof window !== 'undefined' && !window.global) {
  window.global = window;
}

export const useWebSocket = (onBooking, onDashboard, onNotification) => {
  const clientRef = useRef(null)
  const [isConnected, setIsConnected] = useState(false)
  const [connectionStatus, setConnectionStatus] = useState('DISCONNECTED')

  const connect = useCallback(() => {
    if (clientRef.current?.active) return

    setConnectionStatus('CONNECTING')

    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8082/ws'),
      reconnectDelay: 5000,
      debug: (str) => console.log('[STOMP]', str),

      onConnect: () => {
        setIsConnected(true)
        setConnectionStatus('CONNECTED')
        console.log('✅ WebSocket connected')

        client.subscribe('/topic/bookings', (message) => {
          try {
            const data = JSON.parse(message.body)
            if (onBooking) onBooking(data)
          } catch (err) {
            console.error('Failed to parse booking message:', err)
          }
        })

        client.subscribe('/topic/dashboard', (message) => {
          try {
            const data = JSON.parse(message.body)
            if (onDashboard) onDashboard(data)
          } catch (err) {
            console.error('Failed to parse dashboard message:', err)
          }
        })

        client.subscribe('/topic/notifications', (message) => {
          try {
            const data = JSON.parse(message.body)
            window.dispatchEvent(new CustomEvent('kafka-notification', { detail: data }))
            if (onNotification) onNotification(data)
          } catch (err) {
            console.error('Failed to parse notification message:', err)
          }
        })

        client.subscribe('/topic/payments', (message) => {
          try {
            const data = JSON.parse(message.body)
            window.dispatchEvent(new CustomEvent('payment-update', { detail: data }))
          } catch (err) {
            console.error('Failed to parse payment message:', err)
          }
        })
      },

      onDisconnect: () => {
        setIsConnected(false)
        setConnectionStatus('DISCONNECTED')
      },

      onStompError: (frame) => {
        setIsConnected(false)
        setConnectionStatus('ERROR')
        console.error('STOMP error:', frame.headers?.message)
      },

      onWebSocketClose: () => {
        setIsConnected(false)
        setConnectionStatus('RECONNECTING')
      },
    })

    client.activate()
    clientRef.current = client
  }, [onBooking, onDashboard, onNotification])

  useEffect(() => {
    connect()
    return () => {
      if (clientRef.current?.active) {
        clientRef.current.deactivate()
      }
    }
  }, [connect])

  return { isConnected, connectionStatus }
}