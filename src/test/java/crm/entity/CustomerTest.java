package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerTest {

    private Customer customer;
    private Set<Category> categories;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        categories = new HashSet<>();
    }

    @Test
    void testCustomerBuilder() {
        Category category = new Category();
        category.setId(1L);
        category.setName("VIP");
        categories.add(category);

        Customer builtCustomer = Customer.builder()
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

        assertNotNull(builtCustomer);
        assertEquals(1L, builtCustomer.getId());
        assertEquals("Acme Corp", builtCustomer.getName());
        assertEquals("contact@acme.com", builtCustomer.getEmail());
        assertEquals(123456789, builtCustomer.getPhone());
        assertEquals("John", builtCustomer.getFirstName());
        assertEquals("Smith", builtCustomer.getLastName());
        assertEquals("New York", builtCustomer.getCity());
        assertEquals("123 Main St", builtCustomer.getAddress());
        assertEquals(1, builtCustomer.getEnabled());
        assertEquals(categories, builtCustomer.getCategories());
    }

    @Test
    void testNoArgsConstructor() {
        Customer newCustomer = new Customer();
        assertNotNull(newCustomer);
    }

    @Test
    void testAllArgsConstructor() {
        Customer newCustomer = new Customer(1L, "Acme Corp", "contact@acme.com", 123456789,
                                           categories, "John", "Smith", "New York", "123 Main St", 1);

        assertNotNull(newCustomer);
        assertEquals(1L, newCustomer.getId());
        assertEquals("Acme Corp", newCustomer.getName());
        assertEquals("contact@acme.com", newCustomer.getEmail());
        assertEquals(123456789, newCustomer.getPhone());
    }

    @Test
    void testGettersAndSetters() {
        customer.setId(1L);
        customer.setName("Acme Corp");
        customer.setEmail("contact@acme.com");
        customer.setPhone(123456789);
        customer.setFirstName("John");
        customer.setLastName("Smith");
        customer.setCity("New York");
        customer.setAddress("123 Main St");
        customer.setEnabled(1);
        customer.setCategories(categories);

        assertEquals(1L, customer.getId());
        assertEquals("Acme Corp", customer.getName());
        assertEquals("contact@acme.com", customer.getEmail());
        assertEquals(123456789, customer.getPhone());
        assertEquals("John", customer.getFirstName());
        assertEquals("Smith", customer.getLastName());
        assertEquals("New York", customer.getCity());
        assertEquals("123 Main St", customer.getAddress());
        assertEquals(1, customer.getEnabled());
        assertEquals(categories, customer.getCategories());
    }

    @Test
    void testEnabledFlag() {
        customer.setEnabled(1);
        assertEquals(1, customer.getEnabled());

        customer.setEnabled(0);
        assertEquals(0, customer.getEnabled());
    }

    @Test
    void testCategoriesSet() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("VIP");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Premium");

        categories.add(category1);
        categories.add(category2);

        customer.setCategories(categories);

        assertEquals(2, customer.getCategories().size());
        assertTrue(customer.getCategories().contains(category1));
        assertTrue(customer.getCategories().contains(category2));
    }

    @Test
    void testEmptyCategories() {
        customer.setCategories(new HashSet<>());
        assertNotNull(customer.getCategories());
        assertEquals(0, customer.getCategories().size());
    }

    @Test
    void testNullCategories() {
        customer.setCategories(null);
        assertNull(customer.getCategories());
    }

    @Test
    void testPhoneNumber() {
        customer.setPhone(987654321);
        assertEquals(987654321, customer.getPhone());

        customer.setPhone(0);
        assertEquals(0, customer.getPhone());
    }

    @Test
    void testEqualsAndHashCode() {
        Customer customer1 = Customer.builder()
                .id(1L)
                .name("Acme Corp")
                .email("contact@acme.com")
                .build();

        Customer customer2 = Customer.builder()
                .id(1L)
                .name("Acme Corp")
                .email("contact@acme.com")
                .build();

        assertEquals(customer1, customer2);
        assertEquals(customer1.hashCode(), customer2.hashCode());
    }

    @Test
    void testToString() {
        customer.setId(1L);
        customer.setName("Acme Corp");
        customer.setEmail("contact@acme.com");

        String toString = customer.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Acme Corp"));
    }

    @Test
    void testNullName() {
        customer.setName(null);
        assertNull(customer.getName());
    }

    @Test
    void testNullEmail() {
        customer.setEmail(null);
        assertNull(customer.getEmail());
    }

    @Test
    void testNullAddress() {
        customer.setAddress(null);
        assertNull(customer.getAddress());
    }

    @Test
    void testNullCity() {
        customer.setCity(null);
        assertNull(customer.getCity());
    }

    @Test
    void testCompleteCustomerData() {
        customer.setId(100L);
        customer.setName("Tech Solutions Inc");
        customer.setEmail("info@techsolutions.com");
        customer.setPhone(555123456);
        customer.setFirstName("Alice");
        customer.setLastName("Johnson");
        customer.setCity("San Francisco");
        customer.setAddress("456 Tech Blvd");
        customer.setEnabled(1);

        assertNotNull(customer);
        assertEquals(100L, customer.getId());
        assertEquals("Tech Solutions Inc", customer.getName());
        assertEquals("info@techsolutions.com", customer.getEmail());
        assertEquals(555123456, customer.getPhone());
        assertEquals("Alice", customer.getFirstName());
        assertEquals("Johnson", customer.getLastName());
        assertEquals("San Francisco", customer.getCity());
        assertEquals("456 Tech Blvd", customer.getAddress());
        assertEquals(1, customer.getEnabled());
    }
}
