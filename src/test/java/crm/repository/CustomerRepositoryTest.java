package crm.repository;

import crm.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void customerRepository_shouldNotBeNull() {
        assertNotNull(customerRepository);
    }

    @Test
    void save_shouldPersistCustomer() {
        Customer customer = new Customer();
        customer.setName("Test Company");
        customer.setEmail("test@company.com");
        customer.setPhone("123456789");
        customer.setEnabled(true);

        Customer saved = customerRepository.save(customer);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Test Company", saved.getName());
    }

    @Test
    void getMaxId_shouldReturnMaximumId() {
        Long maxId = customerRepository.getMaxId();
        assertNotNull(maxId);
        assertTrue(maxId >= 0);
    }

    @Test
    void findAllByEnabled_shouldReturnEnabledCustomers() {
        Customer customer = new Customer();
        customer.setName("Enabled Company");
        customer.setEmail("enabled@company.com");
        customer.setPhone("111222333");
        customer.setEnabled(true);
        customerRepository.save(customer);

        Iterable<Customer> enabled = customerRepository.findAllByEnabled(true);

        assertNotNull(enabled);
        assertTrue(enabled.iterator().hasNext());
    }

    @Test
    void findOneByEnabledAndName_shouldReturnCustomer() {
        Customer customer = new Customer();
        customer.setName("Unique Company");
        customer.setEmail("unique@company.com");
        customer.setPhone("444555666");
        customer.setEnabled(true);
        customerRepository.save(customer);

        Customer found = customerRepository.findOneByEnabledAndName(true, "Unique Company");

        assertNotNull(found);
        assertEquals("Unique Company", found.getName());
    }

    @Test
    void findByEnabledAndEmail_shouldReturnCustomers() {
        Customer customer = new Customer();
        customer.setName("Email Company");
        customer.setEmail("email@company.com");
        customer.setPhone("777888999");
        customer.setEnabled(true);
        customerRepository.save(customer);

        Iterable<Customer> found = customerRepository.findByEnabledAndEmail(true, "email@company.com");

        assertNotNull(found);
        assertTrue(found.iterator().hasNext());
    }

    @Test
    void findByEnabledAndCity_shouldReturnCustomers() {
        Customer customer = new Customer();
        customer.setName("City Company");
        customer.setEmail("city@company.com");
        customer.setPhone("123123123");
        customer.setCity("New York");
        customer.setEnabled(true);
        customerRepository.save(customer);

        Iterable<Customer> found = customerRepository.findByEnabledAndCity(true, "New York");

        assertNotNull(found);
        assertTrue(found.iterator().hasNext());
    }

    @Test
    void delete_shouldRemoveCustomer() {
        Customer customer = new Customer();
        customer.setName("Delete Company");
        customer.setEmail("delete@company.com");
        customer.setPhone("999999999");
        customer.setEnabled(true);
        Customer saved = customerRepository.save(customer);

        customerRepository.delete(saved);

        assertFalse(customerRepository.findById(saved.getId()).isPresent());
    }
}
