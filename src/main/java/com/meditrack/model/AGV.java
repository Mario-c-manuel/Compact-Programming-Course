package com.meditrack.model;

public class AGV {
    private String id;
    private String name;
    private String status; // 'Idle', 'On Task', 'Charging', 'Maintenance Required'
    private int batteryLevel;
    private String currentLocation;
    private String currentTask;
    
    public AGV() {}
    
    public AGV(String id, String name, String status, int batteryLevel, 
               String currentLocation, String currentTask) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.batteryLevel = batteryLevel;
        this.currentLocation = currentLocation;
        this.currentTask = currentTask;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(int batteryLevel) { this.batteryLevel = batteryLevel; }
    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
    public String getCurrentTask() { return currentTask; }
    public void setCurrentTask(String currentTask) { this.currentTask = currentTask; }
}