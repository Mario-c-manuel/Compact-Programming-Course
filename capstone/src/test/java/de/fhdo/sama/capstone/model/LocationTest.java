package de.fhdo.sama.capstone.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class LocationTest {

	@Test
	public void testLocationRecord() {
		Location location = new Location("L1", "Main Warehouse", 10, 20);
		
		assertEquals("L1", location.id());
		assertEquals("Main Warehouse", location.name());
		assertEquals(10, location.x());
		assertEquals(20, location.y());
	}

	@Test
	public void testLocationEquality() {
		Location location1 = new Location("L1", "Hospital A", 5, 5);
		Location location2 = new Location("L1", "Hospital A", 5, 5);
		Location location3 = new Location("L2", "Hospital B", 10, 10);
		
		assertEquals(location1, location2, "Locations with same values should be equal");
		assertNotEquals(location1, location3, "Locations with different values should not be equal");
	}

	@Test
	public void testLocationToString() {
		Location location = new Location("L1", "Test Location", 15, 25);
		String str = location.toString();
		
		assertNotNull(str);
		assertTrue(str.contains("L1"));
		assertTrue(str.contains("Test Location"));
	}

	@Test
	public void testLocationHashCode() {
		Location location1 = new Location("L1", "Hospital", 0, 0);
		Location location2 = new Location("L1", "Hospital", 0, 0);
		
		assertEquals(location1.hashCode(), location2.hashCode(), "Equal locations should have same hashCode");
	}
}
