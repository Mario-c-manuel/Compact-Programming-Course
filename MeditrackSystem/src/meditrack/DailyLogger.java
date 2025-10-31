package meditrack;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class DailyLogger {
    private static final String LOG_DIR = "logs";

    // Write log entry
    public static void log(String category, String equipmentName, String message) {
        try {
            LocalDate date = LocalDate.now();
            LocalTime time = LocalTime.now();
            String formattedTime = time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            File dir = new File(LOG_DIR + "/" + category);
            if (!dir.exists()) dir.mkdirs();

            File logFile = new File(dir, equipmentName + "_" + date + ".log");
            if (!logFile.exists()) logFile.createNewFile();

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true))) {
                bw.write("[" + formattedTime + "] " + message);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read log file
    public static void readLog(String category, String fileName) {
        File logFile = new File(LOG_DIR + "/" + category + "/" + fileName);
        if (!logFile.exists()) {
            System.out.println("❌ Log not found: " + fileName);
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = br.readLine()) != null) System.out.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Regex-based search in log file
    public static void searchInLog(String category, String fileName, String patternStr) {
        File logFile = new File(LOG_DIR + "/" + category + "/" + fileName);
        if (!logFile.exists()) {
            System.out.println("❌ Log file not found: " + fileName);
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
            String line;
            boolean found = false;

            System.out.println("🔍 Searching for pattern: " + patternStr);
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    System.out.println(line);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No matches found for: " + patternStr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
