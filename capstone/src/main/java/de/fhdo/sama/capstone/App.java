package de.fhdo.sama.capstone;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.fhdo.sama.capstone.model.AGV;
import de.fhdo.sama.capstone.model.Location;
import de.fhdo.sama.capstone.model.Medicine;
import de.fhdo.sama.capstone.model.MedicineCategory;
import de.fhdo.sama.capstone.model.Warehouse;

/**
 * Main application class for the Medicine Delivery Dashboard. Handles GUI setup
 * and user interactions.
 */
public class App {
	// --- GUI Components ---
	private JFrame frame;
	private JTextArea agvArea;
	private JList<String> hospitalList;
	private JList<String> warehouseList;
	private JList<String> medicineList;
	private JList<String> chargingStationList;
	private JTextField quantityField; // Make quantity field a class member for easier access

	// --- Backend/Service ---
	private final DeliveryService deliveryService;
	private final ExecutorService executor;
	private final AppConfig config;

	private int medicineIdCounter = 1000;

	/**
	 * Constructs the application and initializes the GUI.
	 */
	public App() {
		this.executor = Executors.newCachedThreadPool();
		this.deliveryService = new DeliveryService();
		this.config = new AppConfig();
		this.config.loadDefaults();
		initializeGUI();
	}

	/**
	 * Initializes the main GUI layout and components.
	 */
	private void initializeGUI() {
		frame = new JFrame("Medicine Delivery Dashboard");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 800);
		frame.setLocationRelativeTo(null);

		// Main container with BorderLayout
		JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		// --- Left: Entity Lists ---
		JPanel listsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
		listsPanel.setBorder(BorderFactory.createTitledBorder("Entities"));
		listsPanel.add(createListPanel("Warehouses", warehouseList));
		listsPanel.add(createListPanel("Hospitals", hospitalList));
		listsPanel.add(createListPanel("Medicines", medicineList));
		listsPanel.add(createListPanel("Charging Stations", chargingStationList));

		// --- Center: Actions and Order ---
		JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
		centerPanel.add(createActionPanel(), BorderLayout.NORTH);
		centerPanel.add(createOrderPanel(), BorderLayout.CENTER);

		// --- Right: Configuration ---
		JPanel configPanel = createConfigPanel();

		// --- Bottom: AGV Log ---
		agvArea = createTextArea(10);
		agvArea.setToolTipText("AGV operations and system log");
		JScrollPane agvScroll = new JScrollPane(agvArea);
		agvScroll.setBorder(BorderFactory.createTitledBorder("AGV Operations Log"));
		agvScroll.setPreferredSize(new java.awt.Dimension(0, 180));

		// --- Assemble main layout ---
		mainPanel.add(listsPanel, BorderLayout.WEST);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(configPanel, BorderLayout.EAST);
		mainPanel.add(agvScroll, BorderLayout.SOUTH);

