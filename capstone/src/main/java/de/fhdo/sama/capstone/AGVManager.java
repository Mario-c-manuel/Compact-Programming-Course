package de.fhdo.sama.capstone;

import de.fhdo.sama.capstone.model.AGV;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages multiple AGVs, their availability and assignment to tasks.
 */
public class AGVManager {
    private final List<AGV> agvFleet;
    private final List<AGV> busyAgvs;

    public AGVManager() {
        this.agvFleet = new ArrayList<>();
        this.busyAgvs = new ArrayList<>();
        initializeFleet();
    }

    private void initializeFleet() {
        // Create a fleet of 5 AGVs
        for (int i = 1; i <= 5; i++) {
            AGV agv = new AGV("AGV-" + i);
            agv.setId("AGV-" + i);
            // Randomize initial battery levels
            agv.setBatteryLevel(50 + (int)(Math.random() * 50)); // 50-100%
            agvFleet.add(agv);
        }
    }

    /**
     * Get an available AGV with sufficient battery.
     */
    public synchronized Optional<AGV> getAvailableAGV() {
        return agvFleet.stream()
            .filter(agv -> !busyAgvs.contains(agv))
            .filter(agv -> agv.getBatteryLevel() >= 20)
            .findFirst();
    }

    /**
     * Get any available AGV, even with low battery.
     */
    public synchronized Optional<AGV> getAnyAvailableAGV() {
        return agvFleet.stream()
            .filter(agv -> !busyAgvs.contains(agv))
            .findFirst();
    }

    /**
     * Mark an AGV as busy.
     */
    public synchronized void markBusy(AGV agv) {
        if (!busyAgvs.contains(agv)) {
            busyAgvs.add(agv);
        }
    }

    /**
     * Mark an AGV as available.
     */
    public synchronized void markAvailable(AGV agv) {
        busyAgvs.remove(agv);
    }

    /**
     * Get all AGVs in the fleet.
     */
    public List<AGV> getAllAGVs() {
        return new ArrayList<>(agvFleet);
    }

    /**
     * Check if an AGV is busy.
     */
    public synchronized boolean isBusy(AGV agv) {
        return busyAgvs.contains(agv);
    }

    /**
     * Get count of available AGVs.
     */
    public synchronized int getAvailableCount() {
        return agvFleet.size() - busyAgvs.size();
    }

    /**
     * Get a specific AGV by name.
     */
    public Optional<AGV> getAGVByName(String name) {
        return agvFleet.stream()
            .filter(agv -> agv.getName().equals(name))
            .findFirst();
    }
}
