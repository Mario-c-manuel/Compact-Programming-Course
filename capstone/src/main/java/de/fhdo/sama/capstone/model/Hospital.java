package de.fhdo.sama.capstone.model;

import java.util.List;

public class Hospital {
	private int id;
	private String name;
	private List<Medicine> currentStock;
	private List<Medicine> requiredStock;
	private Location location;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Medicine> getCurrentStock() {
		return currentStock;
	}

	public void setCurrentStock(List<Medicine> currentStock) {
		this.currentStock = currentStock;
	}

	public List<Medicine> getRequiredStock() {
		return requiredStock;
	}

	public void setRequiredStock(List<Medicine> requiredStock) {
		this.requiredStock = requiredStock;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
