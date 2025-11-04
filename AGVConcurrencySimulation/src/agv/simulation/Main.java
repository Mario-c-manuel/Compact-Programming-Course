package agv.simulation;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        // Run charging simulation
        ChargingSystem chargingSystem = new ChargingSystem(3);
        chargingSystem.simulateCharging(10);

        // Run task execution simulation
        TaskManagementSystem taskManager = new TaskManagementSystem(2);
        taskManager.simulateTasks(6);
    }
}
