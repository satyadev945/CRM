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
        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(role);

        customer = new Customer();
        customer.setId(1L);
        customer.setName("TestCo");

        contract = new Contract();
    }

    @Test
    void testDefaultConstructor() {
        Contract c = new Contract();
        assertNotNull(c);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDate begin = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 12, 31);
        Contract c = new Contract(1L, "Contract-001", "Content", new BigDecimal("5000.00"),
                begin, end, Status.PROPOSED, customer, user);
        assertNotNull(c);
        assertEquals(1L, c.getId());
        assertEquals("Contract-001", c.getName());
        assertEquals("Content", c.getContent());
        assertEquals(new BigDecimal("5000.00"), c.getValue());
        assertEquals(begin, c.getBeginDate());
        assertEquals(end, c.getEndDate());
        assertEquals(Status.PROPOSED, c.getStatus());
        assertEquals(customer, c.getCustomer());
        assertEquals(user, c.getUser());
    }

    @Test
    void testBuilderPattern() {
        Contract c = Contract.builder()
                .id(2L)
                .name("Contract-002")
                .content("Builder content")
                .value(new BigDecimal("10000.00"))
                .beginDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .status(Status.NEGOTIATED)
                .customer(customer)
                .user(user)
                .build();
        assertNotNull(c);
        assertEquals(2L, c.getId());
        assertEquals("Contract-002", c.getName());
        assertEquals(Status.NEGOTIATED, c.getStatus());
    }

    @Test
    void testSetAndGetId() {
        contract.setId(5L);
        assertEquals(5L, contract.getId());
    }

    @Test
    void testSetAndGetName() {
        contract.setName("Contract-XYZ");
        assertEquals("Contract-XYZ", contract.getName());
    }

    @Test
    void testSetAndGetContent() {
        contract.setContent("Contract content here");
        assertEquals("Contract content here", contract.getContent());
    }

    @Test
    void testSetAndGetValue() {
        contract.setValue(new BigDecimal("2500.50"));
        assertEquals(new BigDecimal("2500.50"), contract.getValue());
    }

    @Test
    void testSetAndGetBeginDate() {
        LocalDate date = LocalDate.of(2023, 6, 15);
        contract.setBeginDate(date);
        assertEquals(date, contract.getBeginDate());
    }

    @Test
    void testSetAndGetEndDate() {
        LocalDate date = LocalDate.of(2024, 6, 15);
        contract.setEndDate(date);
        assertEquals(date, contract.getEndDate());
    }

    @Test
    void testSetAndGetStatus() {
        contract.setStatus(Status.IMPLEMENTED);
        assertEquals(Status.IMPLEMENTED, contract.getStatus());
    }

    @Test
    void testSetAndGetCustomer() {
        contract.setCustomer(customer);
        assertEquals(customer, contract.getCustomer());
    }

    @Test
    void testSetAndGetUser() {
        contract.setUser(user);
        assertEquals(user, contract.getUser());
    }

    @Test
    void testSetStatusProposed() {
        contract.setStatus(Status.PROPOSED);
        assertEquals(Status.PROPOSED, contract.getStatus());
    }

    @Test
    void testSetStatusDone() {
        contract.setStatus(Status.DONE);
        assertEquals(Status.DONE, contract.getStatus());
    }

    @Test
    void testSetStatusNegotiated() {
        contract.setStatus(Status.NEGOTIATED);
        assertEquals(Status.NEGOTIATED, contract.getStatus());
    }

    @Test
    void testSetValueNull() {
        contract.setValue(null);
        assertNull(contract.getValue());
    }

    @Test
    void testSetValueZero() {
        contract.setValue(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, contract.getValue());
    }

    @Test
    void testEqualsAndHashCode() {
        Contract c1 = Contract.builder().id(1L).name("C1").build();
        Contract c2 = Contract.builder().id(1L).name("C1").build();
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testNotEquals() {
        Contract c1 = Contract.builder().id(1L).name("C1").build();
        Contract c2 = Contract.builder().id(2L).name("C2").build();
        assertNotEquals(c1, c2);
    }

    @Test
    void testToString() {
        contract.setId(1L);
        contract.setName("TestContract");
        String str = contract.toString();
        assertNotNull(str);
        assertTrue(str.contains("TestContract"));
    }
}
