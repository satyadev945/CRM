package crm.controller;

import crm.entity.Customer;
import crm.service.CustomerService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
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

    @Mock
    private PrintWriter printWriter;

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
        assertNotNull(csvController);
    }

    @Test
    void findCustomers_shouldReturnAllCustomers() throws IOException {
        when(customerService.listAllCustomers()).thenReturn(testCustomers);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);

        csvController.findCustomers(httpServletResponse);

        verify(customerService).listAllCustomers();
        verify(httpServletResponse).getWriter();
    }

    @Test
    void findCustomers_shouldHandleEmptyList() throws IOException {
        when(customerService.listAllCustomers()).thenReturn(Arrays.asList());
        when(httpServletResponse.getWriter()).thenReturn(printWriter);

        assertDoesNotThrow(() -> csvController.findCustomers(httpServletResponse));
    }

    @Test
    void findCustomer_shouldReturnCustomerById() throws IOException {
        Long customerId = 1L;
        when(customerService.showCustomer(customerId)).thenReturn(testCustomer);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);

        csvController.findCustomer(customerId, httpServletResponse);

        verify(customerService).showCustomer(customerId);
        verify(httpServletResponse).getWriter();
    }

    @Test
    void findCustomer_shouldHandleNullCustomer() throws IOException {
        Long customerId = 999L;
        when(customerService.showCustomer(customerId)).thenReturn(null);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);

        assertDoesNotThrow(() -> csvController.findCustomer(customerId, httpServletResponse));
    }

    @Test
    void findCustomer_shouldHandleIOException() throws IOException {
        Long customerId = 1L;
        when(customerService.showCustomer(customerId)).thenReturn(testCustomer);
        when(httpServletResponse.getWriter()).thenThrow(new IOException("Test exception"));

        assertThrows(IOException.class, () -> csvController.findCustomer(customerId, httpServletResponse));
    }
}
