package crm.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExportCustomersTest {

    @Test
    void testDefaultConstructor() {
        ExportCustomers exportCustomers = new ExportCustomers();
        assertNotNull(exportCustomers);
    }

    @Test
    void testClassInstantiation() {
        ExportCustomers ec = new ExportCustomers();
        assertTrue(ec instanceof ExportCustomers);
    }
}
