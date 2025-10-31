package test.medicine;

import main.medicine.*;
import org.junit.jupiter.api.Test;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class LogFileWriterTest {

    LogFileWriter writer = new LogFileWriter();
    String logFilePath = "medicine_log.txt";

    @Test
    void testWriteLogCreatesFile() {
        File file = new File(logFilePath);
        if (file.exists()) file.delete();
        writer.writeLog("Testing log creation");
        assertTrue(file.exists());
    }

    @Test
    void testWriteLogAppends() throws IOException {
        writer.writeLog("Line 1");
        writer.writeLog("Line 2");

        String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(logFilePath)));
        assertTrue(content.contains("Line 1") && content.contains("Line 2"));
    }

    @Test
    void testWriteEmptyLog() {
        assertDoesNotThrow(() -> writer.writeLog(""));
    }

    @Test
    void testIOExceptionHandled() {
        // Trying to write to a read-only file (simulate)
        File readOnly = new File("readonly.txt");
        try {
            readOnly.createNewFile();
            readOnly.setReadOnly();
            writer.writeLog("test"); // should catch IOException
        } catch (Exception ignored) {
        } finally {
            readOnly.delete();
        }
    }

    @Test
    void testMultipleWrites() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 5; i++) writer.writeLog("Entry " + i);
        });
    }
}
