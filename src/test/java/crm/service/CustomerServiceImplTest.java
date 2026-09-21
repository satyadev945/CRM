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
    private Set<Category> categories;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("VIP");

        categories = new HashSet<>();
        categories.add(category);

        customer = new Customer();
        customer.setId(1L);
        customer.setName("Acme Corp");
        customer.setEmail("acme@example.com");
        customer.setPhone(123456789);
        customer.setCategories(categories);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setCity("New York");
        customer.setAddress("123 Main St");
        customer.setEnabled(1);
    }

    @Test
    void testConstructor() {
        CustomerServiceImpl service = new CustomerServiceImpl(customerRepository);
        assertNotNull(service);
    }

    @Test
    void testGetMaxId() {
        when(customerRepository.getMaxId()).thenReturn(5L);
        Long maxId = customerService.getMaxId();
        assertEquals(5L, maxId);
        verify(customerRepository).getMaxId();
    }

    @Test
    void testListAllCustomers() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findAll()).thenReturn(customers);
        Iterable<Customer> result = customerService.listAllCustomers();
        assertNotNull(result);
        verify(customerRepository).findAll();
    }

    @Test
    void testShowCustomer_found() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        Customer result = customerService.showCustomer(1L);
        assertNotNull(result);
        assertEquals(customer, result);
        verify(customerRepository).findById(1L);
    }

    @Test
    void testShowCustomer_notFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        Customer result = customerService.showCustomer(99L);
        assertNull(result);
        verify(customerRepository).findById(99L);
    }

    @Test
    void testFindAllByEnabledTrue() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findAllByEnabled(1)).thenReturn(customers);
        Iterable<Customer> result = customerService.findAllByEnabledTrue();
        assertNotNull(result);
        verify(customerRepository).findAllByEnabled(1);
    }

    @Test
    void testFindAllByEnabledFalse() {
        List<Customer> customers = new ArrayList<>();
        when(customerRepository.findAllByEnabled(0)).thenReturn(customers);
        Iterable<Customer> result = customerService.findAllByEnabledFalse();
        assertNotNull(result);
        verify(customerRepository).findAllByEnabled(0);
    }

    @Test
    void testFindOneByEnabledTrueAndName() {
        when(customerRepository.findOneByEnabledAndName(1, "Acme Corp")).thenReturn(customer);
        Customer result = customerService.findOneByEnabledTrueAndName("Acme Corp");
        assertEquals(customer, result);
        verify(customerRepository).findOneByEnabledAndName(1, "Acme Corp");
    }

    @Test
    void testFindOneByEnabledFalseAndName() {
        when(customerRepository.findOneByEnabledAndName(0, "Acme Corp")).thenReturn(null);
        Customer result = customerService.findOneByEnabledFalseAndName("Acme Corp");
        assertNull(result);
        verify(customerRepository).findOneByEnabledAndName(0, "Acme Corp");
    }

    @Test
    void testFindOneByName() {
        when(customerRepository.findOneByName("Acme Corp")).thenReturn(customer);
        Customer result = customerService.findOneByName("Acme Corp");
        assertEquals(customer, result);
        verify(customerRepository).findOneByName("Acme Corp");
    }

    @Test
    void testFindByEnabledTrueAndEmail() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findByEnabledAndEmail(1, "acme@example.com")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledTrueAndEmail("acme@example.com");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndEmail(1, "acme@example.com");
    }

    @Test
    void testFindByEnabledFalseAndEmail() {
        List<Customer> customers = new ArrayList<>();
        when(customerRepository.findByEnabledAndEmail(0, "acme@example.com")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledFalseAndEmail("acme@example.com");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndEmail(0, "acme@example.com");
    }

    @Test
    void testFindByEmail() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findByEmail("acme@example.com")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEmail("acme@example.com");
        assertNotNull(result);
        verify(customerRepository).findByEmail("acme@example.com");
    }

    @Test
    void testFindByEnabledTrueAndPhone() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findByEnabledAndPhone(1, 123456789)).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledTrueAndPhone(123456789);
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndPhone(1, 123456789);
    }

    @Test
    void testFindByEnabledFalseAndPhone() {
        List<Customer> customers = new ArrayList<>();
        when(customerRepository.findByEnabledAndPhone(0, 123456789)).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledFalseAndPhone(123456789);
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndPhone(0, 123456789);
    }

    @Test
    void testFindByPhone() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findByPhone(123456789)).thenReturn(customers);
        Iterable<Customer> result = customerService.findByPhone(123456789);
        assertNotNull(result);
        verify(customerRepository).findByPhone(123456789);
    }

    @Test
    void testFindByEnabledTrueAndCategories() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findByEnabledAndCategories(1, categories)).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledTrueAndCategories(categories);
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCategories(1, categories);
    }

    @Test
    void testFindByEnabledFalseAndCategories() {
        List<Customer> customers = new ArrayList<>();
        when(customerRepository.findByEnabledAndCategories(0, categories)).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledFalseAndCategories(categories);
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCategories(0, categories);
    }

    @Test
    void testFindByCategories() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findByCategories(categories)).thenReturn(customers);
        Iterable<Customer> result = customerService.findByCategories(categories);
        assertNotNull(result);
        verify(customerRepository).findByCategories(categories);
    }

    @Test
    void testFindByEnabledTrueAndFirstName() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findByEnabledAndFirstName(1, "John")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledTrueAndFirstName("John");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndFirstName(1, "John");
    }

    @Test
    void testFindByEnabledFalseAndFirstName() {
        List<Customer> customers = new ArrayList<>();
        when(customerRepository.findByEnabledAndFirstName(0, "John")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledFalseAndFirstName("John");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndFirstName(0, "John");
    }

    @Test
    void testFindByFirstName() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findByFirstName("John")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByFirstName("John");
        assertNotNull(result);
        verify(customerRepository).findByFirstName("John");
    }

    @Test
    void testFindByEnabledTrueAndLastName() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findByEnabledAndLastName(1, "Doe")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledTrueAndLastName("Doe");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndLastName(1, "Doe");
    }

    @Test
    void testFindByEnabledFalseAndLastName() {
        List<Customer> customers = new ArrayList<>();
        when(customerRepository.findByEnabledAndLastName(0, "Doe")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledFalseAndLastName("Doe");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndLastName(0, "Doe");
    }

    @Test
    void testFindByLastName() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findByLastName("Doe")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByLastName("Doe");
        assertNotNull(result);
        verify(customerRepository).findByLastName("Doe");
    }

    @Test
    void testFindByEnabledTrueAndFirstNameAndLastName() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findByEnabledAndFirstNameAndLastName(1, "John", "Doe")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledTrueAndFirstNameAndLastName("John", "Doe");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndFirstNameAndLastName(1, "John", "Doe");
    }

    @Test
    void testFindByEnabledFalseAndFirstNameAndLastName() {
        List<Customer> customers = new ArrayList<>();
        when(customerRepository.findByEnabledAndFirstNameAndLastName(0, "John", "Doe")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledFalseAndFirstNameAndLastName("John", "Doe");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndFirstNameAndLastName(0, "John", "Doe");
    }

    @Test
    void testFindByFirstNameAndLastName() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findByFirstNameAndLastName("John", "Doe")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByFirstNameAndLastName("John", "Doe");
        assertNotNull(result);
        verify(customerRepository).findByFirstNameAndLastName("John", "Doe");
    }

    @Test
    void testFindByEnabledTrueAndCity() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findByEnabledAndCity(1, "New York")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledTrueAndCity("New York");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCity(1, "New York");
    }

    @Test
    void testFindByEnabledFalseAndCity() {
        List<Customer> customers = new ArrayList<>();
        when(customerRepository.findByEnabledAndCity(0, "New York")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledFalseAndCity("New York");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCity(0, "New York");
    }

    @Test
    void testFindByCity() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findByCity("New York")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByCity("New York");
        assertNotNull(result);
        verify(customerRepository).findByCity("New York");
    }

    @Test
    void testFindByEnabledTrueAndCityAndAddress() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findByEnabledAndCityAndAddress(1, "New York", "123 Main St")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledTrueAndCityAndAddress("New York", "123 Main St");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCityAndAddress(1, "New York", "123 Main St");
    }

    @Test
    void testFindByEnabledFalseAndCityAndAddress() {
        List<Customer> customers = new ArrayList<>();
        when(customerRepository.findByEnabledAndCityAndAddress(0, "New York", "123 Main St")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledFalseAndCityAndAddress("New York", "123 Main St");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCityAndAddress(0, "New York", "123 Main St");
    }

    @Test
    void testFindByCityAndAddress() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findByCityAndAddress("New York", "123 Main St")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByCityAndAddress("New York", "123 Main St");
        assertNotNull(result);
        verify(customerRepository).findByCityAndAddress("New York", "123 Main St");
    }

    @Test
    void testSaveCustomer() {
        Customer newCustomer = new Customer();
        newCustomer.setName("New Corp");
        newCustomer.setEmail("new@example.com");
        customerService.saveCustomer(newCustomer);
        assertEquals(1, newCustomer.getEnabled());
        verify(customerRepository).save(newCustomer);
    }

    @Test
    void testSaveCustomer_setsEnabledToOne() {
        Customer newCustomer = new Customer();
        newCustomer.setEnabled(0);
        customerService.saveCustomer(newCustomer);
        assertEquals(1, newCustomer.getEnabled());
    }
}
