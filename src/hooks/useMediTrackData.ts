import { useMemo, useState, useEffect } from 'react';

// Define SortMethod locally
enum SortMethod {
  NAME_AZ = 'name_az',
  NAME_ZA = 'name_za', 
  FEFO = 'fefo',
  LEFO = 'lefo'
}

// Define all interfaces
interface Medicine {
  id: string;
  name: string;
  quantity: number;
  expiryDate: string;
  warehouseId: string;
  location: string;
  category: string;
  reorderPoint: number;
  unitPrice: number;
}

interface Warehouse {
  id: string;
  name: string;
  location: string;
  latitude: number;
  longitude: number;
}

interface Transfer {
  transferId: string;
  sourceWarehouseId: string;
  targetWarehouseId: string;
  medicineId: string;
  quantity: number;
  date: string;
  status: 'Completed' | 'In Transit' | 'Pending';
}

interface Distribution {
  distributionId: string;
  source: string;
  destination: string;
  medicineId: string;
  batchNumber: string;
  quantity: number;
  status: 'Delivered' | 'Shipped' | 'Processing';
}

interface Hospital {
  id: string;
  name: string;
  location: string;
}

interface Beneficiary {
  id: string;
  name: string;
  hospitalId: string;
}

interface AGV {
  id: string;
  name: string;
  status: 'Idle' | 'On Task' | 'Charging' | 'Maintenance Required';
  batteryLevel: number;
  currentLocation: string;
  currentTask: string | null;
}

interface Stats {
  totalQuantity: number;
  totalItems: number;
  lowStockItems: Medicine[];
  reorderPointItems: Medicine[];
  expiringSoonItems: Medicine[];
  accessibilityIndex: number;
  expiryWasteCost: number;
  totalStockValue: number;
}

interface ChartData {
  medicinesByWarehouse: Array<{ name: string; value: number }>;
  medicineStockStatus: Array<{ name: string; value: number }>;
  transferStatus: Array<{ name: string; value: number }>;
  distributionStatus: Array<{ name: string; value: number }>;
  beneficiariesByHospital: Array<{ name: string; value: number }>;
  agvStatus: Array<{ name: string; value: number }>;
  inventoryValueByCategory: Array<{ name: string; value: number }>;
  stockStatusDistribution: Array<{ name: string; value: number }>;
}

interface Forecast {
  medicineName: string;
  data: Array<{ month: string; demand: number }>;
}

interface AIRecommendation {
  id: string;
  text: string;
}

interface UseMediTrackDataReturn {
  medicines: Medicine[];
  warehouses: Warehouse[];
  transfers: Transfer[];
  distribution: Distribution[];
  hospitals: Hospital[];
  beneficiaries: Beneficiary[];
  agvs: AGV[];
  forecasts: Forecast[];
  getMedicineName: (id: string) => string;
  getWarehouseName: (id: string) => string;
  getHospitalName: (id: string) => string;
  stats: Stats;
  chartData: ChartData;
  predictiveAnalysis: Array<{ medicineName: string; prediction: string }>;
  aiDecisionRecommendations: AIRecommendation[];
  sortMedicines: (meds: Medicine[], method: SortMethod) => Medicine[];
  addHospital: (hospital: Omit<Hospital, 'id'>) => void;
  addBeneficiary: (beneficiary: Omit<Beneficiary, 'id'>) => void;
  addMedicine: (med: Omit<Medicine, 'id'>) => void;
  addDistribution: (dist: Omit<Distribution, 'distributionId' | 'batchNumber'>) => void;
  updateTransferStatus: (transferId: string, newStatus: Transfer['status']) => void;
  addTransfer: (transfer: Omit<Transfer, 'transferId' | 'date' | 'status'>) => void;
  adjustStock: (medicineId: string, adjustmentValue: number) => void;
  lookupMedicineByBarcode: (barcode: string) => Partial<Omit<Medicine, 'id' | 'quantity' | 'warehouseId'>> | null;
  addWarehouse: (warehouse: Omit<Warehouse, 'id'>) => void;
  loading: boolean;
}

