package crm.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExportCustomersTest {

    @Test
    void exportCustomers_shouldBeInstantiable() {
        // Act
        ExportCustomers exportCustomers = new ExportCustomers();

        // Assert
        assertNotNull(exportCustomers);
    }

    @Test
    void exportCustomers_classExists() {
        // Assert
        assertNotNull(ExportCustomers.class);
    }
}
