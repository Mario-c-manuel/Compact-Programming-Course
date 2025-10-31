package meditrack;

public class ChargingStation {
    private String name;

    public ChargingStation(String name) {
        this.name = name;
    }

    public void chargeAGV(String agvName) {
        DailyLogger.log("ChargingStation", name, "Charging AGV: " + agvName);
    }

    public void finishCharging(String agvName) {
        DailyLogger.log("ChargingStation", name, "Finished charging AGV: " + agvName);
    }

    public String getName() {
        return name;
    }
}
