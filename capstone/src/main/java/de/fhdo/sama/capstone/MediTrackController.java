package de.fhdo.sama.capstone;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import de.fhdo.sama.capstone.model.*;

public class MediTrackController {

    // --- FXML UI Elements reverted to use String for simpler ListViews ---
    @FXML private ListView<String> warehouseList;
    @FXML private ListView<String> hospitalList;
    @FXML private ListView<String> medicineList;
    @FXML private ListView<String> agvList;

    @FXML private ChoiceBox<String> warehouseChoice;
    @FXML private ChoiceBox<String> hospitalChoice;
    @FXML private ChoiceBox<String> medicineChoice;
    @FXML private ChoiceBox<String> stationChoice;
    @FXML private ChoiceBox<String> agvChoice;

    @FXML private TextField quantityField;
    @FXML private Button startDeliveryBtn;
    @FXML private Button chargeAgvBtn;
    @FXML private Button placeOrderBtn;
    @FXML private TextArea logArea;

    // in-memory data
    private final List<Warehouse> warehouses = new ArrayList<>();
    private final List<Hospital> hospitals = new ArrayList<>();
    private final List<Medicine> medicines = new ArrayList<>();
    private final List<AGV> agvs = new ArrayList<>();
    private final List<String> chargingStations = new ArrayList<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final Random random = new Random();

    @FXML
    public void initialize() {
        // Use Platform.runLater to ensure initialization runs after the FXML has fully loaded
        Platform.runLater(() -> {
            loadInitialMockData();
            bindUI();
            // Call refresh methods to populate lists immediately
            refreshWarehouseList();
            refreshHospitalList();
            refreshMedicineList();
            refreshAgvList();
            startBatteryAndAgvMonitoring();
            log("System initialized. Welcome to Meditrack.");
        });
    }

    private void loadInitialMockData() {
        // Charging stations
        chargingStations.addAll(Arrays.asList("Station 1", "Station 2", "Station 3"));

        // Warehouses and medicines
        Warehouse w1 = new Warehouse("wh1", "Main Warehouse", new Location("W1", "Main Warehouse", 0, 0));
        Warehouse w2 = new Warehouse("wh2", "Secondary Warehouse", new Location("W2", "Secondary Warehouse", 50, 0));
        warehouses.addAll(Arrays.asList(w1, w2));

        // Note: medicines here represents all available types, not current stock
        medicines.add(new Medicine("med1", "Artemether/Lumefantrine (20/120mg)", 2500, LocalDate.parse("2025-12-31"), "wh1", "Shelf A1", "Antimalarial", 500, 3.50));
        medicines.add(new Medicine("med2", "Azithromycin 500mg", 90, LocalDate.parse("2024-10-31"), "wh2", "Rack B2", "Antibiotic", 150, 0.75));
        medicines.add(new Medicine("med3", "TLD (Tenofovir/Lamivudine/Dolutegravir)", 800, LocalDate.parse("2026-06-30"), "wh1", "Shelf A2", "Antiretroviral", 200, 7.20));

        // populate warehouses with medicine references (actual stock)
        for (Medicine m : medicines) {
            Warehouse target = warehouses.stream().filter(w -> w.getId().equals(m.getWarehouseId())).findFirst().orElse(warehouses.get(0));
            target.addMedicine(new Medicine(m)); // add a copy
        }

        // Hospitals
        // Assuming Hospital constructor is Hospital(String id, String name, String location)
        hospitals.add(new Hospital("h1", "City Hospital", "Nairobi, Kenya"));
        hospitals.add(new Hospital("h2", "Rural Clinic", "Outskirts"));

        // AGVs
        // Assuming AGV constructor is AGV(String id, String name, Status status, int battery, String location, String task)
        agvs.add(new AGV("agv-001", "PharmaBot Alpha", AGV.Status.IDLE, 85, "Main Warehouse", null));
        agvs.add(new AGV("agv-002", "PharmaBot Beta", AGV.Status.CHARGING, 22, "Main Warehouse", null));
        agvs.add(new AGV("agv-003", "MediMover Gamma", AGV.Status.ON_TASK, 67, "Mogadishu MedHub", "Transferring Azithromycin"));
    }

