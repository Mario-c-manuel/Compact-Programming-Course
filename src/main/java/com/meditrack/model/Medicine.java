package com.meditrack.model;

public class Medicine {
    private String id;
    private String name;
    private int quantity;
    private String expiryDate;
    private String warehouseId;
    private String location;
    private String category;
    private int reorderPoint;
    private double unitPrice;
    
    public Medicine() {}
    
    public Medicine(String id, String name, int quantity, String expiryDate, 
                   String warehouseId, String location, String category, 
                   int reorderPoint, double unitPrice) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.warehouseId = warehouseId;
        this.location = location;
        this.category = category;
        this.reorderPoint = reorderPoint;
        this.unitPrice = unitPrice;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(int reorderPoint) { this.reorderPoint = reorderPoint; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
}