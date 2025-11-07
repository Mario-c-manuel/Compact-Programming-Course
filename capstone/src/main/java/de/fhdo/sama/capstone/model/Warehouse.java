package de.fhdo.sama.capstone.model;

import java.util.List;

public class Warehouse {
	private String id;
	private String name;
	private List<Medicine> stock;
	private Location location;

	public Warehouse(String name, List<Medicine> stock, Location location) {
		this.name = name;
		this.stock = stock;
		this.location = location;
	}

	public List<Medicine> getStock() {
		return stock;
	}

	public void setStock(List<Medicine> stock) {
		this.stock = stock;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean removeMedicine(String medicineName, int quantity) {
		for (Medicine medicine : stock) {
			if (medicine.getName().equals(medicineName) && medicine.getQuantity() >= quantity) {
				medicine.setQuantity(medicine.getQuantity() - quantity);
				return true;
			}
		}
		return false;
	}

	public void addMedicine(Medicine medicine) {
		stock.add(medicine);
	}
}