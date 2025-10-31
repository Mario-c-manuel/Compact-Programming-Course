package test.medicine;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import main.medicine.*;

public class MedicineHandlerTest {
    MedicineHandler handler = new MedicineHandler();
    Medicine med = new Medicine(1, "Aspirin", "B001", "Pharma Inc", 50, "mg", null, "Shelf A", 10);

    @Test
    void testValidUpdate() {
        handler.updateMedicineQuantity(med, 10);
        assertEquals(60, med.getQuantity());
    }

    @Test
    void testNullMedicine() {
        assertDoesNotThrow(() -> handler.updateMedicineQuantity(null, 10));
    }

    @Test
    void testZeroQuantity() {
        assertDoesNotThrow(() -> handler.updateMedicineQuantity(med, 0));
    }

    @Test
    void testNegativeQuantity() {
        handler.updateMedicineQuantity(med, -10);
        assertEquals(40, med.getQuantity());
    }

    @Test
    void testFinalMessage() {
        handler.updateMedicineQuantity(med, 5);
    }
}
