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
        // Create customer
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Test Customer");

        // Create user
        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_ADMIN");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(role);

        // Create contract
        contract = new Contract();
        contract.setId(1L);
        contract.setName("Test Contract");
        contract.setContent("This is a test contract");
        contract.setValue(new BigDecimal("1000.50"));
        contract.setBeginDate(LocalDate.of(2023, 1, 1));
        contract.setEndDate(LocalDate.of(2023, 12, 31));
        contract.setStatus(Status.PROPOSED);
        contract.setCustomer(customer);
        contract.setUser(user);
    }

    @Test
    void testGetId() {
        assertEquals(1L, contract.getId());
    }

    @Test
    void testSetId() {
        contract.setId(2L);
        assertEquals(2L, contract.getId());
    }

    @Test
    void testGetName() {
        assertEquals("Test Contract", contract.getName());
    }

    @Test
    void testSetName() {
        contract.setName("Updated Contract");
        assertEquals("Updated Contract", contract.getName());
    }

    @Test
    void testGetContent() {
        assertEquals("This is a test contract", contract.getContent());
    }

    @Test
    void testSetContent() {
        contract.setContent("Updated content");
        assertEquals("Updated content", contract.getContent());
    }

    @Test
    void testGetValue() {
        assertEquals(new BigDecimal("1000.50"), contract.getValue());
    }

    @Test
    void testSetValue() {
        contract.setValue(new BigDecimal("2000.75"));
        assertEquals(new BigDecimal("2000.75"), contract.getValue());
    }

    @Test
    void testGetBeginDate() {
        assertEquals(LocalDate.of(2023, 1, 1), contract.getBeginDate());
    }

    @Test
    void testSetBeginDate() {
        LocalDate newDate = LocalDate.of(2023, 2, 1);
        contract.setBeginDate(newDate);
        assertEquals(newDate, contract.getBeginDate());
    }

    @Test
    void testGetEndDate() {
        assertEquals(LocalDate.of(2023, 12, 31), contract.getEndDate());
    }

    @Test
    void testSetEndDate() {
        LocalDate newDate = LocalDate.of(2024, 1, 31);
        contract.setEndDate(newDate);
        assertEquals(newDate, contract.getEndDate());
    }

    @Test
    void testGetStatus() {
        assertEquals(Status.PROPOSED, contract.getStatus());
    }

    @Test
    void testSetStatus() {
        contract.setStatus(Status.NEGOTIATED);
        assertEquals(Status.NEGOTIATED, contract.getStatus());

        contract.setStatus(Status.IMPLEMENTED);
        assertEquals(Status.IMPLEMENTED, contract.getStatus());

        contract.setStatus(Status.DONE);
        assertEquals(Status.DONE, contract.getStatus());
    }

    @Test
    void testGetCustomer() {
        assertEquals(customer, contract.getCustomer());
    }

    @Test
    void testSetCustomer() {
        Customer newCustomer = new Customer();
        newCustomer.setId(2L);
        newCustomer.setName("New Customer");

        contract.setCustomer(newCustomer);
        assertEquals(newCustomer, contract.getCustomer());
    }

    @Test
    void testGetUser() {
        assertEquals(user, contract.getUser());
    }

    @Test
    void testSetUser() {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setUsername("newuser");

        contract.setUser(newUser);
        assertEquals(newUser, contract.getUser());
    }

    @Test
    void testBuilderPattern() {
        LocalDate beginDate = LocalDate.of(2023, 3, 1);
        LocalDate endDate = LocalDate.of(2023, 6, 30);

        Contract builtContract = Contract.builder()
                .id(2L)
                .name("Builder Contract")
                .content("Built with builder pattern")
                .value(new BigDecimal("5000.00"))
                .beginDate(beginDate)
                .endDate(endDate)
                .status(Status.NEGOTIATED)
                .customer(customer)
                .user(user)
                .build();

        assertEquals(2L, builtContract.getId());
        assertEquals("Builder Contract", builtContract.getName());
        assertEquals("Built with builder pattern", builtContract.getContent());
        assertEquals(new BigDecimal("5000.00"), builtContract.getValue());
        assertEquals(beginDate, builtContract.getBeginDate());
        assertEquals(endDate, builtContract.getEndDate());
        assertEquals(Status.NEGOTIATED, builtContract.getStatus());
        assertEquals(customer, builtContract.getCustomer());
        assertEquals(user, builtContract.getUser());
    }

    @Test
    void testEqualsAndHashCode() {
        Contract sameContract = new Contract();
        sameContract.setId(1L);
        sameContract.setName("Test Contract");
        sameContract.setContent("This is a test contract");
        sameContract.setValue(new BigDecimal("1000.50"));
        sameContract.setBeginDate(LocalDate.of(2023, 1, 1));
        sameContract.setEndDate(LocalDate.of(2023, 12, 31));
        sameContract.setStatus(Status.PROPOSED);
        sameContract.setCustomer(customer);
        sameContract.setUser(user);

        assertEquals(contract, sameContract);
        assertEquals(contract.hashCode(), sameContract.hashCode());
    }

    @Test
    void testNoArgsConstructor() {
        Contract emptyContract = new Contract();
        assertNull(emptyContract.getId());
        assertNull(emptyContract.getName());
        assertNull(emptyContract.getContent());
        assertNull(emptyContract.getValue());
        assertNull(emptyContract.getBeginDate());
        assertNull(emptyContract.getEndDate());
        assertNull(emptyContract.getStatus());
        assertNull(emptyContract.getCustomer());
        assertNull(emptyContract.getUser());
    }
}