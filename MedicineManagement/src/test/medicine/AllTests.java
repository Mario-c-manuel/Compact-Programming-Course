package test.medicine;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    MedicineHandlerTest.class,
    TransferValidatorTest.class,
    LogFileWriterTest.class,
    WarehouseDispatcherTest.class
})
public class AllTests { }
