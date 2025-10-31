package meditrack;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        SystemManager system = new SystemManager();
        system.systemStart();

        AGV agv1 = new AGV("AGV1");
        AGV agv2 = new AGV("AGV2");

        ChargingStation cs1 = new ChargingStation("CS1");

        MedicineStorage medStorage = new MedicineStorage("MainStorage");
        medStorage.addMedicine("Paracetamol", 50);
        medStorage.addMedicine("Ibuprofen", 30);
        medStorage.removeMedicine("Paracetamol", 10);
        medStorage.removeMedicine("Aspirin", 5);
        medStorage.printInventory();

        agv1.startTask(101);
        agv2.startTask(102);
        cs1.chargeAGV(agv1.getName());
        cs1.finishCharging(agv1.getName());
        agv1.completeTask(101);
        agv2.completeTask(102);
        system.reportStatus("All tasks completed successfully.");
        system.systemStop();

        System.out.println("\n--- Open a log file ---");
        System.out.print("Enter category (AGV / ChargingStation / System / Storage): ");
        String category = scanner.nextLine();

        if (!category.matches("^(AGV|ChargingStation|System|Storage)$")) {
            System.out.println("Invalid category! Try again.");
            scanner.close();
            return;
        }

        System.out.print("Enter equipment name (e.g., AGV1, CS1, System, MainStorage): ");
        String equipment = scanner.nextLine();

        if (!equipment.matches("^[A-Za-z0-9_]+$")) {
            System.out.println("Invalid equipment name! Only letters, numbers, and underscores allowed.");
            scanner.close();
            return;
        }

        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        if (!date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            System.out.println("Invalid date format! Use YYYY-MM-DD.");
            scanner.close();
            return;
        }

        String fileName = equipment + "_" + date + ".log";
        System.out.println("\n--- Reading Log: " + fileName + " ---");
        DailyLogger.readLog(category, fileName);

        System.out.print("\nEnter a keyword or regex to search in this log (or press Enter to skip): ");
        String searchPattern = scanner.nextLine();

        if (!searchPattern.isEmpty()) {
            System.out.println("\n--- Search Results ---");
            DailyLogger.searchInLog(category, fileName, searchPattern);
        }

        scanner.close();
    }
}
