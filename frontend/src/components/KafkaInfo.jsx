// import { useState, useEffect } from 'react'
// import { 
//   getKafkaInfo, 
//   getKafkaTopics, 
//   getKafkaConsumerGroups, 
//   getKafkaOffsets,
//   getProducerBookingTopic,
//   getProducerNotificationTopic,
//   getProducerDashboardTopic 
// } from '../services/api'
// import './KafkaInfo.css'

// const TOPIC_CONFIG = {
//   'booking-events': {
//     name: 'booking-events',
//     displayName: '📋 Booking Events',
//     description: 'Stores all booking requests (PENDING, CONFIRMED, CANCELLED)',
//     color: '#10b981'
//   },
//   'notification-events': {
//     name: 'notification-events', 
//     displayName: '🔔 Notification Events',
//     description: 'Stores booking confirmations and alerts',
//     color: '#f59e0b'
//   },
//   'dashboard-events': {
//     name: 'dashboard-events',
//     displayName: '📊 Dashboard Events',
//     description: 'Stores real-time dashboard analytics',
//     color: '#3b82f6'
//   }
// }

// export default function KafkaInfo() {
//   const [activeTab, setActiveTab] = useState('overview')
//   const [kafkaInfo, setKafkaInfo] = useState(null)
//   const [topicsData, setTopicsData] = useState(null)
//   const [consumerGroupsData, setConsumerGroupsData] = useState(null)
//   const [loading, setLoading] = useState(true)
//   const [selectedTopic, setSelectedTopic] = useState(null)
//   const [topicOffsets, setTopicOffsets] = useState({})

//   const fetchData = async () => {
//     try {
//       const [info, topics, groups] = await Promise.all([
//         getKafkaInfo(),
//         getKafkaTopics(),
//         getKafkaConsumerGroups()
//       ])
//       setKafkaInfo(info)
//       setTopicsData(topics)
//       setConsumerGroupsData(groups)
//       if (Object.keys(topics).length > 0) {
//         setSelectedTopic(Object.keys(topics)[0])
//       }
      
//       const offsetsPromises = Object.keys(topics).map(async (topicName) => {
//         try {
//           const offset = await getKafkaOffsets(topicName)
//           return { topic: topicName, data: offset }
//         } catch {
//           return { topic: topicName, data: null }
//         }
//       })
//       const offsetsResults = await Promise.all(offsetsPromises)
//       const offsetsMap = {}
//       offsetsResults.forEach(({ topic, data }) => {
//         offsetsMap[topic] = data
//       })
//       setTopicOffsets(offsetsMap)
//     } catch (err) {
//       console.error('Failed to fetch Kafka info:', err)
//     } finally {
//       setLoading(false)
//     }
//   }

//   useEffect(() => {
//     fetchData()
//     const interval = setInterval(fetchData, 10000)
//     return () => clearInterval(interval)
//   }, [])

//   if (loading) {
//     return <div className="kafka-info-panel"><div className="spinner" /> Loading Kafka info...</div>
//   }

//   const topics = topicsData ? Object.entries(topicsData).map(([name, data]) => ({ name, ...data })) : []
//   const consumerGroups = consumerGroupsData ? Object.entries(consumerGroupsData).map(([id, data]) => ({ id, ...data })) : []

//   const getTopicInfo = (topicName) => {
//     return topics.find(t => t.name === topicName) || null
//   }

//   const bookingTopic = getTopicInfo('booking-events')
//   const notificationTopic = getTopicInfo('notification-events')
//   const dashboardTopic = getTopicInfo('dashboard-events')

//   const getConsumerGroupForTopic = (topicName) => {
//     return consumerGroups.filter(g => 
//       g.assignedPartitions?.assigned?.some(p => p.topic === topicName)
//     )
//   }

//   const renderTopicCard = (topic, topicName, config) => {
//     if (!topic) return null
//     const configData = TOPIC_CONFIG[topicName] || { displayName: topicName, color: '#6b7280' }
//     const offsetData = topicOffsets[topicName]
//     const consumers = getConsumerGroupForTopic(topicName)

