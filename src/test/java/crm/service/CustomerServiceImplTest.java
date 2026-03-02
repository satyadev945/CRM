package crm.service;

import crm.entity.Customer;
import crm.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Company");
        testCustomer.setEmail("test@company.com");
        testCustomer.setEnabled(true);
    }

    @Test
    void getMaxId_shouldReturnMaxId() {
        when(customerRepository.getMaxId()).thenReturn(10L);

        Long maxId = customerService.getMaxId();

        assertEquals(10L, maxId);
        verify(customerRepository, times(1)).getMaxId();
    }

    @Test
    void listAllCustomers_shouldReturnAllCustomers() {
        when(customerRepository.findAll()).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> customers = customerService.listAllCustomers();

        assertNotNull(customers);
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void showCustomer_shouldReturnCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        Customer found = customerService.showCustomer(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void findAllByEnabledTrue_shouldReturnEnabledCustomers() {
        when(customerRepository.findAllByEnabled(true)).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> customers = customerService.findAllByEnabledTrue();

        assertNotNull(customers);
        verify(customerRepository, times(1)).findAllByEnabled(true);
    }

    @Test
    void saveCustomer_shouldCallRepository() {
        customerService.saveCustomer(testCustomer);

        verify(customerRepository, times(1)).save(testCustomer);
    }

    @Test
    void customerService_shouldImplementCustomerService() {
        assertTrue(customerService instanceof CustomerService);
    }
}
