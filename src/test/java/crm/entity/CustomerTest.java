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
        // Assert
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
    void setName_shouldSetName() {
        // Arrange
        String newName = "Updated Company";

        // Act
        customer.setName(newName);

        // Assert
        assertEquals(newName, customer.getName());
    }

    @Test
    void setEmail_shouldSetEmail() {
        // Arrange
        String newEmail = "updated@example.com";

        // Act
        customer.setEmail(newEmail);

        // Assert
        assertEquals(newEmail, customer.getEmail());
    }

    @Test
    void setPhone_shouldSetPhone() {
        // Arrange
        int newPhone = 987654321;

        // Act
        customer.setPhone(newPhone);

        // Assert
        assertEquals(newPhone, customer.getPhone());
    }

    @Test
    void setCategories_shouldSetCategories() {
        // Arrange
        Category newCategory = new Category();
        newCategory.setId(2L);
        newCategory.setName("Finance");
        Set<Category> newCategories = new HashSet<>();
        newCategories.add(newCategory);

        // Act
        customer.setCategories(newCategories);

        // Assert
        assertEquals(newCategories, customer.getCategories());
        assertEquals(1, customer.getCategories().size());
    }

    @Test
    void setFirstName_shouldSetFirstName() {
        // Arrange
        String newFirstName = "Jane";

        // Act
        customer.setFirstName(newFirstName);

        // Assert
        assertEquals(newFirstName, customer.getFirstName());
    }

    @Test
    void setLastName_shouldSetLastName() {
        // Arrange
        String newLastName = "Smith";

        // Act
        customer.setLastName(newLastName);

        // Assert
        assertEquals(newLastName, customer.getLastName());
    }

    @Test
    void setCity_shouldSetCity() {
        // Arrange
        String newCity = "Los Angeles";

        // Act
        customer.setCity(newCity);

        // Assert
        assertEquals(newCity, customer.getCity());
    }

    @Test
    void setAddress_shouldSetAddress() {
        // Arrange
        String newAddress = "456 Oak Ave";

        // Act
        customer.setAddress(newAddress);

        // Assert
        assertEquals(newAddress, customer.getAddress());
    }

    @Test
    void setEnabled_shouldSetEnabled() {
        // Arrange
        int newEnabled = 0;

        // Act
        customer.setEnabled(newEnabled);

        // Assert
        assertEquals(newEnabled, customer.getEnabled());
    }

    @Test
    void customer_shouldHaveEntityAnnotation() {
        // Assert
        assertTrue(Customer.class.isAnnotationPresent(javax.persistence.Entity.class));
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyCustomer() {
        // Act
        Customer emptyCustomer = new Customer();

        // Assert
        assertNotNull(emptyCustomer);
        assertNull(emptyCustomer.getId());
    }

    @Test
    void allArgsConstructor_shouldCreateCustomerWithAllFields() {
        // Act
        Customer newCustomer = new Customer(
                2L, "New Company", "new@example.com", 111222333,
                categories, "Alice", "Brown", "Chicago", "789 Elm St", 1
        );

        // Assert
        assertNotNull(newCustomer);
        assertEquals(2L, newCustomer.getId());
        assertEquals("New Company", newCustomer.getName());
    }
}