//     return (
//       <div className="topic-detail-card" key={topicName} style={{ borderLeftColor: configData.color }}>
//         <div className="topic-header">
//           <h3>{configData.displayName}</h3>
//           <span className="topic-status active">Active</span>
//         </div>
//         <p className="topic-description">{configData.description || configData.name}</p>
        
//         <div className="topic-metrics">
//           <div className="metric">
//             <span className="metric-value">{topic.partitions}</span>
//             <span className="metric-label">Partitions</span>
//           </div>
//           <div className="metric">
//             <span className="metric-value">{topic.replicationFactor}</span>
//             <span className="metric-label">Replicas</span>
//           </div>
//           <div className="metric">
//             <span className="metric-value">{offsetData?.totalMessages || 0}</span>
//             <span className="metric-label">Messages</span>
//           </div>
//           <div className="metric">
//             <span className="metric-value">{consumers.length}</span>
//             <span className="metric-label">Consumers</span>
//           </div>
//         </div>

//         <div className="partition-details">
//           <h4>Partition Details</h4>
//           <div className="partitions-grid">
//             {topic.partitionDetails?.map(p => (
//               <div key={p.partitionId} className="partition-item">
//                 <span className="partition-id">P{p.partitionId}</span>
//                 <span className="partition-leader">Ldr: {p.leader}</span>
//                 <span className="partition-replicas">Rep: [{p.replicas?.join(',')}]</span>
//                 <span className="partition-isr">ISR: [{p.isr?.join(',')}]</span>
//               </div>
//             ))}
//           </div>
//         </div>

//         <div className="consumer-groups-section">
//           <h4>Consumer Groups</h4>
//           {consumers.length > 0 ? (
//             <div className="consumer-groups-list">
//               {consumers.map(cg => (
//                 <div key={cg.groupId} className="consumer-group-item">
//                   <span className="cg-name">{cg.groupId}</span>
//                   <span className={`cg-state ${cg.state?.toLowerCase()}`}>{cg.state}</span>
//                   <span className="cg-members">{cg.members} members</span>
//                 </div>
//               ))}
//             </div>
//           ) : (
//             <p className="no-consumers">No active consumers</p>
//           )}
//         </div>
//       </div>
//     )
//   }

//   const renderOverview = () => (
//     <div className="kafka-overview">
//       <div className="overview-stats">
//         <div className="stat-card">
//           <span className="stat-icon">📚</span>
//           <span className="stat-value">{topics.length}</span>
//           <span className="stat-label">Topics</span>
//         </div>
//         <div className="stat-card">
//           <span className="stat-icon">👥</span>
//           <span className="stat-value">{consumerGroups.length}</span>
//           <span className="stat-label">Consumer Groups</span>
//         </div>
//         <div className="stat-card">
//           <span className="stat-icon">📊</span>
//           <span className="stat-value">{topics.reduce((acc, t) => acc + (t.partitions || 0), 0)}</span>
//           <span className="stat-label">Total Partitions</span>
//         </div>
//         <div className="stat-card">
//           <span className="stat-icon">💬</span>
//           <span className="stat-value">{Object.values(topicOffsets).reduce((acc, o) => acc + (o?.totalMessages || 0), 0)}</span>
//           <span className="stat-label">Total Messages</span>
//         </div>
//       </div>

//       <div className="topics-section">
//         <h3>Kafka Topics</h3>
//         <div className="topics-grid">
//           {renderTopicCard(bookingTopic, 'booking-events', TOPIC_CONFIG['booking-events'])}
//           {renderTopicCard(notificationTopic, 'notification-events', TOPIC_CONFIG['notification-events'])}
//           {renderTopicCard(dashboardTopic, 'dashboard-events', TOPIC_CONFIG['dashboard-events'])}
//         </div>
//       </div>
//     </div>
//   )

