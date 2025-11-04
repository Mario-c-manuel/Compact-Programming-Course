package agv.simulation;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ChargingSystem {

    private final int chargingStations;

    public ChargingSystem(int chargingStations) {
        this.chargingStations = chargingStations;
    }

    public void simulateCharging(int totalAGVs) throws InterruptedException {

        ExecutorService stationPool = Executors.newFixedThreadPool(chargingStations);
        Random rand = new Random();

        System.out.println("\n=== CHARGING SIMULATION START ===");
        System.out.println("Available charging stations: " + chargingStations + "\n");

        for (int i = 1; i <= totalAGVs; i++) {

            int waitingTime = rand.nextInt(20); // 0–19 minutes
            System.out.println("[AGV " + i + "] Arrived with expected waiting time: " + waitingTime + " mins");

            if (waitingTime > 15) {
                System.out.println("[AGV " + i + "] LEFT queue due to waiting time > 15 mins");
                continue;
            }

            AGV agv = new AGV(i);
            stationPool.submit(agv::charge);
        }

        stationPool.shutdown();
        stationPool.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("=== CHARGING SIMULATION END ===\n");
    }
}