		frame.setContentPane(mainPanel);
		frame.setVisible(true);
	}

	private JPanel createListPanel(String title, JList<String> list) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder(title));
		JScrollPane scroll = new JScrollPane(list);
		scroll.setPreferredSize(new java.awt.Dimension(180, 80));
		panel.add(scroll, BorderLayout.CENTER);
		return panel;
	}

	/**
	 * Creates the action panel with delivery and charge buttons.
	 */
	private JPanel createActionPanel() {
		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // More spacing
		JButton startDeliveryBtn = createButton("Start Delivery", e -> onStartDelivery());
		startDeliveryBtn.setToolTipText("Start delivery from selected warehouse to selected hospital");
		JButton chargeAgvBtn = createButton("Charge AGV", e -> onChargeAGV());
		chargeAgvBtn.setToolTipText("Send AGV to charging station");
		actionPanel.add(startDeliveryBtn);
		actionPanel.add(chargeAgvBtn);
		return actionPanel;
	}

	/**
	 * Creates the order panel with medicine/quantity fields and order button.
	 */
	private JPanel createOrderPanel() {
		JPanel orderPanel = new JPanel(new GridLayout(3, 2, 10, 10));
		orderPanel.setBorder(BorderFactory.createTitledBorder("Place Medicine Order"));
		orderPanel.add(new JLabel("Select Medicine:"));
		orderPanel.add(new JScrollPane(medicineList));
		orderPanel.add(new JLabel("Quantity:"));
		quantityField = new JTextField(5);
		quantityField.setToolTipText("Enter quantity to order");
		orderPanel.add(quantityField);
		JButton orderButton = createButton("Place Order", e -> onPlaceOrder(quantityField));
		orderButton.setToolTipText("Place an order for the selected medicine");
		orderPanel.add(new JLabel()); // Empty cell for alignment
		orderPanel.add(orderButton);
		return orderPanel;
	}

	private JPanel createPanel(String title, JComponent component) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder(title));
		panel.add(new JScrollPane(component), BorderLayout.CENTER);
		return panel;
	}

	/**
	 * Creates a non-editable text area.
	 */
	private JTextArea createTextArea(int rows) {
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		textArea.setRows(rows);
		return textArea;
	}

	/**
	 * Creates a button with the given text and action.
	 */
	private JButton createButton(String text, ActionListener action) {
		JButton button = new JButton(text);
		button.setFont(new Font("Arial", Font.BOLD, 14));
		button.addActionListener(action);
		return button;
	}

	/**
	 * Creates the configuration panel with buttons for adding entities.
	 */
	private JPanel createConfigPanel() {
		JPanel configPanel = new JPanel(new GridLayout(4, 1, 10, 10)); // Vertical, more spacing
		JButton addWarehouseBtn = createButton("Add Warehouse", e -> onAddWarehouse());
		addWarehouseBtn.setToolTipText("Add a new warehouse to the system");
		JButton addMedicineBtn = createButton("Add Medicine", e -> onAddMedicine());
		addMedicineBtn.setToolTipText("Add a new medicine to the system");
		JButton addLocationBtn = createButton("Add Location", e -> onAddLocation());
		addLocationBtn.setToolTipText("Add a new location to the system");
		JButton addChargingStationBtn = createButton("Add Charging Station", e -> onAddChargingStation());
		addChargingStationBtn.setToolTipText("Add a new charging station");
		configPanel.add(addWarehouseBtn);
		configPanel.add(addMedicineBtn);
		configPanel.add(addLocationBtn);
		configPanel.add(addChargingStationBtn);
		configPanel.setBorder(BorderFactory.createTitledBorder("Configuration"));
		return configPanel;
	}

	// --- Event Handlers ---

	/**
	 * Handles the Start Delivery button action.
	 */
	private void onStartDelivery() {
		String selectedWarehouse = warehouseList.getSelectedValue();
		String selectedHospital = hospitalList.getSelectedValue();
		String selectedMedicine = medicineList.getSelectedValue();
		int quantity = 1;
		try {
			if (quantityField != null) {
				quantity = Integer.parseInt(quantityField.getText());
			}
		} catch (Exception ex) {
			quantity = 1;
		}
		if (selectedWarehouse == null || selectedHospital == null || selectedMedicine == null || quantity <= 0) {
			logAGVOperation("Please select a warehouse, hospital, medicine, and enter a valid quantity.");
			return;
		}
		Warehouse warehouse = config.getWarehouses().stream()
			.filter(w -> w.getName().equals(selectedWarehouse))
			.findFirst().orElse(null);
		if (warehouse == null) {
			logAGVOperation("Warehouse not found in config.");
			return;
		}
		Location warehouseLocation = warehouse.getLocation();
		Location hospitalLocation = config.getLocations().stream()
			.filter(l -> l.name().equals(selectedHospital))
			.findFirst().orElse(new Location("H1", selectedHospital, 0, 0));
		deliveryService.startDelivery(warehouseLocation, hospitalLocation, warehouse, selectedMedicine, quantity, this::logAGVOperation);
	}

	/**
	 * Handles the Place Order button action.
	 */
	private void onPlaceOrder(JTextField quantityField) {
		String medicineName = medicineList.getSelectedValue();
		if (medicineName == null) {
			javax.swing.JOptionPane.showMessageDialog(frame, "Please select a medicine from the list.", "Order Error",
					javax.swing.JOptionPane.ERROR_MESSAGE);
			return;
		}
		int quantity;
		try {
			quantity = Integer.parseInt(quantityField.getText());
		} catch (NumberFormatException ex) {
			javax.swing.JOptionPane.showMessageDialog(frame, "Invalid quantity.", "Order Error",
					javax.swing.JOptionPane.ERROR_MESSAGE);
			quantityField.setText("");
			quantityField.requestFocusInWindow();
			return;
		}
		String selectedWarehouse = warehouseList.getSelectedValue();
		if (selectedWarehouse == null) {
			javax.swing.JOptionPane.showMessageDialog(frame, "Please select a warehouse.", "Order Error",
					javax.swing.JOptionPane.ERROR_MESSAGE);
			return;
		}
		Warehouse warehouse = config.getWarehouses().stream().filter(w -> w.getName().equals(selectedWarehouse))
				.findFirst().orElse(null);
		if (warehouse == null) {
			javax.swing.JOptionPane.showMessageDialog(frame, "Warehouse not found in config.", "Order Error",
					javax.swing.JOptionPane.ERROR_MESSAGE);
			return;
		}
		deliveryService.placeOrder(warehouse, medicineName, quantity, this::logAGVOperation);
		quantityField.setText("");
		quantityField.requestFocusInWindow();
	}

	/**
	 * Handles the Charge AGV button action.
	 */
	private void onChargeAGV() {
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

	private void onAddWarehouse() {
		String name = javax.swing.JOptionPane.showInputDialog(frame, "Warehouse name:");
		if (name == null || name.isBlank())
			return;
		Location location = new Location("W" + (config.getLocations().size() + 1), name, 0, 0);
		config.getLocations().add(location);
		Warehouse warehouse = new Warehouse(name, new java.util.ArrayList<>(config.getMedicines()), location);
		config.getWarehouses().add(warehouse);
		updateWarehouseList();
		// Select and scroll to new warehouse
		warehouseList.setSelectedIndex(warehouseList.getModel().getSize() - 1);
		warehouseList.ensureIndexIsVisible(warehouseList.getModel().getSize() - 1);
		javax.swing.JOptionPane.showMessageDialog(frame, "Warehouse '" + name + "' added successfully.", "Success",
				javax.swing.JOptionPane.INFORMATION_MESSAGE);
	}

	private void onAddMedicine() {
		String name = javax.swing.JOptionPane.showInputDialog(frame, "Medicine name:");
		if (name == null || name.isBlank())
			return;
		String id = "M" + (medicineIdCounter++);
		Medicine med = new Medicine(id, name, 100, MedicineCategory.CURATIVE_MEDICINES);
		config.getMedicines().add(med);
		updateMedicineList();
		// Select and scroll to new medicine
		medicineList.setSelectedIndex(medicineList.getModel().getSize() - 1);
		medicineList.ensureIndexIsVisible(medicineList.getModel().getSize() - 1);
		javax.swing.JOptionPane.showMessageDialog(frame, "Medicine '" + name + "' added successfully.", "Success",
				javax.swing.JOptionPane.INFORMATION_MESSAGE);
	}

	private void onAddLocation() {
		String name = javax.swing.JOptionPane.showInputDialog(frame, "Location name:");
		if (name == null || name.isBlank())
			return;
		Location loc = new Location("L" + (config.getLocations().size() + 1), name, 0, 0);
		config.getLocations().add(loc);
		javax.swing.JOptionPane.showMessageDialog(frame, "Location '" + name + "' added successfully.", "Success",
				javax.swing.JOptionPane.INFORMATION_MESSAGE);
	}

	private void onAddChargingStation() {
		String name = javax.swing.JOptionPane.showInputDialog(frame, "Charging Station name:");
		if (name == null || name.isBlank())
			return;
		config.getChargingStations().add(name);
		updateChargingStationList();
		// Select and scroll to new charging station
		chargingStationList.setSelectedIndex(chargingStationList.getModel().getSize() - 1);
		chargingStationList.ensureIndexIsVisible(chargingStationList.getModel().getSize() - 1);
		javax.swing.JOptionPane.showMessageDialog(frame, "Charging Station '" + name + "' added successfully.",
				"Success", javax.swing.JOptionPane.INFORMATION_MESSAGE);
	}

	private void updateWarehouseList() {
		warehouseList.setListData(config.getWarehouses().stream().map(Warehouse::getName).toArray(String[]::new));
		// Reselect the last item if available
		if (warehouseList.getModel().getSize() > 0) {
			warehouseList.setSelectedIndex(warehouseList.getModel().getSize() - 1);
		}
	}

	private void updateMedicineList() {
		medicineList.setListData(config.getMedicines().stream().map(Medicine::getName).toArray(String[]::new));
		if (medicineList.getModel().getSize() > 0) {
			medicineList.setSelectedIndex(medicineList.getModel().getSize() - 1);
		}
	}

	private void updateChargingStationList() {
		chargingStationList.setListData(config.getChargingStations().toArray(new String[0]));
		if (chargingStationList.getModel().getSize() > 0) {
			chargingStationList.setSelectedIndex(chargingStationList.getModel().getSize() - 1);
		}
	}

	/**
	 * Appends a message to the AGV log area.
	 */
	private void logAGVOperation(String message) {
		SwingUtilities.invokeLater(() -> agvArea.append(message + "\n"));
	}

	// --- List selection handlers ---
	private void onWarehouseSelected() {
		String selected = warehouseList.getSelectedValue();
		logAGVOperation("Selected warehouse: " + (selected != null ? selected : "None"));
	}
	private void onHospitalSelected() {
		String selected = hospitalList.getSelectedValue();
		logAGVOperation("Selected hospital: " + (selected != null ? selected : "None"));
	}
	private void onMedicineSelected() {
		String selected = medicineList.getSelectedValue();
		logAGVOperation("Selected medicine: " + (selected != null ? selected : "None"));
	}
	private void onChargingStationSelected() {
		String selected = chargingStationList.getSelectedValue();
		logAGVOperation("Selected charging station: " + (selected != null ? selected : "None"));
	}

	/**
	 * Application entry point.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(App::new);
	}
}
