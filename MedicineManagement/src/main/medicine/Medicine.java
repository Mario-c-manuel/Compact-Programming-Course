package main.medicine;

import java.util.Date;

public class Medicine {
    private int id;
    private String name;
    private String batchNumber;
    private String manufacturer;
    private int quantity;
    private String unit;
    private Date expiryDate;
    private String storageLocation;
    private int reorderLevel;

    public Medicine(int id, String name, String batchNumber, String manufacturer, int quantity, String unit,
                    Date expiryDate, String storageLocation, int reorderLevel) {
        this.id = id;
        this.name = name;
        this.batchNumber = batchNumber;
        this.manufacturer = manufacturer;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
        this.storageLocation = storageLocation;
        this.reorderLevel = reorderLevel;
    }

    public int getQuantity() {
        return quantity;
    }

    public void updateQuantity(int amount) {
        this.quantity += amount;
    }

    @Override
    public String toString() {
        return name + " (" + quantity + " " + unit + ")";
    }
}
