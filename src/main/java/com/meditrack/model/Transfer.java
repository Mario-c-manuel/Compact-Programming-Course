package com.meditrack.model;

public class Transfer {
    private String transferId;
    private String sourceWarehouseId;
    private String targetWarehouseId;
    private String medicineId;
    private int quantity;
    private String date;
    private String status; // 'Completed', 'In Transit', 'Pending'
    
    public Transfer() {}
    
    public Transfer(String transferId, String sourceWarehouseId, String targetWarehouseId, 
                   String medicineId, int quantity, String date, String status) {
        this.transferId = transferId;
        this.sourceWarehouseId = sourceWarehouseId;
        this.targetWarehouseId = targetWarehouseId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.date = date;
        this.status = status;
    }
    
    public String getTransferId() { return transferId; }
    public void setTransferId(String transferId) { this.transferId = transferId; }
    public String getSourceWarehouseId() { return sourceWarehouseId; }
    public void setSourceWarehouseId(String sourceWarehouseId) { this.sourceWarehouseId = sourceWarehouseId; }
    public String getTargetWarehouseId() { return targetWarehouseId; }
    public void setTargetWarehouseId(String targetWarehouseId) { this.targetWarehouseId = targetWarehouseId; }
    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}