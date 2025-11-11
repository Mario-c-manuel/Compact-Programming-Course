package de.fhdo.sama.capstone;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.fhdo.sama.capstone.model.Location;
import de.fhdo.sama.capstone.model.Medicine;
import de.fhdo.sama.capstone.model.MedicineCategory;
import de.fhdo.sama.capstone.model.Transfer;
import de.fhdo.sama.capstone.model.Warehouse;

import java.util.ArrayList;

public class AppConfigTest {
	private AppConfig config;

	@BeforeEach
	public void setUp() {
		config = new AppConfig();
	}

	@Test
	public void testLoadDefaults() {
		config.loadDefaults();
		
		assertFalse(config.getLocations().isEmpty(), "Should have default locations");
		assertFalse(config.getWarehouses().isEmpty(), "Should have default warehouses");
		assertFalse(config.getMedicines().isEmpty(), "Should have default medicines");
		assertFalse(config.getChargingStations().isEmpty(), "Should have default charging stations");
		assertFalse(config.getHospitals().isEmpty(), "Should have default hospitals");
		
		assertEquals(4, config.getLocations().size(), "Should have 4 default locations");
		assertEquals(2, config.getWarehouses().size(), "Should have 2 default warehouses");
		assertEquals(3, config.getMedicines().size(), "Should have 3 default medicines");
		assertEquals(2, config.getChargingStations().size(), "Should have 2 charging stations");
		assertEquals(2, config.getHospitals().size(), "Should have 2 hospitals");
	}

	@Test
	public void testAddLocation() {
		Location location = new Location("L1", "Test Location", 5, 5);
		config.addLocation(location);
		
		assertTrue(config.getLocations().contains(location), "Location should be added");
	}

	@Test
	public void testRemoveLocationById() {
		Location location = new Location("L1", "Test Location", 5, 5);
		config.addLocation(location);
		config.removeLocationById("L1");
		
		assertFalse(config.getLocations().contains(location), "Location should be removed");
	}

	@Test
	public void testAddWarehouse() {
		Location location = new Location("W1", "Warehouse Location", 0, 0);
		Warehouse warehouse = new Warehouse("Test Warehouse", new ArrayList<>(), location);
		config.addWarehouse(warehouse);
		
		assertTrue(config.getWarehouses().contains(warehouse), "Warehouse should be added");
	}

	@Test
	public void testRemoveWarehouseByName() {
		Location location = new Location("W1", "Warehouse Location", 0, 0);
		Warehouse warehouse = new Warehouse("Test Warehouse", new ArrayList<>(), location);
		config.addWarehouse(warehouse);
		config.removeWarehouseByName("Test Warehouse");
		
		assertFalse(config.getWarehouses().contains(warehouse), "Warehouse should be removed");
	}

	@Test
	public void testAddMedicine() {
		Medicine medicine = new Medicine("M1", "Test Medicine", 100, MedicineCategory.CURATIVE_MEDICINES);
		config.addMedicine(medicine);
		
		assertTrue(config.getMedicines().contains(medicine), "Medicine should be added");
	}

	@Test
	public void testRemoveMedicineById() {
		Medicine medicine = new Medicine("M1", "Test Medicine", 100, MedicineCategory.CURATIVE_MEDICINES);
		config.addMedicine(medicine);
		config.removeMedicineById("M1");
		
		assertFalse(config.getMedicines().contains(medicine), "Medicine should be removed");
	}

	@Test
	public void testAddChargingStation() {
		config.addChargingStation("Station 3");
		
		assertTrue(config.getChargingStations().contains("Station 3"), "Charging station should be added");
	}

	@Test
	public void testRemoveChargingStation() {
		config.addChargingStation("Station 3");
		config.removeChargingStation("Station 3");
		
		assertFalse(config.getChargingStations().contains("Station 3"), "Charging station should be removed");
	}

	@Test
	public void testAddHospital() {
		config.addHospital("Test Hospital");
		
		assertTrue(config.getHospitals().contains("Test Hospital"), "Hospital should be added");
	}

	@Test
	public void testRemoveHospital() {
		config.addHospital("Test Hospital");
		config.removeHospital("Test Hospital");
		
		assertFalse(config.getHospitals().contains("Test Hospital"), "Hospital should be removed");
	}

	@Test
	public void testAddTransfer() {
		Transfer transfer = new Transfer();
		transfer.setId("T1");
		config.addTransfer(transfer);
		
		assertTrue(config.getTransfers().contains(transfer), "Transfer should be added");
	}

	@Test
	public void testRemoveTransferById() {
		Transfer transfer = new Transfer();
		transfer.setId("T1");
		config.addTransfer(transfer);
		config.removeTransferById("T1");
		
		assertFalse(config.getTransfers().contains(transfer), "Transfer should be removed");
	}
}