//   return (
//     <div className="kafka-info-panel">
//       <div className="kafka-info-header">
//         <h2>⚡ Kafka Cluster Info</h2>
//         <button className="btn btn-secondary" onClick={fetchData} style={{ fontSize: '0.8rem' }}>
//           🔄 Refresh
//         </button>
//       </div>

//       <div className="kafka-tabs">
//         <button 
//           className={`kafka-tab ${activeTab === 'overview' ? 'active' : ''}`}
//           onClick={() => setActiveTab('overview')}
//         >
//           📊 Overview
//         </button>
//         <button 
//           className={`kafka-tab ${activeTab === 'topics' ? 'active' : ''}`}
//           onClick={() => setActiveTab('topics')}
//         >
//           📚 Topics ({topics.length})
//         </button>
//         <button 
//           className={`kafka-tab ${activeTab === 'groups' ? 'active' : ''}`}
//           onClick={() => setActiveTab('groups')}
//         >
//           👥 Groups ({consumerGroups.length})
//         </button>
//         <button 
//           className={`kafka-tab ${activeTab === 'partitions' ? 'active' : ''}`}
//           onClick={() => setActiveTab('partitions')}
//         >
//           🔀 Partitions
//         </button>
//       </div>

//       <div className="kafka-content">
//         {activeTab === 'overview' && renderOverview()}
        
//         {activeTab === 'topics' && (
//           <div className="kafka-topics">
//             <table className="kafka-table">
//               <thead>
//                 <tr>
//                   <th>Topic Name</th>
//                   <th>Partitions</th>
//                   <th>Replicas</th>
//                   <th>Messages</th>
//                   <th>Partitions</th>
//                 </tr>
//               </thead>
//               <tbody>
//                 {topics.map((topic) => (
//                   <tr key={topic.name}>
//                     <td><code>{topic.name}</code></td>
//                     <td><span className="badge partitions">{topic.partitions}</span></td>
//                     <td><span className="badge replication">{topic.replicationFactor}</span></td>
//                     <td>{topicOffsets[topic.name]?.totalMessages || 0}</td>
//                     <td>
//                       {topic.partitionDetails?.map(p => (
//                         <span key={p.partitionId} className="partition-badge">
//                           P{p.partitionId}
//                         </span>
//                       ))}
//                     </td>
//                   </tr>
//                 ))}
//               </tbody>
//             </table>
//           </div>
//         )}

//         {activeTab === 'groups' && (
//           <div className="kafka-groups">
//             <table className="kafka-table">
//               <thead>
//                 <tr>
//                   <th>Group ID</th>
//                   <th>State</th>
//                   <th>Members</th>
//                   <th>Assigned Partitions</th>
//                 </tr>
//               </thead>
//               <tbody>
//                 {consumerGroups.map((group) => (
//                   <tr key={group.groupId}>
//                     <td><code>{group.groupId}</code></td>
//                     <td><span className={`badge ${group.state === 'STABLE' ? 'badge-green' : 'badge-orange'}`}>{group.state}</span></td>
//                     <td>{group.members}</td>
//                     <td>
//                       {group.assignedPartitions?.assigned?.map((p, i) => (
//                         <span key={i} className="partition-badge">{p.topic}:{p.partition}</span>
//                       )) || '-'}
//                     </td>
//                   </tr>
//                 ))}
//               </tbody>
//             </table>
//           </div>
//         )}