// Your Java backend base URL
const API_BASE_URL = 'http://localhost:8080';

// Mock data as fallback - ALL YOUR ORIGINAL DATA
const initialMedicines: Medicine[] = [
  { id: 'med1', name: 'Artemether/Lumefantrine (20/120mg)', quantity: 2500, expiryDate: '2025-12-31', warehouseId: 'wh1', location: 'Shelf A1', category: 'Antimalarial', reorderPoint: 500, unitPrice: 3.50 },
  { id: 'med2', name: 'Azithromycin 500mg', quantity: 90, expiryDate: '2024-10-31', warehouseId: 'wh2', location: 'Rack B2', category: 'Antibiotic', reorderPoint: 150, unitPrice: 0.75 },
  { id: 'med3', name: 'Tenofovir/Lamivudine/Dolutegravir (TLD)', quantity: 800, expiryDate: '2026-06-30', warehouseId: 'wh1', location: 'Shelf A2', category: 'Antiretroviral', reorderPoint: 200, unitPrice: 7.20 },
];

const initialWarehouses: Warehouse[] = [
  { id: 'wh1', name: 'Nairobi Central Store', location: 'Nairobi, Kenya', latitude: -1.2921, longitude: 36.8219 },
  { id: 'wh2', name: 'Mogadishu MedHub', location: 'Mogadishu, Somalia', latitude: 2.0469, longitude: 45.3182 },
  { id: 'wh3', name: 'Juba Distribution Point', location: 'Juba, South Sudan', latitude: 4.8594, longitude: 31.5713 },
];

const initialTransfers: Transfer[] = [
  { transferId: 'tr1', sourceWarehouseId: 'wh1', targetWarehouseId: 'wh2', medicineId: 'med1', quantity: 500, date: '2024-05-10', status: 'Completed' },
  { transferId: 'tr2', sourceWarehouseId: 'wh3', targetWarehouseId: 'wh1', medicineId: 'med2', quantity: 300, date: '2024-05-12', status: 'In Transit' },
];

const initialDistribution: Distribution[] = [
  { distributionId: 'dist1', source: 'wh1', destination: 'Kenyatta National Hospital', medicineId: 'med3', batchNumber: 'B3-456', quantity: 150, status: 'Delivered' },
];

const initialHospitals: Hospital[] = [
  { id: 'hosp1', name: 'Kenyatta National Hospital', location: 'Nairobi, Kenya' },
  { id: 'hosp2', name: 'Banadir Hospital', location: 'Mogadishu, Somalia' },
];

const initialBeneficiaries: Beneficiary[] = [
  { id: 'ben1', name: 'Fatuma Ahmed', hospitalId: 'hosp2' },
  { id: 'ben2', name: 'James Otieno', hospitalId: 'hosp1' },
];

const initialAGVs: AGV[] = [
  { id: 'agv-001', name: 'PharmaBot Alpha', status: 'Idle', batteryLevel: 85, currentLocation: 'Nairobi Central Store', currentTask: null },
  { id: 'agv-002', name: 'PharmaBot Beta', status: 'Charging', batteryLevel: 22, currentLocation: 'Nairobi Central Store', currentTask: null },
];

const forecastData: Forecast[] = [
  { medicineName: 'Artemether/Lumefantrine (20/120mg)', data: [ { month: 'Jun', demand: 2800 }, { month: 'Jul', demand: 3200 }, { month: 'Aug', demand: 3500 }, { month: 'Sep', demand: 3100 } ]},
  { medicineName: 'Oral Rehydration Salts (ORS)', data: [ { month: 'Jun', demand: 5500 }, { month: 'Jul', demand: 6200 }, { month: 'Aug', demand: 6800 }, { month: 'Sep', demand: 6000 } ]},
];

