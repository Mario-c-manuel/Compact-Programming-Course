// src/types.ts
export interface Medicine {
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

export interface Warehouse {
  id: string;
  name: string;
  location: string;
  latitude: number;
  longitude: number;
}

export interface Transfer {
  transferId: string;
  sourceWarehouseId: string;
  targetWarehouseId: string;
  medicineId: string;
  quantity: number;
  date: string;
  status: 'Completed' | 'In Transit' | 'Pending';
}

export interface Distribution {
  distributionId: string;
  source: string;
  destination: string;
  medicineId: string;
  batchNumber: string;
  quantity: number;
  status: 'Delivered' | 'Shipped' | 'Processing';
}

export interface Hospital {
  id: string;
  name: string;
  location: string;
}

export interface Beneficiary {
  id: string;
  name: string;
  hospitalId: string;
}

export interface AGV {
  id: string;
  name: string;
  status: 'Idle' | 'On Task' | 'Charging' | 'Maintenance Required';
  batteryLevel: number;
  currentLocation: string;
  currentTask: string | null;
}

// Define the stats structure to fix the any types
export interface Stats {
  totalQuantity: number;
  totalItems: number;
  lowStockItems: Medicine[];
  reorderPointItems: Medicine[];
  expiringSoonItems: Medicine[];
  accessibilityIndex: number;
  expiryWasteCost: number;
  totalStockValue: number;
}

// Define chart data structure
export interface ChartData {
  medicinesByWarehouse: Array<{ name: string; value: number }>;
  medicineStockStatus: Array<{ name: string; value: number }>;
  transferStatus: Array<{ name: string; value: number }>;
  distributionStatus: Array<{ name: string; value: number }>;
  beneficiariesByHospital: Array<{ name: string; value: number }>;
  agvStatus: Array<{ name: string; value: number }>;
  inventoryValueByCategory: Array<{ name: string; value: number }>;
  stockStatusDistribution: Array<{ name: string; value: number }>;
}

// Define forecast structure
export interface Forecast {
  medicineName: string;
  data: Array<{ month: string; demand: number }>;
}

// Define AI recommendation structure
export interface AIRecommendation {
  id: string;
  text: string;
}

export interface UseMediTrackDataReturn {
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

export enum SortMethod {
  NAME_AZ = 'name_az',
  NAME_ZA = 'name_za', 
  FEFO = 'fefo',
  LEFO = 'lefo'
}