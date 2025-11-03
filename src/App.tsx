import React, { useState } from 'react';
import { useMediTrackData } from './hooks/useMediTrackData';

function App() {
  const { 
    medicines, 
    warehouses, 
    transfers, 
    distribution, 
    hospitals, 
    beneficiaries, 
    agvs,
    stats,
    chartData,
    predictiveAnalysis,
    aiDecisionRecommendations,
    loading 
  } = useMediTrackData();

  const [activeTab, setActiveTab] = useState('dashboard');

  if (loading) {
    return (
      <div style={styles.loadingContainer}>
        <div style={styles.loadingSpinner}>⏳</div>
        <h2>Loading MediTrack System...</h2>
        <p>Please wait while we load your data</p>
      </div>
    );
  }

  return (
    <div style={styles.container}>
      {/* Sidebar */}
      <div style={styles.sidebar}>
        <div style={styles.sidebarHeader}>
          <h2 style={styles.logo}>🏥 MediTrack</h2>
        </div>
        <nav style={styles.nav}>
          {[
            { id: 'dashboard', label: '📊 Dashboard', icon: '📊' },
            { id: 'medicines', label: '💊 Medicines', icon: '💊' },
            { id: 'warehouses', label: '🏭 Warehouses', icon: '🏭' },
            { id: 'transfers', label: '🚚 Transfers', icon: '🚚' },
            { id: 'distribution', label: '📦 Distribution', icon: '📦' },
            { id: 'hospitals', label: '🏥 Hospitals', icon: '🏥' },
            { id: 'agvs', label: '🤖 AGVs', icon: '🤖' },
            { id: 'analytics', label: '📈 Analytics', icon: '📈' },
          ].map(item => (
            <button
              key={item.id}
              style={{
                ...styles.navButton,
                ...(activeTab === item.id ? styles.navButtonActive : {})
              }}
              onClick={() => setActiveTab(item.id)}
            >
              <span style={styles.navIcon}>{item.icon}</span>
              {item.label}
            </button>
          ))}
        </nav>
      </div>

      {/* Main Content */}
      <div style={styles.mainContent}>
        <header style={styles.header}>
          <h1 style={styles.title}>
            {activeTab === 'dashboard' && '📊 Dashboard'}
            {activeTab === 'medicines' && '💊 Medicines Management'}
            {activeTab === 'warehouses' && '🏭 Warehouse Overview'}
            {activeTab === 'transfers' && '🚚 Transfer Operations'}
            {activeTab === 'distribution' && '📦 Distribution Network'}
            {activeTab === 'hospitals' && '🏥 Hospital Partners'}
            {activeTab === 'agvs' && '🤖 AGV Fleet Management'}
            {activeTab === 'analytics' && '📈 Predictive Analytics'}
          </h1>
          <div style={styles.statsBar}>
            <div style={styles.statItem}>
              <span style={styles.statNumber}>{stats.totalItems}</span>
              <span style={styles.statLabel}>Medicines</span>
            </div>
            <div style={styles.statItem}>
              <span style={styles.statNumber}>{warehouses.length}</span>
              <span style={styles.statLabel}>Warehouses</span>
            </div>
            <div style={styles.statItem}>
              <span style={styles.statNumber}>{stats.totalQuantity}</span>
              <span style={styles.statLabel}>Total Units</span>
            </div>
            <div style={styles.statItem}>
              <span style={styles.statNumber}>{stats.accessibilityIndex}%</span>
              <span style={styles.statLabel}>Accessibility</span>
            </div>
          </div>
        </header>

        <div style={styles.content}>
          {/* Dashboard Tab */}
          {activeTab === 'dashboard' && (
            <div style={styles.dashboard}>
              {/* AI Recommendations */}
              <div style={styles.section}>
                <h3 style={styles.sectionTitle}>🚨 AI Recommendations</h3>
                <div style={styles.cardsGrid}>
                  {aiDecisionRecommendations.map(rec => (
                    <div key={rec.id} style={styles.recommendationCard}>
                      <div style={styles.recommendationIcon}>💡</div>
                      <p style={styles.recommendationText}>{rec.text}</p>
                    </div>
                  ))}
                </div>
              </div>

              {/* Quick Stats */}
              <div style={styles.section}>
                <h3 style={styles.sectionTitle}>📈 Quick Stats</h3>
                <div style={styles.statsGrid}>
                  <div style={styles.statCard}>
                    <h4>Stock Status</h4>
                    <div style={styles.statBars}>
                      {chartData.stockStatusDistribution.map(item => (
                        <div key={item.name} style={styles.statBar}>
                          <span>{item.name}</span>
                          <span style={styles.statValue}>{item.value}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                  <div style={styles.statCard}>
                    <h4>Transfer Status</h4>
                    <div style={styles.statBars}>
                      {chartData.transferStatus.map(item => (
                        <div key={item.name} style={styles.statBar}>
                          <span>{item.name}</span>
                          <span style={styles.statValue}>{item.value}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              </div>

              {/* Predictive Analysis */}
              <div style={styles.section}>
                <h3 style={styles.sectionTitle}>🔮 Predictive Analysis</h3>
                <div style={styles.cardsGrid}>
                  {predictiveAnalysis.map((item, index) => (
                    <div key={index} style={styles.predictionCard}>
                      <h4>{item.medicineName}</h4>
                      <p>{item.prediction}</p>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {/* Medicines Tab */}
          {activeTab === 'medicines' && (
            <div style={styles.tabContent}>
              <div style={styles.sectionHeader}>
                <h3>Medicine Inventory</h3>
                <span style={styles.badge}>
                  Total Value: ${stats.totalStockValue.toLocaleString()}
                </span>
              </div>
              <div style={styles.cardsGrid}>
                {medicines.map(medicine => (
                  <div key={medicine.id} style={styles.medicineCard}>
                    <h4 style={styles.medicineName}>{medicine.name}</h4>
                    <div style={styles.medicineDetails}>
                      <div style={styles.detailRow}>
                        <span>Quantity:</span>
                        <span style={medicine.quantity < 100 ? styles.lowStock : styles.normalStock}>
                          {medicine.quantity} units
                        </span>
                      </div>
                      <div style={styles.detailRow}>
                        <span>Category:</span>
                        <span style={styles.category}>{medicine.category}</span>
                      </div>
                      <div style={styles.detailRow}>
                        <span>Location:</span>
                        <span>{medicine.location}</span>
                      </div>
                      <div style={styles.detailRow}>
                        <span>Expires:</span>
                        <span>{medicine.expiryDate}</span>
                      </div>
                      <div style={styles.detailRow}>
                        <span>Value:</span>
                        <span>${(medicine.quantity * medicine.unitPrice).toLocaleString()}</span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Warehouses Tab */}
          {activeTab === 'warehouses' && (
            <div style={styles.tabContent}>
              <div style={styles.cardsGrid}>
                {warehouses.map(warehouse => {
                  const warehouseMedicines = medicines.filter(m => m.warehouseId === warehouse.id);
                  const totalValue = warehouseMedicines.reduce((sum, med) => sum + (med.quantity * med.unitPrice), 0);
                  
                  return (
                    <div key={warehouse.id} style={styles.warehouseCard}>
                      <h4 style={styles.warehouseName}>{warehouse.name}</h4>
                      <p style={styles.warehouseLocation}>{warehouse.location}</p>
                      <div style={styles.warehouseStats}>
                        <div style={styles.warehouseStat}>
                          <span>Medicines:</span>
                          <strong>{warehouseMedicines.length}</strong>
                        </div>
                        <div style={styles.warehouseStat}>
                          <span>Total Value:</span>
                          <strong>${totalValue.toLocaleString()}</strong>
                        </div>
                        <div style={styles.warehouseStat}>
                          <span>Coordinates:</span>
                          <span>{warehouse.latitude}, {warehouse.longitude}</span>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* Transfers Tab */}
          {activeTab === 'transfers' && (
            <div style={styles.tabContent}>
              <div style={styles.tableContainer}>
                <table style={styles.table}>
                  <thead>
                    <tr>
                      <th>Transfer ID</th>
                      <th>Medicine</th>
                      <th>From → To</th>
                      <th>Quantity</th>
                      <th>Date</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {transfers.map(transfer => (
                      <tr key={transfer.transferId}>
                        <td>{transfer.transferId}</td>
                        <td>{medicines.find(m => m.id === transfer.medicineId)?.name}</td>
                        <td>
                          {warehouses.find(w => w.id === transfer.sourceWarehouseId)?.name} → 
                          {warehouses.find(w => w.id === transfer.targetWarehouseId)?.name}
                        </td>
                        <td>{transfer.quantity}</td>
                        <td>{transfer.date}</td>
                        <td>
                          <span style={{
                            ...styles.statusBadge,
                            ...(transfer.status === 'Completed' ? styles.statusCompleted : 
                                 transfer.status === 'In Transit' ? styles.statusInTransit : 
                                 styles.statusPending)
                          }}>
                            {transfer.status}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {/* Add more tabs for Distribution, Hospitals, AGVs, Analytics... */}

          {/* AGVs Tab */}
          {activeTab === 'agvs' && (
            <div style={styles.tabContent}>
              <div style={styles.cardsGrid}>
                {agvs.map(agv => (
                  <div key={agv.id} style={styles.agvCard}>
                    <h4 style={styles.agvName}>{agv.name}</h4>
                    <div style={styles.agvDetails}>
                      <div style={styles.detailRow}>
                        <span>Status:</span>
                        <span style={{
                          ...styles.statusBadge,
                          ...(agv.status === 'Idle' ? styles.statusIdle :
                               agv.status === 'On Task' ? styles.statusOnTask :
                               agv.status === 'Charging' ? styles.statusCharging :
                               styles.statusMaintenance)
                        }}>
                          {agv.status}
                        </span>
                      </div>
                      <div style={styles.detailRow}>
                        <span>Battery:</span>
                        <div style={styles.batteryContainer}>
                          <div 
                            style={{
                              ...styles.batteryLevel,
                              width: `${agv.batteryLevel}%`,
                              backgroundColor: agv.batteryLevel > 20 ? '#27ae60' : '#e74c3c'
                            }}
                          />
                        </div>
                        <span>{agv.batteryLevel}%</span>
                      </div>
                      <div style={styles.detailRow}>
                        <span>Location:</span>
                        <span>{agv.currentLocation}</span>
                      </div>
                      <div style={styles.detailRow}>
                        <span>Current Task:</span>
                        <span>{agv.currentTask || 'None'}</span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

// ... (keep all the styles from previous message, they're too long to repeat here)
// You already have the styles from the beautiful UI version

const styles = {
  container: {
    display: 'flex',
    minHeight: '100vh',
    backgroundColor: '#f8f9fa',
    fontFamily: 'Arial, sans-serif',
  },
  sidebar: {
    width: '280px',
    backgroundColor: '#2c3e50',
    color: 'white',
    display: 'flex',
    flexDirection: 'column' as const,
  },
  sidebarHeader: {
    padding: '2rem 1.5rem',
    borderBottom: '1px solid #34495e',
  },
  logo: {
    margin: 0,
    fontSize: '1.5rem',
    fontWeight: 'bold',
  },
  nav: {
    flex: 1,
    padding: '1rem 0',
  },
  navButton: {
    width: '100%',
    backgroundColor: 'transparent',
    border: 'none',
    color: '#bdc3c7',
    padding: '1rem 1.5rem',
    textAlign: 'left' as const,
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    gap: '0.75rem',
    fontSize: '1rem',
    transition: 'all 0.2s',
  },
  navButtonActive: {
    backgroundColor: '#34495e',
    color: 'white',
    borderRight: '4px solid #3498db',
  },
  navIcon: {
    fontSize: '1.2rem',
  },
  mainContent: {
    flex: 1,
    display: 'flex',
    flexDirection: 'column' as const,
  },
  header: {
    backgroundColor: 'white',
    padding: '1.5rem 2rem',
    borderBottom: '1px solid #e1e8ed',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  title: {
    margin: '0 0 1rem 0',
    fontSize: '1.8rem',
    color: '#2c3e50',
  },
  statsBar: {
    display: 'flex',
    gap: '2rem',
  },
  statItem: {
    display: 'flex',
    flexDirection: 'column' as const,
    alignItems: 'center',
  },
  statNumber: {
    fontSize: '1.5rem',
    fontWeight: 'bold',
    color: '#2c3e50',
  },
  statLabel: {
    fontSize: '0.9rem',
    color: '#7f8c8d',
    marginTop: '0.25rem',
  },
  content: {
    flex: 1,
    padding: '2rem',
    overflowY: 'auto' as const,
  },
  dashboard: {
    display: 'flex',
    flexDirection: 'column' as const,
    gap: '2rem',
  },
  section: {
    backgroundColor: 'white',
    borderRadius: '12px',
    padding: '1.5rem',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  sectionTitle: {
    margin: '0 0 1rem 0',
    fontSize: '1.3rem',
    color: '#2c3e50',
  },
  sectionHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '1.5rem',
  },
  badge: {
    backgroundColor: '#3498db',
    color: 'white',
    padding: '0.5rem 1rem',
    borderRadius: '20px',
    fontSize: '0.9rem',
    fontWeight: 'bold',
  },
  cardsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
    gap: '1.5rem',
  },
  recommendationCard: {
    backgroundColor: '#fff3cd',
    border: '1px solid #ffeaa7',
    borderRadius: '8px',
    padding: '1rem',
    display: 'flex',
    alignItems: 'flex-start',
    gap: '1rem',
  },
  recommendationIcon: {
    fontSize: '1.5rem',
    flexShrink: 0,
  },
  recommendationText: {
    margin: 0,
    color: '#856404',
    lineHeight: 1.4,
  },
  statsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
    gap: '1rem',
  },
  statCard: {
    backgroundColor: '#f8f9fa',
    border: '1px solid #e9ecef',
    borderRadius: '8px',
    padding: '1rem',
  },
  statBars: {
    display: 'flex',
    flexDirection: 'column' as const,
    gap: '0.5rem',
  },
  statBar: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  statValue: {
    fontWeight: 'bold',
    color: '#2c3e50',
  },
  predictionCard: {
    backgroundColor: '#e8f4fd',
    border: '1px solid #b3e0ff',
    borderRadius: '8px',
    padding: '1rem',
  },
  tabContent: {
    backgroundColor: 'white',
    borderRadius: '12px',
    padding: '1.5rem',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  medicineCard: {
    backgroundColor: 'white',
    border: '1px solid #e1e8ed',
    borderRadius: '8px',
    padding: '1.5rem',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  medicineName: {
    margin: '0 0 1rem 0',
    fontSize: '1.1rem',
    color: '#2c3e50',
    borderBottom: '2px solid #3498db',
    paddingBottom: '0.5rem',
  },
  medicineDetails: {
    display: 'flex',
    flexDirection: 'column' as const,
    gap: '0.5rem',
  },
  detailRow: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  lowStock: {
    color: '#e74c3c',
    fontWeight: 'bold',
  },
  normalStock: {
    color: '#27ae60',
    fontWeight: 'bold',
  },
  category: {
    backgroundColor: '#ecf0f1',
    padding: '0.2rem 0.5rem',
    borderRadius: '4px',
    fontSize: '0.8rem',
  },
  warehouseCard: {
    backgroundColor: 'white',
    border: '1px solid #e1e8ed',
    borderRadius: '8px',
    padding: '1.5rem',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  warehouseName: {
    margin: '0 0 0.5rem 0',
    fontSize: '1.1rem',
    color: '#2c3e50',
  },
  warehouseLocation: {
    margin: '0 0 1rem 0',
    color: '#7f8c8d',
    fontSize: '0.9rem',
  },
  warehouseStats: {
    display: 'flex',
    flexDirection: 'column' as const,
    gap: '0.5rem',
  },
  warehouseStat: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    fontSize: '0.9rem',
  },
  tableContainer: {
    backgroundColor: 'white',
    borderRadius: '8px',
    overflow: 'hidden',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  table: {
    width: '100%',
    borderCollapse: 'collapse' as const,
  },
  statusBadge: {
    padding: '0.25rem 0.75rem',
    borderRadius: '12px',
    fontSize: '0.8rem',
    fontWeight: 'bold',
  },
  statusCompleted: { backgroundColor: '#d4edda', color: '#155724' },
  statusInTransit: { backgroundColor: '#fff3cd', color: '#856404' },
  statusPending: { backgroundColor: '#f8d7da', color: '#721c24' },
  statusIdle: { backgroundColor: '#e2e3e5', color: '#383d41' },
  statusOnTask: { backgroundColor: '#d1ecf1', color: '#0c5460' },
  statusCharging: { backgroundColor: '#d4edda', color: '#155724' },
  statusMaintenance: { backgroundColor: '#f8d7da', color: '#721c24' },
  agvCard: {
    backgroundColor: 'white',
    border: '1px solid #e1e8ed',
    borderRadius: '8px',
    padding: '1.5rem',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  agvName: {
    margin: '0 0 1rem 0',
    fontSize: '1.1rem',
    color: '#2c3e50',
  },
  agvDetails: {
    display: 'flex',
    flexDirection: 'column' as const,
    gap: '0.75rem',
  },
  batteryContainer: {
    flex: 1,
    height: '8px',
    backgroundColor: '#ecf0f1',
    borderRadius: '4px',
    overflow: 'hidden',
    margin: '0 0.5rem',
  },
  batteryLevel: {
    height: '100%',
    borderRadius: '4px',
    transition: 'width 0.3s',
  },
  loadingContainer: {
    display: 'flex',
    flexDirection: 'column' as const,
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: '100vh',
    backgroundColor: '#f8f9fa',
    fontFamily: 'Arial, sans-serif',
  },
  loadingSpinner: {
    fontSize: '3rem',
    marginBottom: '1rem',
    animation: 'spin 2s linear infinite',
  },
};

// Add CSS animation
const styleSheet = document.styleSheets[0];
styleSheet.insertRule(`
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
`, styleSheet.cssRules.length);

export default App;