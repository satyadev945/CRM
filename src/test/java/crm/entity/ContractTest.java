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
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");
        user.setEnabled(1);
        user.setRole(role);

        customer = new Customer();
        customer.setId(1L);
        customer.setName("Acme Corp");
        customer.setEmail("acme@example.com");
        customer.setPhone(123456789);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setCity("New York");
        customer.setAddress("123 Main St");
        customer.setEnabled(1);

        contract = new Contract();
        contract.setId(1L);
        contract.setName("Contract-001");
        contract.setContent("Contract content");
        contract.setValue(new BigDecimal("10000.00"));
        contract.setBeginDate(LocalDate.of(2024, 1, 1));
        contract.setEndDate(LocalDate.of(2024, 12, 31));
        contract.setStatus(Status.PROPOSED);
        contract.setCustomer(customer);
        contract.setUser(user);
    }

    @Test
    void testContractDefaultConstructor() {
        Contract newContract = new Contract();
        assertNotNull(newContract);
    }

    @Test
    void testContractAllArgsConstructor() {
        Contract newContract = new Contract(
                1L, "Contract-002", "Content", new BigDecimal("5000.00"),
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 30),
                Status.NEGOTIATED, customer, user);
        assertNotNull(newContract);
        assertEquals(1L, newContract.getId());
        assertEquals("Contract-002", newContract.getName());
        assertEquals("Content", newContract.getContent());
        assertEquals(new BigDecimal("5000.00"), newContract.getValue());
        assertEquals(Status.NEGOTIATED, newContract.getStatus());
    }

    @Test
    void testContractBuilder() {
        Contract builtContract = Contract.builder()
                .id(2L)
                .name("Contract-Builder")
                .content("Builder content")
                .value(new BigDecimal("20000.00"))
                .beginDate(LocalDate.of(2024, 2, 1))
                .endDate(LocalDate.of(2024, 11, 30))
                .status(Status.IMPLEMENTED)
                .customer(customer)
                .user(user)
                .build();
        assertNotNull(builtContract);
        assertEquals(2L, builtContract.getId());
        assertEquals("Contract-Builder", builtContract.getName());
    }

    @Test
    void testGetId() {
        assertEquals(1L, contract.getId());
    }

    @Test
    void testSetId() {
        contract.setId(99L);
        assertEquals(99L, contract.getId());
    }

    @Test
    void testGetName() {
        assertEquals("Contract-001", contract.getName());
    }

    @Test
    void testSetName() {
        contract.setName("Contract-Updated");
        assertEquals("Contract-Updated", contract.getName());
    }

    @Test
    void testGetContent() {
        assertEquals("Contract content", contract.getContent());
    }

    @Test
    void testSetContent() {
        contract.setContent("New content");
        assertEquals("New content", contract.getContent());
    }

    @Test
    void testGetValue() {
        assertEquals(new BigDecimal("10000.00"), contract.getValue());
    }

    @Test
    void testSetValue() {
        contract.setValue(new BigDecimal("50000.00"));
        assertEquals(new BigDecimal("50000.00"), contract.getValue());
    }

    @Test
    void testGetBeginDate() {
        assertEquals(LocalDate.of(2024, 1, 1), contract.getBeginDate());
    }

    @Test
    void testSetBeginDate() {
        contract.setBeginDate(LocalDate.of(2024, 3, 15));
        assertEquals(LocalDate.of(2024, 3, 15), contract.getBeginDate());
    }

    @Test
    void testGetEndDate() {
        assertEquals(LocalDate.of(2024, 12, 31), contract.getEndDate());
    }

    @Test
    void testSetEndDate() {
        contract.setEndDate(LocalDate.of(2025, 6, 30));
        assertEquals(LocalDate.of(2025, 6, 30), contract.getEndDate());
    }

    @Test
    void testGetStatus() {
        assertEquals(Status.PROPOSED, contract.getStatus());
    }

    @Test
    void testSetStatus_Proposed() {
        contract.setStatus(Status.PROPOSED);
        assertEquals(Status.PROPOSED, contract.getStatus());
    }

    @Test
    void testSetStatus_Negotiated() {
        contract.setStatus(Status.NEGOTIATED);
        assertEquals(Status.NEGOTIATED, contract.getStatus());
    }

    @Test
    void testSetStatus_Implemented() {
        contract.setStatus(Status.IMPLEMENTED);
        assertEquals(Status.IMPLEMENTED, contract.getStatus());
    }

    @Test
    void testSetStatus_Done() {
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
        newCustomer.setName("New Corp");
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
    void testEqualsAndHashCode() {
        Contract c1 = Contract.builder()
                .id(1L)
                .name("Contract-001")
                .content("Contract content")
                .value(new BigDecimal("10000.00"))
                .beginDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .status(Status.PROPOSED)
                .customer(customer)
                .user(user)
                .build();

        Contract c2 = Contract.builder()
                .id(1L)
                .name("Contract-001")
                .content("Contract content")
                .value(new BigDecimal("10000.00"))
                .beginDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .status(Status.PROPOSED)
                .customer(customer)
                .user(user)
                .build();

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testToString() {
        String toString = contract.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Contract-001"));
    }

    @Test
    void testContractWithNullValues() {
        Contract nullContract = new Contract();
        assertNull(nullContract.getName());
        assertNull(nullContract.getContent());
        assertNull(nullContract.getValue());
        assertNull(nullContract.getBeginDate());
        assertNull(nullContract.getEndDate());
        assertNull(nullContract.getStatus());
        assertNull(nullContract.getCustomer());
        assertNull(nullContract.getUser());
    }
}