const barcodeDatabase: Record<string, Partial<Omit<Medicine, 'id' | 'quantity' | 'warehouseId'>>> = {
  '6291107421021': { name: 'Amoxicillin 250mg', expiryDate: '2025-08-15', location: 'Shelf C1', category: 'Antibiotic', reorderPoint: 400, unitPrice: 0.45 },
  '8901234567897': { name: 'Oral Rehydration Salts (ORS)', expiryDate: '2026-11-30', location: 'Area D', category: 'General', reorderPoint: 1000, unitPrice: 0.15 },
};

export const useMediTrackData = (): UseMediTrackDataReturn => {
  const [medicines, setMedicines] = useState<Medicine[]>([]);
  const [warehouses, setWarehouses] = useState<Warehouse[]>([]);
  const [transfers, setTransfers] = useState<Transfer[]>(initialTransfers);
  const [distribution, setDistribution] = useState<Distribution[]>(initialDistribution);
  const [hospitals, setHospitals] = useState<Hospital[]>(initialHospitals);
  const [beneficiaries, setBeneficiaries] = useState<Beneficiary[]>(initialBeneficiaries);
  const [agvs, setAgvs] = useState<AGV[]>(initialAGVs);
  const [loading, setLoading] = useState(true);

  // Fetch data from Java backend
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        console.log('🔄 Fetching data from Java backend...');
        
        // Fetch medicines from Java backend
        const medicinesResponse = await fetch(`${API_BASE_URL}/medicines`);
        if (!medicinesResponse.ok) throw new Error('Failed to fetch medicines');
        const medicinesData = await medicinesResponse.json();
        console.log('✅ Medicines from Java:', medicinesData);
        setMedicines(medicinesData);

        // Fetch warehouses from Java backend  
        const warehousesResponse = await fetch(`${API_BASE_URL}/warehouses`);
        if (!warehousesResponse.ok) throw new Error('Failed to fetch warehouses');
        const warehousesData = await warehousesResponse.json();
        console.log('✅ Warehouses from Java:', warehousesData);
        setWarehouses(warehousesData);

      } catch (error) {
        console.error('❌ Error fetching data from Java backend:', error);
        console.log('🔄 Falling back to mock data...');
        // Fallback to mock data if Java backend is not available
        setMedicines(initialMedicines);
        setWarehouses(initialWarehouses);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const medicineMap = useMemo(() => new Map(medicines.map((m: Medicine) => [m.id, m.name])), [medicines]);
  const warehouseMap = useMemo(() => new Map(warehouses.map((w: Warehouse) => [w.id, w.name])), [warehouses]);

  const memoizedData = useMemo(() => {
    const hospitalMap = new Map(hospitals.map((h: Hospital) => [h.id, h.name]));

    const getMedicineName = (id: string) => medicineMap.get(id) || 'Unknown Medicine';
    const getWarehouseName = (id: string) => warehouseMap.get(id) || 'Unknown Warehouse';
    const getHospitalName = (id: string) => hospitalMap.get(id) || 'Unknown Hospital';

    const totalQuantity = medicines.reduce((sum: number, med: Medicine) => sum + med.quantity, 0);
    const totalItems = medicines.length;
    const lowStockItems = medicines.filter((m: Medicine) => m.quantity < 100);
    const reorderPointItems = medicines.filter((m: Medicine) => m.quantity > 100 && m.quantity <= m.reorderPoint);

    const sixMonthsFromNow = new Date();
    sixMonthsFromNow.setMonth(sixMonthsFromNow.getMonth() + 6);
    const expiringSoonItems = medicines.filter((m: Medicine) => new Date(m.expiryDate) < sixMonthsFromNow);
    
    const accessibilityIndex = Math.round(((totalItems - lowStockItems.length) / totalItems) * 100);
    const expiryWasteCost = expiringSoonItems.reduce((sum: number, item: Medicine) => sum + (item.quantity * item.unitPrice), 0);
    const totalStockValue = medicines.reduce((sum: number, med: Medicine) => sum + (med.quantity * med.unitPrice), 0);

    const stats: Stats = { 
      totalQuantity, 
      totalItems, 
      lowStockItems, 
      reorderPointItems, 
      expiringSoonItems, 
      accessibilityIndex, 
      expiryWasteCost, 
      totalStockValue 
    };
    
    const medicinesByWarehouse = warehouses.map((w: Warehouse) => ({
        name: w.name,
        value: medicines.filter((m: Medicine) => m.warehouseId === w.id).reduce((sum: number, m: Medicine) => sum + m.quantity, 0)
    }));

    const medicineStockStatus = [
        { name: 'Low Stock', value: lowStockItems.length },
        { name: 'Healthy Stock', value: totalItems - lowStockItems.length },
    ];
    
    const transferStatus = ['Completed', 'In Transit', 'Pending'].map(status => ({
        name: status,
        value: transfers.filter((t: Transfer) => t.status === status).length,
    }));
    
    const distributionStatus = ['Delivered', 'Shipped', 'Processing'].map(status => ({
        name: status,
        value: distribution.filter((d: Distribution) => d.status === status).length,
    }));
    
    const beneficiariesByHospital = hospitals.map((h: Hospital) => ({
        name: h.name,
        value: beneficiaries.filter((b: Beneficiary) => b.hospitalId === h.id).length
    }));
    
    const agvStatus = ['Idle', 'On Task', 'Charging', 'Maintenance Required'].map(status => ({
        name: status,
        value: agvs.filter((a: AGV) => a.status === status).length,
    }));

    const inventoryValueByCategory: Array<{name: string, value: number}> = [];
    medicines.forEach((med: Medicine) => {
        const value = med.quantity * med.unitPrice;
        const category = inventoryValueByCategory.find(c => c.name === med.category);
        if (category) {
            category.value += value;
        } else {
            inventoryValueByCategory.push({ name: med.category, value });
        }
    });

    const stockStatusDistribution = [
        { name: 'Healthy', value: medicines.filter((m: Medicine) => m.quantity > m.reorderPoint).length },
        { name: 'Reorder', value: reorderPointItems.length },
        { name: 'Low Stock', value: lowStockItems.length },
    ];
    
    const chartData: ChartData = { 
      medicinesByWarehouse, 
      medicineStockStatus, 
      transferStatus, 
      distributionStatus, 
      beneficiariesByHospital, 
      agvStatus, 
      inventoryValueByCategory, 
      stockStatusDistribution 
    };

    const predictiveAnalysis = [
        { medicineName: 'Artemether/Lumefantrine', prediction: 'High seasonal demand expected due to rainy season. Monitor stock closely.' },
        { medicineName: 'Azithromycin 500mg', prediction: 'Stock is critically low. High risk of stockout. Immediate transfer required.' },
    ];

    const aiDecisionRecommendations: AIRecommendation[] = [];
    if (lowStockItems.length > 0) {
        const item = lowStockItems[0];
        aiDecisionRecommendations.push({
            id: 'rec_low_stock',
            text: `Low stock of ${item.name} at ${getWarehouseName(item.warehouseId)}. Consider emergency air-lift or re-routing from a nearby hub.`
        });
    }
    
    if (expiringSoonItems.length > 0) {
        const item = expiringSoonItems[0];
        aiDecisionRecommendations.push({
            id: 'rec_expiry',
            text: `${item.name} at ${getWarehouseName(item.warehouseId)} is expiring soon. Prioritize distribution to high-traffic clinics to prevent waste.`
        });
    }

    const agvInMaintenance = agvs.find((a: AGV) => a.status === 'Maintenance Required');
    if (agvInMaintenance) {
         aiDecisionRecommendations.push({
            id: 'rec_agv',
            text: `AGV '${agvInMaintenance.name}' requires maintenance. Internal logistics may be slower. Address immediately.`
        });
    } else {
        aiDecisionRecommendations.push({
            id: 'rec_agv_ok',
            text: 'AGV fleet efficiency is optimal. No immediate actions required for internal logistics automation.'
        });
    }

    const sortMedicines = (meds: Medicine[], method: SortMethod): Medicine[] => {
        const sorted = [...meds];
        switch(method) {
            case SortMethod.NAME_AZ:
                return sorted.sort((a: Medicine, b: Medicine) => a.name.localeCompare(b.name));
            case SortMethod.NAME_ZA:
                return sorted.sort((a: Medicine, b: Medicine) => b.name.localeCompare(a.name));
            case SortMethod.FEFO:
                return sorted.sort((a: Medicine, b: Medicine) => new Date(a.expiryDate).getTime() - new Date(b.expiryDate).getTime());
            case SortMethod.LEFO:
                return sorted.sort((a: Medicine, b: Medicine) => new Date(b.expiryDate).getTime() - new Date(a.expiryDate).getTime());
            default:
                return sorted;
        }
    };

    return {
      medicines,
      warehouses,
      transfers,
      distribution,
      hospitals,
      beneficiaries,
      agvs,
      forecasts: forecastData,
      getMedicineName,
      getWarehouseName,
      getHospitalName,
      stats,
      chartData,
      predictiveAnalysis,
      aiDecisionRecommendations,
      sortMedicines,
    };
  }, [medicines, warehouses, transfers, distribution, hospitals, beneficiaries, agvs, medicineMap, warehouseMap]);

  const addHospital = (hospital: Omit<Hospital, 'id'>) => {
    const newHospital: Hospital = { ...hospital, id: `hosp_${Date.now()}` };
    setHospitals((prev: Hospital[]) => [...prev, newHospital]);
  };

  const addBeneficiary = (beneficiary: Omit<Beneficiary, 'id'>) => {
    const newBeneficiary: Beneficiary = { ...beneficiary, id: `ben_${Date.now()}` };
    setBeneficiaries((prev: Beneficiary[]) => [...prev, newBeneficiary]);
  };

  const addMedicine = (med: Omit<Medicine, 'id'>) => {
    const newMedicine: Medicine = {
        ...med,
        id: `med_${Date.now()}`,
    };
    setMedicines((prev: Medicine[]) => [...prev, newMedicine]);
  };

  const addDistribution = (dist: Omit<Distribution, 'distributionId' | 'batchNumber'>) => {
    const newDistribution: Distribution = {
        ...dist,
        distributionId: `dist_${Date.now()}`,
        batchNumber: `B${Math.floor(Math.random() * 100)}-${Math.floor(Math.random() * 900) + 100}`
    };
    setDistribution((prev: Distribution[]) => [...prev, newDistribution]);
  };
  
  const updateTransferStatus = (transferId: string, newStatus: Transfer['status']) => {
    setTransfers((currentTransfers: Transfer[]) =>
        currentTransfers.map((t: Transfer) =>
            t.transferId === transferId ? { ...t, status: newStatus } : t
        )
    );
  };

  const addTransfer = (transfer: Omit<Transfer, 'transferId' | 'date' | 'status'>) => {
    const newTransfer: Transfer = {
      ...transfer,
      transferId: `tr_${Date.now()}`,
      date: new Date().toISOString().split('T')[0],
      status: 'Pending',
    };
    setTransfers((prev: Transfer[]) => [newTransfer, ...prev]);
  };

  const adjustStock = (medicineId: string, adjustmentValue: number) => {
    setMedicines((currentMedicines: Medicine[]) =>
      currentMedicines.map((med: Medicine) =>
        med.id === medicineId
          ? { ...med, quantity: Math.max(0, med.quantity + adjustmentValue) }
          : med
      )
    );
  };

  const lookupMedicineByBarcode = (barcode: string): Partial<Omit<Medicine, 'id' | 'quantity' | 'warehouseId'>> | null => {
    return barcodeDatabase[barcode] || null;
  };

  const addWarehouse = (warehouse: Omit<Warehouse, 'id'>) => {
    const newWarehouse: Warehouse = { ...warehouse, id: `wh_${Date.now()}`};
    setWarehouses((prev: Warehouse[]) => [...prev, newWarehouse]);
  };

  return {
    ...memoizedData,
    loading,
    addHospital,
    addBeneficiary,
    addMedicine,
    addDistribution,
    updateTransferStatus,
    addTransfer,
    adjustStock,
    lookupMedicineByBarcode,
    addWarehouse,
  };
};