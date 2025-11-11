package de.fhdo.sama.capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.fhdo.sama.capstone.model.AGV;
import de.fhdo.sama.capstone.model.Location;
import de.fhdo.sama.capstone.model.Medicine;
import de.fhdo.sama.capstone.model.Warehouse;

/**
 * Integration test for the Medicine Delivery System.
 * Tests the complete workflow from task assignment to delivery completion.
 */
public class MedicineDeliveryIntegrationTest {
    
    private DeliveryService deliveryService;
    private AGVManager agvManager;
    private AppConfig config;
    private List<String> logMessages;
    
    @BeforeEach
    public void setUp() {
        deliveryService = new DeliveryService();
        agvManager = deliveryService.getAgvManager();
        config = new AppConfig();
        config.loadDefaults();
        logMessages = new ArrayList<>();
    }
    
    @Test
    @DisplayName("Integration Test: Single AGV Delivery from Warehouse to Hospital")
    public void testSingleAGVDelivery() throws InterruptedException {
        // Arrange
        Warehouse warehouse = config.getWarehouses().get(0);
        Location warehouseLocation = warehouse.getLocation();
        Location hospitalLocation = config.getLocations().stream()
            .filter(l -> l.name().contains("Hospital"))
            .findFirst()
            .orElseThrow();
        
        int initialStock = warehouse.getStock().get(0).getQuantity();
        String medicineName = warehouse.getStock().get(0).getName();
        int deliveryQuantity = 10;
        
        CountDownLatch deliveryComplete = new CountDownLatch(1);
        
        // Act
        deliveryService.startDelivery(
            warehouseLocation, 
            hospitalLocation, 
            warehouse, 
            medicineName, 
            deliveryQuantity, 
            message -> {
                logMessages.add(message);
                if (message.contains("delivered") && message.contains(medicineName)) {
                    deliveryComplete.countDown();
                }
            }
        );
        
        // Assert
        assertTrue(deliveryComplete.await(10, TimeUnit.SECONDS), 
            "Delivery should complete within 10 seconds");
        
        // Verify stock was reduced
        int finalStock = warehouse.getStock().get(0).getQuantity();
        assertEquals(initialStock - deliveryQuantity, finalStock, 
            "Warehouse stock should be reduced by delivery quantity");
        
        // Verify log messages contain expected events
        assertTrue(logMessages.stream().anyMatch(msg -> msg.contains("picked up")), 
            "Should log medicine pickup");
        assertTrue(logMessages.stream().anyMatch(msg -> msg.contains("delivered")), 
            "Should log successful delivery");
    }
    
    @Test
    @DisplayName("Integration Test: Multiple AGVs with Different Tasks")
    public void testMultipleAGVsWithDifferentTasks() throws InterruptedException {
        // Arrange
        Warehouse warehouse = config.getWarehouses().get(0);
        Location warehouseLocation = warehouse.getLocation();
        Location hospital1 = config.getLocations().stream()
            .filter(l -> l.name().contains("Hospital"))
            .findFirst()
            .orElseThrow();
        Location hospital2 = config.getLocations().stream()
            .filter(l -> l.name().contains("Hospital"))
            .skip(1)
            .findFirst()
            .orElse(hospital1);
        
        int numberOfDeliveries = 3;
        CountDownLatch allDeliveriesComplete = new CountDownLatch(numberOfDeliveries);
        List<String> medicines = config.getMedicines().stream()
            .limit(numberOfDeliveries)
            .map(Medicine::getName)
            .toList();
        
        // Act - Start multiple deliveries simultaneously
        for (int i = 0; i < numberOfDeliveries; i++) {
            String medicine = medicines.get(i);
            Location hospital = (i % 2 == 0) ? hospital1 : hospital2;
            
            deliveryService.startDelivery(
                warehouseLocation, 
                hospital, 
                warehouse, 
                medicine, 
                5, 
                message -> {
                    logMessages.add(message);
                    if (message.contains("delivered")) {
                        allDeliveriesComplete.countDown();
                    }
                }
            );
        }
        
        // Assert
        assertTrue(allDeliveriesComplete.await(15, TimeUnit.SECONDS), 
            "All deliveries should complete within 15 seconds");
        
        // Verify AGVs were properly managed
        assertTrue(agvManager.getAvailableCount() > 0, 
            "At least some AGVs should be available after deliveries");
        
        // Verify all deliveries were logged
        long deliveredCount = logMessages.stream()
            .filter(msg -> msg.contains("delivered"))
            .count();
        assertEquals(numberOfDeliveries, deliveredCount, 
            "Should have logged all deliveries");
    }
    
