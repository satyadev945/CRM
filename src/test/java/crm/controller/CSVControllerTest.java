package crm.controller;

import crm.entity.Customer;
import crm.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CSVControllerTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private HttpServletResponse httpServletResponse;

    @InjectMocks
    private CSVController csvController;

    private Customer testCustomer;
    private List<Customer> testCustomers;

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
    }

    @Test
    void constructor_shouldInitializeCustomerService() {
        // Assert
        assertNotNull(csvController);
    }

    @Test
    void findCustomers_shouldReturnAllCustomersAsCsv() throws IOException {
        // Arrange
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(customerService.listAllCustomers()).thenReturn(testCustomers);
        when(httpServletResponse.getWriter()).thenReturn(writer);

        // Act
        csvController.findCustomers(httpServletResponse);

        // Assert
        verify(customerService).listAllCustomers();
        verify(httpServletResponse).getWriter();
    }

    @Test
    void findCustomers_shouldHandleEmptyList() throws IOException {
        // Arrange
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(customerService.listAllCustomers()).thenReturn(Arrays.asList());
        when(httpServletResponse.getWriter()).thenReturn(writer);

        // Act
        csvController.findCustomers(httpServletResponse);

        // Assert
        verify(customerService).listAllCustomers();
        verify(httpServletResponse).getWriter();
    }

    @Test
    void findCustomer_shouldReturnSingleCustomerAsCsv() throws IOException {
        // Arrange
        Long customerId = 1L;
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(customerService.showCustomer(customerId)).thenReturn(testCustomer);
        when(httpServletResponse.getWriter()).thenReturn(writer);

        // Act
        csvController.findCustomer(customerId, httpServletResponse);

        // Assert
        verify(customerService).showCustomer(customerId);
        verify(httpServletResponse).getWriter();
    }

    @Test
    void findCustomer_shouldHandleNullCustomer() throws IOException {
        // Arrange
        Long customerId = 999L;
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(customerService.showCustomer(customerId)).thenReturn(null);
        when(httpServletResponse.getWriter()).thenReturn(writer);

        // Act
        csvController.findCustomer(customerId, httpServletResponse);

        // Assert
        verify(customerService).showCustomer(customerId);
        verify(httpServletResponse).getWriter();
    }

    @Test
    void findCustomers_shouldThrowIOException_whenWriterFails() throws IOException {
        // Arrange
        when(customerService.listAllCustomers()).thenReturn(testCustomers);
        when(httpServletResponse.getWriter()).thenThrow(new IOException("Writer error"));

        // Act & Assert
        assertThrows(IOException.class, () -> {
            csvController.findCustomers(httpServletResponse);
        });
    }

    @Test
    void findCustomer_shouldThrowIOException_whenWriterFails() throws IOException {
        // Arrange
        Long customerId = 1L;
        when(customerService.showCustomer(customerId)).thenReturn(testCustomer);
        when(httpServletResponse.getWriter()).thenThrow(new IOException("Writer error"));

        // Act & Assert
        assertThrows(IOException.class, () -> {
            csvController.findCustomer(customerId, httpServletResponse);
        });
    }
}
