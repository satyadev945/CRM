package crm.service;

import crm.entity.Category;
import crm.entity.Customer;
import crm.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerServiceImpl customerService;
    private Customer testCustomer;
    private Set<Category> categories;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(customerRepository);

        // Create category
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Retail");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Corporate");

        categories = new HashSet<>();
        categories.add(category1);
        categories.add(category2);

        // Create test customer
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Company");
        testCustomer.setEmail("contact@testcompany.com");
        testCustomer.setPhone(123456789);
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setCity("New York");
        testCustomer.setAddress("123 Test St");
        testCustomer.setEnabled(1);
        testCustomer.setCategories(categories);
    }

    @Test
    void getMaxId_ReturnsMaxId() {
        // Arrange
        when(customerRepository.getMaxId()).thenReturn(10L);

        // Act
        Long result = customerService.getMaxId();

        // Assert
        assertEquals(10L, result);
        verify(customerRepository).getMaxId();
    }

    @Test
    void listAllCustomers_ReturnsAllCustomers() {
        // Arrange
        List<Customer> customers = new ArrayList<>();
        customers.add(testCustomer);
        when(customerRepository.findAll()).thenReturn(customers);

        // Act
        Iterable<Customer> result = customerService.listAllCustomers();

        // Assert
        assertEquals(customers, result);
        verify(customerRepository).findAll();
    }

    @Test
    void showCustomer_ExistingCustomer_ReturnsCustomer() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        // Act
        Customer result = customerService.showCustomer(1L);

        // Assert
        assertEquals(testCustomer, result);
        verify(customerRepository).findById(1L);
    }

    @Test
    void showCustomer_NonExistingCustomer_ReturnsNull() {
        // Arrange
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Customer result = customerService.showCustomer(999L);

        // Assert
        assertNull(result);
        verify(customerRepository).findById(999L);
    }

    @Test
    void findAllByEnabledTrue_ReturnsEnabledCustomers() {
        // Arrange
        List<Customer> enabledCustomers = new ArrayList<>();
        enabledCustomers.add(testCustomer);
        when(customerRepository.findAllByEnabled(1)).thenReturn(enabledCustomers);

        // Act
        Iterable<Customer> result = customerService.findAllByEnabledTrue();

        // Assert
        assertEquals(enabledCustomers, result);
        verify(customerRepository).findAllByEnabled(1);
    }

    @Test
    void findAllByEnabledFalse_ReturnsDisabledCustomers() {
        // Arrange
        List<Customer> disabledCustomers = new ArrayList<>();
        testCustomer.setEnabled(0);
        disabledCustomers.add(testCustomer);
        when(customerRepository.findAllByEnabled(0)).thenReturn(disabledCustomers);

        // Act
        Iterable<Customer> result = customerService.findAllByEnabledFalse();

        // Assert
        assertEquals(disabledCustomers, result);
        verify(customerRepository).findAllByEnabled(0);
    }

    @Test
    void findOneByEnabledTrueAndName_ReturnsEnabledCustomer() {
        // Arrange
        when(customerRepository.findOneByEnabledAndName(1, "Test Company")).thenReturn(testCustomer);

        // Act
        Customer result = customerService.findOneByEnabledTrueAndName("Test Company");

        // Assert
        assertEquals(testCustomer, result);
        verify(customerRepository).findOneByEnabledAndName(1, "Test Company");
    }

    @Test
    void findOneByEnabledFalseAndName_ReturnsDisabledCustomer() {
        // Arrange
        testCustomer.setEnabled(0);
        when(customerRepository.findOneByEnabledAndName(0, "Test Company")).thenReturn(testCustomer);

        // Act
        Customer result = customerService.findOneByEnabledFalseAndName("Test Company");

        // Assert
        assertEquals(testCustomer, result);
        verify(customerRepository).findOneByEnabledAndName(0, "Test Company");
    }

    @Test
    void findOneByName_ReturnsCustomer() {
        // Arrange
        when(customerRepository.findOneByName("Test Company")).thenReturn(testCustomer);

        // Act
        Customer result = customerService.findOneByName("Test Company");

        // Assert
        assertEquals(testCustomer, result);
        verify(customerRepository).findOneByName("Test Company");
    }

    @Test
    void findByEnabledTrueAndEmail_ReturnsEnabledCustomers() {
        // Arrange
        List<Customer> enabledCustomers = new ArrayList<>();
        enabledCustomers.add(testCustomer);
        when(customerRepository.findByEnabledAndEmail(1, "contact@testcompany.com")).thenReturn(enabledCustomers);

        // Act
        Iterable<Customer> result = customerService.findByEnabledTrueAndEmail("contact@testcompany.com");

        // Assert
        assertEquals(enabledCustomers, result);
        verify(customerRepository).findByEnabledAndEmail(1, "contact@testcompany.com");
    }

    @Test
    void saveCustomer_SetsEnabledAndSaves() {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setName("New Company");
        newCustomer.setEmail("new@company.com");

        // Act
        customerService.saveCustomer(newCustomer);

        // Assert
        assertEquals(1, newCustomer.getEnabled());
        verify(customerRepository).save(newCustomer);
    }

    // Additional tests for the many finder methods
    @Test
    void findByCategories_ReturnsCustomersWithCategories() {
        // Arrange
        List<Customer> customerList = Collections.singletonList(testCustomer);
        when(customerRepository.findByCategories(categories)).thenReturn(customerList);

        // Act
        Iterable<Customer> result = customerService.findByCategories(categories);

        // Assert
        assertEquals(customerList, result);
        verify(customerRepository).findByCategories(categories);
    }

    @Test
    void findByCity_ReturnsCustomersInCity() {
        // Arrange
        List<Customer> customerList = Collections.singletonList(testCustomer);
        when(customerRepository.findByCity("New York")).thenReturn(customerList);

        // Act
        Iterable<Customer> result = customerService.findByCity("New York");

        // Assert
        assertEquals(customerList, result);
        verify(customerRepository).findByCity("New York");
    }

    @Test
    void findByFirstNameAndLastName_ReturnsCustomersWithName() {
        // Arrange
        List<Customer> customerList = Collections.singletonList(testCustomer);
        when(customerRepository.findByFirstNameAndLastName("John", "Doe")).thenReturn(customerList);

        // Act
        Iterable<Customer> result = customerService.findByFirstNameAndLastName("John", "Doe");

        // Assert
        assertEquals(customerList, result);
        verify(customerRepository).findByFirstNameAndLastName("John", "Doe");
    }
}