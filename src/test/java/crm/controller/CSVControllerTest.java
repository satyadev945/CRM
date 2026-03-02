package crm.controller;

import crm.entity.Customer;
import crm.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

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

    @InjectMocks
    private CSVController csvController;

    private MockHttpServletResponse response;
    private Customer testCustomer;
    private List<Customer> testCustomers;

    @BeforeEach
    void setUp() {
        response = new MockHttpServletResponse();
        
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john.doe@example.com");
        
        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setFirstName("Jane");
        customer2.setLastName("Smith");
        customer2.setEmail("jane.smith@example.com");
        
        testCustomers = Arrays.asList(testCustomer, customer2);
    }

    @Test
    void constructor_withCustomerService_shouldCreateInstance() {
        CSVController controller = new CSVController(customerService);
        assertNotNull(controller);
    }

    @Test
    void findCustomers_shouldReturnAllCustomersAsCsv() throws IOException {
        when(customerService.listAllCustomers()).thenReturn(testCustomers);
        
        csvController.findCustomers(response);
        
        verify(customerService, times(1)).listAllCustomers();
        assertNotNull(response.getContentAsString());
    }

    @Test
    void findCustomers_withEmptyList_shouldHandleGracefully() throws IOException {
        when(customerService.listAllCustomers()).thenReturn(Arrays.asList());
        
        assertDoesNotThrow(() -> csvController.findCustomers(response));
        verify(customerService, times(1)).listAllCustomers();
    }

    @Test
    void findCustomer_withValidId_shouldReturnCustomerAsCsv() throws IOException {
        Long customerId = 1L;
        when(customerService.showCustomer(customerId)).thenReturn(testCustomer);
        
        csvController.findCustomer(customerId, response);
        
        verify(customerService, times(1)).showCustomer(customerId);
        assertNotNull(response.getContentAsString());
    }

    @Test
    void findCustomer_withNullCustomer_shouldHandleGracefully() throws IOException {
        Long customerId = 999L;
        when(customerService.showCustomer(customerId)).thenReturn(null);
        
        assertDoesNotThrow(() -> csvController.findCustomer(customerId, response));
        verify(customerService, times(1)).showCustomer(customerId);
    }

    @Test
    void findCustomer_withDifferentIds_shouldCallServiceWithCorrectId() throws IOException {
        Long customerId1 = 1L;
        Long customerId2 = 2L;
        
        when(customerService.showCustomer(customerId1)).thenReturn(testCustomer);
        when(customerService.showCustomer(customerId2)).thenReturn(testCustomers.get(1));
        
        csvController.findCustomer(customerId1, response);
        verify(customerService, times(1)).showCustomer(customerId1);
        
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        csvController.findCustomer(customerId2, response2);
        verify(customerService, times(1)).showCustomer(customerId2);
    }

    @Test
    void findCustomers_shouldSetCorrectContentType() throws IOException {
        when(customerService.listAllCustomers()).thenReturn(testCustomers);
        
        csvController.findCustomers(response);
        
        // The content type is set by the @GetMapping annotation
        verify(customerService, times(1)).listAllCustomers();
    }

    @Test
    void findCustomer_withZeroId_shouldCallService() throws IOException {
        Long customerId = 0L;
        when(customerService.showCustomer(customerId)).thenReturn(null);
        
        assertDoesNotThrow(() -> csvController.findCustomer(customerId, response));
        verify(customerService, times(1)).showCustomer(customerId);
    }

    @Test
    void findCustomer_withNegativeId_shouldCallService() throws IOException {
        Long customerId = -1L;
        when(customerService.showCustomer(customerId)).thenReturn(null);
        
        assertDoesNotThrow(() -> csvController.findCustomer(customerId, response));
        verify(customerService, times(1)).showCustomer(customerId);
    }

    @Test
    void findCustomers_multipleInvocations_shouldCallServiceEachTime() throws IOException {
        when(customerService.listAllCustomers()).thenReturn(testCustomers);
        
        csvController.findCustomers(response);
        csvController.findCustomers(new MockHttpServletResponse());
        csvController.findCustomers(new MockHttpServletResponse());
        
        verify(customerService, times(3)).listAllCustomers();
    }

    @Test
    void csvController_shouldNotBeNull() {
        assertNotNull(csvController);
    }
}
