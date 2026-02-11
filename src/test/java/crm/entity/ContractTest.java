package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ContractTest {

    @Mock
    private Customer mockCustomer;

    @Mock
    private User mockUser;

    private Contract contract;

    @BeforeEach
    void setUp() {
        contract = new Contract();
    }

    @Test
    void testContractBuilder() {
        LocalDate beginDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        BigDecimal value = new BigDecimal("50000.00");

        Contract builtContract = Contract.builder()
                .id(1L)
                .name("Service Contract 2024")
                .content("Annual service agreement")
                .value(value)
                .beginDate(beginDate)
                .endDate(endDate)
                .status(Status.PROPOSED)
                .customer(mockCustomer)
                .user(mockUser)
                .build();

        assertNotNull(builtContract);
        assertEquals(1L, builtContract.getId());
        assertEquals("Service Contract 2024", builtContract.getName());
        assertEquals("Annual service agreement", builtContract.getContent());
        assertEquals(value, builtContract.getValue());
        assertEquals(beginDate, builtContract.getBeginDate());
        assertEquals(endDate, builtContract.getEndDate());
        assertEquals(Status.PROPOSED, builtContract.getStatus());
        assertEquals(mockCustomer, builtContract.getCustomer());
        assertEquals(mockUser, builtContract.getUser());
    }

    @Test
    void testNoArgsConstructor() {
        Contract newContract = new Contract();
        assertNotNull(newContract);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDate beginDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        BigDecimal value = new BigDecimal("50000.00");

        Contract newContract = new Contract(1L, "Service Contract 2024", "Annual service agreement",
                                           value, beginDate, endDate, Status.PROPOSED, mockCustomer, mockUser);

        assertNotNull(newContract);
        assertEquals(1L, newContract.getId());
        assertEquals("Service Contract 2024", newContract.getName());
        assertEquals(value, newContract.getValue());
    }

    @Test
    void testGettersAndSetters() {
        LocalDate beginDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        BigDecimal value = new BigDecimal("50000.00");

        contract.setId(1L);
        contract.setName("Service Contract 2024");
        contract.setContent("Annual service agreement");
        contract.setValue(value);
        contract.setBeginDate(beginDate);
        contract.setEndDate(endDate);
        contract.setStatus(Status.PROPOSED);
        contract.setCustomer(mockCustomer);
        contract.setUser(mockUser);

        assertEquals(1L, contract.getId());
        assertEquals("Service Contract 2024", contract.getName());
        assertEquals("Annual service agreement", contract.getContent());
        assertEquals(value, contract.getValue());
        assertEquals(beginDate, contract.getBeginDate());
        assertEquals(endDate, contract.getEndDate());
        assertEquals(Status.PROPOSED, contract.getStatus());
        assertEquals(mockCustomer, contract.getCustomer());
        assertEquals(mockUser, contract.getUser());
    }

    @Test
    void testAllStatusValues() {
        contract.setStatus(Status.PROPOSED);
        assertEquals(Status.PROPOSED, contract.getStatus());

        contract.setStatus(Status.NEGOTIATED);
        assertEquals(Status.NEGOTIATED, contract.getStatus());

        contract.setStatus(Status.IMPLEMENTED);
        assertEquals(Status.IMPLEMENTED, contract.getStatus());

        contract.setStatus(Status.DONE);
        assertEquals(Status.DONE, contract.getStatus());
    }

    @Test
    void testValueCalculations() {
        BigDecimal value1 = new BigDecimal("10000.00");
        BigDecimal value2 = new BigDecimal("25000.50");
        BigDecimal value3 = new BigDecimal("0.00");

        contract.setValue(value1);
        assertEquals(value1, contract.getValue());

        contract.setValue(value2);
        assertEquals(value2, contract.getValue());

        contract.setValue(value3);
        assertEquals(value3, contract.getValue());
    }

    @Test
    void testNullValue() {
        contract.setValue(null);
        assertNull(contract.getValue());
    }

    @Test
    void testDateRange() {
        LocalDate beginDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        contract.setBeginDate(beginDate);
        contract.setEndDate(endDate);

        assertTrue(contract.getEndDate().isAfter(contract.getBeginDate()));
    }

    @Test
    void testNullDates() {
        contract.setBeginDate(null);
        contract.setEndDate(null);

        assertNull(contract.getBeginDate());
        assertNull(contract.getEndDate());
    }

    @Test
    void testNullCustomer() {
        contract.setCustomer(null);
        assertNull(contract.getCustomer());
    }

    @Test
    void testNullUser() {
        contract.setUser(null);
        assertNull(contract.getUser());
    }

    @Test
    void testNullContent() {
        contract.setContent(null);
        assertNull(contract.getContent());
    }

    @Test
    void testEqualsAndHashCode() {
        Contract contract1 = Contract.builder()
                .id(1L)
                .name("Contract A")
                .build();

        Contract contract2 = Contract.builder()
                .id(1L)
                .name("Contract A")
                .build();

        assertEquals(contract1, contract2);
        assertEquals(contract1.hashCode(), contract2.hashCode());
    }

    @Test
    void testToString() {
        contract.setId(1L);
        contract.setName("Service Contract");

        String toString = contract.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Service Contract"));
    }

    @Test
    void testCompleteContract() {
        LocalDate beginDate = LocalDate.of(2024, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 5, 31);
        BigDecimal value = new BigDecimal("150000.00");

        contract.setId(10L);
        contract.setName("Major Service Agreement");
        contract.setContent("Comprehensive service package");
        contract.setValue(value);
        contract.setBeginDate(beginDate);
        contract.setEndDate(endDate);
        contract.setStatus(Status.IMPLEMENTED);
        contract.setCustomer(mockCustomer);
        contract.setUser(mockUser);

        assertNotNull(contract);
        assertEquals(10L, contract.getId());
        assertEquals("Major Service Agreement", contract.getName());
        assertEquals("Comprehensive service package", contract.getContent());
        assertEquals(value, contract.getValue());
        assertEquals(beginDate, contract.getBeginDate());
        assertEquals(endDate, contract.getEndDate());
        assertEquals(Status.IMPLEMENTED, contract.getStatus());
        assertNotNull(contract.getCustomer());
        assertNotNull(contract.getUser());
    }

    @Test
    void testNegativeValue() {
        BigDecimal negativeValue = new BigDecimal("-1000.00");
        contract.setValue(negativeValue);
        assertEquals(negativeValue, contract.getValue());
    }
}
