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
        testCustomer = Customer.builder()
                .id(1L)
                .name("Test Company")
                .email("test@example.com")
                .phone(123456789)
                .firstName("John")
                .lastName("Doe")
                .city("New York")
                .address("123 Main St")
                .enabled(1)
                .build();

        testCustomers = Arrays.asList(testCustomer);
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
    }

    @Test
    void writeCsvToResponse_shouldBeInstantiable() {
        // Act
        WriteCsvToResponse writeCsvToResponse = new WriteCsvToResponse();

        // Assert
        assertNotNull(writeCsvToResponse);
    }

    @Test
    void writeCustomers_shouldWriteCustomersToWriter() {
        // Act
        WriteCsvToResponse.writeCustomers(printWriter, testCustomers);
        printWriter.flush();

        // Assert
        String result = stringWriter.toString();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void writeCustomers_shouldHandleEmptyList() {
        // Act
        WriteCsvToResponse.writeCustomers(printWriter, Arrays.asList());
        printWriter.flush();

        // Assert
        String result = stringWriter.toString();
        assertNotNull(result);
    }

    @Test
    void writeCustomer_shouldWriteSingleCustomerToWriter() {
        // Act
        WriteCsvToResponse.writeCustomer(printWriter, testCustomer);
        printWriter.flush();

        // Assert
        String result = stringWriter.toString();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void writeCustomers_shouldBeStaticMethod() throws NoSuchMethodException {
        // Assert
        assertTrue(java.lang.reflect.Modifier.isStatic(
                WriteCsvToResponse.class.getMethod("writeCustomers", PrintWriter.class, List.class).getModifiers()
        ));
    }

    @Test
    void writeCustomer_shouldBeStaticMethod() throws NoSuchMethodException {
        // Assert
        assertTrue(java.lang.reflect.Modifier.isStatic(
                WriteCsvToResponse.class.getMethod("writeCustomer", PrintWriter.class, Customer.class).getModifiers()
        ));
    }
}
