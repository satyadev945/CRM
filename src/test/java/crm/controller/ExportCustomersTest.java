package crm.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExportCustomersTest {

    @Test
    void exportCustomers_shouldBeInstantiable() {
        ExportCustomers exportCustomers = new ExportCustomers();
        assertNotNull(exportCustomers);
    }

    @Test
    void exportCustomers_classExists() {
        assertNotNull(ExportCustomers.class);
    }
}