    private void bindUI() {
        // Lists for ChoiceBoxes (using names)
        ObservableList<String> whNames = FXCollections.observableArrayList();
        warehouses.forEach(w -> whNames.add(w.getName()));
        warehouseChoice.setItems(whNames);

        ObservableList<String> hospNames = FXCollections.observableArrayList();
        hospitals.forEach(h -> hospNames.add(h.getName()));
        hospitalChoice.setItems(hospNames);

        ObservableList<String> medNames = FXCollections.observableArrayList();
        medicines.forEach(m -> medNames.add(m.getName()));
        medicineChoice.setItems(medNames);

        ObservableList<String> agvNames = FXCollections.observableArrayList();
        agvs.forEach(a -> agvNames.add(a.getName()));
        agvChoice.setItems(agvNames);

        stationChoice.setItems(FXCollections.observableArrayList(chargingStations));

        // defaults
        if (!whNames.isEmpty()) warehouseChoice.setValue(whNames.get(0));
        if (!hospNames.isEmpty()) hospitalChoice.setValue(hospNames.get(0));
        if (!medNames.isEmpty()) medicineChoice.setValue(medNames.get(0));
        if (!agvNames.isEmpty()) agvChoice.setValue(agvNames.get(0));
        if (!chargingStations.isEmpty()) stationChoice.setValue(chargingStations.get(0));

        // button actions
        // FIX: Replaced lambda function parameters with (ignored) underscore where not needed to clear warnings
        startDeliveryBtn.setOnAction(_ -> handleStartDelivery()); // Fixed/Checked
        chargeAgvBtn.setOnAction(_ -> handleChargeSelectedAgv()); // Fixed/Checked
        placeOrderBtn.setOnAction(_ -> handlePlaceOrder()); // Fixed/Checked
    }

