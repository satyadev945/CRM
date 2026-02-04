package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Customer customer;
    private Set<Category> categories;
    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        // Create categories
        category1 = new Category();
        category1.setId(1L);
        category1.setName("Retail");

        category2 = new Category();
        category2.setId(2L);
        category2.setName("Corporate");

        categories = new HashSet<>();
        categories.add(category1);
        categories.add(category2);

        // Create customer
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Test Company");
        customer.setEmail("contact@testcompany.com");
        customer.setPhone(123456789);
        customer.setCategories(categories);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setCity("New York");
        customer.setAddress("123 Test St");
        customer.setEnabled(1);
    }

    @Test
    void testGetId() {
        assertEquals(1L, customer.getId());
    }

    @Test
    void testSetId() {
        customer.setId(2L);
        assertEquals(2L, customer.getId());
    }

    @Test
    void testGetName() {
        assertEquals("Test Company", customer.getName());
    }

    @Test
    void testSetName() {
        customer.setName("New Company Name");
        assertEquals("New Company Name", customer.getName());
    }

    @Test
    void testGetEmail() {
        assertEquals("contact@testcompany.com", customer.getEmail());
    }

    @Test
    void testSetEmail() {
        customer.setEmail("newemail@testcompany.com");
        assertEquals("newemail@testcompany.com", customer.getEmail());
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
        assertEquals(2, customer.getCategories().size());
        assertTrue(customer.getCategories().contains(category1));
        assertTrue(customer.getCategories().contains(category2));
    }

    @Test
    void testSetCategories() {
        Set<Category> newCategories = new HashSet<>();
        Category category3 = new Category();
        category3.setId(3L);
        category3.setName("VIP");
        newCategories.add(category3);

        customer.setCategories(newCategories);

        assertEquals(1, customer.getCategories().size());
        assertTrue(customer.getCategories().contains(category3));
        assertFalse(customer.getCategories().contains(category1));
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
        customer.setCity("Boston");
        assertEquals("Boston", customer.getCity());
    }

    @Test
    void testGetAddress() {
        assertEquals("123 Test St", customer.getAddress());
    }

    @Test
    void testSetAddress() {
        customer.setAddress("456 New St");
        assertEquals("456 New St", customer.getAddress());
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
    void testBuilderPattern() {
        Customer builtCustomer = Customer.builder()
                .id(2L)
                .name("Builder Company")
                .email("builder@company.com")
                .phone(555123456)
                .categories(categories)
                .firstName("Built")
                .lastName("Customer")
                .city("Chicago")
                .address("789 Builder St")
                .enabled(1)
                .build();

        assertEquals(2L, builtCustomer.getId());
        assertEquals("Builder Company", builtCustomer.getName());
        assertEquals("builder@company.com", builtCustomer.getEmail());
        assertEquals(555123456, builtCustomer.getPhone());
        assertEquals(categories, builtCustomer.getCategories());
        assertEquals("Built", builtCustomer.getFirstName());
        assertEquals("Customer", builtCustomer.getLastName());
        assertEquals("Chicago", builtCustomer.getCity());
        assertEquals("789 Builder St", builtCustomer.getAddress());
        assertEquals(1, builtCustomer.getEnabled());
    }

    @Test
    void testEqualsAndHashCode() {
        Customer sameCustomer = new Customer();
        sameCustomer.setId(1L);
        sameCustomer.setName("Test Company");
        sameCustomer.setEmail("contact@testcompany.com");
        sameCustomer.setPhone(123456789);
        sameCustomer.setCategories(categories);
        sameCustomer.setFirstName("John");
        sameCustomer.setLastName("Doe");
        sameCustomer.setCity("New York");
        sameCustomer.setAddress("123 Test St");
        sameCustomer.setEnabled(1);

        assertEquals(customer, sameCustomer);
        assertEquals(customer.hashCode(), sameCustomer.hashCode());
    }

    @Test
    void testNoArgsConstructor() {
        Customer emptyCustomer = new Customer();
        assertNull(emptyCustomer.getId());
        assertNull(emptyCustomer.getName());
        assertNull(emptyCustomer.getEmail());
        assertEquals(0, emptyCustomer.getPhone());
        assertNull(emptyCustomer.getCategories());
        assertNull(emptyCustomer.getFirstName());
        assertNull(emptyCustomer.getLastName());
        assertNull(emptyCustomer.getCity());
        assertNull(emptyCustomer.getAddress());
        assertEquals(0, emptyCustomer.getEnabled());
    }
}