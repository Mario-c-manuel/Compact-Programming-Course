package com.meditrack.model;

public class Distribution {
    private String distributionId;
    private String source; // Supplier or Warehouse ID
    private String destination; // Vendor or Customer
    private String medicineId;
    private String batchNumber;
    private int quantity;
    private String status; // 'Delivered', 'Shipped', 'Processing'
    
    public Distribution() {}
    
    public Distribution(String distributionId, String source, String destination, 
                       String medicineId, String batchNumber, int quantity, String status) {
        this.distributionId = distributionId;
        this.source = source;
        this.destination = destination;
        this.medicineId = medicineId;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
        this.status = status;
    }
    
    public String getDistributionId() { return distributionId; }
    public void setDistributionId(String distributionId) { this.distributionId = distributionId; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}