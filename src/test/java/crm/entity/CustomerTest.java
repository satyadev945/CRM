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
        Category category = new Category();
        category.setId(1L);
        category.setName("Technology");

        categories = new HashSet<>();
        categories.add(category);

        customer = Customer.builder()
                .id(1L)
                .name("Test Company")
                .email("test@example.com")
                .phone(123456789)
                .categories(categories)
                .firstName("John")
                .lastName("Doe")
                .city("New York")
                .address("123 Main St")
                .enabled(1)
                .build();
    }

    @Test
    void builder_shouldCreateCustomerWithAllFields() {
        assertNotNull(customer);
        assertEquals(1L, customer.getId());
        assertEquals("Test Company", customer.getName());
        assertEquals("test@example.com", customer.getEmail());
        assertEquals(123456789, customer.getPhone());
        assertEquals(categories, customer.getCategories());
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("New York", customer.getCity());
        assertEquals("123 Main St", customer.getAddress());
        assertEquals(1, customer.getEnabled());
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyCustomer() {
        Customer emptyCustomer = new Customer();
        
        assertNotNull(emptyCustomer);
        assertNull(emptyCustomer.getId());
        assertNull(emptyCustomer.getName());
    }

    @Test
    void allArgsConstructor_shouldCreateCustomerWithAllFields() {
        Customer newCustomer = new Customer(
                2L,
                "New Company",
                "new@example.com",
                987654321,
                categories,
                "Jane",
                "Smith",
                "Los Angeles",
                "456 Oak Ave",
                1
        );

        assertNotNull(newCustomer);
        assertEquals(2L, newCustomer.getId());
        assertEquals("New Company", newCustomer.getName());
    }

    @Test
    void setAndGetId_shouldWorkCorrectly() {
        customer.setId(100L);
        assertEquals(100L, customer.getId());
    }

    @Test
    void setAndGetName_shouldWorkCorrectly() {
        customer.setName("Updated Company");
        assertEquals("Updated Company", customer.getName());
    }

    @Test
    void setAndGetEmail_shouldWorkCorrectly() {
        customer.setEmail("updated@example.com");
        assertEquals("updated@example.com", customer.getEmail());
    }

    @Test
    void setAndGetPhone_shouldWorkCorrectly() {
        customer.setPhone(999888777);
        assertEquals(999888777, customer.getPhone());
    }

    @Test
    void setAndGetCategories_shouldWorkCorrectly() {
        Set<Category> newCategories = new HashSet<>();
        Category newCategory = new Category();
        newCategory.setId(2L);
        newCategory.setName("Finance");
        newCategories.add(newCategory);

        customer.setCategories(newCategories);
        assertEquals(newCategories, customer.getCategories());
    }

    @Test
    void setAndGetFirstName_shouldWorkCorrectly() {
        customer.setFirstName("Jane");
        assertEquals("Jane", customer.getFirstName());
    }

    @Test
    void setAndGetLastName_shouldWorkCorrectly() {
        customer.setLastName("Smith");
        assertEquals("Smith", customer.getLastName());
    }

    @Test
    void setAndGetCity_shouldWorkCorrectly() {
        customer.setCity("Los Angeles");
        assertEquals("Los Angeles", customer.getCity());
    }

    @Test
    void setAndGetAddress_shouldWorkCorrectly() {
        customer.setAddress("456 Oak Ave");
        assertEquals("456 Oak Ave", customer.getAddress());
    }

    @Test
    void setAndGetEnabled_shouldWorkCorrectly() {
        customer.setEnabled(0);
        assertEquals(0, customer.getEnabled());
    }

    @Test
    void customer_withNullValues_shouldHandleGracefully() {
        Customer nullCustomer = Customer.builder().build();
        
        assertNull(nullCustomer.getId());
        assertNull(nullCustomer.getName());
        assertNull(nullCustomer.getEmail());
        assertEquals(0, nullCustomer.getPhone());
        assertEquals(0, nullCustomer.getEnabled());
    }

    @Test
    void customer_withEmptyCategories_shouldHandleGracefully() {
        customer.setCategories(new HashSet<>());
        
        assertNotNull(customer.getCategories());
        assertEquals(0, customer.getCategories().size());
    }

    @Test
    void customer_equalsAndHashCode_shouldWorkCorrectly() {
        Customer customer1 = Customer.builder()
                .id(1L)
                .name("Test")
                .email("test@example.com")
                .build();

        Customer customer2 = Customer.builder()
                .id(1L)
                .name("Test")
                .email("test@example.com")
                .build();

        assertEquals(customer1, customer2);
        assertEquals(customer1.hashCode(), customer2.hashCode());
    }

    @Test
    void customer_toString_shouldReturnString() {
        String result = customer.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("Test Company"));
    }
}