//         {activeTab === 'partitions' && (
//           <div className="kafka-partitions">
//             <div className="topic-selector">
//               <label>Select Topic:</label>
//               <select value={selectedTopic || ''} onChange={(e) => setSelectedTopic(e.target.value)}>
//                 {topics.map(t => <option key={t.name} value={t.name}>{t.name}</option>)}
//               </select>
//             </div>
//             {selectedTopic && topics.find(t => t.name === selectedTopic) && (
//               <table className="kafka-table">
//                 <thead>
//                   <tr>
//                     <th>Partition</th>
//                     <th>Leader</th>
//                     <th>Replicas</th>
//                     <th>ISR</th>
//                     <th>Messages</th>
//                   </tr>
//                 </thead>
//                 <tbody>
//                   {topics.find(t => t.name === selectedTopic)?.partitionDetails?.map(p => (
//                     <tr key={p.partitionId}>
//                       <td><span className="badge partitions">{p.partitionId}</span></td>
//                       <td>{p.leader}</td>
//                       <td>{p.replicas?.join(', ')}</td>
//                       <td>{p.isr?.join(', ')}</td>
//                       <td>{topicOffsets[selectedTopic]?.partitions?.find(part => part.partition === p.partitionId)?.lag || 0}</td>
//                     </tr>
//                   ))}
//                 </tbody>
//               </table>
//             )}
//           </div>
//         )}
//       </div>
//     </div>
//   )
// }

import { useState, useEffect } from 'react'
import { 
  getKafkaInfo, 
  getKafkaTopics, 
  getKafkaConsumerGroups, 
  getKafkaOffsets,
  getConsumerGroupOffsets,
  getAllOffsets 
} from '../services/api'
import './KafkaInfo.css'

