package crm.service;

import crm.entity.Category;
import crm.entity.Customer;
import crm.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer testCustomer;
    private Set<Category> categories;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Technology");

        categories = new HashSet<>();
        categories.add(category);

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
                .categories(categories)
                .build();
    }

    @Test
    void getMaxId_shouldReturnMaxId() {
        when(customerRepository.getMaxId()).thenReturn(10L);

        Long result = customerService.getMaxId();

        assertEquals(10L, result);
        verify(customerRepository).getMaxId();
    }

    @Test
    void listAllCustomers_shouldReturnAllCustomers() {
        when(customerRepository.findAll()).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.listAllCustomers();

        assertNotNull(result);
        verify(customerRepository).findAll();
    }

    @Test
    void showCustomer_shouldReturnCustomerById() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        Customer result = customerService.showCustomer(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(customerRepository).findById(1L);
    }

    @Test
    void showCustomer_withNonExistentId_shouldReturnNull() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        Customer result = customerService.showCustomer(999L);

        assertNull(result);
    }

    @Test
    void findAllByEnabledTrue_shouldReturnEnabledCustomers() {
        when(customerRepository.findAllByEnabled(1)).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findAllByEnabledTrue();

        assertNotNull(result);
        verify(customerRepository).findAllByEnabled(1);
    }

    @Test
    void findAllByEnabledFalse_shouldReturnDisabledCustomers() {
        when(customerRepository.findAllByEnabled(0)).thenReturn(Arrays.asList());

        Iterable<Customer> result = customerService.findAllByEnabledFalse();

        assertNotNull(result);
        verify(customerRepository).findAllByEnabled(0);
    }

    @Test
    void findOneByEnabledTrueAndName_shouldReturnCustomer() {
        when(customerRepository.findOneByEnabledAndName(1, "Test Company")).thenReturn(testCustomer);

        Customer result = customerService.findOneByEnabledTrueAndName("Test Company");

        assertNotNull(result);
        assertEquals("Test Company", result.getName());
        verify(customerRepository).findOneByEnabledAndName(1, "Test Company");
    }

    @Test
    void findByEnabledTrueAndEmail_shouldReturnCustomers() {
        when(customerRepository.findByEnabledAndEmail(1, "test@example.com")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledTrueAndEmail("test@example.com");

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndEmail(1, "test@example.com");
    }

    @Test
    void findByEnabledTrueAndPhone_shouldReturnCustomers() {
        when(customerRepository.findByEnabledAndPhone(1, 123456789)).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledTrueAndPhone(123456789);

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndPhone(1, 123456789);
    }

    @Test
    void findByEnabledTrueAndFirstName_shouldReturnCustomers() {
        when(customerRepository.findByEnabledAndFirstName(1, "John")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledTrueAndFirstName("John");

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndFirstName(1, "John");
    }

    @Test
    void findByEnabledTrueAndLastName_shouldReturnCustomers() {
        when(customerRepository.findByEnabledAndLastName(1, "Doe")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledTrueAndLastName("Doe");

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndLastName(1, "Doe");
    }

    @Test
    void findByEnabledTrueAndFirstNameAndLastName_shouldReturnCustomers() {
        when(customerRepository.findByEnabledAndFirstNameAndLastName(1, "John", "Doe"))
                .thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledTrueAndFirstNameAndLastName("John", "Doe");

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndFirstNameAndLastName(1, "John", "Doe");
    }

    @Test
    void findByEnabledTrueAndCity_shouldReturnCustomers() {
        when(customerRepository.findByEnabledAndCity(1, "New York")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledTrueAndCity("New York");

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCity(1, "New York");
    }

    @Test
    void findByEnabledTrueAndCityAndAddress_shouldReturnCustomers() {
        when(customerRepository.findByEnabledAndCityAndAddress(1, "New York", "123 Main St"))
                .thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledTrueAndCityAndAddress("New York", "123 Main St");

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCityAndAddress(1, "New York", "123 Main St");
    }

    @Test
    void findByEnabledTrueAndCategories_shouldReturnCustomers() {
        when(customerRepository.findByEnabledAndCategories(1, categories)).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledTrueAndCategories(categories);

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCategories(1, categories);
    }

    @Test
    void saveCustomer_shouldSetEnabledAndSave() {
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);

        customerService.saveCustomer(testCustomer);

        assertEquals(1, testCustomer.getEnabled());
        verify(customerRepository).save(testCustomer);
    }

    @Test
    void saveCustomer_shouldSetEnabledToOneEvenIfZero() {
        testCustomer.setEnabled(0);
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);

        customerService.saveCustomer(testCustomer);

        assertEquals(1, testCustomer.getEnabled());
        verify(customerRepository).save(testCustomer);
    }
}
