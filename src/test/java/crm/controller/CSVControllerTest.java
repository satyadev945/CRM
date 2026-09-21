package crm.controller;

import crm.entity.Customer;
import crm.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletResponse;
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

    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    void setUp() {
        customer1 = Customer.builder()
                .id(1L).name("Corp A").email("a@corp.com")
                .phone(111222333).firstName("John").lastName("Doe")
                .city("NYC").address("5th Ave").enabled(1).build();

        customer2 = Customer.builder()
                .id(2L).name("Corp B").email("b@corp.com")
                .phone(444555666).firstName("Jane").lastName("Smith")
                .city("LA").address("Sunset Blvd").enabled(1).build();
    }

    @Test
    void testFindCustomers_CallsCustomerService() throws IOException {
        List<Customer> customers = Arrays.asList(customer1, customer2);
        when(customerService.listAllCustomers()).thenReturn(customers);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);

        csvController.findCustomers(httpServletResponse);

        verify(customerService).listAllCustomers();
    }

    @Test
    void testFindCustomers_GetsWriter() throws IOException {
        List<Customer> customers = Arrays.asList(customer1, customer2);
        when(customerService.listAllCustomers()).thenReturn(customers);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);

        csvController.findCustomers(httpServletResponse);

        verify(httpServletResponse).getWriter();
    }

    @Test
    void testFindCustomer_CallsShowCustomer() throws IOException {
        when(customerService.showCustomer(1L)).thenReturn(customer1);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);

        csvController.findCustomer(1L, httpServletResponse);

        verify(customerService).showCustomer(1L);
    }

    @Test
    void testFindCustomer_GetsWriter() throws IOException {
        when(customerService.showCustomer(1L)).thenReturn(customer1);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);

        csvController.findCustomer(1L, httpServletResponse);

        verify(httpServletResponse).getWriter();
    }

    @Test
    void testConstructor_WithCustomerService() {
        CSVController controller = new CSVController(customerService);
        assertNotNull(controller);
    }

    @Test
    void testFindCustomer_WithDifferentId() throws IOException {
        when(customerService.showCustomer(2L)).thenReturn(customer2);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);

        csvController.findCustomer(2L, httpServletResponse);

        verify(customerService).showCustomer(2L);
    }
}
