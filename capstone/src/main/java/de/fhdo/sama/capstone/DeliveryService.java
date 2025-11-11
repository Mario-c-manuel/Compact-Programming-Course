package de.fhdo.sama.capstone;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.fhdo.sama.capstone.model.AGV;
import de.fhdo.sama.capstone.model.Location;
import de.fhdo.sama.capstone.model.Warehouse;

public class DeliveryService {
    private final ExecutorService executor;
    private final AGVManager agvManager;

    public DeliveryService() {
        this.executor = Executors.newCachedThreadPool();
        this.agvManager = new AGVManager();
    }

    public AGVManager getAgvManager() {
        return agvManager;
    }

    public void startDelivery(Location warehouseLocation, Location hospitalLocation, Warehouse warehouse, String medicineName, int quantity, DeliveryCallback callback) {
        if (warehouseLocation == null || hospitalLocation == null || warehouse == null) {
            callback.onLog("ERROR: Please provide valid warehouse and hospital details.");
            return;
        }
        if (medicineName == null || medicineName.isBlank() || quantity <= 0) {
            callback.onLog("ERROR: Please provide a valid medicine and quantity.");
            return;
        }

        // Get an available AGV
        var agvOpt = agvManager.getAnyAvailableAGV();
        if (agvOpt.isEmpty()) {
            callback.onLog("ERROR: No AGVs available. All AGVs are busy.");
            return;
        }

        AGV agv = agvOpt.get();
        agvManager.markBusy(agv);

        executor.submit(() -> {
            try {
                if (agv.getBatteryLevel() < 20) {
                    callback.onLog(agv.getName() + " battery low (" + agv.getBatteryLevel() + "%). Automatically charging.");
                    chargeAGV(agv, callback);
                }

                callback.onLog(agv.getName() + " started delivery from " + warehouseLocation.name() + " to " + hospitalLocation.name() + " (Battery: " + agv.getBatteryLevel() + "%)");

                // Simulate picking up medicine from the warehouse
                boolean success = warehouse.removeMedicine(medicineName, quantity);
                if (!success) {
                    callback.onLog(agv.getName() + " failed to pick up medicine. Not enough stock.");
                    return;
                }
                
                callback.onLog(agv.getName() + " picked up " + quantity + " units of " + medicineName + " at " + warehouseLocation.name() + " (Battery: " + agv.getBatteryLevel() + "%)");

                Thread.sleep(2000); // Simulate delivery time
                
                // Simulate battery consumption during delivery
                int batteryConsumed = 10 + (int)(Math.random() * 15); // Random 10-25%
                agv.setBatteryLevel(Math.max(0, agv.getBatteryLevel() - batteryConsumed));

                callback.onLog(agv.getName() + " delivered " + quantity + " units of " + medicineName + " to " + hospitalLocation.name() + " (Battery: " + agv.getBatteryLevel() + "%)");
            } catch (InterruptedException e) {
                callback.onLog(agv.getName() + " was interrupted.");
                Thread.currentThread().interrupt();
            } finally {
                agvManager.markAvailable(agv);
            }
        });
    }

    public void chargeAGV(AGV agv, DeliveryCallback callback) {
        try {
            callback.onLog(agv.getName() + " is charging at Station 1.");
            Thread.sleep(3000); // Simulate charging time
            agv.setBatteryLevel(100);
            callback.onLog(agv.getName() + " is fully charged.");
        } catch (InterruptedException e) {
            callback.onLog(agv.getName() + " charging was interrupted.");
            Thread.currentThread().interrupt();
        }
    }

    public interface DeliveryCallback {
        void onLog(String message);
    }
}