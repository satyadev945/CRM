package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
    }

    @Test
    void testDefaultConstructor() {
        Customer c = new Customer();
        assertNotNull(c);
        assertNull(c.getId());
        assertNull(c.getName());
        assertNull(c.getEmail());
        assertEquals(0, c.getPhone());
        assertNull(c.getCategories());
        assertNull(c.getFirstName());
        assertNull(c.getLastName());
        assertNull(c.getCity());
        assertNull(c.getAddress());
        assertEquals(0, c.getEnabled());
    }

    @Test
    void testAllArgsConstructor() {
        Set<Category> categories = new HashSet<>();
        Customer c = new Customer(1L, "CompanyA", "a@a.com", 123456789,
                categories, "John", "Doe", "NYC", "5th Ave", 1);
        assertEquals(1L, c.getId());
        assertEquals("CompanyA", c.getName());
        assertEquals("a@a.com", c.getEmail());
        assertEquals(123456789, c.getPhone());
        assertEquals(categories, c.getCategories());
        assertEquals("John", c.getFirstName());
        assertEquals("Doe", c.getLastName());
        assertEquals("NYC", c.getCity());
        assertEquals("5th Ave", c.getAddress());
        assertEquals(1, c.getEnabled());
    }

    @Test
    void testBuilderPattern() {
        Customer c = Customer.builder()
                .id(2L)
                .name("BuilderCo")
                .email("builder@test.com")
                .phone(987654321)
                .firstName("Jane")
                .lastName("Smith")
                .city("LA")
                .address("Sunset Blvd")
                .enabled(1)
                .build();

        assertEquals(2L, c.getId());
        assertEquals("BuilderCo", c.getName());
        assertEquals("builder@test.com", c.getEmail());
        assertEquals(987654321, c.getPhone());
        assertEquals("Jane", c.getFirstName());
        assertEquals("Smith", c.getLastName());
        assertEquals("LA", c.getCity());
        assertEquals("Sunset Blvd", c.getAddress());
        assertEquals(1, c.getEnabled());
    }

    @Test
    void testSetAndGetId() {
        customer.setId(10L);
        assertEquals(10L, customer.getId());
    }

    @Test
    void testSetAndGetName() {
        customer.setName("TestCorp");
        assertEquals("TestCorp", customer.getName());
    }

    @Test
    void testSetAndGetEmail() {
        customer.setEmail("test@example.com");
        assertEquals("test@example.com", customer.getEmail());
    }

    @Test
    void testSetAndGetPhone() {
        customer.setPhone(555123456);
        assertEquals(555123456, customer.getPhone());
    }

    @Test
    void testSetAndGetFirstName() {
        customer.setFirstName("Alice");
        assertEquals("Alice", customer.getFirstName());
    }

    @Test
    void testSetAndGetLastName() {
        customer.setLastName("Wonder");
        assertEquals("Wonder", customer.getLastName());
    }

    @Test
    void testSetAndGetCity() {
        customer.setCity("Chicago");
        assertEquals("Chicago", customer.getCity());
    }

    @Test
    void testSetAndGetAddress() {
        customer.setAddress("123 Main St");
        assertEquals("123 Main St", customer.getAddress());
    }

    @Test
    void testSetAndGetEnabled_Active() {
        customer.setEnabled(1);
        assertEquals(1, customer.getEnabled());
    }

    @Test
    void testSetAndGetEnabled_Inactive() {
        customer.setEnabled(0);
        assertEquals(0, customer.getEnabled());
    }

    @Test
    void testSetAndGetCategories() {
        Set<Category> categories = new HashSet<>();
        Category cat = new Category();
        cat.setId(1L);
        cat.setName("VIP");
        categories.add(cat);
        customer.setCategories(categories);
        assertEquals(categories, customer.getCategories());
        assertEquals(1, customer.getCategories().size());
    }

    @Test
    void testSetCategoriesEmpty() {
        customer.setCategories(new HashSet<>());
        assertNotNull(customer.getCategories());
        assertTrue(customer.getCategories().isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        Customer c1 = Customer.builder().id(1L).name("Corp").email("c@c.com").build();
        Customer c2 = Customer.builder().id(1L).name("Corp").email("c@c.com").build();
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testNotEquals() {
        Customer c1 = Customer.builder().id(1L).name("Corp1").build();
        Customer c2 = Customer.builder().id(2L).name("Corp2").build();
        assertNotEquals(c1, c2);
    }

    @Test
    void testToString() {
        customer.setId(1L);
        customer.setName("TestCorp");
        String str = customer.toString();
        assertNotNull(str);
        assertTrue(str.contains("TestCorp"));
    }

    @Test
    void testSetNullName() {
        customer.setName(null);
        assertNull(customer.getName());
    }

    @Test
    void testSetNullEmail() {
        customer.setEmail(null);
        assertNull(customer.getEmail());
    }
}
