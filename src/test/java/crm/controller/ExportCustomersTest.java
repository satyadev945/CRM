package crm.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for ExportCustomers class
 * Note: This class is commented out in the source code
 */
class ExportCustomersTest {

    @Test
    void testExportCustomersClassExists() {
        ExportCustomers exportCustomers = new ExportCustomers();
        assertNotNull(exportCustomers);
    }

    @Test
    void testExportCustomersIsNotController() {
        // Class exists but is not annotated as @Controller in source
        assertFalse(ExportCustomers.class.isAnnotationPresent(org.springframework.stereotype.Controller.class));
    }
}
