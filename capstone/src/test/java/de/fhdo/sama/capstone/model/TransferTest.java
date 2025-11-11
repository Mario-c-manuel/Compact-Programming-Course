package de.fhdo.sama.capstone.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransferTest {
	private Transfer transfer;

	@BeforeEach
	public void setUp() {
		transfer = new Transfer();
	}

	@Test
	public void testGettersAndSetters() {
		transfer.setId("T001");
		assertEquals("T001", transfer.getId());
		
		Medicine cargo = new Medicine("M1", "Aspirin", 50, MedicineCategory.CURATIVE_MEDICINES);
		transfer.setCargo(cargo);
		assertEquals(cargo, transfer.getCargo());
		
		Location from = new Location("W1", "Warehouse", 0, 0);
		transfer.setFrom(from);
		assertEquals(from, transfer.getFrom());
		
		Location to = new Location("H1", "Hospital", 10, 10);
		transfer.setTo(to);
		assertEquals(to, transfer.getTo());
		
		AGV executer = new AGV("AGV-1");
		transfer.setExecuter(executer);
		assertEquals(executer, transfer.getExecuter());
	}

	@Test
	public void testCompleteTransferSetup() {
		transfer.setId("T002");
		transfer.setCargo(new Medicine("M2", "Vitamin C", 100, MedicineCategory.PREVENTIVE_MEDICINES));
		transfer.setFrom(new Location("W1", "Main Warehouse", 0, 0));
		transfer.setTo(new Location("H1", "City Hospital", 15, 15));
		transfer.setExecuter(new AGV("AGV-2"));
		
		assertNotNull(transfer.getId());
		assertNotNull(transfer.getCargo());
		assertNotNull(transfer.getFrom());
		assertNotNull(transfer.getTo());
		assertNotNull(transfer.getExecuter());
		
		assertEquals("T002", transfer.getId());
		assertEquals("Vitamin C", transfer.getCargo().getName());
		assertEquals("Main Warehouse", transfer.getFrom().name());
		assertEquals("City Hospital", transfer.getTo().name());
		assertEquals("AGV-2", transfer.getExecuter().getName());
	}
}
