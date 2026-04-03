import './StatsCard.css'

const COLOR_MAP = {
  blue:   { border: '#3b82f6', glow: 'rgba(59,130,246,0.2)', bg: 'rgba(59,130,246,0.08)' },
  green:  { border: '#10b981', glow: 'rgba(16,185,129,0.2)', bg: 'rgba(16,185,129,0.08)' },
  orange: { border: '#f59e0b', glow: 'rgba(245,158,11,0.2)', bg: 'rgba(245,158,11,0.08)' },
  purple: { border: '#8b5cf6', glow: 'rgba(139,92,246,0.2)', bg: 'rgba(139,92,246,0.08)' },
  red:    { border: '#ef4444', glow: 'rgba(239,68,68,0.2)', bg: 'rgba(239,68,68,0.08)' },
}

export default function StatsCard({ label, value, icon, color = 'blue', trend }) {
  const c = COLOR_MAP[color] || COLOR_MAP.blue

  return (
    <div className="stats-card" style={{ borderColor: c.border, background: c.bg }}>
      <div className="stats-card-top">
        <span className="stats-icon">{icon}</span>
        <div className="stats-content">
          <span className="stats-label">{label}</span>
          <span className="stats-value">{value}</span>
        </div>
      </div>
      {trend && <div className="stats-trend" style={{ color: c.border }}>{trend}</div>}
    </div>
  )
}