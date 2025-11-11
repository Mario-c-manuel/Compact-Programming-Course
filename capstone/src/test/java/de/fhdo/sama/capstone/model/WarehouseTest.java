package de.fhdo.sama.capstone.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class WarehouseTest {
	private Warehouse warehouse;
	private List<Medicine> stock;
	private Location location;

	@BeforeEach
	public void setUp() {
		location = new Location("W1", "Main Warehouse", 0, 0);
		stock = new ArrayList<>();
		stock.add(new Medicine("M1", "Aspirin", 100, MedicineCategory.CURATIVE_MEDICINES));
		stock.add(new Medicine("M2", "Vitamin C", 50, MedicineCategory.PREVENTIVE_MEDICINES));
		warehouse = new Warehouse("Main Warehouse", stock, location);
	}

	@Test
	public void testRemoveMedicineSuccess() {
		boolean result = warehouse.removeMedicine("Aspirin", 30);
		
		assertTrue(result, "Should successfully remove medicine");
		assertEquals(70, stock.get(0).getQuantity(), "Quantity should be reduced");
	}

	@Test
	public void testRemoveMedicineInsufficientStock() {
		boolean result = warehouse.removeMedicine("Aspirin", 150);
		
		assertFalse(result, "Should fail when insufficient stock");
		assertEquals(100, stock.get(0).getQuantity(), "Quantity should remain unchanged");
	}

	@Test
	public void testRemoveMedicineNotFound() {
		boolean result = warehouse.removeMedicine("Ibuprofen", 10);
		
		assertFalse(result, "Should fail when medicine not found");
	}

	@Test
	public void testRemoveMedicineExactAmount() {
		boolean result = warehouse.removeMedicine("Vitamin C", 50);
		
		assertTrue(result, "Should successfully remove exact amount");
		assertEquals(0, stock.get(1).getQuantity(), "Quantity should be zero");
	}

	@Test
	public void testAddMedicine() {
		Medicine newMedicine = new Medicine("M3", "Paracetamol", 75, MedicineCategory.SYMTOMATIC_MEDICINES);
		warehouse.addMedicine(newMedicine);
		
		assertEquals(3, warehouse.getStock().size(), "Stock should contain 3 medicines");
		assertTrue(warehouse.getStock().contains(newMedicine), "New medicine should be in stock");
	}

	@Test
	public void testGettersAndSetters() {
		warehouse.setId("WH001");
		assertEquals("WH001", warehouse.getId());
		
		warehouse.setName("Updated Warehouse");
		assertEquals("Updated Warehouse", warehouse.getName());
		
		Location newLocation = new Location("W2", "New Location", 5, 5);
		warehouse.setLocation(newLocation);
		assertEquals(newLocation, warehouse.getLocation());
		
		List<Medicine> newStock = new ArrayList<>();
		warehouse.setStock(newStock);
		assertEquals(newStock, warehouse.getStock());
	}
}