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
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class CSVControllerTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private HttpServletResponse httpServletResponse;

    @InjectMocks
    private CSVController csvController;

    private Customer testCustomer;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws IOException {
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

        StringWriter stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        lenient().when(httpServletResponse.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testConstructor() {
        CustomerService mockService = mock(CustomerService.class);
        CSVController controller = new CSVController(mockService);
        assertNotNull(controller);
    }

    @Test
    void testFindCustomers() throws IOException {
        when(customerService.listAllCustomers()).thenReturn(Arrays.asList(testCustomer));

        csvController.findCustomers(httpServletResponse);

        verify(customerService).listAllCustomers();
        verify(httpServletResponse).getWriter();
    }

    @Test
    void testFindCustomer() throws IOException {
        when(customerService.showCustomer(1L)).thenReturn(testCustomer);

        csvController.findCustomer(1L, httpServletResponse);

        verify(customerService).showCustomer(1L);
        verify(httpServletResponse).getWriter();
    }

    @Test
    void testFindCustomerNotFound() throws IOException {
        when(customerService.showCustomer(999L)).thenReturn(null);

        csvController.findCustomer(999L, httpServletResponse);

        verify(customerService).showCustomer(999L);
    }
}