export default function KafkaInfo() {
  const [activeTab, setActiveTab] = useState('topics')
  const [loading, setLoading] = useState(true)
  const [kafkaInfo, setKafkaInfo] = useState(null)
  const [topics, setTopics] = useState([])
  const [consumerGroups, setConsumerGroups] = useState([])
  const [selectedTopic, setSelectedTopic] = useState(null)
  const [topicOffsets, setTopicOffsets] = useState({})
  const [consumerOffsets, setConsumerOffsets] = useState({})
  const [refreshInterval, setRefreshInterval] = useState(null)

  const fetchAllData = async () => {
    try {
      setLoading(true)
      
      // Fetch all Kafka data in parallel
      const [info, topicsData, groupsData] = await Promise.all([
        getKafkaInfo(),
        getKafkaTopics(),
        getKafkaConsumerGroups()
      ])
      
      setKafkaInfo(info)
      
      // Process topics
      const topicsList = Object.entries(topicsData).map(([name, data]) => ({
        name,
        ...data,
        partitions: data.partitions || 0,
        replicationFactor: data.replicationFactor || 1,
        partitionDetails: data.partitionDetails || []
      }))
      setTopics(topicsList)
      
      // Process consumer groups
      const groupsList = Object.entries(groupsData).map(([id, data]) => ({
        groupId: id,
        ...data,
        members: data.members || 0,
        state: data.state || 'UNKNOWN',
        assignedPartitions: data.assignedPartitions || { assigned: [] }
      }))
      setConsumerGroups(groupsList)
      
      // Fetch offsets for all topics
      const offsetsPromises = topicsList.map(async (topic) => {
        try {
          const offsetData = await getKafkaOffsets(topic.name)
          return { topic: topic.name, data: offsetData }
        } catch (err) {
          console.error(`Failed to fetch offsets for ${topic.name}:`, err)
          return { topic: topic.name, data: null }
        }
      })
      
      const offsetsResults = await Promise.all(offsetsPromises)
      const offsetsMap = {}
      offsetsResults.forEach(({ topic, data }) => {
        offsetsMap[topic] = data
      })
      setTopicOffsets(offsetsMap)
      
      // Fetch consumer group offsets for each group
      const consumerOffsetsPromises = groupsList.map(async (group) => {
        try {
          const offsetData = await getConsumerGroupOffsets(group.groupId)
          return { groupId: group.groupId, data: offsetData }
        } catch (err) {
          console.error(`Failed to fetch offsets for group ${group.groupId}:`, err)
          return { groupId: group.groupId, data: null }
        }
      })
      
      const consumerOffsetsResults = await Promise.all(consumerOffsetsPromises)
      const consumerOffsetsMap = {}
      consumerOffsetsResults.forEach(({ groupId, data }) => {
        consumerOffsetsMap[groupId] = data
      })
      setConsumerOffsets(consumerOffsetsMap)
      
      // Set default selected topic
      if (topicsList.length > 0 && !selectedTopic) {
        setSelectedTopic(topicsList[0].name)
      }
      
    } catch (err) {
      console.error('Failed to fetch Kafka data:', err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchAllData()
    
    // Auto refresh every 10 seconds
    const interval = setInterval(fetchAllData, 10000)
    setRefreshInterval(interval)
    
    return () => {
      if (refreshInterval) clearInterval(refreshInterval)
    }
  }, [])

  const getConsumerGroupsForTopic = (topicName) => {
    return consumerGroups.filter(group => {
      const assigned = group.assignedPartitions?.assigned || []
      return assigned.some(p => p.topic === topicName)
    })
  }

  const getPartitionConsumerInfo = (topicName, partitionId) => {
    const consumers = []
    consumerGroups.forEach(group => {
      const assigned = group.assignedPartitions?.assigned || []
      const partitionAssigned = assigned.find(p => p.topic === topicName && p.partition === partitionId)
      if (partitionAssigned) {
        consumers.push({
          groupId: group.groupId,
          consumerId: partitionAssigned.consumerId || group.groupId,
          host: partitionAssigned.host || 'unknown'
        })
      }
    })
    return consumers
  }

  const renderTopicsOverview = () => {
    return (
      <div className="topics-overview">
        <div className="stats-grid-small">
          <div className="stat-card-small">
            <span className="stat-icon">📚</span>
            <span className="stat-value">{topics.length}</span>
            <span className="stat-label">Total Topics</span>
          </div>
          <div className="stat-card-small">
            <span className="stat-icon">🔀</span>
            <span className="stat-value">{topics.reduce((acc, t) => acc + (t.partitions || 0), 0)}</span>
            <span className="stat-label">Total Partitions</span>
          </div>
          <div className="stat-card-small">
            <span className="stat-icon">👥</span>
            <span className="stat-value">{consumerGroups.length}</span>
            <span className="stat-label">Consumer Groups</span>
          </div>
          <div className="stat-card-small">
            <span className="stat-icon">💬</span>
            <span className="stat-value">{Object.values(topicOffsets).reduce((acc, o) => acc + (o?.totalMessages || 0), 0)}</span>
            <span className="stat-label">Total Messages</span>
          </div>
        </div>

        <div className="topics-list">
          <h3>All Topics</h3>
          <div className="topics-grid">
            {topics.map(topic => {
              const consumerGroupsForTopic = getConsumerGroupsForTopic(topic.name)
              const offsetData = topicOffsets[topic.name]
              
              return (
                <div 
                  key={topic.name} 
                  className={`topic-card ${selectedTopic === topic.name ? 'selected' : ''}`}
                  onClick={() => setSelectedTopic(topic.name)}
                >
                  <div className="topic-card-header">
                    <span className="topic-name">{topic.name}</span>
                    <span className={`topic-status ${consumerGroupsForTopic.length > 0 ? 'active' : 'inactive'}`}>
                      {consumerGroupsForTopic.length > 0 ? 'Active' : 'No Consumer'}
                    </span>
                  </div>
                  <div className="topic-card-stats">
                    <div className="stat">
                      <span className="stat-num">{topic.partitions}</span>
                      <span className="stat-text">Partitions</span>
                    </div>
                    <div className="stat">
                      <span className="stat-num">{topic.replicationFactor}</span>
                      <span className="stat-text">Replicas</span>
                    </div>
                    <div className="stat">
                      <span className="stat-num">{offsetData?.totalMessages || 0}</span>
                      <span className="stat-text">Messages</span>
                    </div>
                    <div className="stat">
                      <span className="stat-num">{consumerGroupsForTopic.length}</span>
                      <span className="stat-text">Consumers</span>
                    </div>
                  </div>
                </div>
              )
            })}
          </div>
        </div>
      </div>
    )
  }

  const renderTopicDetails = () => {
    if (!selectedTopic) return null
    
    const topic = topics.find(t => t.name === selectedTopic)
    if (!topic) return null
    
    const consumerGroupsForTopic = getConsumerGroupsForTopic(selectedTopic)
    const offsetData = topicOffsets[selectedTopic]
    
    return (
      <div className="topic-details">
        <div className="topic-details-header">
          <h3>
            <span className="topic-icon">📋</span>
            {selectedTopic}
          </h3>
          <button className="btn-refresh" onClick={fetchAllData}>🔄 Refresh</button>
        </div>
        
        <div className="topic-info-grid">
          <div className="info-card">
            <div className="info-label">Partitions</div>
            <div className="info-value">{topic.partitions}</div>
          </div>
          <div className="info-card">
            <div className="info-label">Replication Factor</div>
            <div className="info-value">{topic.replicationFactor}</div>
          </div>
          <div className="info-card">
            <div className="info-label">Total Messages</div>
            <div className="info-value">{offsetData?.totalMessages || 0}</div>
          </div>
          <div className="info-card">
            <div className="info-label">Active Consumers</div>
            <div className="info-value">{consumerGroupsForTopic.length}</div>
          </div>
        </div>

        <div className="partitions-section">
          <h4>Partition Details</h4>
          <div className="partitions-table-wrapper">
            <table className="partitions-table">
              <thead>
                <tr>
                  <th>Partition</th>
                  <th>Leader</th>
                  <th>Replicas</th>
                  <th>ISR</th>
                  <th>Current Offset</th>
                  <th>End Offset</th>
                  <th>Lag</th>
                  <th>Consumer Group</th>
                  <th>Consumer ID</th>
                </tr>
              </thead>
              <tbody>
                {topic.partitionDetails?.map(partition => {
                  const consumers = getPartitionConsumerInfo(selectedTopic, partition.partitionId)
                  const partitionOffset = offsetData?.partitions?.find(p => p.partition === partition.partitionId)
                  const lag = partitionOffset?.lag || 0
                  
                  return (
                    <tr key={partition.partitionId} className={lag > 100 ? 'high-lag' : ''}>
                      <td><span className="partition-badge">P{partition.partitionId}</span></td>
                      <td>{partition.leader !== -1 ? `Broker ${partition.leader}` : 'N/A'}</td>
                      <td>{partition.replicas?.map(r => `B${r}`).join(', ') || 'N/A'}</td>
                      <td>{partition.isr?.map(r => `B${r}`).join(', ') || 'N/A'}</td>
                      <td>{partitionOffset?.currentOffset || 0}</td>
                      <td>{partitionOffset?.logEndOffset || 0}</td>
                      <td className={lag > 0 ? 'lag-positive' : 'lag-zero'}>{lag}</td>
                      <td>
                        {consumers.length > 0 ? (
                          <div className="consumer-groups-list">
                            {consumers.map((c, idx) => (
                              <span key={idx} className="consumer-group-badge">{c.groupId}</span>
                            ))}
                          </div>
                        ) : <span className="no-consumer">No consumer</span>}
                      </td>
                      <td>
                        {consumers.length > 0 ? (
                          <div className="consumer-ids-list">
                            {consumers.map((c, idx) => (
                              <span key={idx} className="consumer-id" title={c.consumerId}>
                                {c.consumerId?.substring(0, 20)}...
                              </span>
                            ))}
                          </div>
                        ) : <span className="no-consumer">-</span>}
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        </div>

        <div className="consumer-groups-section">
          <h4>Consumer Groups for this Topic</h4>
          {consumerGroupsForTopic.length > 0 ? (
            <div className="consumer-groups-table-wrapper">
              <table className="consumer-groups-table">
                <thead>
                  <tr>
                    <th>Group ID</th>
                    <th>State</th>
                    <th>Members</th>
                    <th>Assigned Partitions</th>
                    <th>Total Lag</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {consumerGroupsForTopic.map(group => {
                    const groupOffsetData = consumerOffsets[group.groupId]
                    const totalLag = groupOffsetData?.partitions?.reduce((acc, p) => acc + (p.lag || 0), 0) || 0
                    
                    return (
                      <tr key={group.groupId}>
                        <td><code className="group-id">{group.groupId}</code></td>
                        <td>
                          <span className={`state-badge ${group.state?.toLowerCase()}`}>
                            {group.state || 'UNKNOWN'}
                          </span>
                        </td>
                        <td>{group.members || 0}</td>
                        <td>
                          {group.assignedPartitions?.assigned?.map((p, idx) => (
                            <span key={idx} className="partition-assigned">
                              P{p.partition}
                            </span>
                          )) || '-'}
                        </td>
                        <td className={totalLag > 0 ? 'lag-positive' : 'lag-zero'}>{totalLag}</td>
                        <td>
                          <button 
                            className="btn-details"
                            onClick={() => setActiveTab('groups')}
                          >
                            View Details
                          </button>
                        </td>
                      </tr>
                    )
                  })}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="no-consumers-message">
              <span>🔍</span>
              <p>No consumer groups are currently consuming from this topic</p>
            </div>
          )}
        </div>
      </div>
    )
  }

  const renderConsumerGroups = () => {
    return (
      <div className="consumer-groups-container">
        <div className="groups-header">
          <h3>Consumer Groups</h3>
          <span className="groups-count">{consumerGroups.length} groups</span>
        </div>
        
        <div className="groups-table-wrapper">
          <table className="groups-table">
            <thead>
              <tr>
                <th>Group ID</th>
                <th>State</th>
                <th>Members</th>
                <th>Topics Consumed</th>
                <th>Total Partitions</th>
                <th>Total Lag</th>
                <th>Details</th>
              </tr>
            </thead>
            <tbody>
              {consumerGroups.map(group => {
                const topicsConsumed = new Set()
                const assigned = group.assignedPartitions?.assigned || []
                assigned.forEach(p => topicsConsumed.add(p.topic))
                
                const groupOffsetData = consumerOffsets[group.groupId]
                const totalLag = groupOffsetData?.partitions?.reduce((acc, p) => acc + (p.lag || 0), 0) || 0
                
                return (
                  <tr key={group.groupId}>
                    <td><code className="group-id">{group.groupId}</code></td>
                    <td>
                      <span className={`state-badge ${group.state?.toLowerCase()}`}>
                        {group.state || 'UNKNOWN'}
                      </span>
                    </td>
                    <td>{group.members || 0}</td>
                    <td>
                      <div className="topics-consumed">
                        {Array.from(topicsConsumed).map(topic => (
                          <span 
                            key={topic} 
                            className="topic-consumed-badge"
                            onClick={() => {
                              setSelectedTopic(topic)
                              setActiveTab('topics')
                            }}
                          >
                            {topic}
                          </span>
                        ))}
                      </div>
                    </td>
                    <td>{assigned.length}</td>
                    <td className={totalLag > 0 ? 'lag-positive' : 'lag-zero'}>{totalLag}</td>
                    <td>
                      <details className="group-details">
                        <summary>View Details</summary>
                        <div className="group-details-content">
                          <h5>Assigned Partitions:</h5>
                          <table className="details-table">
                            <thead>
                              <tr><th>Topic</th><th>Partition</th><th>Consumer ID</th><th>Host</th></tr>
                            </thead>
                            <tbody>
                              {assigned.map((p, idx) => (
                                <tr key={idx}>
                                  <td>{p.topic}</td>
                                  <td>P{p.partition}</td>
                                  <td className="consumer-id">{p.consumerId?.substring(0, 30)}...</td>
                                  <td>{p.host || 'unknown'}</td>
                                </tr>
                              ))}
                            </tbody>
                          </table>
                        </div>
                      </details>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      </div>
    )
  }

  const renderPartitionMonitor = () => {
    if (!selectedTopic) return null
    
    const topic = topics.find(t => t.name === selectedTopic)
    if (!topic) return null
    
    const offsetData = topicOffsets[selectedTopic]
    
    return (
      <div className="partition-monitor">
        <div className="monitor-header">
          <h3>Real-time Partition Monitor - {selectedTopic}</h3>
          <div className="monitor-controls">
            <label>
              <input 
                type="checkbox" 
                checked={!!refreshInterval}
                onChange={() => {
                  if (refreshInterval) {
                    clearInterval(refreshInterval)
                    setRefreshInterval(null)
                  } else {
                    const interval = setInterval(fetchAllData, 5000)
                    setRefreshInterval(interval)
                  }
                }}
              />
              Auto-refresh (5s)
            </label>
          </div>
        </div>
        
        <div className="partitions-visualization">
          {topic.partitionDetails?.map(partition => {
            const partitionOffset = offsetData?.partitions?.find(p => p.partition === partition.partitionId)
            const lag = partitionOffset?.lag || 0
            const consumers = getPartitionConsumerInfo(selectedTopic, partition.partitionId)
            
            // Calculate percentage for visual bar
            const totalMessages = partitionOffset?.logEndOffset || 0
            const consumedPercent = totalMessages > 0 ? ((totalMessages - lag) / totalMessages) * 100 : 0
            
            return (
              <div key={partition.partitionId} className="partition-visual-card">
                <div className="partition-visual-header">
                  <span className="partition-title">Partition {partition.partitionId}</span>
                  <span className={`lag-status ${lag > 0 ? 'has-lag' : 'no-lag'}`}>
                    {lag > 0 ? `Lag: ${lag}` : 'Up to date'}
                  </span>
                </div>
                
                <div className="progress-bar-container">
                  <div 
                    className="progress-bar-fill"
                    style={{ width: `${consumedPercent}%` }}
                  />
                </div>
                
                <div className="partition-stats">
                  <div className="stat-item">
                    <span className="stat-label">Consumed:</span>
                    <span className="stat-value">{partitionOffset?.currentOffset || 0}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">Produced:</span>
                    <span className="stat-value">{partitionOffset?.logEndOffset || 0}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">Progress:</span>
                    <span className="stat-value">{consumedPercent.toFixed(1)}%</span>
                  </div>
                </div>
                
                <div className="partition-consumers">
                  <div className="label">Active Consumers:</div>
                  {consumers.length > 0 ? (
                    consumers.map((c, idx) => (
                      <div key={idx} className="consumer-info">
                        <span className="consumer-group">{c.groupId}</span>
                        <span className="consumer-host">{c.host}</span>
                      </div>
                    ))
                  ) : (
                    <span className="no-consumer-message">No active consumer for this partition</span>
                  )}
                </div>
              </div>
            )
          })}
        </div>
      </div>
    )
  }

  if (loading) {
    return (
      <div className="kafka-info-container">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Loading Kafka cluster information...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="kafka-info-container">
      <div className="page-header">
        <h1 className="page-title">⚡ Kafka Cluster Monitor</h1>
        <p className="page-subtitle">Real-time monitoring of topics, partitions, offsets, and consumer groups</p>
      </div>

      <div className="kafka-tabs">
        <button 
          className={`tab-btn ${activeTab === 'topics' ? 'active' : ''}`}
          onClick={() => setActiveTab('topics')}
        >
          📚 Topics & Partitions
        </button>
        <button 
          className={`tab-btn ${activeTab === 'groups' ? 'active' : ''}`}
          onClick={() => setActiveTab('groups')}
        >
          👥 Consumer Groups
        </button>
        <button 
          className={`tab-btn ${activeTab === 'monitor' ? 'active' : ''}`}
          onClick={() => setActiveTab('monitor')}
        >
          📊 Partition Monitor
        </button>
      </div>

      <div className="kafka-content">
        {activeTab === 'topics' && (
          <div className="topics-container">
            <div className="topics-sidebar">
              {renderTopicsOverview()}
            </div>
            <div className="topic-details-panel">
              {renderTopicDetails()}
            </div>
          </div>
        )}
        
        {activeTab === 'groups' && renderConsumerGroups()}
        
        {activeTab === 'monitor' && renderPartitionMonitor()}
      </div>
    </div>
  )
}