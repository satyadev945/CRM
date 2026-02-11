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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        categories = new HashSet<>();
        Category category = new Category();
        category.setId(1L);
        category.setName("VIP");
        categories.add(category);

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
                .categories(categories)
                .build();
    }

    @Test
    void testGetMaxId() {
        when(customerRepository.getMaxId()).thenReturn(100L);

        Long result = customerService.getMaxId();

        assertEquals(100L, result);
        verify(customerRepository).getMaxId();
    }

    @Test
    void testListAllCustomers() {
        when(customerRepository.findAll()).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.listAllCustomers();

        assertNotNull(result);
        verify(customerRepository).findAll();
    }

    @Test
    void testShowCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        Customer result = customerService.showCustomer(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(customerRepository).findById(1L);
    }

    @Test
    void testShowCustomerNotFound() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        Customer result = customerService.showCustomer(999L);

        assertNull(result);
        verify(customerRepository).findById(999L);
    }

    @Test
    void testFindAllByEnabledTrue() {
        when(customerRepository.findAllByEnabled(1)).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findAllByEnabledTrue();

        assertNotNull(result);
        verify(customerRepository).findAllByEnabled(1);
    }

    @Test
    void testFindAllByEnabledFalse() {
        when(customerRepository.findAllByEnabled(0)).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findAllByEnabledFalse();

        assertNotNull(result);
        verify(customerRepository).findAllByEnabled(0);
    }

    @Test
    void testFindOneByEnabledTrueAndName() {
        when(customerRepository.findOneByEnabledAndName(1, "Acme Corp")).thenReturn(testCustomer);

        Customer result = customerService.findOneByEnabledTrueAndName("Acme Corp");

        assertNotNull(result);
        assertEquals("Acme Corp", result.getName());
        verify(customerRepository).findOneByEnabledAndName(1, "Acme Corp");
    }

    @Test
    void testFindOneByEnabledFalseAndName() {
        when(customerRepository.findOneByEnabledAndName(0, "Acme Corp")).thenReturn(testCustomer);

        Customer result = customerService.findOneByEnabledFalseAndName("Acme Corp");

        assertNotNull(result);
        verify(customerRepository).findOneByEnabledAndName(0, "Acme Corp");
    }

    @Test
    void testFindOneByName() {
        when(customerRepository.findOneByName("Acme Corp")).thenReturn(testCustomer);

        Customer result = customerService.findOneByName("Acme Corp");

        assertNotNull(result);
        assertEquals("Acme Corp", result.getName());
        verify(customerRepository).findOneByName("Acme Corp");
    }

    @Test
    void testFindByEnabledTrueAndEmail() {
        when(customerRepository.findByEnabledAndEmail(1, "contact@acme.com")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledTrueAndEmail("contact@acme.com");

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndEmail(1, "contact@acme.com");
    }

    @Test
    void testFindByEnabledFalseAndEmail() {
        when(customerRepository.findByEnabledAndEmail(0, "contact@acme.com")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledFalseAndEmail("contact@acme.com");

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndEmail(0, "contact@acme.com");
    }

    @Test
    void testFindByEmail() {
        when(customerRepository.findByEmail("contact@acme.com")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEmail("contact@acme.com");

        assertNotNull(result);
        verify(customerRepository).findByEmail("contact@acme.com");
    }

    @Test
    void testFindByEnabledTrueAndPhone() {
        when(customerRepository.findByEnabledAndPhone(1, 123456789)).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledTrueAndPhone(123456789);

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndPhone(1, 123456789);
    }

    @Test
    void testFindByEnabledFalseAndPhone() {
        when(customerRepository.findByEnabledAndPhone(0, 123456789)).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledFalseAndPhone(123456789);

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndPhone(0, 123456789);
    }

    @Test
    void testFindByPhone() {
        when(customerRepository.findByPhone(123456789)).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByPhone(123456789);

        assertNotNull(result);
        verify(customerRepository).findByPhone(123456789);
    }

    @Test
    void testFindByEnabledTrueAndCategories() {
        when(customerRepository.findByEnabledAndCategories(1, categories)).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledTrueAndCategories(categories);

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCategories(1, categories);
    }

    @Test
    void testFindByEnabledFalseAndCategories() {
        when(customerRepository.findByEnabledAndCategories(0, categories)).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledFalseAndCategories(categories);

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCategories(0, categories);
    }

    @Test
    void testFindByCategories() {
        when(customerRepository.findByCategories(categories)).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByCategories(categories);

        assertNotNull(result);
        verify(customerRepository).findByCategories(categories);
    }

    @Test
    void testFindByEnabledTrueAndFirstName() {
        when(customerRepository.findByEnabledAndFirstName(1, "John")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledTrueAndFirstName("John");

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndFirstName(1, "John");
    }

    @Test
    void testFindByEnabledFalseAndFirstName() {
        when(customerRepository.findByEnabledAndFirstName(0, "John")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledFalseAndFirstName("John");

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndFirstName(0, "John");
    }

    @Test
    void testFindByFirstName() {
        when(customerRepository.findByFirstName("John")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByFirstName("John");

        assertNotNull(result);
        verify(customerRepository).findByFirstName("John");
    }

    @Test
    void testFindByEnabledTrueAndLastName() {
        when(customerRepository.findByEnabledAndLastName(1, "Smith")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledTrueAndLastName("Smith");

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndLastName(1, "Smith");
    }

    @Test
    void testFindByEnabledFalseAndLastName() {
        when(customerRepository.findByEnabledAndLastName(0, "Smith")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledFalseAndLastName("Smith");

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndLastName(0, "Smith");
    }

    @Test
    void testFindByLastName() {
        when(customerRepository.findByLastName("Smith")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByLastName("Smith");

        assertNotNull(result);
        verify(customerRepository).findByLastName("Smith");
    }

    @Test
    void testFindByEnabledTrueAndFirstNameAndLastName() {
        when(customerRepository.findByEnabledAndFirstNameAndLastName(1, "John", "Smith")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledTrueAndFirstNameAndLastName("John", "Smith");

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndFirstNameAndLastName(1, "John", "Smith");
    }

    @Test
    void testFindByEnabledFalseAndFirstNameAndLastName() {
        when(customerRepository.findByEnabledAndFirstNameAndLastName(0, "John", "Smith")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledFalseAndFirstNameAndLastName("John", "Smith");

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndFirstNameAndLastName(0, "John", "Smith");
    }

    @Test
    void testFindByFirstNameAndLastName() {
        when(customerRepository.findByFirstNameAndLastName("John", "Smith")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByFirstNameAndLastName("John", "Smith");

        assertNotNull(result);
        verify(customerRepository).findByFirstNameAndLastName("John", "Smith");
    }

    @Test
    void testFindByEnabledTrueAndCity() {
        when(customerRepository.findByEnabledAndCity(1, "New York")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledTrueAndCity("New York");

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCity(1, "New York");
    }

    @Test
    void testFindByEnabledFalseAndCity() {
        when(customerRepository.findByEnabledAndCity(0, "New York")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledFalseAndCity("New York");

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCity(0, "New York");
    }

    @Test
    void testFindByCity() {
        when(customerRepository.findByCity("New York")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByCity("New York");

        assertNotNull(result);
        verify(customerRepository).findByCity("New York");
    }

    @Test
    void testFindByEnabledTrueAndCityAndAddress() {
        when(customerRepository.findByEnabledAndCityAndAddress(1, "New York", "123 Main St")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledTrueAndCityAndAddress("New York", "123 Main St");

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCityAndAddress(1, "New York", "123 Main St");
    }

    @Test
    void testFindByEnabledFalseAndCityAndAddress() {
        when(customerRepository.findByEnabledAndCityAndAddress(0, "New York", "123 Main St")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByEnabledFalseAndCityAndAddress("New York", "123 Main St");

        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCityAndAddress(0, "New York", "123 Main St");
    }

    @Test
    void testFindByCityAndAddress() {
        when(customerRepository.findByCityAndAddress("New York", "123 Main St")).thenReturn(Arrays.asList(testCustomer));

        Iterable<Customer> result = customerService.findByCityAndAddress("New York", "123 Main St");

        assertNotNull(result);
        verify(customerRepository).findByCityAndAddress("New York", "123 Main St");
    }

    @Test
    void testSaveCustomer() {
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        customerService.saveCustomer(testCustomer);

        assertEquals(1, testCustomer.getEnabled());
        verify(customerRepository).save(testCustomer);
    }

    @Test
    void testSaveCustomerSetsEnabled() {
        testCustomer.setEnabled(0);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        customerService.saveCustomer(testCustomer);

        assertEquals(1, testCustomer.getEnabled());
        verify(customerRepository).save(testCustomer);
    }
}
