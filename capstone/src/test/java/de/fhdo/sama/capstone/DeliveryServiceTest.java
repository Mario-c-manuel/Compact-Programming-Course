package de.fhdo.sama.capstone;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.fhdo.sama.capstone.model.AGV;
import de.fhdo.sama.capstone.model.Location;
import de.fhdo.sama.capstone.model.Medicine;
import de.fhdo.sama.capstone.model.MedicineCategory;
import de.fhdo.sama.capstone.model.Warehouse;

public class DeliveryServiceTest {
	private DeliveryService deliveryService;
	private Warehouse warehouse;
	private Location warehouseLocation;
	private Location hospitalLocation;
	private List<String> logMessages;

	@BeforeEach
	public void setUp() {
		deliveryService = new DeliveryService();
		warehouseLocation = new Location("W1", "Main Warehouse", 0, 0);
		hospitalLocation = new Location("H1", "City Hospital", 10, 10);
		
		List<Medicine> stock = new ArrayList<>();
		stock.add(new Medicine("M1", "Aspirin", 100, MedicineCategory.CURATIVE_MEDICINES));
		stock.add(new Medicine("M2", "Vitamin C", 50, MedicineCategory.PREVENTIVE_MEDICINES));
		
		warehouse = new Warehouse("Main Warehouse", stock, warehouseLocation);
		logMessages = new ArrayList<>();
	}

	@Test
	public void testStartDeliverySuccess() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		
		deliveryService.startDelivery(warehouseLocation, hospitalLocation, warehouse, "Aspirin", 10, message -> {
			logMessages.add(message);
			if (message.contains("delivered")) {
				latch.countDown();
			}
		});
		
		assertTrue(latch.await(5, TimeUnit.SECONDS), "Delivery should complete within 5 seconds");
		assertTrue(logMessages.stream().anyMatch(msg -> msg.contains("delivered")), "Should log delivery completion");
		
