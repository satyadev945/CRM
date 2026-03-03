package crm.utils;

import crm.entity.Customer;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WriteCsvToResponseTest {

    @Test
    void writeCsvToResponse_classExists() {
        assertNotNull(WriteCsvToResponse.class);
    }

    @Test
    void writeCsvToResponse_hasWriteCustomersMethod() throws NoSuchMethodException {
        assertNotNull(WriteCsvToResponse.class.getMethod("writeCustomers", PrintWriter.class, List.class));
    }

    @Test
    void writeCsvToResponse_hasWriteCustomerMethod() throws NoSuchMethodException {
        assertNotNull(WriteCsvToResponse.class.getMethod("writeCustomer", PrintWriter.class, Customer.class));
    }

    @Test
    void writeCsvToResponse_shouldBeInstantiable() {
        assertDoesNotThrow(() -> new WriteCsvToResponse());
    }
}
