package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ContractTest {

    private Contract contract;

    @BeforeEach
    void setUp() {
        contract = new Contract();
    }

    @Test
    void testDefaultConstructor() {
        Contract c = new Contract();
        assertNotNull(c);
        assertNull(c.getId());
        assertNull(c.getName());
        assertNull(c.getContent());
        assertNull(c.getValue());
        assertNull(c.getBeginDate());
        assertNull(c.getEndDate());
        assertNull(c.getStatus());
        assertNull(c.getCustomer());
        assertNull(c.getUser());
    }

    @Test
    void testAllArgsConstructor() {
        Customer customer = new Customer();
        User user = new User();
        LocalDate begin = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 12, 31);
        BigDecimal value = new BigDecimal("5000.00");

        Contract c = new Contract(1L, "Contract A", "Content", value, begin, end, Status.PROPOSED, customer, user);
        assertEquals(1L, c.getId());
        assertEquals("Contract A", c.getName());
        assertEquals("Content", c.getContent());
        assertEquals(value, c.getValue());
        assertEquals(begin, c.getBeginDate());
        assertEquals(end, c.getEndDate());
        assertEquals(Status.PROPOSED, c.getStatus());
        assertEquals(customer, c.getCustomer());
        assertEquals(user, c.getUser());
    }

    @Test
    void testBuilderPattern() {
        Contract c = Contract.builder()
                .id(10L)
                .name("Test Contract")
                .content("Some content")
                .value(new BigDecimal("1000.00"))
                .status(Status.NEGOTIATED)
                .build();

        assertEquals(10L, c.getId());
        assertEquals("Test Contract", c.getName());
        assertEquals("Some content", c.getContent());
        assertEquals(new BigDecimal("1000.00"), c.getValue());
        assertEquals(Status.NEGOTIATED, c.getStatus());
    }

    @Test
    void testSetAndGetId() {
        contract.setId(5L);
        assertEquals(5L, contract.getId());
    }

    @Test
    void testSetAndGetName() {
        contract.setName("Contract XYZ");
        assertEquals("Contract XYZ", contract.getName());
    }

    @Test
    void testSetAndGetContent() {
        contract.setContent("This is the contract content.");
        assertEquals("This is the contract content.", contract.getContent());
    }

    @Test
    void testSetAndGetValue() {
        BigDecimal value = new BigDecimal("9999.99");
        contract.setValue(value);
        assertEquals(value, contract.getValue());
    }

    @Test
    void testSetAndGetBeginDate() {
        LocalDate date = LocalDate.of(2024, 3, 15);
        contract.setBeginDate(date);
        assertEquals(date, contract.getBeginDate());
    }

    @Test
    void testSetAndGetEndDate() {
        LocalDate date = LocalDate.of(2025, 6, 30);
        contract.setEndDate(date);
        assertEquals(date, contract.getEndDate());
    }

    @Test
    void testSetAndGetStatus_Proposed() {
        contract.setStatus(Status.PROPOSED);
        assertEquals(Status.PROPOSED, contract.getStatus());
    }

    @Test
    void testSetAndGetStatus_Negotiated() {
        contract.setStatus(Status.NEGOTIATED);
        assertEquals(Status.NEGOTIATED, contract.getStatus());
    }

    @Test
    void testSetAndGetStatus_Implemented() {
        contract.setStatus(Status.IMPLEMENTED);
        assertEquals(Status.IMPLEMENTED, contract.getStatus());
    }

    @Test
    void testSetAndGetStatus_Done() {
        contract.setStatus(Status.DONE);
        assertEquals(Status.DONE, contract.getStatus());
    }

    @Test
    void testSetAndGetCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        contract.setCustomer(customer);
        assertEquals(customer, contract.getCustomer());
    }

    @Test
    void testSetAndGetUser() {
        User user = new User();
        user.setId(1L);
        contract.setUser(user);
        assertEquals(user, contract.getUser());
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
        contract.setName("Test");
        String str = contract.toString();
        assertNotNull(str);
        assertTrue(str.contains("Test"));
    }

    @Test
    void testSetNullValues() {
        contract.setName(null);
        contract.setContent(null);
        contract.setValue(null);
        assertNull(contract.getName());
        assertNull(contract.getContent());
        assertNull(contract.getValue());
    }
}
