import React from 'react';
import { useMediTrackData } from './hooks/useMediTrackData';

function App() {
  const { medicines, warehouses, loading } = useMediTrackData();

  if (loading) {
    return <div style={{ padding: '20px', fontSize: '18px' }}>Loading MediTrack System...</div>;
  }

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
      <h1>🏥 MediTrack System</h1>
      
      <div style={{ display: 'flex', gap: '20px', flexWrap: 'wrap' }}>
        <div style={{ flex: '1', minWidth: '300px' }}>
          <h2>💊 Medicines ({medicines.length})</h2>
          {medicines.map(medicine => (
            <div key={medicine.id} style={{ 
              border: '1px solid #ddd', 
              padding: '10px', 
              margin: '10px 0',
              borderRadius: '5px'
            }}>
              <h3>{medicine.name}</h3>
              <p>Quantity: {medicine.quantity}</p>
              <p>Category: {medicine.category}</p>
              <p>Location: {medicine.location}</p>
            </div>
          ))}
        </div>

        <div style={{ flex: '1', minWidth: '300px' }}>
          <h2>🏭 Warehouses ({warehouses.length})</h2>
          {warehouses.map(warehouse => (
            <div key={warehouse.id} style={{ 
              border: '1px solid #ddd', 
              padding: '10px', 
              margin: '10px 0',
              borderRadius: '5px'
            }}>
              <h3>{warehouse.name}</h3>
              <p>Location: {warehouse.location}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default App;