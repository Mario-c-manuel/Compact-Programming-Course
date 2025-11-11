package de.fhdo.sama.capstone.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class AGVTest {

	@Test
	public void testAGVConstructor() {
		AGV agv = new AGV("AGV-1");
		
		assertEquals("AGV-1", agv.getName());
		assertEquals(100, agv.getBatteryLevel(), "Default battery level should be 100");
	}

	@Test
	public void testGettersAndSetters() {
		AGV agv = new AGV("AGV-2");
		
		agv.setId("AGV-001");
		assertEquals("AGV-001", agv.getId());
		
		agv.setName("AGV-Updated");
		assertEquals("AGV-Updated", agv.getName());
		
		agv.setBatteryLevel(75);
		assertEquals(75, agv.getBatteryLevel());
		
		agv.setCurrentTask("Delivery to Hospital");
		assertEquals("Delivery to Hospital", agv.getCurrentTask());
	}

	@Test
	public void testExecuteTask() {
		AGV agv = new AGV("AGV-3");
		agv.executeTask("Pick up medicine");
		
		assertEquals("Pick up medicine", agv.getCurrentTask());
	}

	@Test
	public void testBatteryLevelBoundaries() {
		AGV agv = new AGV("AGV-4");
		
		agv.setBatteryLevel(0);
		assertEquals(0, agv.getBatteryLevel());
		
		agv.setBatteryLevel(100);
		assertEquals(100, agv.getBatteryLevel());
		
		agv.setBatteryLevel(50);
		assertEquals(50, agv.getBatteryLevel());
	}
}