    private void startBatteryAndAgvMonitoring() {
        // drain battery for ON_TASK AGVs and charge for CHARGING
        scheduler.scheduleAtFixedRate(() -> {
            boolean changed = false;
            for (AGV a : agvs) {
                if (a.getStatus() == AGV.Status.ON_TASK) {
                    a.setBatteryLevel(Math.max(0, a.getBatteryLevel() - 2));
                    if (a.getBatteryLevel() <= 20) {
                        a.setStatus(AGV.Status.CHARGING);
                        a.setCurrentTask("Returning to charging station");
                        changed = true;
                        logAsync(a.getName() + " low battery -> returning to charge.");
                    }
                } else if (a.getStatus() == AGV.Status.CHARGING) {
                    a.setBatteryLevel(Math.min(100, a.getBatteryLevel() + 5));
                    if (a.getBatteryLevel() >= 100) {
                        a.setStatus(AGV.Status.IDLE);
                        a.setCurrentTask(null);
                        changed = true;
                        logAsync(a.getName() + " fully charged.");
                    }
                }
            }
            if (changed) refreshAgvList();
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void handleStartDelivery() {
        String agvName = agvChoice.getValue();
        String warehouseName = warehouseChoice.getValue();
        String hospitalName = hospitalChoice.getValue();
        String medicineName = medicineChoice.getValue();
        String qtyText = quantityField.getText().trim();

        if (agvName == null || warehouseName == null || hospitalName == null || medicineName == null || qtyText.isEmpty()) {
            log("Please select AGV, warehouse, hospital, medicine and enter quantity.");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyText);
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            log("Invalid quantity. Enter a positive integer.");
            return;
        }

        AGV selectedAgv = agvs.stream().filter(a -> a.getName().equals(agvName)).findFirst().orElse(null);
        Warehouse source = warehouses.stream().filter(w -> w.getName().equals(warehouseName)).findFirst().orElse(null);
        Hospital dest = hospitals.stream().filter(h -> h.getName().equals(hospitalName)).findFirst().orElse(null);
        Medicine medTemplate = medicines.stream().filter(m -> m.getName().equals(medicineName)).findFirst().orElse(null);

        if (selectedAgv == null || source == null || dest == null || medTemplate == null) {
            log("Selected data not found. Please verify selections.");
            return;
        }

        // Run delivery on executor
        scheduler.execute(() -> runDelivery(selectedAgv, source, dest, medTemplate, qty));
    }

    private void runDelivery(AGV agv, Warehouse source, Hospital dest, Medicine medicineTemplate, int qty) {
        synchronized (source) {
            // check stock
            Optional<Medicine> stockOpt = source.getMedicines().stream().filter(m -> m.getName().equals(medicineTemplate.getName())).findFirst();
            if (stockOpt.isEmpty() || stockOpt.get().getQuantity() < qty) {
                logAsync("Order failed: Not enough stock of " + medicineTemplate.getName() + " at " + source.getName());
                return;
            }
            // reserve / reduce source immediately
            stockOpt.get().setQuantity(stockOpt.get().getQuantity() - qty);
        }

        // assign AGV
        agv.setStatus(AGV.Status.ON_TASK);
        agv.setCurrentTask("Picking up " + qty + " x " + medicineTemplate.getName());
        agv.setCurrentLocation(source.getName());
        refreshAgvList();
        refreshWarehouseList(); // Update warehouse list after stock change
        logAsync(agv.getName() + " assigned: Pick up " + qty + " units of " + medicineTemplate.getName() + " from " + source.getName() + " to " + dest.getName());

        // simulate pickup time
        sleepMillis(1000 + random.nextInt(2000));

        // simulate travel
        agv.setCurrentTask("Traveling to " + dest.getName());
        logAsync(agv.getName() + " traveling to " + dest.getName() + "...");
        refreshAgvList(); // Update AGV status while traveling
        sleepMillis(1500 + random.nextInt(3000));

        // deliver: could create new stock at destination if warehouse exists; here just log delivery
        agv.setCurrentTask("Delivering " + qty + " x " + medicineTemplate.getName());
        logAsync(agv.getName() + " delivered " + qty + " units of " + medicineTemplate.getName() + " to " + dest.getName());

        // after delivery, battery drains
        agv.setBatteryLevel(Math.max(0, agv.getBatteryLevel() - (10 + random.nextInt(10))));

        // decide whether to go charge or return to idle
        if (agv.getBatteryLevel() <= 20) {
            String station = chooseChargingStation();
            agv.setCurrentTask("Going to charge at " + station);
            agv.setStatus(AGV.Status.CHARGING);
            logAsync(agv.getName() + " going to charge at " + station + ".");
            // charging will be handled by scheduled updater
        } else {
            agv.setStatus(AGV.Status.IDLE);
            agv.setCurrentTask(null);
            agv.setCurrentLocation(dest.getName());
        }
        refreshAgvList();
    }

    private void handleChargeSelectedAgv() {
        String agvName = agvChoice.getValue();
        if (agvName == null) {
            log("Select an AGV first.");
            return;
        }
        AGV agv = agvs.stream().filter(a -> a.getName().equals(agvName)).findFirst().orElse(null);
        if (agv == null) { log("AGV not found."); return; }

        // set to charging and schedule immediate charging simulation
        agv.setStatus(AGV.Status.CHARGING);
        agv.setCurrentTask("Charging at " + chooseChargingStation());
        refreshAgvList();
        log("Manual charging started for " + agv.getName());
    }

    private void handlePlaceOrder() {
        String medicineName = medicineChoice.getValue();
        String qtyText = quantityField.getText().trim();
        String warehouseName = warehouseChoice.getValue();

        if (medicineName == null || qtyText.isEmpty() || warehouseName == null) {
            log("Select warehouse, medicine and enter quantity to place order.");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyText);
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            log("Invalid quantity.");
            return;
        }

        Warehouse warehouse = warehouses.stream().filter(w -> w.getName().equals(warehouseName)).findFirst().orElse(null);
        if (warehouse == null) { log("Warehouse not found."); return; }

        boolean success = warehouse.removeMedicineByName(medicineName, qty);
        if (success) {
            log("Order placed: " + qty + " units of " + medicineName + " delivered from " + warehouse.getName());
        } else {
            log("Order failed: Not enough stock of " + medicineName + " at " + warehouse.getName());
        }
        refreshWarehouseList();
    }

    private String chooseChargingStation() {
        return chargingStations.get(random.nextInt(chargingStations.size()));
    }

    // --- REFRESH METHODS UPDATED TO AVOID MISSING toDisplayString() METHODS ---

    private void refreshAgvList() {
        Platform.runLater(() -> {
            ObservableList<String> items = FXCollections.observableArrayList();
            // Using AGV properties directly to form a display string
            for (AGV a : agvs) {
                String task = a.getCurrentTask() != null ? " - Task: " + a.getCurrentTask() : "";
                items.add(String.format("%s (%d%%) | Status: %s%s", 
                    a.getName(), a.getBatteryLevel(), a.getStatus(), task));
            }
            agvList.setItems(items);
        });
    }

    private void refreshWarehouseList() {
        Platform.runLater(() -> {
            ObservableList<String> items = FXCollections.observableArrayList();
            // Display warehouse with individual medicine stock items
            for (Warehouse w : warehouses) {
                // Add warehouse header
                items.add(String.format(">> %s (ID: %s):", w.getName(), w.getId()));
                
                // Add each medicine with its quantity
                List<Medicine> medicines = w.getMedicines();
                if (medicines.isEmpty()) {
                    items.add("    - No medicines in stock");
                } else {
                    for (Medicine m : medicines) {
                        items.add(String.format("    - %s: %d units", m.getName(), m.getQuantity()));
                    }
                }
                // Add empty line between warehouses for readability
                items.add("");
            }
            warehouseList.setItems(items);
        });
    }

    private void refreshHospitalList() {
        // FIX: Removed call to h.toDisplayString() which caused the InvocationTargetException/Unresolved compilation problem
        Platform.runLater(() -> {
            ObservableList<String> items = FXCollections.observableArrayList();
            // Using Hospital name and location directly
            for (Hospital h : hospitals) items.add(h.getName() + " (" + h.getLocation() + ")");
            hospitalList.setItems(items);
        });
    }

    private void refreshMedicineList() {
        Platform.runLater(() -> {
            ObservableList<String> items = FXCollections.observableArrayList();
            // Display medicine name
            for (Medicine m : medicines) items.add(m.getName());
            medicineList.setItems(items);
        });
    }
    
    // --- UPDATED LOG METHOD (Removed timestamp) ---

    private void log(String msg) {
        Platform.runLater(() -> {
            logArea.appendText(msg + "\n");
        });
    }

    private void logAsync(String msg) {
        // off-thread call-safe log
        log(msg);
    }

    private void sleepMillis(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}