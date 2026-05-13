package crm.controller;

import crm.entity.Customer;
import crm.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletResponse;
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

    private Customer customer;
    private PrintWriter printWriter;
    private StringWriter stringWriter;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .name("TestCo")
                .email("test@example.com")
                .phone(123456789)
                .firstName("John")
                .lastName("Doe")
                .city("NYC")
                .address("123 Main St")
                .enabled(1)
                .build();

        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
    }

    @Test
    void testConstructor() {
        CSVController controller = new CSVController(customerService);
        assertNotNull(controller);
    }

    @Test
    void testFindCustomers_callsListAllCustomers() throws IOException {
        List<Customer> customers = Arrays.asList(customer);
        when(customerService.listAllCustomers()).thenReturn(customers);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);

        csvController.findCustomers(httpServletResponse);

        verify(customerService).listAllCustomers();
    }

    @Test
    void testFindCustomers_callsGetWriter() throws IOException {
        List<Customer> customers = Arrays.asList(customer);
        when(customerService.listAllCustomers()).thenReturn(customers);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);

        csvController.findCustomers(httpServletResponse);

        verify(httpServletResponse).getWriter();
    }

    @Test
    void testFindCustomer_callsShowCustomer() throws IOException {
        when(customerService.showCustomer(1L)).thenReturn(customer);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);

        csvController.findCustomer(1L, httpServletResponse);

        verify(customerService).showCustomer(1L);
    }

    @Test
    void testFindCustomer_callsGetWriter() throws IOException {
        when(customerService.showCustomer(1L)).thenReturn(customer);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);

        csvController.findCustomer(1L, httpServletResponse);

        verify(httpServletResponse).getWriter();
    }
}
