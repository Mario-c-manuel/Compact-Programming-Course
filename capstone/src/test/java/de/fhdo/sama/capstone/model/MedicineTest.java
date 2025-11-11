package de.fhdo.sama.capstone.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MedicineTest {

	@Test
	public void testMedicineConstructor() {
		Medicine medicine = new Medicine("M1", "Aspirin", 100, MedicineCategory.CURATIVE_MEDICINES);
		
		assertEquals("M1", medicine.getId());
		assertEquals("Aspirin", medicine.getName());
		assertEquals(100, medicine.getQuantity());
		assertEquals(MedicineCategory.CURATIVE_MEDICINES, medicine.getCategory());
	}

	@Test
	public void testMedicineBuilder() {
		Medicine medicine = new Medicine.Builder()
			.setId("M2")
			.setName("Vitamin C")
			.setQuantity(50)
			.setCategory(MedicineCategory.PREVENTIVE_MEDICINES)
			.build();
		
		assertEquals("M2", medicine.getId());
		assertEquals("Vitamin C", medicine.getName());
		assertEquals(50, medicine.getQuantity());
		assertEquals(MedicineCategory.PREVENTIVE_MEDICINES, medicine.getCategory());
	}

	@Test
	public void testSetters() {
		Medicine medicine = new Medicine("M1", "Aspirin", 100, MedicineCategory.CURATIVE_MEDICINES);
		
		medicine.setId("M3");
		assertEquals("M3", medicine.getId());
		
		medicine.setName("Paracetamol");
		assertEquals("Paracetamol", medicine.getName());
		
		medicine.setQuantity(200);
		assertEquals(200, medicine.getQuantity());
		
		medicine.setCategory(MedicineCategory.SYMTOMATIC_MEDICINES);
		assertEquals(MedicineCategory.SYMTOMATIC_MEDICINES, medicine.getCategory());
	}

	@Test
	public void testBuilderChaining() {
		Medicine.Builder builder = new Medicine.Builder();
		Medicine medicine = builder
			.setId("M4")
			.setName("Test Med")
			.setQuantity(75)
			.setCategory(MedicineCategory.CURATIVE_MEDICINES)
			.build();
		
		assertNotNull(medicine);
		assertEquals("M4", medicine.getId());
	}

	@Test
	public void testMedicineCategoryEnum() {
		assertEquals(3, MedicineCategory.values().length, "Should have 3 medicine categories");
		
		MedicineCategory preventive = MedicineCategory.PREVENTIVE_MEDICINES;
		MedicineCategory curative = MedicineCategory.CURATIVE_MEDICINES;
		MedicineCategory symptomatic = MedicineCategory.SYMTOMATIC_MEDICINES;
		
		assertNotNull(preventive);
		assertNotNull(curative);
		assertNotNull(symptomatic);
	}
}