    @Test
    @DisplayName("Integration Test: AGV Battery Management During Delivery")
    public void testAGVBatteryManagement() throws InterruptedException {
        // Arrange
        AGV agv = agvManager.getAllAGVs().get(0);
        agv.setBatteryLevel(15); // Set low battery
        
        Warehouse warehouse = config.getWarehouses().get(0);
        Location warehouseLocation = warehouse.getLocation();
        Location hospitalLocation = config.getLocations().stream()
            .filter(l -> l.name().contains("Hospital"))
            .findFirst()
            .orElseThrow();
        
        String medicineName = warehouse.getStock().get(0).getName();
        CountDownLatch chargingStarted = new CountDownLatch(1);
        CountDownLatch deliveryComplete = new CountDownLatch(1);
        
        // Act
        deliveryService.startDelivery(
            warehouseLocation, 
            hospitalLocation, 
            warehouse, 
            medicineName, 
            5, 
            message -> {
                logMessages.add(message);
                if (message.contains("charging")) {
                    chargingStarted.countDown();
                }
                if (message.contains("delivered")) {
                    deliveryComplete.countDown();
                }
            }
        );
        
        // Assert
        assertTrue(chargingStarted.await(5, TimeUnit.SECONDS), 
            "AGV should start charging when battery is low");
        assertTrue(deliveryComplete.await(12, TimeUnit.SECONDS), 
            "Delivery should complete after charging");
        
        // Verify charging occurred
        assertTrue(logMessages.stream().anyMatch(msg -> msg.contains("charging")), 
            "Should log charging event");
        assertTrue(logMessages.stream().anyMatch(msg -> msg.contains("fully charged")), 
            "Should log charging completion");
    }
    
    @Test
    @DisplayName("Integration Test: Insufficient Stock Handling")
    public void testInsufficientStock() throws InterruptedException {
        // Arrange
        Warehouse warehouse = config.getWarehouses().get(0);
        Location warehouseLocation = warehouse.getLocation();
        Location hospitalLocation = config.getLocations().stream()
            .filter(l -> l.name().contains("Hospital"))
            .findFirst()
            .orElseThrow();
        
        Medicine medicine = warehouse.getStock().get(0);
        int availableStock = medicine.getQuantity();
        int requestedQuantity = availableStock + 50; // Request more than available
        
        CountDownLatch deliveryAttempted = new CountDownLatch(1);
        
        // Act
        deliveryService.startDelivery(
            warehouseLocation, 
            hospitalLocation, 
            warehouse, 
            medicine.getName(), 
            requestedQuantity, 
            message -> {
                logMessages.add(message);
                if (message.contains("failed") || message.contains("Not enough stock")) {
                    deliveryAttempted.countDown();
                }
            }
        );
        
        // Assert
        assertTrue(deliveryAttempted.await(5, TimeUnit.SECONDS), 
            "Should fail delivery when stock is insufficient");
        
        assertTrue(logMessages.stream().anyMatch(msg -> msg.contains("failed")), 
            "Should log delivery failure");
        
        // Verify stock unchanged
        assertEquals(availableStock, medicine.getQuantity(), 
            "Stock should remain unchanged on failed delivery");
    }
    
    @Test
    @DisplayName("Integration Test: AGV Assignment and Availability")
    public void testAGVAssignmentAndAvailability() throws InterruptedException {
        // Arrange
        int initialAvailableAGVs = agvManager.getAvailableCount();
        assertTrue(initialAvailableAGVs > 0, "Should have available AGVs");
        
        Warehouse warehouse = config.getWarehouses().get(0);
        Location warehouseLocation = warehouse.getLocation();
        Location hospitalLocation = config.getLocations().stream()
            .filter(l -> l.name().contains("Hospital"))
            .findFirst()
            .orElseThrow();
        
        String medicineName = warehouse.getStock().get(0).getName();
        CountDownLatch deliveryStarted = new CountDownLatch(1);
        
        // Act - Start a delivery
        deliveryService.startDelivery(
            warehouseLocation, 
            hospitalLocation, 
            warehouse, 
            medicineName, 
            5, 
            message -> {
                logMessages.add(message);
                if (message.contains("started delivery")) {
                    deliveryStarted.countDown();
                }
            }
        );
        
        // Assert - Check AGV is assigned
        assertTrue(deliveryStarted.await(3, TimeUnit.SECONDS), 
            "Delivery should start");
        
        // Wait for delivery to complete
        Thread.sleep(3000);
        
        // Verify specific AGV was used
        assertTrue(logMessages.stream().anyMatch(msg -> msg.contains("AGV-")), 
            "Should log which AGV was used");
    }
    
