package de.fhdo.sama.capstone.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class HospitalTest {
	private Hospital hospital;

	@BeforeEach
	public void setUp() {
		hospital = new Hospital();
	}

	@Test
	public void testGettersAndSetters() {
		hospital.setId(1);
		assertEquals(1, hospital.getId());
		
		hospital.setName("City Hospital");
		assertEquals("City Hospital", hospital.getName());
		
		Location location = new Location("H1", "Hospital Location", 10, 10);
		hospital.setLocation(location);
		assertEquals(location, hospital.getLocation());
	}

	@Test
	public void testCurrentStock() {
		List<Medicine> currentStock = new ArrayList<>();
		currentStock.add(new Medicine("M1", "Aspirin", 50, MedicineCategory.CURATIVE_MEDICINES));
		
		hospital.setCurrentStock(currentStock);
		assertEquals(currentStock, hospital.getCurrentStock());
		assertEquals(1, hospital.getCurrentStock().size());
	}

	@Test
	public void testRequiredStock() {
		List<Medicine> requiredStock = new ArrayList<>();
		requiredStock.add(new Medicine("M1", "Aspirin", 100, MedicineCategory.CURATIVE_MEDICINES));
		requiredStock.add(new Medicine("M2", "Vitamin C", 75, MedicineCategory.PREVENTIVE_MEDICINES));
		
		hospital.setRequiredStock(requiredStock);
		assertEquals(requiredStock, hospital.getRequiredStock());
		assertEquals(2, hospital.getRequiredStock().size());
	}
}
