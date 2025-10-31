package meditrack;

public class SystemManager {

    public void systemStart() {
        DailyLogger.log("System", "System", "System started.");
    }

    public void systemStop() {
        DailyLogger.log("System", "System", "System stopped.");
    }

    public void reportStatus(String message) {
        DailyLogger.log("System", "System", message);
    }
}
