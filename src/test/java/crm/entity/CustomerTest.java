package crm.entity;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void testCustomerCreation() {
        // Arrange
        Customer customer = new Customer();
        Long id = 1L;
        String name = "Test Customer";
        String email = "test@example.com";
        int phone = 123456789;
        String firstName = "John";
        String lastName = "Doe";
        String city = "Test City";
        String address = "Test Address";
        int enabled = 1;

        Set<Category> categories = new HashSet<>();
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        categories.add(category);

        // Act
        customer.setId(id);
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setCategories(categories);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setCity(city);
        customer.setAddress(address);
        customer.setEnabled(enabled);

        // Assert
        assertEquals(id, customer.getId());
        assertEquals(name, customer.getName());
        assertEquals(email, customer.getEmail());
        assertEquals(phone, customer.getPhone());
        assertEquals(categories, customer.getCategories());
        assertEquals(firstName, customer.getFirstName());
        assertEquals(lastName, customer.getLastName());
        assertEquals(city, customer.getCity());
        assertEquals(address, customer.getAddress());
        assertEquals(enabled, customer.getEnabled());
    }

    @Test
    void testBuilderPattern() {
        // Arrange
        Long id = 1L;
        String name = "Test Customer";
        String email = "test@example.com";
        int phone = 123456789;
        String firstName = "John";
        String lastName = "Doe";
        String city = "Test City";
        String address = "Test Address";
        int enabled = 1;

        Set<Category> categories = new HashSet<>();
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        categories.add(category);

        // Act
        Customer customer = Customer.builder()
                .id(id)
                .name(name)
                .email(email)
                .phone(phone)
                .categories(categories)
                .firstName(firstName)
                .lastName(lastName)
                .city(city)
                .address(address)
                .enabled(enabled)
                .build();

        // Assert
        assertEquals(id, customer.getId());
        assertEquals(name, customer.getName());
        assertEquals(email, customer.getEmail());
        assertEquals(phone, customer.getPhone());
        assertEquals(categories, customer.getCategories());
        assertEquals(firstName, customer.getFirstName());
        assertEquals(lastName, customer.getLastName());
        assertEquals(city, customer.getCity());
        assertEquals(address, customer.getAddress());
        assertEquals(enabled, customer.getEnabled());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        Long id = 1L;
        String name = "Test Customer";
        String email = "test@example.com";
        int phone = 123456789;
        String firstName = "John";
        String lastName = "Doe";
        String city = "Test City";
        String address = "Test Address";
        int enabled = 1;

        Set<Category> categories = new HashSet<>();
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        categories.add(category);

        // Act
        Customer customer = new Customer(id, name, email, phone, categories, firstName, lastName, city, address, enabled);

        // Assert
        assertEquals(id, customer.getId());
        assertEquals(name, customer.getName());
        assertEquals(email, customer.getEmail());
        assertEquals(phone, customer.getPhone());
        assertEquals(categories, customer.getCategories());
        assertEquals(firstName, customer.getFirstName());
        assertEquals(lastName, customer.getLastName());
        assertEquals(city, customer.getCity());
        assertEquals(address, customer.getAddress());
        assertEquals(enabled, customer.getEnabled());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Customer customer1 = Customer.builder()
                .id(1L)
                .name("Test Customer")
                .email("test@example.com")
                .build();

        Customer customer2 = Customer.builder()
                .id(1L)
                .name("Test Customer")
                .email("test@example.com")
                .build();

        Customer customer3 = Customer.builder()
                .id(2L)
                .name("Another Customer")
                .email("another@example.com")
                .build();

        // Assert
        assertEquals(customer1, customer2);
        assertEquals(customer1.hashCode(), customer2.hashCode());
        assertNotEquals(customer1, customer3);
        assertNotEquals(customer1.hashCode(), customer3.hashCode());
    }
}