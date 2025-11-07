package de.fhdo.sama.capstone.model;

public class AGV {
	private String id;
	private String name;
	private int batteryLevel;
	private String currentTask;

	public AGV(String name) {
		this.name = name;
		this.batteryLevel = 100; // Default battery level
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

	public int getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(int batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	public String getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(String currentTask) {
		this.currentTask = currentTask;
	}

	public void executeTask(String task) {
		this.currentTask = task;
		// Simulate task execution
		System.out.println(name + " is executing task: " + task);
	}
}