package de.fhdo.sama.capstone;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JList;

import de.fhdo.sama.capstone.model.AGV;
import de.fhdo.sama.capstone.model.Location;
import de.fhdo.sama.capstone.model.Medicine;
import de.fhdo.sama.capstone.model.MedicineCategory;
import de.fhdo.sama.capstone.model.Transfer;
import de.fhdo.sama.capstone.model.Warehouse;

public class App {
    private JFrame frame;
    private JTextArea warehouseArea;
    private JTextArea agvArea;
    private JTextArea hospitalArea;
    private JTextField medicineField;
    private JTextField quantityField;
    private ExecutorService executor;

    private JList<String> hospitalList;
    private JList<String> warehouseList;
    private JList<String> medicineList;
    private JList<String> chargingStationList;

    public App() {
        executor = Executors.newCachedThreadPool();
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Medicine Delivery Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 600);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Warehouse Panel
        JPanel warehousePanel = new JPanel(new BorderLayout());
        warehousePanel.setBorder(BorderFactory.createTitledBorder("Warehouses"));
        warehouseArea = new JTextArea();
        warehouseArea.setEditable(false);
        warehouseList = new JList<>(new String[]{"Main Warehouse", "Secondary Warehouse"});
        warehousePanel.add(new JScrollPane(warehouseList), BorderLayout.CENTER);

        // AGV Panel
        JPanel agvPanel = new JPanel(new BorderLayout());
        agvPanel.setBorder(BorderFactory.createTitledBorder("AGV Operations"));
        agvArea = new JTextArea();
        agvArea.setEditable(false);
        agvPanel.add(new JScrollPane(agvArea), BorderLayout.CENTER);

        // Hospital Panel
        JPanel hospitalPanel = new JPanel(new BorderLayout());
        hospitalPanel.setBorder(BorderFactory.createTitledBorder("Hospitals"));
        hospitalArea = new JTextArea();
        hospitalArea.setEditable(false);
        hospitalList = new JList<>(new String[]{"City Hospital", "Rural Clinic"});
        hospitalPanel.add(new JScrollPane(hospitalList), BorderLayout.CENTER);

        // Medicine Panel
        JPanel medicinePanel = new JPanel(new BorderLayout());
        medicinePanel.setBorder(BorderFactory.createTitledBorder("Medicines"));
        medicineList = new JList<>(new String[]{"Med1", "Med2", "Med3"});
        medicinePanel.add(new JScrollPane(medicineList), BorderLayout.CENTER);

        // Charging Station Panel
        JPanel chargingStationPanel = new JPanel(new BorderLayout());
        chargingStationPanel.setBorder(BorderFactory.createTitledBorder("Charging Stations"));
        chargingStationList = new JList<>(new String[]{"Station 1", "Station 2"});
        chargingStationPanel.add(new JScrollPane(chargingStationList), BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel();
        JButton startDeliveryButton = new JButton("Start Delivery");
        startDeliveryButton.addActionListener(_ -> startDelivery());
        controlPanel.add(startDeliveryButton);

        JButton chargeButton = new JButton("Charge AGV");
        chargeButton.addActionListener(_ -> chargeAGV());
        controlPanel.add(chargeButton);

        JLabel medicineLabel = new JLabel("Medicine:");
        medicineField = new JTextField(10);
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityField = new JTextField(5);
        JButton orderButton = new JButton("Place Order");
        orderButton.addActionListener(_ -> placeOrder());

        controlPanel.add(medicineLabel);
        controlPanel.add(medicineField);
        controlPanel.add(quantityLabel);
        controlPanel.add(quantityField);
        controlPanel.add(orderButton);

        JPanel listPanel = new JPanel(new GridLayout(1, 4));
        listPanel.add(warehousePanel);
        listPanel.add(hospitalPanel);
        listPanel.add(medicinePanel);
        listPanel.add(chargingStationPanel);

        mainPanel.add(listPanel, BorderLayout.NORTH);
        mainPanel.add(agvPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void startDelivery() {
        AGV agv = new AGV("AGV-1");
        Location warehouseLocation = new Location("W1", "Main Warehouse", 0, 0);
        Location hospitalLocation = new Location("H1", "City Hospital", 10, 10);
        Warehouse warehouse = new Warehouse("Main Warehouse", List.of(new Medicine("Med1", 100, MedicineCategory.CURATIVE_MEDICINES)), warehouseLocation);

        executor.submit(() -> {
            try {
                if (agv.getBatteryLevel() < 20) {
                    logAGVOperation(agv.getName() + " battery low. Automatically charging.");
                    chargeAGV(agv);
                }

                logAGVOperation(agv.getName() + " started delivery.");

                // Simulate picking up medicine from the warehouse
                boolean success = warehouse.removeMedicine("Med1", 10);
                if (success) {
                    logAGVOperation(agv.getName() + " picked up 10 units of Med1.");
                } else {
                    logAGVOperation(agv.getName() + " failed to pick up medicine. Not enough stock.");
                    return;
                }

                Thread.sleep(2000); // Simulate delivery time

                // Create a transfer object to represent the delivery
                Transfer transfer = new Transfer();
                transfer.setId("T1");
                transfer.setCargo(new Medicine("Med1", 10, MedicineCategory.CURATIVE_MEDICINES));
                transfer.setFrom(warehouseLocation);
                transfer.setTo(hospitalLocation);
                transfer.setExecuter(agv);

                // Simulate completing the delivery
                logAGVOperation(agv.getName() + " delivered 10 units of Med1 to " + hospitalLocation.name() + ".");
            } catch (InterruptedException e) {
                logAGVOperation(agv.getName() + " was interrupted.");
            }
        });
    }

    private void placeOrder() {
        String medicineName = medicineField.getText();
        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            logHospitalOperation("Invalid quantity.");
            return;
        }

        Warehouse warehouse = new Warehouse("Main Warehouse", List.of(new Medicine("Med1", 100, MedicineCategory.CURATIVE_MEDICINES)), new Location("W1", "Main Warehouse", 0, 0));
        boolean success = warehouse.removeMedicine(medicineName, quantity);

        if (success) {
            logHospitalOperation("Order placed: " + quantity + " units of " + medicineName + " delivered.");
        } else {
            logHospitalOperation("Order failed: Not enough stock of " + medicineName + ".");
        }
    }

    private void chargeAGV() {
        AGV agv = new AGV("AGV-1");
        executor.submit(() -> {
            try {
                logAGVOperation(agv.getName() + " is charging at Station 1.");
                Thread.sleep(3000); // Simulate charging time
                agv.setBatteryLevel(100);
                logAGVOperation(agv.getName() + " is fully charged.");
            } catch (InterruptedException e) {
                logAGVOperation(agv.getName() + " charging was interrupted.");
            }
        });
    }

    private void chargeAGV(AGV agv) {
        try {
            logAGVOperation(agv.getName() + " is charging at Station 1.");
            Thread.sleep(3000); // Simulate charging time
            agv.setBatteryLevel(100);
            logAGVOperation(agv.getName() + " is fully charged.");
        } catch (InterruptedException e) {
            logAGVOperation(agv.getName() + " charging was interrupted.");
        }
    }

    private void logAGVOperation(String message) {
        SwingUtilities.invokeLater(() -> agvArea.append(message + "\n"));
    }

    private void logHospitalOperation(String message) {
        SwingUtilities.invokeLater(() -> hospitalArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }
}