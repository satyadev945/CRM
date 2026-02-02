package crm.controller;

import crm.entity.Customer;
import crm.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class CSVControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CSVController csvController;

    @Mock
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findCustomersShouldWriteAllCustomersToCsv() throws IOException {
        // Arrange
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setName("Test Customer 1");

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("Test Customer 2");

        List<Customer> customers = Arrays.asList(customer1, customer2);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(customerService.listAllCustomers()).thenReturn(customers);
        when(response.getWriter()).thenReturn(printWriter);

        // Act
        csvController.findCustomers(response);

        // Assert
        verify(customerService).listAllCustomers();
        verify(response).getWriter();
    }

    @Test
    void findCustomerShouldWriteSpecificCustomerToCsv() throws IOException {
        // Arrange
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName("Test Customer");

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(customerService.showCustomer(customerId)).thenReturn(customer);
        when(response.getWriter()).thenReturn(printWriter);

        // Act
        csvController.findCustomer(customerId, response);

        // Assert
        verify(customerService).showCustomer(customerId);
        verify(response).getWriter();
    }
}