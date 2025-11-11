package de.fhdo.sama.capstone;

import de.fhdo.sama.capstone.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Central configuration for locations, warehouses, medicines, etc. This class
 * is mutable and can be updated by the GUI.
 */
public class AppConfig {
	private final List<Location> locations = new ArrayList<>();
	private final List<Warehouse> warehouses = new ArrayList<>();
	private final List<Medicine> medicines = new ArrayList<>();
	private final List<String> chargingStations = new ArrayList<>();
	private final List<String> hospitals = new ArrayList<>();
	private final List<Transfer> transfers = new ArrayList<>();

	public List<Location> getLocations() {
		return locations;
	}

	public List<Warehouse> getWarehouses() {
		return warehouses;
	}

	public List<Medicine> getMedicines() {
		return medicines;
	}

	public List<String> getChargingStations() {
		return chargingStations;
	}

	public List<String> getHospitals() {
		return hospitals;
	}

	public List<Transfer> getTransfers() {
		return transfers;
	}

	/**
	 * Add a new location.
	 */
	public void addLocation(Location location) {
		locations.add(location);
	}

	/**
	 * Remove a location by ID.
	 */
	public void removeLocationById(String id) {
		locations.removeIf(l -> l.id().equals(id));
	}

	/**
	 * Add a new warehouse.
	 */
	public void addWarehouse(Warehouse warehouse) {
		warehouses.add(warehouse);
	}

	/**
	 * Remove a warehouse by name.
	 */
	public void removeWarehouseByName(String name) {
		warehouses.removeIf(w -> w.getName().equals(name));
	}

	/**
	 * Add a new medicine.
	 */
	public void addMedicine(Medicine medicine) {
		medicines.add(medicine);
	}

	/**
	 * Remove a medicine by ID.
	 */
	public void removeMedicineById(String id) {
		medicines.removeIf(m -> m.getId().equals(id));
	}

	/**
	 * Add a new charging station.
	 */
	public void addChargingStation(String name) {
		chargingStations.add(name);
	}

	/**
	 * Remove a charging station by name.
	 */
	public void removeChargingStation(String name) {
		chargingStations.remove(name);
	}

	/**
	 * Add a new hospital.
	 */
	public void addHospital(String name) {
		hospitals.add(name);
	}

	/**
	 * Remove a hospital by name.
	 */
	public void removeHospital(String name) {
		hospitals.remove(name);
	}

	/**
	 * Add a new transfer.
	 */
	public void addTransfer(Transfer transfer) {
		transfers.add(transfer);
	}

	/**
	 * Remove a transfer by ID.
	 */
	public void removeTransferById(String id) {
		transfers.removeIf(t -> t.getId().equals(id));
	}

	// Example: populate with some defaults
	public void loadDefaults() {
		locations.clear();
		warehouses.clear();
		medicines.clear();
		chargingStations.clear();
		hospitals.clear();
		transfers.clear();

		locations.add(new Location("W1", "Main Warehouse", 0, 0));
		locations.add(new Location("W2", "Secondary Warehouse", 5, 5));
		locations.add(new Location("H1", "City Hospital", 10, 10));
		locations.add(new Location("H2", "Rural Clinic", 20, 20));

		medicines.add(new Medicine("0", "Med1", 100, MedicineCategory.CURATIVE_MEDICINES));
		medicines.add(new Medicine("1", "Med2", 50, MedicineCategory.PREVENTIVE_MEDICINES));
		medicines.add(new Medicine("2", "Med3", 75, MedicineCategory.SYMTOMATIC_MEDICINES));

		warehouses.add(new Warehouse("Main Warehouse", new ArrayList<>(medicines), locations.get(0)));
		warehouses.add(new Warehouse("Secondary Warehouse", new ArrayList<>(medicines), locations.get(1)));

		chargingStations.add("Station 1");
		chargingStations.add("Station 2");

		hospitals.add("City Hospital");
		hospitals.add("Rural Clinic");
	}
}