package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

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
    void testCustomerDefaultConstructor() {
        Customer newCustomer = new Customer();
        assertNotNull(newCustomer);
    }

    @Test
    void testCustomerAllArgsConstructor() {
        Customer newCustomer = new Customer(1L, "Corp", "corp@test.com", 987654321,
                categories, "Jane", "Smith", "Boston", "456 Elm St", 1);
        assertNotNull(newCustomer);
        assertEquals(1L, newCustomer.getId());
        assertEquals("Corp", newCustomer.getName());
        assertEquals("corp@test.com", newCustomer.getEmail());
        assertEquals(987654321, newCustomer.getPhone());
        assertEquals("Jane", newCustomer.getFirstName());
        assertEquals("Smith", newCustomer.getLastName());
        assertEquals("Boston", newCustomer.getCity());
        assertEquals("456 Elm St", newCustomer.getAddress());
        assertEquals(1, newCustomer.getEnabled());
    }

    @Test
    void testCustomerBuilder() {
        Customer builtCustomer = Customer.builder()
                .id(2L)
                .name("Builder Corp")
                .email("builder@test.com")
                .phone(111222333)
                .categories(categories)
                .firstName("Builder")
                .lastName("Test")
                .city("Chicago")
                .address("789 Oak Ave")
                .enabled(1)
                .build();
        assertNotNull(builtCustomer);
        assertEquals(2L, builtCustomer.getId());
        assertEquals("Builder Corp", builtCustomer.getName());
    }

    @Test
    void testGetId() {
        assertEquals(1L, customer.getId());
    }

    @Test
    void testSetId() {
        customer.setId(99L);
        assertEquals(99L, customer.getId());
    }

    @Test
    void testGetName() {
        assertEquals("Acme Corp", customer.getName());
    }

    @Test
    void testSetName() {
        customer.setName("New Corp");
        assertEquals("New Corp", customer.getName());
    }

    @Test
    void testGetEmail() {
        assertEquals("acme@example.com", customer.getEmail());
    }

    @Test
    void testSetEmail() {
        customer.setEmail("new@example.com");
        assertEquals("new@example.com", customer.getEmail());
    }

    @Test
    void testGetPhone() {
        assertEquals(123456789, customer.getPhone());
    }

    @Test
    void testSetPhone() {
        customer.setPhone(987654321);
        assertEquals(987654321, customer.getPhone());
    }

    @Test
    void testGetCategories() {
        assertNotNull(customer.getCategories());
        assertEquals(1, customer.getCategories().size());
    }

    @Test
    void testSetCategories() {
        Set<Category> newCategories = new HashSet<>();
        Category newCat = new Category();
        newCat.setId(2L);
        newCat.setName("Premium");
        newCategories.add(newCat);
        customer.setCategories(newCategories);
        assertEquals(newCategories, customer.getCategories());
    }

    @Test
    void testGetFirstName() {
        assertEquals("John", customer.getFirstName());
    }

    @Test
    void testSetFirstName() {
        customer.setFirstName("Jane");
        assertEquals("Jane", customer.getFirstName());
    }

    @Test
    void testGetLastName() {
        assertEquals("Doe", customer.getLastName());
    }

    @Test
    void testSetLastName() {
        customer.setLastName("Smith");
        assertEquals("Smith", customer.getLastName());
    }

    @Test
    void testGetCity() {
        assertEquals("New York", customer.getCity());
    }

    @Test
    void testSetCity() {
        customer.setCity("Los Angeles");
        assertEquals("Los Angeles", customer.getCity());
    }

    @Test
    void testGetAddress() {
        assertEquals("123 Main St", customer.getAddress());
    }

    @Test
    void testSetAddress() {
        customer.setAddress("456 Elm St");
        assertEquals("456 Elm St", customer.getAddress());
    }

    @Test
    void testGetEnabled() {
        assertEquals(1, customer.getEnabled());
    }

    @Test
    void testSetEnabled() {
        customer.setEnabled(0);
        assertEquals(0, customer.getEnabled());
    }

    @Test
    void testEqualsAndHashCode() {
        Customer c1 = Customer.builder()
                .id(1L)
                .name("Acme Corp")
                .email("acme@example.com")
                .phone(123456789)
                .firstName("John")
                .lastName("Doe")
                .city("New York")
                .address("123 Main St")
                .enabled(1)
                .build();

        Customer c2 = Customer.builder()
                .id(1L)
                .name("Acme Corp")
                .email("acme@example.com")
                .phone(123456789)
                .firstName("John")
                .lastName("Doe")
                .city("New York")
                .address("123 Main St")
                .enabled(1)
                .build();

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testToString() {
        String toString = customer.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Acme Corp"));
    }

    @Test
    void testCustomerWithNullCategories() {
        customer.setCategories(null);
        assertNull(customer.getCategories());
    }

    @Test
    void testCustomerWithEmptyCategories() {
        customer.setCategories(new HashSet<>());
        assertTrue(customer.getCategories().isEmpty());
    }
}
