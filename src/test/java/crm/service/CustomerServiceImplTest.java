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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("VIP");

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
    }

    @Test
    void testConstructor() {
        CustomerServiceImpl service = new CustomerServiceImpl(customerRepository);
        assertNotNull(service);
    }

    @Test
    void testGetMaxId() {
        when(customerRepository.getMaxId()).thenReturn(5L);
        Long result = customerService.getMaxId();
        assertEquals(5L, result);
        verify(customerRepository).getMaxId();
    }

    @Test
    void testListAllCustomers() {
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer));
        Iterable<Customer> result = customerService.listAllCustomers();
        assertNotNull(result);
        verify(customerRepository).findAll();
    }

    @Test
    void testShowCustomer_returnsCustomer_whenFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        Customer result = customerService.showCustomer(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testShowCustomer_returnsNull_whenNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        Customer result = customerService.showCustomer(99L);
        assertNull(result);
    }

    @Test
    void testFindAllByEnabledTrue() {
        when(customerRepository.findAllByEnabled(1)).thenReturn(Arrays.asList(customer));
        Iterable<Customer> result = customerService.findAllByEnabledTrue();
        assertNotNull(result);
        verify(customerRepository).findAllByEnabled(1);
    }

    @Test
    void testFindAllByEnabledFalse() {
        when(customerRepository.findAllByEnabled(0)).thenReturn(Collections.emptyList());
        Iterable<Customer> result = customerService.findAllByEnabledFalse();
        assertNotNull(result);
        verify(customerRepository).findAllByEnabled(0);
    }

    @Test
    void testFindOneByEnabledTrueAndName() {
        when(customerRepository.findOneByEnabledAndName(1, "TestCo")).thenReturn(customer);
        Customer result = customerService.findOneByEnabledTrueAndName("TestCo");
        assertNotNull(result);
        assertEquals("TestCo", result.getName());
    }

    @Test
    void testFindOneByEnabledFalseAndName() {
        when(customerRepository.findOneByEnabledAndName(0, "TestCo")).thenReturn(null);
        Customer result = customerService.findOneByEnabledFalseAndName("TestCo");
        assertNull(result);
    }

    @Test
    void testFindOneByName() {
        when(customerRepository.findOneByName("TestCo")).thenReturn(customer);
        Customer result = customerService.findOneByName("TestCo");
        assertNotNull(result);
    }

    @Test
    void testFindByEnabledTrueAndEmail() {
        when(customerRepository.findByEnabledAndEmail(1, "test@example.com")).thenReturn(Arrays.asList(customer));
        Iterable<Customer> result = customerService.findByEnabledTrueAndEmail("test@example.com");
        assertNotNull(result);
    }

    @Test
    void testFindByEnabledFalseAndEmail() {
        when(customerRepository.findByEnabledAndEmail(0, "test@example.com")).thenReturn(Collections.emptyList());
        Iterable<Customer> result = customerService.findByEnabledFalseAndEmail("test@example.com");
        assertNotNull(result);
    }

    @Test
    void testFindByEmail() {
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Arrays.asList(customer));
        Iterable<Customer> result = customerService.findByEmail("test@example.com");
        assertNotNull(result);
    }

    @Test
    void testFindByEnabledTrueAndPhone() {
        when(customerRepository.findByEnabledAndPhone(1, 123456789)).thenReturn(Arrays.asList(customer));
        Iterable<Customer> result = customerService.findByEnabledTrueAndPhone(123456789);
        assertNotNull(result);
    }

    @Test
    void testFindByEnabledFalseAndPhone() {
        when(customerRepository.findByEnabledAndPhone(0, 123456789)).thenReturn(Collections.emptyList());
        Iterable<Customer> result = customerService.findByEnabledFalseAndPhone(123456789);
        assertNotNull(result);
    }

    @Test
    void testFindByPhone() {
        when(customerRepository.findByPhone(123456789)).thenReturn(Arrays.asList(customer));
        Iterable<Customer> result = customerService.findByPhone(123456789);
        assertNotNull(result);
    }

    @Test
    void testFindByEnabledTrueAndCategories() {
        Set<Category> categories = new HashSet<>(Arrays.asList(category));
        when(customerRepository.findByEnabledAndCategories(1, categories)).thenReturn(Arrays.asList(customer));
        Iterable<Customer> result = customerService.findByEnabledTrueAndCategories(categories);
        assertNotNull(result);
    }

    @Test
    void testFindByEnabledFalseAndCategories() {
        Set<Category> categories = new HashSet<>(Arrays.asList(category));
        when(customerRepository.findByEnabledAndCategories(0, categories)).thenReturn(Collections.emptyList());
        Iterable<Customer> result = customerService.findByEnabledFalseAndCategories(categories);
        assertNotNull(result);
    }

    @Test
    void testFindByCategories() {
        Set<Category> categories = new HashSet<>(Arrays.asList(category));
        when(customerRepository.findByCategories(categories)).thenReturn(Arrays.asList(customer));
        Iterable<Customer> result = customerService.findByCategories(categories);
        assertNotNull(result);
    }

    @Test
    void testFindByEnabledTrueAndFirstName() {
        when(customerRepository.findByEnabledAndFirstName(1, "John")).thenReturn(Arrays.asList(customer));
        Iterable<Customer> result = customerService.findByEnabledTrueAndFirstName("John");
        assertNotNull(result);
    }

    @Test
    void testFindByEnabledFalseAndFirstName() {
        when(customerRepository.findByEnabledAndFirstName(0, "John")).thenReturn(Collections.emptyList());
        Iterable<Customer> result = customerService.findByEnabledFalseAndFirstName("John");
        assertNotNull(result);
    }

    @Test
    void testFindByFirstName() {
        when(customerRepository.findByFirstName("John")).thenReturn(Arrays.asList(customer));
        Iterable<Customer> result = customerService.findByFirstName("John");
        assertNotNull(result);
    }

    @Test
    void testFindByEnabledTrueAndLastName() {
        when(customerRepository.findByEnabledAndLastName(1, "Doe")).thenReturn(Arrays.asList(customer));
        Iterable<Customer> result = customerService.findByEnabledTrueAndLastName("Doe");
        assertNotNull(result);
    }

    @Test
    void testFindByEnabledFalseAndLastName() {
        when(customerRepository.findByEnabledAndLastName(0, "Doe")).thenReturn(Collections.emptyList());
        Iterable<Customer> result = customerService.findByEnabledFalseAndLastName("Doe");
        assertNotNull(result);
    }

    @Test
    void testFindByLastName() {
        when(customerRepository.findByLastName("Doe")).thenReturn(Arrays.asList(customer));
        Iterable<Customer> result = customerService.findByLastName("Doe");
        assertNotNull(result);
    }

    @Test
    void testFindByEnabledTrueAndFirstNameAndLastName() {
        when(customerRepository.findByEnabledAndFirstNameAndLastName(1, "John", "Doe")).thenReturn(Arrays.asList(customer));
        Iterable<Customer> result = customerService.findByEnabledTrueAndFirstNameAndLastName("John", "Doe");
        assertNotNull(result);
    }

    @Test
    void testFindByEnabledFalseAndFirstNameAndLastName() {
        when(customerRepository.findByEnabledAndFirstNameAndLastName(0, "John", "Doe")).thenReturn(Collections.emptyList());
        Iterable<Customer> result = customerService.findByEnabledFalseAndFirstNameAndLastName("John", "Doe");
        assertNotNull(result);
    }

    @Test
    void testFindByFirstNameAndLastName() {
        when(customerRepository.findByFirstNameAndLastName("John", "Doe")).thenReturn(Arrays.asList(customer));
        Iterable<Customer> result = customerService.findByFirstNameAndLastName("John", "Doe");
        assertNotNull(result);
    }

    @Test
    void testFindByEnabledTrueAndCity() {
        when(customerRepository.findByEnabledAndCity(1, "NYC")).thenReturn(Arrays.asList(customer));
        Iterable<Customer> result = customerService.findByEnabledTrueAndCity("NYC");
        assertNotNull(result);
    }

    @Test
    void testFindByEnabledFalseAndCity() {
        when(customerRepository.findByEnabledAndCity(0, "NYC")).thenReturn(Collections.emptyList());
        Iterable<Customer> result = customerService.findByEnabledFalseAndCity("NYC");
        assertNotNull(result);
    }

    @Test
    void testFindByCity() {
        when(customerRepository.findByCity("NYC")).thenReturn(Arrays.asList(customer));
        Iterable<Customer> result = customerService.findByCity("NYC");
        assertNotNull(result);
    }

    @Test
    void testFindByEnabledTrueAndCityAndAddress() {
        when(customerRepository.findByEnabledAndCityAndAddress(1, "NYC", "123 Main St")).thenReturn(Arrays.asList(customer));
        Iterable<Customer> result = customerService.findByEnabledTrueAndCityAndAddress("NYC", "123 Main St");
        assertNotNull(result);
    }

    @Test
    void testFindByEnabledFalseAndCityAndAddress() {
        when(customerRepository.findByEnabledAndCityAndAddress(0, "NYC", "123 Main St")).thenReturn(Collections.emptyList());
        Iterable<Customer> result = customerService.findByEnabledFalseAndCityAndAddress("NYC", "123 Main St");
        assertNotNull(result);
    }

    @Test
    void testFindByCityAndAddress() {
        when(customerRepository.findByCityAndAddress("NYC", "123 Main St")).thenReturn(Arrays.asList(customer));
        Iterable<Customer> result = customerService.findByCityAndAddress("NYC", "123 Main St");
        assertNotNull(result);
    }

    @Test
    void testSaveCustomer_setsEnabledAndSaves() {
        Customer newCustomer = new Customer();
        newCustomer.setName("NewCo");
        customerService.saveCustomer(newCustomer);
        assertEquals(1, newCustomer.getEnabled());
        verify(customerRepository).save(newCustomer);
    }
}
