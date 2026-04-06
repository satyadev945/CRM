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
import java.util.List;
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
    private List<Customer> testCustomers;
    private Set<Category> testCategories;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Technology");

        testCategories = new HashSet<>();
        testCategories.add(category);

        testCustomer = Customer.builder()
                .id(1L)
                .name("Test Company")
                .email("test@example.com")
                .phone(123456789)
                .categories(testCategories)
                .firstName("John")
                .lastName("Doe")
                .city("New York")
                .address("123 Main St")
                .enabled(1)
                .build();

        testCustomers = Arrays.asList(testCustomer);
    }

    @Test
    void getMaxId_shouldReturnMaxId() {
        // Arrange
        when(customerRepository.getMaxId()).thenReturn(10L);

        // Act
        Long result = customerService.getMaxId();

        // Assert
        assertEquals(10L, result);
        verify(customerRepository).getMaxId();
    }

    @Test
    void listAllCustomers_shouldReturnAllCustomers() {
        // Arrange
        when(customerRepository.findAll()).thenReturn(testCustomers);

        // Act
        Iterable<Customer> result = customerService.listAllCustomers();

        // Assert
        assertNotNull(result);
        verify(customerRepository).findAll();
    }

    @Test
    void showCustomer_shouldReturnCustomer() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        // Act
        Customer result = customerService.showCustomer(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(customerRepository).findById(1L);
    }

    @Test
    void showCustomer_shouldReturnNull_whenNotFound() {
        // Arrange
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Customer result = customerService.showCustomer(999L);

        // Assert
        assertNull(result);
        verify(customerRepository).findById(999L);
    }

    @Test
    void findAllByEnabledTrue_shouldReturnEnabledCustomers() {
        // Arrange
        when(customerRepository.findAllByEnabled(1)).thenReturn(testCustomers);

        // Act
        Iterable<Customer> result = customerService.findAllByEnabledTrue();

        // Assert
        assertNotNull(result);
        verify(customerRepository).findAllByEnabled(1);
    }

    @Test
    void findAllByEnabledFalse_shouldReturnDisabledCustomers() {
        // Arrange
        when(customerRepository.findAllByEnabled(0)).thenReturn(Arrays.asList());

        // Act
        Iterable<Customer> result = customerService.findAllByEnabledFalse();

        // Assert
        assertNotNull(result);
        verify(customerRepository).findAllByEnabled(0);
    }

    @Test
    void findOneByEnabledTrueAndName_shouldReturnCustomer() {
        // Arrange
        when(customerRepository.findOneByEnabledAndName(1, "Test Company")).thenReturn(testCustomer);

        // Act
        Customer result = customerService.findOneByEnabledTrueAndName("Test Company");

        // Assert
        assertNotNull(result);
        assertEquals("Test Company", result.getName());
        verify(customerRepository).findOneByEnabledAndName(1, "Test Company");
    }

    @Test
    void findByEnabledTrueAndEmail_shouldReturnCustomers() {
        // Arrange
        when(customerRepository.findByEnabledAndEmail(1, "test@example.com")).thenReturn(testCustomers);

        // Act
        Iterable<Customer> result = customerService.findByEnabledTrueAndEmail("test@example.com");

        // Assert
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndEmail(1, "test@example.com");
    }

    @Test
    void findByEnabledTrueAndPhone_shouldReturnCustomers() {
        // Arrange
        when(customerRepository.findByEnabledAndPhone(1, 123456789)).thenReturn(testCustomers);

        // Act
        Iterable<Customer> result = customerService.findByEnabledTrueAndPhone(123456789);

        // Assert
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndPhone(1, 123456789);
    }

    @Test
    void findByEnabledTrueAndCategories_shouldReturnCustomers() {
        // Arrange
        when(customerRepository.findByEnabledAndCategories(1, testCategories)).thenReturn(testCustomers);

        // Act
        Iterable<Customer> result = customerService.findByEnabledTrueAndCategories(testCategories);

        // Assert
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCategories(1, testCategories);
    }

    @Test
    void findByEnabledTrueAndFirstName_shouldReturnCustomers() {
        // Arrange
        when(customerRepository.findByEnabledAndFirstName(1, "John")).thenReturn(testCustomers);

        // Act
        Iterable<Customer> result = customerService.findByEnabledTrueAndFirstName("John");

        // Assert
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndFirstName(1, "John");
    }

    @Test
    void findByEnabledTrueAndLastName_shouldReturnCustomers() {
        // Arrange
        when(customerRepository.findByEnabledAndLastName(1, "Doe")).thenReturn(testCustomers);

        // Act
        Iterable<Customer> result = customerService.findByEnabledTrueAndLastName("Doe");

        // Assert
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndLastName(1, "Doe");
    }

    @Test
    void findByEnabledTrueAndFirstNameAndLastName_shouldReturnCustomers() {
        // Arrange
        when(customerRepository.findByEnabledAndFirstNameAndLastName(1, "John", "Doe")).thenReturn(testCustomers);

        // Act
        Iterable<Customer> result = customerService.findByEnabledTrueAndFirstNameAndLastName("John", "Doe");

        // Assert
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndFirstNameAndLastName(1, "John", "Doe");
    }

    @Test
    void findByEnabledTrueAndCity_shouldReturnCustomers() {
        // Arrange
        when(customerRepository.findByEnabledAndCity(1, "New York")).thenReturn(testCustomers);

        // Act
        Iterable<Customer> result = customerService.findByEnabledTrueAndCity("New York");

        // Assert
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCity(1, "New York");
    }

    @Test
    void findByEnabledTrueAndCityAndAddress_shouldReturnCustomers() {
        // Arrange
        when(customerRepository.findByEnabledAndCityAndAddress(1, "New York", "123 Main St")).thenReturn(testCustomers);

        // Act
        Iterable<Customer> result = customerService.findByEnabledTrueAndCityAndAddress("New York", "123 Main St");

        // Assert
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCityAndAddress(1, "New York", "123 Main St");
    }

    @Test
    void saveCustomer_shouldSetEnabledAndSave() {
        // Arrange
        Customer newCustomer = Customer.builder()
                .name("New Company")
                .email("new@example.com")
                .enabled(0)
                .build();
        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);

        // Act
        customerService.saveCustomer(newCustomer);

        // Assert
        assertEquals(1, newCustomer.getEnabled());
        verify(customerRepository).save(newCustomer);
    }

    @Test
    void findOneByName_shouldReturnCustomer() {
        // Arrange
        when(customerRepository.findOneByName("Test Company")).thenReturn(testCustomer);

        // Act
        Customer result = customerService.findOneByName("Test Company");

        // Assert
        assertNotNull(result);
        assertEquals("Test Company", result.getName());
        verify(customerRepository).findOneByName("Test Company");
    }

    @Test
    void findByEmail_shouldReturnCustomers() {
        // Arrange
        when(customerRepository.findByEmail("test@example.com")).thenReturn(testCustomers);

        // Act
        Iterable<Customer> result = customerService.findByEmail("test@example.com");

        // Assert
        assertNotNull(result);
        verify(customerRepository).findByEmail("test@example.com");
    }

    @Test
    void findByPhone_shouldReturnCustomers() {
        // Arrange
        when(customerRepository.findByPhone(123456789)).thenReturn(testCustomers);

        // Act
        Iterable<Customer> result = customerService.findByPhone(123456789);

        // Assert
        assertNotNull(result);
        verify(customerRepository).findByPhone(123456789);
    }
}
