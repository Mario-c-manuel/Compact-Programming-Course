package main.medicine;

import java.io.FileWriter;
import java.io.IOException;

public class LogFileWriter {

    public void writeLog(String message) {
        try (FileWriter writer = new FileWriter("medicine_log.txt", true)) {
            writer.write(message + "\n");
        } catch (IOException e) {
            System.err.println("Error writing log: " + e.getMessage());
        }
    }
}
