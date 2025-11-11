package de.fhdo.sama.capstone;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import de.fhdo.sama.capstone.model.AGV;
import de.fhdo.sama.capstone.model.Location;
import de.fhdo.sama.capstone.model.Medicine;
import de.fhdo.sama.capstone.model.MedicineCategory;
import de.fhdo.sama.capstone.model.Warehouse;

/**
 * Main application class for the Medicine Delivery Dashboard.
 */
public class App {
	private JFrame frame;
	private JTextArea logArea;
	private JList<String> hospitalList;
	private JList<String> warehouseList;
	private JList<String> medicineList;
	private JList<String> agvList;
	private JList<String> assignedTasksList;
	private DefaultListModel<String> assignedTasksModel;
	private JTextField quantityField;
	private JLabel statusLabel;

	private final DeliveryService deliveryService;
	private final AppConfig config;
	private int medicineIdCounter = 1000;
	private Random random = new Random();
	
	// Task assignment queue
	private List<DeliveryTask> pendingTasks = new ArrayList<>();

	public App() {
		this.deliveryService = new DeliveryService();
		this.config = new AppConfig();
		this.config.loadDefaults();

		initializeGUI();
		updateLists();
	}

	private void initializeGUI() {
		frame = new JFrame("MediTrack Dashboard");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 700);
		frame.setLocationRelativeTo(null);

		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		
		// Top: Selection Lists
		JPanel listsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
		
