package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ContractTest {

    private Contract contract;
    private Customer customer;
    private User user;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .name("Test Company")
                .email("test@example.com")
                .build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("user@example.com")
                .build();

        contract = Contract.builder()
                .id(1L)
                .name("Test Contract")
                .content("Contract content")
                .value(new BigDecimal("10000.00"))
                .beginDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .status(Status.PROPOSED)
                .customer(customer)
                .user(user)
                .build();
    }

    @Test
    void builder_shouldCreateContractWithAllFields() {
        // Assert
        assertNotNull(contract);
        assertEquals(1L, contract.getId());
        assertEquals("Test Contract", contract.getName());
        assertEquals("Contract content", contract.getContent());
        assertEquals(new BigDecimal("10000.00"), contract.getValue());
        assertEquals(LocalDate.of(2024, 1, 1), contract.getBeginDate());
        assertEquals(LocalDate.of(2024, 12, 31), contract.getEndDate());
        assertEquals(Status.PROPOSED, contract.getStatus());
        assertEquals(customer, contract.getCustomer());
        assertEquals(user, contract.getUser());
    }

    @Test
    void setName_shouldSetName() {
        // Arrange
        String newName = "Updated Contract";

        // Act
        contract.setName(newName);

        // Assert
        assertEquals(newName, contract.getName());
    }

    @Test
    void setValue_shouldSetValue() {
        // Arrange
        BigDecimal newValue = new BigDecimal("20000.00");

        // Act
        contract.setValue(newValue);

        // Assert
        assertEquals(newValue, contract.getValue());
    }

    @Test
    void setStatus_shouldSetStatus() {
        // Arrange
        Status newStatus = Status.DONE;

        // Act
        contract.setStatus(newStatus);

        // Assert
        assertEquals(newStatus, contract.getStatus());
    }

    @Test
    void setCustomer_shouldSetCustomer() {
        // Arrange
        Customer newCustomer = Customer.builder()
                .id(2L)
                .name("New Company")
                .build();

        // Act
        contract.setCustomer(newCustomer);

        // Assert
        assertEquals(newCustomer, contract.getCustomer());
    }

    @Test
    void setUser_shouldSetUser() {
        // Arrange
        User newUser = User.builder()
                .id(2L)
                .username("newuser")
                .build();

        // Act
        contract.setUser(newUser);

        // Assert
        assertEquals(newUser, contract.getUser());
    }

    @Test
    void contract_shouldHaveEntityAnnotation() {
        // Assert
        assertTrue(Contract.class.isAnnotationPresent(javax.persistence.Entity.class));
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyContract() {
        // Act
        Contract emptyContract = new Contract();

        // Assert
        assertNotNull(emptyContract);
        assertNull(emptyContract.getId());
    }

    @Test
    void allArgsConstructor_shouldCreateContractWithAllFields() {
        // Act
        Contract newContract = new Contract(
                2L, "New Contract", "New Content", new BigDecimal("5000.00"),
                LocalDate.now(), LocalDate.now().plusMonths(6),
                Status.PROPOSED, customer, user
        );

        // Assert
        assertNotNull(newContract);
        assertEquals(2L, newContract.getId());
        assertEquals("New Contract", newContract.getName());
    }
}
