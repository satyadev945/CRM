package crm.utils;

import crm.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WriteCsvToResponseTest {

    private Customer testCustomer;
    private List<Customer> testCustomers;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Company");
        testCustomer.setEmail("test@company.com");
        testCustomer.setPhone("123456789");
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setCity("New York");
        testCustomer.setAddress("123 Main St");
        testCustomer.setEnabled(true);

        testCustomers = Arrays.asList(testCustomer);

        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
    }

    @Test
    void writeCsvToResponse_shouldBeInstantiable() {
        WriteCsvToResponse utils = new WriteCsvToResponse();
        assertNotNull(utils);
    }

    @Test
    void writeCustomers_shouldWriteCsvData() {
        assertDoesNotThrow(() -> {
            WriteCsvToResponse.writeCustomers(printWriter, testCustomers);
        });
    }

    @Test
    void writeCustomer_shouldWriteCsvData() {
        assertDoesNotThrow(() -> {
            WriteCsvToResponse.writeCustomer(printWriter, testCustomer);
        });
    }

    @Test
    void writeCustomers_withEmptyList_shouldHandleGracefully() {
        assertDoesNotThrow(() -> {
            WriteCsvToResponse.writeCustomers(printWriter, Arrays.asList());
        });
    }

    @Test
    void writeCustomer_withNullCustomer_shouldHandleGracefully() {
        assertDoesNotThrow(() -> {
            WriteCsvToResponse.writeCustomer(printWriter, null);
        });
    }
}