    @Test
    @DisplayName("Integration Test: Complete Workflow - Assign Multiple Tasks and Execute")
    public void testCompleteWorkflowMultipleTasks() throws InterruptedException {
        // Arrange - Simulate the UI workflow
        Warehouse warehouse1 = config.getWarehouses().get(0);
        Warehouse warehouse2 = config.getWarehouses().size() > 1 ? 
            config.getWarehouses().get(1) : warehouse1;
        
        // Get actual medicine names from warehouse stock
        String medicine1 = warehouse1.getStock().get(0).getName();
        String medicine2 = warehouse1.getStock().size() > 1 ? 
            warehouse1.getStock().get(1).getName() : medicine1;
        String medicine3 = warehouse1.getStock().size() > 2 ? 
            warehouse1.getStock().get(2).getName() : medicine1;
        
        // Simulate task queue
        List<TaskInfo> tasks = new ArrayList<>();
        tasks.add(new TaskInfo("AGV-1", warehouse1, medicine1, 10));
        tasks.add(new TaskInfo("AGV-2", warehouse2, medicine2, 15));
        tasks.add(new TaskInfo("AGV-3", warehouse1, medicine3, 20));
        
        AtomicInteger completedTasks = new AtomicInteger(0);
        CountDownLatch allTasksComplete = new CountDownLatch(tasks.size());
        
        // Act - Execute all tasks (simulating "Start Delivery" button)
        for (TaskInfo task : tasks) {
            AGV assignedAGV = agvManager.getAGVByName(task.agvName).orElseThrow();
            
            // Mark AGV as busy
            agvManager.markBusy(assignedAGV);
            
            // Start delivery
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Simulate delivery time
                    
                    // Find the medicine in the warehouse and remove it
                    synchronized (task.warehouse) {
                        boolean success = task.warehouse.removeMedicine(task.medicine, task.quantity);
                        if (success) {
                            logMessages.add(assignedAGV.getName() + " delivered " + 
                                task.quantity + " units of " + task.medicine);
                            completedTasks.incrementAndGet();
                        } else {
                            logMessages.add(assignedAGV.getName() + " failed - insufficient stock of " + task.medicine);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    agvManager.markAvailable(assignedAGV);
                    allTasksComplete.countDown();
                }
            }).start();
        }
        
        // Assert
        assertTrue(allTasksComplete.await(5, TimeUnit.SECONDS), 
            "All tasks should complete within 5 seconds");
        assertEquals(tasks.size(), completedTasks.get(), 
            "All tasks should complete successfully");
        
        // Verify all AGVs are available again
        tasks.forEach(task -> {
            AGV agv = agvManager.getAGVByName(task.agvName).orElseThrow();
            assertFalse(agvManager.isBusy(agv), 
                task.agvName + " should be available after task completion");
        });
    }
    
    @Test
    @DisplayName("Integration Test: Concurrent Deliveries with Resource Contention")
    public void testConcurrentDeliveriesWithResourceContention() throws InterruptedException {
        // Arrange
        Warehouse warehouse = config.getWarehouses().get(0);
        Location warehouseLocation = warehouse.getLocation();
        Location hospitalLocation = config.getLocations().stream()
            .filter(l -> l.name().contains("Hospital"))
            .findFirst()
            .orElseThrow();
        
        int numberOfConcurrentDeliveries = 5;
        CountDownLatch allComplete = new CountDownLatch(numberOfConcurrentDeliveries);
        String medicineName = warehouse.getStock().get(0).getName();
        
        // Act - Start more deliveries than available AGVs
        for (int i = 0; i < numberOfConcurrentDeliveries; i++) {
            deliveryService.startDelivery(
                warehouseLocation, 
                hospitalLocation, 
                warehouse, 
                medicineName, 
                2, 
                message -> {
                    logMessages.add(message);
                    if (message.contains("delivered") || message.contains("No AGVs available")) {
                        allComplete.countDown();
                    }
                }
            );
        }
        
        // Assert
        assertTrue(allComplete.await(20, TimeUnit.SECONDS), 
            "All delivery attempts should complete or fail within 20 seconds");
        
        // Verify system handled resource contention gracefully
        assertTrue(logMessages.size() > 0, "Should have logged delivery attempts");
    }
    
    @Test
    @DisplayName("Integration Test: System Initialization and Configuration")
    public void testSystemInitializationAndConfiguration() {
        // Assert - Verify all components initialized correctly
        assertNotNull(deliveryService, "DeliveryService should be initialized");
        assertNotNull(agvManager, "AGVManager should be initialized");
        assertNotNull(config, "AppConfig should be initialized");
        
        // Verify AGV fleet
        List<AGV> agvFleet = agvManager.getAllAGVs();
        assertEquals(5, agvFleet.size(), "Should have 5 AGVs in the fleet");
        
        agvFleet.forEach(agv -> {
            assertNotNull(agv.getName(), "Each AGV should have a name");
            assertTrue(agv.getBatteryLevel() >= 50 && agv.getBatteryLevel() <= 100, 
                "Each AGV should have initial battery between 50-100%");
        });
        
        // Verify configuration
        assertFalse(config.getWarehouses().isEmpty(), "Should have warehouses configured");
        assertFalse(config.getHospitals().isEmpty(), "Should have hospitals configured");
        assertFalse(config.getMedicines().isEmpty(), "Should have medicines configured");
        assertFalse(config.getChargingStations().isEmpty(), "Should have charging stations configured");
        
        // Verify all warehouses have stock
        config.getWarehouses().forEach(warehouse -> {
            assertFalse(warehouse.getStock().isEmpty(), 
                "Each warehouse should have initial stock");
        });
    }
    
    // Helper class to represent a task
    private static class TaskInfo {
        String agvName;
        Warehouse warehouse;
        String medicine;
        int quantity;
        
        TaskInfo(String agvName, Warehouse warehouse, String medicine, int quantity) {
            this.agvName = agvName;
            this.warehouse = warehouse;
            this.medicine = medicine;
            this.quantity = quantity;
        }
    }
}
