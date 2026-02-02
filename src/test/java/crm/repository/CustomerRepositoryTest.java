package crm.repository;

import crm.entity.Category;
import crm.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer enabledCustomer;
    private Customer disabledCustomer;
    private Category category;

    @BeforeEach
    void setUp() {
        // Create a category
        category = new Category();
        category.setName("Test Category");
        category = entityManager.persist(category);

        // Create an enabled customer
        enabledCustomer = new Customer();
        enabledCustomer.setName("Enabled Customer");
        enabledCustomer.setEmail("enabled@example.com");
        enabledCustomer.setPhone(123456789);
        enabledCustomer.setFirstName("John");
        enabledCustomer.setLastName("Doe");
        enabledCustomer.setCity("Test City");
        enabledCustomer.setAddress("123 Test St");
        enabledCustomer.setEnabled(1);

        Set<Category> categories = new HashSet<>();
        categories.add(category);
        enabledCustomer.setCategories(categories);

        enabledCustomer = entityManager.persist(enabledCustomer);

        // Create a disabled customer
        disabledCustomer = new Customer();
        disabledCustomer.setName("Disabled Customer");
        disabledCustomer.setEmail("disabled@example.com");
        disabledCustomer.setPhone(987654321);
        disabledCustomer.setFirstName("Jane");
        disabledCustomer.setLastName("Smith");
        disabledCustomer.setCity("Another City");
        disabledCustomer.setAddress("456 Test Ave");
        disabledCustomer.setEnabled(0);
        disabledCustomer = entityManager.persist(disabledCustomer);

        entityManager.flush();
    }

    @Test
    void getMaxIdShouldReturnHighestCustomerId() {
        // Act
        Long maxId = customerRepository.getMaxId();

        // Assert
        assertNotNull(maxId);
        assertTrue(maxId >= enabledCustomer.getId());
        assertTrue(maxId >= disabledCustomer.getId());
    }

    @Test
    void findAllByEnabledShouldReturnOnlyEnabledCustomers() {
        // Act
        List<Customer> enabledCustomers = (List<Customer>) customerRepository.findAllByEnabled(1);
        List<Customer> disabledCustomers = (List<Customer>) customerRepository.findAllByEnabled(0);

        // Assert
        assertFalse(enabledCustomers.isEmpty());
        assertTrue(enabledCustomers.contains(enabledCustomer));
        assertFalse(enabledCustomers.contains(disabledCustomer));

        assertFalse(disabledCustomers.isEmpty());
        assertFalse(disabledCustomers.contains(enabledCustomer));
        assertTrue(disabledCustomers.contains(disabledCustomer));
    }

    @Test
    void findOneByEnabledAndNameShouldReturnMatchingCustomer() {
        // Act
        Customer found = customerRepository.findOneByEnabledAndName(1, "Enabled Customer");

        // Assert
        assertNotNull(found);
        assertEquals(enabledCustomer.getId(), found.getId());

        // Act - disabled customer
        found = customerRepository.findOneByEnabledAndName(1, "Disabled Customer");

        // Assert
        assertNull(found);

        // Act - non-existent customer
        found = customerRepository.findOneByEnabledAndName(1, "Non-existent Customer");

        // Assert
        assertNull(found);
    }

    @Test
    void findByEnabledAndCityShouldReturnMatchingCustomers() {
        // Act
        List<Customer> customers = (List<Customer>) customerRepository.findByEnabledAndCity(1, "Test City");

        // Assert
        assertFalse(customers.isEmpty());
        assertTrue(customers.contains(enabledCustomer));

        // Act - different city
        customers = (List<Customer>) customerRepository.findByEnabledAndCity(1, "Another City");

        // Assert
        assertTrue(customers.isEmpty());
    }

    @Test
    void findByEnabledAndFirstNameAndLastNameShouldReturnMatchingCustomers() {
        // Act
        List<Customer> customers = (List<Customer>) customerRepository.findByEnabledAndFirstNameAndLastName(
                1, "John", "Doe");

        // Assert
        assertFalse(customers.isEmpty());
        assertTrue(customers.contains(enabledCustomer));

        // Act - no match
        customers = (List<Customer>) customerRepository.findByEnabledAndFirstNameAndLastName(
                1, "Jane", "Smith");

        // Assert
        assertTrue(customers.isEmpty());
    }

    @Test
    void findByCategoriesShouldReturnMatchingCustomers() {
        // Arrange
        Set<Category> categories = new HashSet<>();
        categories.add(category);

        // Act
        List<Customer> customers = (List<Customer>) customerRepository.findByCategories(categories);

        // Assert
        assertFalse(customers.isEmpty());
        assertTrue(customers.contains(enabledCustomer));
    }

    @Test
    void findByEnabledAndCategoriesShouldReturnMatchingCustomers() {
        // Arrange
        Set<Category> categories = new HashSet<>();
        categories.add(category);

        // Act
        List<Customer> customers = (List<Customer>) customerRepository.findByEnabledAndCategories(1, categories);

        // Assert
        assertFalse(customers.isEmpty());
        assertTrue(customers.contains(enabledCustomer));

        // Act - disabled
        customers = (List<Customer>) customerRepository.findByEnabledAndCategories(0, categories);

        // Assert
        assertTrue(customers.isEmpty());
    }
}