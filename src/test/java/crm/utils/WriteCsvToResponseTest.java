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

    private Customer customer;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        customer.setPhone(123456789);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setCity("Test City");
        customer.setAddress("Test Address");
        customer.setEnabled(1);

        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
    }

    @Test
    void writeCustomersShouldWriteMultipleCustomers() {
        // Arrange
        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("Another Customer");
        customer2.setEmail("another@example.com");
        List<Customer> customers = Arrays.asList(customer, customer2);

        // Act
        WriteCsvToResponse.writeCustomers(printWriter, customers);

        // Assert
        String result = stringWriter.toString();
        assertNotNull(result);
        assertTrue(result.contains("Test Customer"));
        assertTrue(result.contains("Another Customer"));
        assertTrue(result.contains("test@example.com"));
        assertTrue(result.contains("another@example.com"));
    }

    @Test
    void writeCustomerShouldWriteSingleCustomer() {
        // Act
        WriteCsvToResponse.writeCustomer(printWriter, customer);

        // Assert
        String result = stringWriter.toString();
        assertNotNull(result);
        assertTrue(result.contains("Test Customer"));
        assertTrue(result.contains("test@example.com"));
    }
}