		warehouseList = new JList<>();
		warehouseList.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					showWarehouseStockPopup();
				}
			}
		});
		JScrollPane warehouseScroll = new JScrollPane(warehouseList);
		warehouseScroll.setBorder(new TitledBorder("Warehouses (Double-click for stock)"));
		listsPanel.add(warehouseScroll);
		
		hospitalList = new JList<>();
		hospitalList.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					showHospitalStockPopup();
				}
			}
		});
		JScrollPane hospitalScroll = new JScrollPane(hospitalList);
		hospitalScroll.setBorder(new TitledBorder("Hospitals (Double-click for stock)"));
		listsPanel.add(hospitalScroll);
		
		medicineList = new JList<>();
		JScrollPane medicineScroll = new JScrollPane(medicineList);
		medicineScroll.setBorder(new TitledBorder("Medicines"));
		listsPanel.add(medicineScroll);
		
		// AGV list
		agvList = new JList<>();
		JScrollPane agvScroll = new JScrollPane(agvList);
		agvScroll.setBorder(new TitledBorder("AGVs (Select for assignment)"));
		listsPanel.add(agvScroll);
		
		mainPanel.add(listsPanel, BorderLayout.NORTH);

		// Center: Split between Log and Assigned Tasks
		JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
		
		// Log Area
		logArea = new JTextArea();
		logArea.setEditable(false);
		JScrollPane logScroll = new JScrollPane(logArea);
		logScroll.setBorder(new TitledBorder("Log"));
		centerPanel.add(logScroll);
		
		// Assigned Tasks List
		assignedTasksModel = new DefaultListModel<>();
		assignedTasksList = new JList<>(assignedTasksModel);
		JScrollPane tasksScroll = new JScrollPane(assignedTasksList);
		tasksScroll.setBorder(new TitledBorder("Assigned Tasks Queue"));
		centerPanel.add(tasksScroll);
		
		mainPanel.add(centerPanel, BorderLayout.CENTER);

		// Bottom: Controls
		JPanel controlPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		
		// Row 1: Quantity and action buttons
		JPanel row1 = new JPanel(new GridLayout(1, 5, 5, 5));
		row1.add(new JLabel("Quantity:"));
		quantityField = new JTextField("10");
		row1.add(quantityField);
		
		JButton assignBtn = new JButton("Assign Task");
		assignBtn.addActionListener(e -> onAssignTask());
		row1.add(assignBtn);
		
		JButton startDeliveryBtn = new JButton("Start Delivery");
		startDeliveryBtn.addActionListener(e -> onStartAllDeliveries());
		row1.add(startDeliveryBtn);
		
		JButton clearTasksBtn = new JButton("Clear Tasks");
		clearTasksBtn.addActionListener(e -> onClearTasks());
		row1.add(clearTasksBtn);
		
		controlPanel.add(row1);
		
		// Row 2: Management buttons and status
		JPanel row2 = new JPanel(new GridLayout(1, 7, 5, 5));
		
		JButton addWarehouseBtn = new JButton("Add Warehouse");
		addWarehouseBtn.addActionListener(e -> onAddWarehouse());
		row2.add(addWarehouseBtn);
		
		JButton addMedicineBtn = new JButton("Add Medicine");
		addMedicineBtn.addActionListener(e -> onAddMedicine());
		row2.add(addMedicineBtn);
		
		JButton viewWarehouseBtn = new JButton("Warehouse Stock");
		viewWarehouseBtn.addActionListener(e -> showWarehouseStockPopup());
		row2.add(viewWarehouseBtn);
		
		JButton viewHospitalBtn = new JButton("Hospital Stock");
		viewHospitalBtn.addActionListener(e -> showHospitalStockPopup());
		row2.add(viewHospitalBtn);
		
		JButton viewChargingBtn = new JButton("Charging Stations");
		viewChargingBtn.addActionListener(e -> showChargingStationPopup());
		row2.add(viewChargingBtn);
		
		JButton clearLogBtn = new JButton("Clear Log");
		clearLogBtn.addActionListener(e -> {
			logArea.setText("");
			log("Log cleared");
		});
		row2.add(clearLogBtn);
		
		statusLabel = new JLabel("Ready");
		row2.add(statusLabel);
		
		controlPanel.add(row2);
		mainPanel.add(controlPanel, BorderLayout.SOUTH);

		frame.setContentPane(mainPanel);
		frame.setVisible(true);
	}

	private void updateLists() {
		warehouseList.setListData(config.getWarehouses().stream()
			.map(Warehouse::getName).toArray(String[]::new));
		hospitalList.setListData(config.getHospitals().toArray(new String[0]));
		medicineList.setListData(config.getMedicines().stream()
			.map(Medicine::getName).toArray(String[]::new));
		
		// Update AGV list
		AGVManager agvManager = deliveryService.getAgvManager();
		String[] agvNames = agvManager.getAllAGVs().stream()
			.map(agv -> agv.getName() + " (Battery: " + agv.getBatteryLevel() + "%)")
			.toArray(String[]::new);
		agvList.setListData(agvNames);
		
		if (warehouseList.getModel().getSize() > 0) warehouseList.setSelectedIndex(0);
		if (hospitalList.getModel().getSize() > 0) hospitalList.setSelectedIndex(0);
		if (medicineList.getModel().getSize() > 0) medicineList.setSelectedIndex(0);
		if (agvList.getModel().getSize() > 0) agvList.setSelectedIndex(0);
	}

	private void onAssignTask() {
		String selectedWarehouse = warehouseList.getSelectedValue();
		String selectedHospital = hospitalList.getSelectedValue();
		String selectedMedicine = medicineList.getSelectedValue();
		String selectedAgvInfo = agvList.getSelectedValue();
		
		if (selectedWarehouse == null || selectedMedicine == null || selectedAgvInfo == null) {
			log("ERROR: Please select warehouse, medicine, and AGV");
			statusLabel.setText("Missing selection");
			return;
		}
		
		// Extract AGV name from the selection (format: "AGV-1 (Battery: 75%)")
		String agvName = selectedAgvInfo.split(" ")[0];
		
		int quantity;
		try {
			quantity = Integer.parseInt(quantityField.getText());
			if (quantity <= 0) throw new NumberFormatException();
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(frame, "Invalid quantity", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		Warehouse warehouse = config.getWarehouses().stream()
			.filter(w -> w.getName().equals(selectedWarehouse))
			.findFirst().orElse(null);
		
		if (warehouse == null) {
			log("ERROR: Warehouse not found");
			return;
		}
		
		Medicine med = warehouse.getStock().stream()
			.filter(m -> m.getName().equals(selectedMedicine))
			.findFirst().orElse(null);
		
		if (med == null || med.getQuantity() < quantity) {
			log("ERROR: Insufficient stock for " + selectedMedicine);
			JOptionPane.showMessageDialog(frame, 
				"Insufficient stock. Available: " + (med != null ? med.getQuantity() : 0), 
				"Stock Error", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		// Create and add task
		DeliveryTask task = new DeliveryTask(
			agvName,
			selectedWarehouse,
			selectedHospital,
			selectedMedicine,
			quantity
		);
		
		pendingTasks.add(task);
		
		// Update assigned tasks display
		String taskDescription = String.format("%s: %s -> %s | %d x %s", 
			agvName, 
			selectedWarehouse, 
			selectedHospital != null ? selectedHospital : "Warehouse Only",
			quantity, 
			selectedMedicine);
		assignedTasksModel.addElement(taskDescription);
		
		log("Task assigned to " + agvName + ": " + taskDescription);
		statusLabel.setText("Task assigned to " + agvName);
	}

	private void onStartAllDeliveries() {
		if (pendingTasks.isEmpty()) {
			log("ERROR: No tasks assigned. Use 'Assign Task' button first.");
			statusLabel.setText("No tasks to start");
			return;
		}
		
		log("========== Starting all deliveries (" + pendingTasks.size() + " tasks) ==========");
		statusLabel.setText("Starting " + pendingTasks.size() + " deliveries...");
		
		// Process all tasks
		List<DeliveryTask> tasksToExecute = new ArrayList<>(pendingTasks);
		pendingTasks.clear();
		assignedTasksModel.clear();
		
		for (DeliveryTask task : tasksToExecute) {
			executeTask(task);
		}
		
		statusLabel.setText("All deliveries initiated");
	}

	private void executeTask(DeliveryTask task) {
		Warehouse warehouse = config.getWarehouses().stream()
			.filter(w -> w.getName().equals(task.warehouse))
			.findFirst().orElse(null);
		
		if (warehouse == null) {
			log("ERROR: Warehouse not found for task: " + task.warehouse);
			return;
		}
		
		AGVManager agvManager = deliveryService.getAgvManager();
		AGV agv = agvManager.getAGVByName(task.agvName).orElse(null);
		
		if (agv == null) {
			log("ERROR: AGV not found: " + task.agvName);
			return;
		}
		
		if (agvManager.isBusy(agv)) {
			log("WARNING: " + task.agvName + " is already busy, queuing task...");
		}
		
		agvManager.markBusy(agv);
		
		boolean isWarehouseOrder = (task.hospital == null);
		
		if (isWarehouseOrder) {
			// Warehouse-only order
			new Thread(() -> {
				try {
					log(agv.getName() + " assigned to process order for " + task.quantity + " units of " + task.medicine + " (Battery: " + agv.getBatteryLevel() + "%)");
					
					Thread.sleep(1500);
					
					boolean success = warehouse.removeMedicine(task.medicine, task.quantity);
					
					if (success) {
						int batteryConsumed = 5 + (int)(Math.random() * 10);
						agv.setBatteryLevel(Math.max(0, agv.getBatteryLevel() - batteryConsumed));
						
						log(agv.getName() + " - Order placed: " + task.quantity + " units of " + task.medicine + " delivered (Battery: " + agv.getBatteryLevel() + "%)");
						SwingUtilities.invokeLater(this::updateLists);
					} else {
						log(agv.getName() + " - Order failed: Not enough stock of " + task.medicine);
					}
				} catch (InterruptedException e) {
					log(agv.getName() + " - Order was interrupted");
					Thread.currentThread().interrupt();
				} finally {
					agvManager.markAvailable(agv);
					SwingUtilities.invokeLater(this::updateLists);
				}
			}).start();
		} else {
			// Full delivery to hospital
			Location warehouseLocation = warehouse.getLocation();
			Location hospitalLocation = config.getLocations().stream()
				.filter(l -> l.name().equals(task.hospital))
				.findFirst().orElse(new Location("H1", task.hospital, 0, 0));
			
			// Release the AGV as deliveryService will manage it
			agvManager.markAvailable(agv);
			
			deliveryService.startDelivery(warehouseLocation, hospitalLocation, warehouse, 
				task.medicine, task.quantity, message -> {
				log(message);
				if (message.contains("delivered") || message.contains("failed")) {
					SwingUtilities.invokeLater(this::updateLists);
				}
			});
		}
	}

	private void onClearTasks() {
		pendingTasks.clear();
		assignedTasksModel.clear();
		log("All pending tasks cleared");
		statusLabel.setText("Tasks cleared");
	}

	private void onAddWarehouse() {
		String name = JOptionPane.showInputDialog(frame, "Enter warehouse name:", "Add Warehouse", JOptionPane.PLAIN_MESSAGE);
		if (name == null || name.isBlank()) return;
		
		Location location = new Location("W" + (config.getLocations().size() + 1), name, 0, 0);
		config.getLocations().add(location);
		Warehouse warehouse = new Warehouse(name, new java.util.ArrayList<>(config.getMedicines()), location);
		config.getWarehouses().add(warehouse);
		updateLists();
		log("Warehouse '" + name + "' added");
		statusLabel.setText("Warehouse added");
	}

	private void onAddMedicine() {
		String name = JOptionPane.showInputDialog(frame, "Enter medicine name:", "Add Medicine", JOptionPane.PLAIN_MESSAGE);
		if (name == null || name.isBlank()) return;
		
		String id = "M" + (medicineIdCounter++);
		Medicine med = new Medicine(id, name, 100, MedicineCategory.CURATIVE_MEDICINES);
		config.getMedicines().add(med);
		updateLists();
		log("Medicine '" + name + "' added (100 units)");
		statusLabel.setText("Medicine added");
	}

	private void showHospitalStockPopup() {
		String selectedHospital = hospitalList.getSelectedValue();
		if (selectedHospital == null) {
			JOptionPane.showMessageDialog(frame, "Please select a hospital first", "No Selection", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		JDialog dialog = new JDialog(frame, "Hospital Stock - " + selectedHospital, true);
		dialog.setSize(400, 300);
		dialog.setLocationRelativeTo(frame);
		
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		
		JTextArea stockArea = new JTextArea();
		stockArea.setEditable(false);
		
		StringBuilder stockInfo = new StringBuilder();
		stockInfo.append("Stock levels at ").append(selectedHospital).append(":\n\n");
		
		// Simulate hospital stock (in real app, this would come from data model)
		for (Medicine med : config.getMedicines()) {
			int hospitalStock = random.nextInt(50) + 10; // Random stock 10-60
			stockInfo.append(med.getName())
				.append(": ")
				.append(hospitalStock)
				.append(" units\n");
		}
		
		stockArea.setText(stockInfo.toString());
		
		JScrollPane scrollPane = new JScrollPane(stockArea);
		panel.add(scrollPane, BorderLayout.CENTER);
		
		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(e -> dialog.dispose());
		panel.add(closeBtn, BorderLayout.SOUTH);
		
		dialog.setContentPane(panel);
		dialog.setVisible(true);
	}

	private void showWarehouseStockPopup() {
		String selectedWarehouse = warehouseList.getSelectedValue();
		if (selectedWarehouse == null) {
			JOptionPane.showMessageDialog(frame, "Please select a warehouse first", "No Selection", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		JDialog dialog = new JDialog(frame, "Warehouse Stock - " + selectedWarehouse, true);
		dialog.setSize(400, 300);
		dialog.setLocationRelativeTo(frame);
		
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		
		JTextArea stockArea = new JTextArea();
		stockArea.setEditable(false);
		
		StringBuilder stockInfo = new StringBuilder();
		stockInfo.append("Stock levels at ").append(selectedWarehouse).append(":\n\n");
		
		Warehouse warehouse = config.getWarehouses().stream()
			.filter(w -> w.getName().equals(selectedWarehouse))
			.findFirst().orElse(null);
		
		if (warehouse != null) {
			for (Medicine med : warehouse.getStock()) {
				stockInfo.append(med.getName())
					.append(": ")
					.append(med.getQuantity())
					.append(" units\n");
			}
		} else {
			stockInfo.append("No stock information available.");
		}
		
		stockArea.setText(stockInfo.toString());
		
		JScrollPane scrollPane = new JScrollPane(stockArea);
		panel.add(scrollPane, BorderLayout.CENTER);
		
		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(e -> dialog.dispose());
		panel.add(closeBtn, BorderLayout.SOUTH);
		
		dialog.setContentPane(panel);
		dialog.setVisible(true);
	}

	private void showChargingStationPopup() {
		JDialog dialog = new JDialog(frame, "Charging Station Status", true);
		dialog.setSize(450, 350);
		dialog.setLocationRelativeTo(frame);
		
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		
		JTextArea statusArea = new JTextArea();
		statusArea.setEditable(false);
		
		StringBuilder statusInfo = new StringBuilder();
		statusInfo.append("Charging Station Occupancy:\n\n");
		
		for (String station : config.getChargingStations()) {
			boolean occupied = random.nextBoolean();
			int batteryLevel = occupied ? random.nextInt(100) : 0;
			
			statusInfo.append(station).append(":\n");
			statusInfo.append("  Status: ").append(occupied ? "OCCUPIED" : "AVAILABLE").append("\n");
			
			if (occupied) {
				String agvName = "AGV-" + (random.nextInt(5) + 1);
				statusInfo.append("  AGV: ").append(agvName).append("\n");
				statusInfo.append("  Battery Level: ").append(batteryLevel).append("%\n");
				int remainingTime = (100 - batteryLevel) * 2; // 2 seconds per %
				statusInfo.append("  Est. Time Remaining: ").append(remainingTime).append(" seconds\n");
			}
			statusInfo.append("\n");
		}
		
		statusArea.setText(statusInfo.toString());
		
		JScrollPane scrollPane = new JScrollPane(statusArea);
		panel.add(scrollPane, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
		
		JButton refreshBtn = new JButton("Refresh");
		refreshBtn.addActionListener(e -> {
			dialog.dispose();
			showChargingStationPopup();
		});
		buttonPanel.add(refreshBtn);
		
		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(e -> dialog.dispose());
		buttonPanel.add(closeBtn);
		
		panel.add(buttonPanel, BorderLayout.SOUTH);
		
		dialog.setContentPane(panel);
		dialog.setVisible(true);
	}

	private void log(String message) {
		SwingUtilities.invokeLater(() -> {
			String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
			logArea.append("[" + timestamp + "] " + message + "\n");
			logArea.setCaretPosition(logArea.getDocument().getLength());
		});
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(App::new);
	}
	
	// Inner class to represent a delivery task
	private static class DeliveryTask {
		String agvName;
		String warehouse;
		String hospital;
		String medicine;
		int quantity;
		
		DeliveryTask(String agvName, String warehouse, String hospital, String medicine, int quantity) {
			this.agvName = agvName;
			this.warehouse = warehouse;
			this.hospital = hospital;
			this.medicine = medicine;
			this.quantity = quantity;
		}
	}
}