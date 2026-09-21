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
                .city("LA").address("Sunset Blvd").enabled(0).build();
    }

    @Test
    void testGetMaxId_ReturnsMaxId() {
        when(customerRepository.getMaxId()).thenReturn(10L);
        Long result = customerService.getMaxId();
        assertEquals(10L, result);
        verify(customerRepository).getMaxId();
    }

    @Test
    void testListAllCustomers_ReturnsAllCustomers() {
        List<Customer> customers = Arrays.asList(customer1, customer2);
        when(customerRepository.findAll()).thenReturn(customers);
        Iterable<Customer> result = customerService.listAllCustomers();
        assertNotNull(result);
        verify(customerRepository).findAll();
    }

    @Test
    void testShowCustomer_ExistingId_ReturnsCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));
        Customer result = customerService.showCustomer(1L);
        assertNotNull(result);
        assertEquals(customer1, result);
        verify(customerRepository).findById(1L);
    }

    @Test
    void testShowCustomer_NonExistingId_ReturnsNull() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        Customer result = customerService.showCustomer(99L);
        assertNull(result);
        verify(customerRepository).findById(99L);
    }

    @Test
    void testFindAllByEnabledTrue_ReturnsEnabledCustomers() {
        List<Customer> enabled = Collections.singletonList(customer1);
        when(customerRepository.findAllByEnabled(1)).thenReturn(enabled);
        Iterable<Customer> result = customerService.findAllByEnabledTrue();
        assertNotNull(result);
        verify(customerRepository).findAllByEnabled(1);
    }

    @Test
    void testFindAllByEnabledFalse_ReturnsDisabledCustomers() {
        List<Customer> disabled = Collections.singletonList(customer2);
        when(customerRepository.findAllByEnabled(0)).thenReturn(disabled);
        Iterable<Customer> result = customerService.findAllByEnabledFalse();
        assertNotNull(result);
        verify(customerRepository).findAllByEnabled(0);
    }

    @Test
    void testFindOneByEnabledTrueAndName_ReturnsCustomer() {
        when(customerRepository.findOneByEnabledAndName(1, "Corp A")).thenReturn(customer1);
        Customer result = customerService.findOneByEnabledTrueAndName("Corp A");
        assertEquals(customer1, result);
        verify(customerRepository).findOneByEnabledAndName(1, "Corp A");
    }

    @Test
    void testFindOneByEnabledFalseAndName_ReturnsCustomer() {
        when(customerRepository.findOneByEnabledAndName(0, "Corp B")).thenReturn(customer2);
        Customer result = customerService.findOneByEnabledFalseAndName("Corp B");
        assertEquals(customer2, result);
        verify(customerRepository).findOneByEnabledAndName(0, "Corp B");
    }

    @Test
    void testFindOneByName_ReturnsCustomer() {
        when(customerRepository.findOneByName("Corp A")).thenReturn(customer1);
        Customer result = customerService.findOneByName("Corp A");
        assertEquals(customer1, result);
        verify(customerRepository).findOneByName("Corp A");
    }

    @Test
    void testFindByEnabledTrueAndEmail_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer1);
        when(customerRepository.findByEnabledAndEmail(1, "a@corp.com")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledTrueAndEmail("a@corp.com");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndEmail(1, "a@corp.com");
    }

    @Test
    void testFindByEnabledFalseAndEmail_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer2);
        when(customerRepository.findByEnabledAndEmail(0, "b@corp.com")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledFalseAndEmail("b@corp.com");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndEmail(0, "b@corp.com");
    }

    @Test
    void testFindByEmail_ReturnsCustomers() {
        List<Customer> customers = Arrays.asList(customer1, customer2);
        when(customerRepository.findByEmail("a@corp.com")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEmail("a@corp.com");
        assertNotNull(result);
        verify(customerRepository).findByEmail("a@corp.com");
    }

    @Test
    void testFindByEnabledTrueAndPhone_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer1);
        when(customerRepository.findByEnabledAndPhone(1, 111222333)).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledTrueAndPhone(111222333);
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndPhone(1, 111222333);
    }

    @Test
    void testFindByEnabledFalseAndPhone_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer2);
        when(customerRepository.findByEnabledAndPhone(0, 444555666)).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledFalseAndPhone(444555666);
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndPhone(0, 444555666);
    }

    @Test
    void testFindByPhone_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer1);
        when(customerRepository.findByPhone(111222333)).thenReturn(customers);
        Iterable<Customer> result = customerService.findByPhone(111222333);
        assertNotNull(result);
        verify(customerRepository).findByPhone(111222333);
    }

    @Test
    void testFindByEnabledTrueAndCategories_ReturnsCustomers() {
        Set<Category> categories = new HashSet<>();
        Category cat = new Category();
        cat.setId(1L);
        cat.setName("VIP");
        categories.add(cat);
        List<Customer> customers = Collections.singletonList(customer1);
        when(customerRepository.findByEnabledAndCategories(1, categories)).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledTrueAndCategories(categories);
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCategories(1, categories);
    }

    @Test
    void testFindByEnabledFalseAndCategories_ReturnsCustomers() {
        Set<Category> categories = new HashSet<>();
        List<Customer> customers = Collections.singletonList(customer2);
        when(customerRepository.findByEnabledAndCategories(0, categories)).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledFalseAndCategories(categories);
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCategories(0, categories);
    }

    @Test
    void testFindByCategories_ReturnsCustomers() {
        Set<Category> categories = new HashSet<>();
        List<Customer> customers = Arrays.asList(customer1, customer2);
        when(customerRepository.findByCategories(categories)).thenReturn(customers);
        Iterable<Customer> result = customerService.findByCategories(categories);
        assertNotNull(result);
        verify(customerRepository).findByCategories(categories);
    }

    @Test
    void testFindByEnabledTrueAndFirstName_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer1);
        when(customerRepository.findByEnabledAndFirstName(1, "John")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledTrueAndFirstName("John");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndFirstName(1, "John");
    }

    @Test
    void testFindByEnabledFalseAndFirstName_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer2);
        when(customerRepository.findByEnabledAndFirstName(0, "Jane")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledFalseAndFirstName("Jane");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndFirstName(0, "Jane");
    }

    @Test
    void testFindByFirstName_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer1);
        when(customerRepository.findByFirstName("John")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByFirstName("John");
        assertNotNull(result);
        verify(customerRepository).findByFirstName("John");
    }

    @Test
    void testFindByEnabledTrueAndLastName_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer1);
        when(customerRepository.findByEnabledAndLastName(1, "Doe")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledTrueAndLastName("Doe");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndLastName(1, "Doe");
    }

    @Test
    void testFindByEnabledFalseAndLastName_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer2);
        when(customerRepository.findByEnabledAndLastName(0, "Smith")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledFalseAndLastName("Smith");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndLastName(0, "Smith");
    }

    @Test
    void testFindByLastName_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer1);
        when(customerRepository.findByLastName("Doe")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByLastName("Doe");
        assertNotNull(result);
        verify(customerRepository).findByLastName("Doe");
    }

    @Test
    void testFindByEnabledTrueAndFirstNameAndLastName_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer1);
        when(customerRepository.findByEnabledAndFirstNameAndLastName(1, "John", "Doe")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledTrueAndFirstNameAndLastName("John", "Doe");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndFirstNameAndLastName(1, "John", "Doe");
    }

    @Test
    void testFindByEnabledFalseAndFirstNameAndLastName_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer2);
        when(customerRepository.findByEnabledAndFirstNameAndLastName(0, "Jane", "Smith")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledFalseAndFirstNameAndLastName("Jane", "Smith");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndFirstNameAndLastName(0, "Jane", "Smith");
    }

    @Test
    void testFindByFirstNameAndLastName_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer1);
        when(customerRepository.findByFirstNameAndLastName("John", "Doe")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByFirstNameAndLastName("John", "Doe");
        assertNotNull(result);
        verify(customerRepository).findByFirstNameAndLastName("John", "Doe");
    }

    @Test
    void testFindByEnabledTrueAndCity_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer1);
        when(customerRepository.findByEnabledAndCity(1, "NYC")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledTrueAndCity("NYC");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCity(1, "NYC");
    }

    @Test
    void testFindByEnabledFalseAndCity_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer2);
        when(customerRepository.findByEnabledAndCity(0, "LA")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledFalseAndCity("LA");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCity(0, "LA");
    }

    @Test
    void testFindByCity_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer1);
        when(customerRepository.findByCity("NYC")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByCity("NYC");
        assertNotNull(result);
        verify(customerRepository).findByCity("NYC");
    }

    @Test
    void testFindByEnabledTrueAndCityAndAddress_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer1);
        when(customerRepository.findByEnabledAndCityAndAddress(1, "NYC", "5th Ave")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledTrueAndCityAndAddress("NYC", "5th Ave");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCityAndAddress(1, "NYC", "5th Ave");
    }

    @Test
    void testFindByEnabledFalseAndCityAndAddress_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer2);
        when(customerRepository.findByEnabledAndCityAndAddress(0, "LA", "Sunset Blvd")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByEnabledFalseAndCityAndAddress("LA", "Sunset Blvd");
        assertNotNull(result);
        verify(customerRepository).findByEnabledAndCityAndAddress(0, "LA", "Sunset Blvd");
    }

    @Test
    void testFindByCityAndAddress_ReturnsCustomers() {
        List<Customer> customers = Collections.singletonList(customer1);
        when(customerRepository.findByCityAndAddress("NYC", "5th Ave")).thenReturn(customers);
        Iterable<Customer> result = customerService.findByCityAndAddress("NYC", "5th Ave");
        assertNotNull(result);
        verify(customerRepository).findByCityAndAddress("NYC", "5th Ave");
    }

    @Test
    void testSaveCustomer_SetsEnabledAndSaves() {
        Customer newCustomer = new Customer();
        newCustomer.setName("New Corp");
        newCustomer.setEmail("new@corp.com");

        customerService.saveCustomer(newCustomer);

        assertEquals(1, newCustomer.getEnabled());
        verify(customerRepository).save(newCustomer);
    }

    @Test
    void testSaveCustomer_OverridesEnabled() {
        Customer c = new Customer();
        c.setEnabled(0);
        customerService.saveCustomer(c);
        assertEquals(1, c.getEnabled());
        verify(customerRepository).save(c);
    }
}
