package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ContractTest {

    private Contract contract;
    private Customer testCustomer;
    private User testUser;

    @BeforeEach
    void setUp() {
        contract = new Contract();
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Customer");
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
    }

    @Test
    void contract_defaultConstructor_shouldCreateInstance() {
        assertNotNull(contract);
    }

    @Test
    void contract_allArgsConstructor_shouldCreateInstanceWithValues() {
        Contract c = new Contract(1L, "Test Contract", "Contract content", 
                new BigDecimal("10000.00"), LocalDate.now(), LocalDate.now().plusMonths(6), 
                Status.ACTIVE, testCustomer, testUser);

        assertNotNull(c);
        assertEquals(1L, c.getId());
        assertEquals("Test Contract", c.getName());
        assertEquals("Contract content", c.getContent());
    }

    @Test
    void contract_builder_shouldCreateInstance() {
        Contract c = Contract.builder()
                .id(1L)
                .name("Builder Contract")
                .content("Builder content")
                .value(new BigDecimal("5000.00"))
                .beginDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .status(Status.PROPOSED)
                .customer(testCustomer)
                .user(testUser)
                .build();

        assertNotNull(c);
        assertEquals("Builder Contract", c.getName());
        assertEquals(Status.PROPOSED, c.getStatus());
    }

    @Test
    void setId_shouldSetIdValue() {
        contract.setId(10L);
        assertEquals(10L, contract.getId());
    }

    @Test
    void setName_shouldSetNameValue() {
        contract.setName("New Contract");
        assertEquals("New Contract", contract.getName());
    }

    @Test
    void setContent_shouldSetContentValue() {
        contract.setContent("Contract details");
        assertEquals("Contract details", contract.getContent());
    }

    @Test
    void setValue_shouldSetValueValue() {
        BigDecimal value = new BigDecimal("15000.50");
        contract.setValue(value);
        assertEquals(value, contract.getValue());
    }

    @Test
    void setBeginDate_shouldSetBeginDateValue() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        contract.setBeginDate(date);
        assertEquals(date, contract.getBeginDate());
    }

    @Test
    void setEndDate_shouldSetEndDateValue() {
        LocalDate date = LocalDate.of(2024, 12, 31);
        contract.setEndDate(date);
        assertEquals(date, contract.getEndDate());
    }

    @Test
    void setStatus_shouldSetStatusValue() {
        contract.setStatus(Status.DONE);
        assertEquals(Status.DONE, contract.getStatus());
    }

    @Test
    void setCustomer_shouldSetCustomerValue() {
        contract.setCustomer(testCustomer);
        assertEquals(testCustomer, contract.getCustomer());
    }

    @Test
    void setUser_shouldSetUserValue() {
        contract.setUser(testUser);
        assertEquals(testUser, contract.getUser());
    }

    @Test
    void equals_withSameValues_shouldReturnTrue() {
        Contract c1 = new Contract(1L, "Contract", "Content", new BigDecimal("1000"), 
                LocalDate.now(), LocalDate.now(), Status.ACTIVE, testCustomer, testUser);
        Contract c2 = new Contract(1L, "Contract", "Content", new BigDecimal("1000"), 
                LocalDate.now(), LocalDate.now(), Status.ACTIVE, testCustomer, testUser);

        assertEquals(c1, c2);
    }

    @Test
    void hashCode_withSameValues_shouldReturnSameHashCode() {
        Contract c1 = new Contract(1L, "Contract", "Content", new BigDecimal("1000"), 
                LocalDate.now(), LocalDate.now(), Status.ACTIVE, testCustomer, testUser);
        Contract c2 = new Contract(1L, "Contract", "Content", new BigDecimal("1000"), 
                LocalDate.now(), LocalDate.now(), Status.ACTIVE, testCustomer, testUser);

        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void toString_shouldContainFieldValues() {
        contract.setId(1L);
        contract.setName("Test Contract");
        contract.setValue(new BigDecimal("5000"));

        String toString = contract.toString();

        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("Test Contract"));
    }

    @Test
    void builder_withPartialFields_shouldCreateInstance() {
        Contract c = Contract.builder()
                .name("Partial Contract")
                .status(Status.NEGOTIATED)
                .build();

        assertNotNull(c);
        assertEquals("Partial Contract", c.getName());
        assertEquals(Status.NEGOTIATED, c.getStatus());
        assertNull(c.getValue());
    }

    @Test
    void setValue_withZero_shouldSetZero() {
        contract.setValue(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, contract.getValue());
    }

    @Test
    void setStatus_withAllStatuses_shouldSetCorrectly() {
        contract.setStatus(Status.PROPOSED);
        assertEquals(Status.PROPOSED, contract.getStatus());

        contract.setStatus(Status.NEGOTIATED);
        assertEquals(Status.NEGOTIATED, contract.getStatus());

        contract.setStatus(Status.IMPLEMENTED);
        assertEquals(Status.IMPLEMENTED, contract.getStatus());

        contract.setStatus(Status.DONE);
        assertEquals(Status.DONE, contract.getStatus());
    }
}
