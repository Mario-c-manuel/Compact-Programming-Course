package agv.simulation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TaskManagementSystem {

    private final int availableAGVs;

    public TaskManagementSystem(int availableAGVs) {
        this.availableAGVs = availableAGVs;
    }

    public void simulateTasks(int totalTasks) throws InterruptedException {

        ExecutorService taskPool = Executors.newFixedThreadPool(availableAGVs);

        System.out.println("\n=== TASK SIMULATION START ===");
        System.out.println("Available AGVs for tasks: " + availableAGVs + "\n");

        for (int i = 1; i <= totalTasks; i++) {
            int assignedAGV = (i % availableAGVs) + 1;

            AGV agv = new AGV(assignedAGV);
            taskPool.submit(agv::runTask);
        }

        taskPool.shutdown();
        taskPool.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("=== TASK SIMULATION END ===\n");
    }
}
