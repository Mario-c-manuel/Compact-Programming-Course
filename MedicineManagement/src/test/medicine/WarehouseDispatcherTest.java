package test.medicine;

import main.medicine.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WarehouseDispatcherTest {

    WarehouseDispatcher dispatcher = new WarehouseDispatcher();
    Medicine med = new Medicine(3, "Cough Syrup", "B007", "HealthCorp", 30, "ml", null, "Rack D", 5);

    @Test
    void testValidDispatch() throws Exception {
        dispatcher.dispatchMedicine("Main Warehouse", med, 10);
        assertEquals(20, med.getQuantity());
    }

    @Test
    void testNullWarehouse() {
        Exception e = assertThrows(Exception.class, () -> dispatcher.dispatchMedicine(null, med, 10));
        assertTrue(e.getMessage().contains("Dispatch operation failed"));
    }

    @Test
    void testNullMedicine() {
        Exception e = assertThrows(Exception.class, () -> dispatcher.dispatchMedicine("Main Warehouse", null, 10));
        assertTrue(e.getMessage().contains("Dispatch operation failed"));
    }

    @Test
    void testInvalidQuantity() {
        Exception e = assertThrows(Exception.class, () -> dispatcher.dispatchMedicine("Main Warehouse", med, -5));
        assertTrue(e.getMessage().contains("Dispatch operation failed"));
    }

    @Test
    void testChainedExceptionCause() {
        try {
            dispatcher.dispatchMedicine(null, med, 10);
        } catch (Exception e) {
            assertNotNull(e.getCause());
        }
    }
}
