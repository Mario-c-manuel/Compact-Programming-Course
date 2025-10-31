package meditrack;

import java.util.HashMap;
import java.util.Map;

public class MedicineStorage {
    private String storageName;
    private Map<String, Integer> inventory;

    public MedicineStorage(String storageName) {
        this.storageName = storageName;
        this.inventory = new HashMap<>();
    }

    public void addMedicine(String medicineName, int quantity) {
        inventory.put(medicineName, inventory.getOrDefault(medicineName, 0) + quantity);
        DailyLogger.log("Storage", storageName, "Added " + quantity + " of " + medicineName);
    }

    public void removeMedicine(String medicineName, int quantity) {
        int currentQty = inventory.getOrDefault(medicineName, 0);
        if (currentQty >= quantity) {
            inventory.put(medicineName, currentQty - quantity);
            DailyLogger.log("Storage", storageName, "Removed " + quantity + " of " + medicineName);
        } else {
            DailyLogger.log("Storage", storageName, "Failed to remove " + quantity + " of " + medicineName + " (only " + currentQty + " available)");
        }
    }

    public void printInventory() {
        System.out.println("Inventory of " + storageName + ":");
        inventory.forEach((medicine, qty) -> System.out.println(medicine + " -> " + qty));
    }

    public String getStorageName() {
        return storageName;
    }
}
