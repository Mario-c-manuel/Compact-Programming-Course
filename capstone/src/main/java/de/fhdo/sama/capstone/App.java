package de.fhdo.sama.capstone;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import de.fhdo.sama.capstone.model.AGV;
import de.fhdo.sama.capstone.model.Location;
import de.fhdo.sama.capstone.model.Medicine;
import de.fhdo.sama.capstone.model.MedicineCategory;
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
        frame.setSize(1400, 900);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Warehouse Panel
        JPanel warehousePanel = createPanel("Warehouses", warehouseList = new JList<>(new String[]{"Main Warehouse", "Secondary Warehouse"}));

        // AGV Panel
        JPanel agvPanel = createPanel("AGV Operations", agvArea = createTextArea(15));

        // Hospital Panel
        JPanel hospitalPanel = createPanel("Hospitals", hospitalList = new JList<>(new String[]{"City Hospital", "Rural Clinic"}));

        // Medicine Panel
        JPanel medicinePanel = createPanel("Medicines", medicineList = new JList<>(new String[]{"Med1", "Med2", "Med3"}));

        // Charging Station Panel
        JPanel chargingStationPanel = createPanel("Charging Stations", chargingStationList = new JList<>(new String[]{"Station 1", "Station 2"}));

        // Control Panel
        JPanel controlPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        actionPanel.add(createButton("Start Delivery", _ -> startDelivery()));
        actionPanel.add(createButton("Charge AGV", _ -> chargeAGV()));

        JPanel orderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        orderPanel.add(new JLabel("Medicine:"));
        medicineField = new JTextField(10);
        orderPanel.add(medicineField);
        orderPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField(5);
        orderPanel.add(quantityField);
        orderPanel.add(createButton("Place Order", _ -> placeOrder()));

        controlPanel.add(actionPanel);
        controlPanel.add(orderPanel);

        JPanel listPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        listPanel.add(warehousePanel);
        listPanel.add(hospitalPanel);
        listPanel.add(medicinePanel);
        listPanel.add(chargingStationPanel);

        mainPanel.add(listPanel, BorderLayout.CENTER);
        mainPanel.add(agvPanel, BorderLayout.SOUTH);
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createPanel(String title, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(new JScrollPane(component), BorderLayout.CENTER);
        return panel;
    }

    private JTextArea createTextArea(int rows) {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setRows(rows);
        return textArea;
    }

    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.addActionListener(action);
        return button;
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