		// Verify stock was reduced
		Medicine aspirin = warehouse.getStock().stream()
			.filter(m -> m.getName().equals("Aspirin"))
			.findFirst().orElse(null);
		assertNotNull(aspirin);
		assertEquals(90, aspirin.getQuantity(), "Stock should be reduced by 10");
	}

	@Test
	public void testStartDeliveryInsufficientStock() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		
		deliveryService.startDelivery(warehouseLocation, hospitalLocation, warehouse, "Aspirin", 200, message -> {
			logMessages.add(message);
			if (message.contains("failed") || message.contains("Not enough stock")) {
				latch.countDown();
			}
		});
		
		assertTrue(latch.await(5, TimeUnit.SECONDS), "Should fail quickly");
		assertTrue(logMessages.stream().anyMatch(msg -> msg.contains("Not enough stock")), "Should log stock failure");
		
		// Verify stock was NOT reduced
		Medicine aspirin = warehouse.getStock().stream()
			.filter(m -> m.getName().equals("Aspirin"))
			.findFirst().orElse(null);
		assertNotNull(aspirin);
		assertEquals(100, aspirin.getQuantity(), "Stock should remain unchanged");
	}

	@Test
	public void testStartDeliveryNullWarehouse() {
		deliveryService.startDelivery(warehouseLocation, hospitalLocation, null, "Aspirin", 10, message -> {
			logMessages.add(message);
		});
		
		assertTrue(logMessages.stream().anyMatch(msg -> msg.contains("valid warehouse")), "Should log validation error");
	}

	@Test
	public void testStartDeliveryInvalidMedicine() {
		deliveryService.startDelivery(warehouseLocation, hospitalLocation, warehouse, "", 10, message -> {
			logMessages.add(message);
		});
		
		assertTrue(logMessages.stream().anyMatch(msg -> msg.contains("valid medicine")), "Should log validation error");
	}

	@Test
	public void testStartDeliveryInvalidQuantity() {
		deliveryService.startDelivery(warehouseLocation, hospitalLocation, warehouse, "Aspirin", 0, message -> {
			logMessages.add(message);
		});
		
		assertTrue(logMessages.stream().anyMatch(msg -> msg.contains("valid medicine")), "Should log validation error");
	}

	@Test
	public void testStartDeliveryNullLocation() {
		deliveryService.startDelivery(null, hospitalLocation, warehouse, "Aspirin", 10, message -> {
			logMessages.add(message);
		});
		
		assertTrue(logMessages.stream().anyMatch(msg -> msg.contains("valid warehouse and hospital details")), "Should log validation error");
	}

	@Test
	public void testAGVManagerHasMultipleAGVs() {
		AGVManager agvManager = deliveryService.getAgvManager();
		assertEquals(5, agvManager.getAllAGVs().size(), "Should have 5 AGVs in fleet");
	}

	@Test
	public void testAGVManagerAssignsAvailableAGV() {
		AGVManager agvManager = deliveryService.getAgvManager();
		var agvOpt = agvManager.getAnyAvailableAGV();
		assertTrue(agvOpt.isPresent(), "Should have an available AGV");
	}

	@Test
	public void testAGVManagerMarksAGVBusy() {
		AGVManager agvManager = deliveryService.getAgvManager();
		var agvOpt = agvManager.getAnyAvailableAGV();
		assertTrue(agvOpt.isPresent());
		
		AGV agv = agvOpt.get();
		agvManager.markBusy(agv);
		
		assertTrue(agvManager.isBusy(agv), "AGV should be marked as busy");
		assertEquals(4, agvManager.getAvailableCount(), "Should have 4 available AGVs");
	}

	@Test
	public void testAGVManagerReleasesAGV() {
		AGVManager agvManager = deliveryService.getAgvManager();
		var agvOpt = agvManager.getAnyAvailableAGV();
		assertTrue(agvOpt.isPresent());
		
		AGV agv = agvOpt.get();
		agvManager.markBusy(agv);
		agvManager.markAvailable(agv);
		
		assertFalse(agvManager.isBusy(agv), "AGV should be available again");
		assertEquals(5, agvManager.getAvailableCount(), "Should have 5 available AGVs");
	}

	@Test
	public void testMultipleDeliveriesUseDifferentAGVs() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(2);
		List<String> agvNames = new ArrayList<>();
		
		// Start two deliveries
		deliveryService.startDelivery(warehouseLocation, hospitalLocation, warehouse, "Aspirin", 5, message -> {
			if (message.contains("started delivery")) {
				synchronized (agvNames) {
					String agvName = message.split(" ")[0]; // Extract AGV name
					agvNames.add(agvName);
				}
			}
			if (message.contains("delivered")) {
				latch.countDown();
			}
		});
		
		deliveryService.startDelivery(warehouseLocation, hospitalLocation, warehouse, "Vitamin C", 5, message -> {
			if (message.contains("started delivery")) {
				synchronized (agvNames) {
					String agvName = message.split(" ")[0]; // Extract AGV name
					agvNames.add(agvName);
				}
			}
			if (message.contains("delivered")) {
				latch.countDown();
			}
		});
		
		assertTrue(latch.await(10, TimeUnit.SECONDS), "Both deliveries should complete");
		assertEquals(2, agvNames.size(), "Should have 2 AGV assignments");
		// Note: AGVs might be the same if one finishes before the other starts
	}

	@Test
	public void testNoAGVAvailableWhenAllBusy() throws InterruptedException {
		AGVManager agvManager = deliveryService.getAgvManager();
		
		// Mark all AGVs as busy
		List<AGV> allAgvs = agvManager.getAllAGVs();
		for (AGV agv : allAgvs) {
			agvManager.markBusy(agv);
		}
		
		// Try to start a delivery
		deliveryService.startDelivery(warehouseLocation, hospitalLocation, warehouse, "Aspirin", 10, message -> {
			logMessages.add(message);
		});
		
		// Give it a moment to process
		Thread.sleep(100);
		
		assertTrue(logMessages.stream().anyMatch(msg -> msg.contains("No AGVs available")), 
			"Should log that no AGVs are available");
		
		// Cleanup - release all AGVs
		for (AGV agv : allAgvs) {
			agvManager.markAvailable(agv);
		}
	}

	@Test
	public void testChargeAGV() throws InterruptedException {
		AGV agv = new AGV("AGV-1");
		agv.setBatteryLevel(50);
		
		CountDownLatch latch = new CountDownLatch(1);
		
		deliveryService.chargeAGV(agv, message -> {
			logMessages.add(message);
			if (message.contains("fully charged")) {
				latch.countDown();
			}
		});
		
		assertTrue(latch.await(5, TimeUnit.SECONDS), "Charging should complete within 5 seconds");
		assertEquals(100, agv.getBatteryLevel(), "Battery should be at 100%");
		assertTrue(logMessages.stream().anyMatch(msg -> msg.contains("charging at Station 1")), "Should log charging");
		assertTrue(logMessages.stream().anyMatch(msg -> msg.contains("fully charged")), "Should log completion");
	}

	@Test
	public void testWarehouseThreadSafety() throws InterruptedException {
		// Test concurrent stock removal
		int threadCount = 5;
		CountDownLatch latch = new CountDownLatch(threadCount);
		
		for (int i = 0; i < threadCount; i++) {
			new Thread(() -> {
				warehouse.removeMedicine("Aspirin", 10);
				latch.countDown();
			}).start();
		}
		
		assertTrue(latch.await(2, TimeUnit.SECONDS), "All threads should complete");
		
		Medicine aspirin = warehouse.getStock().stream()
			.filter(m -> m.getName().equals("Aspirin"))
			.findFirst().orElse(null);
		assertNotNull(aspirin);
		assertEquals(50, aspirin.getQuantity(), "Stock should be reduced by 50 (5 threads x 10 units)");
	}

	@Test
	public void testDeliveryWithLowBattery() throws InterruptedException {
		// This test verifies that low battery AGVs get charged automatically
		CountDownLatch latch = new CountDownLatch(1);
		
		// We'll need to manually set an AGV's battery low for this test
		// Since AGVs are created with random battery levels, we'll just verify the delivery completes
		deliveryService.startDelivery(warehouseLocation, hospitalLocation, warehouse, "Aspirin", 10, message -> {
			logMessages.add(message);
			if (message.contains("delivered")) {
				latch.countDown();
			}
		});
		
		assertTrue(latch.await(10, TimeUnit.SECONDS), "Delivery should complete even with charging");
		
		// Check if charging happened (optional, depends on random battery level)
		boolean chargingOccurred = logMessages.stream().anyMatch(msg -> msg.contains("battery low"));
		// We don't assert this since battery levels are random, but we log it
		if (chargingOccurred) {
			assertTrue(logMessages.stream().anyMatch(msg -> msg.contains("fully charged")), 
				"If charging started, it should complete");
		}
	}
}
