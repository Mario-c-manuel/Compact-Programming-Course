package meditrack;

public class AGV {
    private String name;

    public AGV(String name) {
        this.name = name;
    }

    public void startTask(int taskId) {
        DailyLogger.log("AGV", name, "Started task #" + taskId);
    }

    public void completeTask(int taskId) {
        DailyLogger.log("AGV", name, "Completed task #" + taskId);
    }

    public String getName() {
        return name;
    }
}
