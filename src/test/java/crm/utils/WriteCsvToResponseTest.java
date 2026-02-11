package crm.utils;

import crm.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for WriteCsvToResponse utility class
 */
class WriteCsvToResponseTest {

    private PrintWriter printWriter;
    private StringWriter stringWriter;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);

        testCustomer = Customer.builder()
                .id(1L)
                .name("Acme Corp")
                .email("contact@acme.com")
                .phone(123456789)
                .firstName("John")
                .lastName("Smith")
                .city("New York")
                .address("123 Main St")
                .enabled(1)
                .categories(new HashSet<>())
                .build();
    }

    @Test
    void testWriteCustomers() {
        List<Customer> customers = new ArrayList<>();
        customers.add(testCustomer);

        assertDoesNotThrow(() -> {
            WriteCsvToResponse.writeCustomers(printWriter, customers);
        });
    }

    @Test
    void testWriteCustomersEmptyList() {
        List<Customer> customers = new ArrayList<>();

        assertDoesNotThrow(() -> {
            WriteCsvToResponse.writeCustomers(printWriter, customers);
        });
    }

    @Test
    void testWriteCustomer() {
        assertDoesNotThrow(() -> {
            WriteCsvToResponse.writeCustomer(printWriter, testCustomer);
        });
    }

    @Test
    void testWriteCustomersMultiple() {
        List<Customer> customers = new ArrayList<>();
        Customer customer2 = Customer.builder()
                .id(2L)
                .name("Tech Corp")
                .email("info@tech.com")
                .phone(987654321)
                .firstName("Jane")
                .lastName("Doe")
                .city("Boston")
                .address("456 Tech Ave")
                .enabled(1)
                .categories(new HashSet<>())
                .build();

        customers.add(testCustomer);
        customers.add(customer2);

        assertDoesNotThrow(() -> {
            WriteCsvToResponse.writeCustomers(printWriter, customers);
        });
    }

    @Test
    void testWriteCsvToResponseClassExists() {
        assertNotNull(WriteCsvToResponse.class);
    }

    @Test
    void testWriteCsvToResponseCanBeInstantiated() {
        assertDoesNotThrow(() -> new WriteCsvToResponse());
    }

    @Test
    void testWriteCustomersWithNullValues() {
        Customer nullCustomer = Customer.builder()
                .id(3L)
                .name(null)
                .email(null)
                .build();

        assertDoesNotThrow(() -> {
            WriteCsvToResponse.writeCustomer(printWriter, nullCustomer);
        });
    }
}
