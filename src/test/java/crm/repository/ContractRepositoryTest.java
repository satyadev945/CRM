package crm.repository;

import crm.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ContractRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ContractRepository contractRepository;

    private Customer customer;
    private User user;
    private Contract contract;

    @BeforeEach
    void setUp() {
        // Create a role for the user
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        role = entityManager.persist(role);

        // Create a user
        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");
        user.setEnabled(1);
        user.setRole(role);
        user = entityManager.persist(user);

        // Create a customer
        customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("customer@example.com");
        customer.setPhone(123456789);
        customer.setFirstName("Customer");
        customer.setLastName("Test");
        customer.setCity("Test City");
        customer.setAddress("Test Address");
        customer.setEnabled(1);
        customer = entityManager.persist(customer);

        // Create a contract
        contract = new Contract();
        contract.setName("Test Contract");
        contract.setContent("Contract content");
        contract.setValue(new BigDecimal("1000.00"));
        contract.setBeginDate(LocalDate.of(2023, 1, 1));
        contract.setEndDate(LocalDate.of(2023, 12, 31));
        contract.setStatus(Status.PROPOSED);
        contract.setCustomer(customer);
        contract.setUser(user);
        contract = entityManager.persist(contract);

        entityManager.flush();
    }

    @Test
    void findByNameShouldReturnContract() {
        // Act
        Contract found = contractRepository.findByName("Test Contract");

        // Assert
        assertNotNull(found);
        assertEquals("Test Contract", found.getName());
    }

    @Test
    void findAllByValueLessThanEqualShouldReturnMatchingContracts() {
        // Act
        List<Contract> contracts = (List<Contract>) contractRepository.findAllByValueLessThanEqual(new BigDecimal("1500.00"));

        // Assert
        assertFalse(contracts.isEmpty());
        assertTrue(contracts.contains(contract));

        // Act - should not be returned
        contracts = (List<Contract>) contractRepository.findAllByValueLessThanEqual(new BigDecimal("500.00"));

        // Assert
        assertTrue(contracts.isEmpty());
    }

    @Test
    void findAllByValueGreaterThanEqualShouldReturnMatchingContracts() {
        // Act
        List<Contract> contracts = (List<Contract>) contractRepository.findAllByValueGreaterThanEqual(new BigDecimal("500.00"));

        // Assert
        assertFalse(contracts.isEmpty());
        assertTrue(contracts.contains(contract));

        // Act - should not be returned
        contracts = (List<Contract>) contractRepository.findAllByValueGreaterThanEqual(new BigDecimal("1500.00"));

        // Assert
        assertTrue(contracts.isEmpty());
    }

    @Test
    void findAllByBeginDateShouldReturnMatchingContracts() {
        // Act
        List<Contract> contracts = (List<Contract>) contractRepository.findAllByBeginDate(LocalDate.of(2023, 1, 1));

        // Assert
        assertFalse(contracts.isEmpty());
        assertTrue(contracts.contains(contract));

        // Act - should not be returned
        contracts = (List<Contract>) contractRepository.findAllByBeginDate(LocalDate.of(2023, 2, 1));

        // Assert
        assertTrue(contracts.isEmpty());
    }

    @Test
    void findAllByStatusShouldReturnMatchingContracts() {
        // Act
        List<Contract> contracts = (List<Contract>) contractRepository.findAllByStatus(Status.PROPOSED);

        // Assert
        assertFalse(contracts.isEmpty());
        assertTrue(contracts.contains(contract));

        // Act - should not be returned
        contracts = (List<Contract>) contractRepository.findAllByStatus(Status.DONE);

        // Assert
        assertTrue(contracts.isEmpty());
    }

    @Test
    void findAllByCustomerShouldReturnMatchingContracts() {
        // Act
        List<Contract> contracts = (List<Contract>) contractRepository.findAllByCustomer(customer);

        // Assert
        assertFalse(contracts.isEmpty());
        assertTrue(contracts.contains(contract));

        // Act - should not be returned
        Customer anotherCustomer = new Customer();
        anotherCustomer.setName("Another Customer");
        anotherCustomer.setEmail("another@example.com");
        anotherCustomer = entityManager.persist(anotherCustomer);

        contracts = (List<Contract>) contractRepository.findAllByCustomer(anotherCustomer);

        // Assert
        assertTrue(contracts.isEmpty());
    }

    @Test
    void findAllByUserShouldReturnMatchingContracts() {
        // Act
        List<Contract> contracts = (List<Contract>) contractRepository.findAllByUser(user);

        // Assert
        assertFalse(contracts.isEmpty());
        assertTrue(contracts.contains(contract));

        // Act - should not be returned
        User anotherUser = new User();
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("another@example.com");
        anotherUser = entityManager.persist(anotherUser);

        contracts = (List<Contract>) contractRepository.findAllByUser(anotherUser);

        // Assert
        assertTrue(contracts.isEmpty());
    }

    @Test
    void findAllByCustomerAndUserShouldReturnMatchingContracts() {
        // Act
        List<Contract> contracts = (List<Contract>) contractRepository.findAllByCustomerAndUser(customer, user);

        // Assert
        assertFalse(contracts.isEmpty());
        assertTrue(contracts.contains(contract));

        // Act - should not be returned
        User anotherUser = new User();
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("another@example.com");
        anotherUser = entityManager.persist(anotherUser);

        contracts = (List<Contract>) contractRepository.findAllByCustomerAndUser(customer, anotherUser);

        // Assert
        assertTrue(contracts.isEmpty());
    }
}