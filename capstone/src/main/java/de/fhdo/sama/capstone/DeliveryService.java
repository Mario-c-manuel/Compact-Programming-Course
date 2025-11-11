package de.fhdo.sama.capstone;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.fhdo.sama.capstone.model.AGV;
import de.fhdo.sama.capstone.model.Location;
import de.fhdo.sama.capstone.model.Warehouse;

public class DeliveryService {
    private final ExecutorService executor;

    public DeliveryService() {
        this.executor = Executors.newCachedThreadPool();
    }

    public void startDelivery(Location warehouseLocation, Location hospitalLocation, Warehouse warehouse, String medicineName, int quantity, DeliveryCallback callback) {
        AGV agv = new AGV("AGV-1");

        if (warehouseLocation == null || hospitalLocation == null || warehouse == null) {
            callback.onLog("Please provide valid warehouse and hospital details.");
            return;
        }
        if (medicineName == null || medicineName.isBlank() || quantity <= 0) {
            callback.onLog("Please provide a valid medicine and quantity.");
            return;
        }

        executor.submit(() -> {
            try {
                if (agv.getBatteryLevel() < 20) {
                    callback.onLog(agv.getName() + " battery low. Automatically charging.");
                    chargeAGV(agv, callback);
                }

                callback.onLog(agv.getName() + " started delivery.");

                // Simulate picking up medicine from the warehouse
                boolean success = warehouse.removeMedicine(medicineName, quantity);
                if (success) {
                    callback.onLog(agv.getName() + " picked up " + quantity + " units of " + medicineName + ".");
                } else {
                    callback.onLog(agv.getName() + " failed to pick up medicine. Not enough stock.");
                    return;
                }

                Thread.sleep(2000); // Simulate delivery time

                callback.onLog(agv.getName() + " delivered " + quantity + " units of " + medicineName + " to " + hospitalLocation.name() + ".");
            } catch (InterruptedException e) {
                callback.onLog(agv.getName() + " was interrupted.");
            }
        });
    }

    public void placeOrder(Warehouse warehouse, String medicineName, int quantity, DeliveryCallback callback) {
        if (warehouse == null) {
            callback.onLog("Please provide a valid warehouse.");
            return;
        }

        boolean success = warehouse.removeMedicine(medicineName, quantity);

        if (success) {
            callback.onLog("Order placed: " + quantity + " units of " + medicineName + " delivered.");
        } else {
            callback.onLog("Order failed: Not enough stock of " + medicineName + ".");
        }
    }

    public void chargeAGV(AGV agv, DeliveryCallback callback) {
        try {
            callback.onLog(agv.getName() + " is charging at Station 1.");
            Thread.sleep(3000); // Simulate charging time
            agv.setBatteryLevel(100);
            callback.onLog(agv.getName() + " is fully charged.");
        } catch (InterruptedException e) {
            callback.onLog(agv.getName() + " charging was interrupted.");
        }
    }

    public interface DeliveryCallback {
        void onLog(String message);
    }
}