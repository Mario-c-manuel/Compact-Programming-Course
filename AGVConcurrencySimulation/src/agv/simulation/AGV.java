package agv.simulation;

public class AGV {

    private final int id;

    public AGV(int id) {
        this.id = id;
    }

    public void charge() {
        try {
            System.out.println("[AGV " + id + "] Started charging...");
            Thread.sleep(3000);
            System.out.println("[AGV " + id + "] Finished charging.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void runTask() {
        try {
            System.out.println("[AGV " + id + "] Executing task...");
            Thread.sleep(2000);
            System.out.println("[AGV " + id + "] Completed task.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
