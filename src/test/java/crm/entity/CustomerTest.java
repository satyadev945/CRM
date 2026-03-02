package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Customer customer;
    private Set<Category> categories;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        categories = new HashSet<>();
        Category category = new Category(1L, "Electronics");
        categories.add(category);
    }

    @Test
    void customer_defaultConstructor_shouldCreateInstance() {
        assertNotNull(customer);
    }

    @Test
    void customer_allArgsConstructor_shouldCreateInstanceWithValues() {
        Customer c = new Customer(1L, "Test Company", "test@example.com", "123456789", 
                categories, "John", "Doe", "New York", "123 Main St", true);

        assertNotNull(c);
        assertEquals(1L, c.getId());
        assertEquals("Test Company", c.getName());
        assertEquals("test@example.com", c.getEmail());
    }

    @Test
    void customer_builder_shouldCreateInstance() {
        Customer c = Customer.builder()
                .id(1L)
                .name("Builder Company")
                .email("builder@example.com")
                .phone("987654321")
                .firstName("Jane")
                .lastName("Smith")
                .city("Boston")
                .address("456 Oak Ave")
                .enabled(true)
                .categories(categories)
                .build();

        assertNotNull(c);
        assertEquals("Builder Company", c.getName());
        assertEquals("builder@example.com", c.getEmail());
    }

    @Test
    void setId_shouldSetIdValue() {
        customer.setId(10L);
        assertEquals(10L, customer.getId());
    }

    @Test
    void setName_shouldSetNameValue() {
        customer.setName("New Company");
        assertEquals("New Company", customer.getName());
    }

    @Test
    void setEmail_shouldSetEmailValue() {
        customer.setEmail("new@example.com");
        assertEquals("new@example.com", customer.getEmail());
    }

    @Test
    void setPhone_shouldSetPhoneValue() {
        customer.setPhone("555123456");
        assertEquals(555123456, customer.getPhone());
    }

    @Test
    void setCategories_shouldSetCategoriesValue() {
        customer.setCategories(categories);
        assertEquals(categories, customer.getCategories());
        assertEquals(1, customer.getCategories().size());
    }

    @Test
    void setFirstName_shouldSetFirstNameValue() {
        customer.setFirstName("Alice");
        assertEquals("Alice", customer.getFirstName());
    }

    @Test
    void setLastName_shouldSetLastNameValue() {
        customer.setLastName("Johnson");
        assertEquals("Johnson", customer.getLastName());
    }

    @Test
    void setCity_shouldSetCityValue() {
        customer.setCity("Chicago");
        assertEquals("Chicago", customer.getCity());
    }

    @Test
    void setAddress_shouldSetAddressValue() {
        customer.setAddress("789 Elm St");
        assertEquals("789 Elm St", customer.getAddress());
    }

    @Test
    void setEnabled_shouldSetEnabledValue() {
        customer.setEnabled(true);
        assertEquals(1, customer.isEnabled());
    }

    @Test
    void equals_withSameValues_shouldReturnTrue() {
        Customer c1 = new Customer(1L, "Company", "email@test.com", "123", categories, 
                "John", "Doe", "NYC", "123 St", true);
        Customer c2 = new Customer(1L, "Company", "email@test.com", "123", categories, 
                "John", "Doe", "NYC", "123 St", true);

        assertEquals(c1, c2);
    }

    @Test
    void hashCode_withSameValues_shouldReturnSameHashCode() {
        Customer c1 = new Customer(1L, "Company", "email@test.com", "123", categories, 
                "John", "Doe", "NYC", "123 St", true);
        Customer c2 = new Customer(1L, "Company", "email@test.com", "123", categories, 
                "John", "Doe", "NYC", "123 St", true);

        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void toString_shouldContainFieldValues() {
        customer.setId(1L);
        customer.setName("Test Company");
        customer.setEmail("test@example.com");

        String toString = customer.toString();

        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("Test Company"));
    }

    @Test
    void setEnabled_withZero_shouldSetZero() {
        customer.setEnabled(false);
        assertEquals(0, customer.isEnabled());
    }

    @Test
    void builder_withPartialFields_shouldCreateInstance() {
        Customer c = Customer.builder()
                .name("Partial Company")
                .email("partial@test.com")
                .build();

        assertNotNull(c);
        assertEquals("Partial Company", c.getName());
        assertNull(c.getFirstName());
    }

    @Test
    void setCategories_withEmptySet_shouldSetEmptySet() {
        Set<Category> emptyCategories = new HashSet<>();
        customer.setCategories(emptyCategories);
        
        assertNotNull(customer.getCategories());
        assertTrue(customer.getCategories().isEmpty());
    }
}
