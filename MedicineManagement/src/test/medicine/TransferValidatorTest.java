package test.medicine;

import main.medicine.*;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TransferValidatorTest {

    TransferValidator validator = new TransferValidator();

    @Test
    void testValidTransfer() {
        List<Medicine> meds = List.of(new Medicine(1, "Paracetamol", "B002", "MediCorp", 100, "mg", null, "Rack B", 20));
        assertDoesNotThrow(() -> validator.validateTransfer(meds));
    }

    @Test
    void testNullList() {
        Exception e = assertThrows(Exception.class, () -> validator.validateTransfer(null));
        assertTrue(e instanceof NullPointerException);
    }

    @Test
    void testEmptyList() {
        Exception e = assertThrows(Exception.class, () -> validator.validateTransfer(new ArrayList<>()));
        assertTrue(e instanceof IllegalArgumentException);
    }

    @Test
    void testReThrownExceptionMessage() {
        Exception e = assertThrows(Exception.class, () -> validator.validateTransfer(null));
        assertEquals("Transfer list cannot be null", e.getMessage());
    }

    @Test
    void testMultipleMedicines() {
        List<Medicine> meds = List.of(
                new Medicine(1, "Amoxicillin", "B004", "Pharma", 20, "caps", null, "Shelf B", 5),
                new Medicine(2, "Ibuprofen", "B005", "MediCorp", 50, "mg", null, "Shelf C", 10)
        );
        assertDoesNotThrow(() -> validator.validateTransfer(meds));
    }
}
