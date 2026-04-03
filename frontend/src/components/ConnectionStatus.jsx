import './ConnectionStatus.css'

const STATUS_CONFIG = {
  CONNECTED:    { label: 'LIVE', color: 'green' },
  CONNECTING:   { label: 'Connecting...', color: 'orange' },
  RECONNECTING: { label: 'Reconnecting...', color: 'orange' },
  DISCONNECTED: { label: 'Offline', color: 'red' },
  ERROR:        { label: 'Error', color: 'red' },
}

export default function ConnectionStatus({ status, isConnected }) {
  const cfg = STATUS_CONFIG[status] || STATUS_CONFIG.DISCONNECTED

  return (
    <div className={`connection-status status-${cfg.color}`}>
      <span className={`cs-dot ${isConnected ? '' : 'cs-dot-offline'}`} />
      <span className="cs-label">WebSocket {cfg.label}</span>
    </div>
  )
}