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
                .build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        contract = Contract.builder()
                .id(1L)
                .name("Test Contract")
                .content("Test Content")
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
        assertNotNull(contract);
        assertEquals(1L, contract.getId());
        assertEquals("Test Contract", contract.getName());
        assertEquals("Test Content", contract.getContent());
        assertEquals(new BigDecimal("10000.00"), contract.getValue());
        assertEquals(LocalDate.of(2024, 1, 1), contract.getBeginDate());
        assertEquals(LocalDate.of(2024, 12, 31), contract.getEndDate());
        assertEquals(Status.PROPOSED, contract.getStatus());
        assertEquals(customer, contract.getCustomer());
        assertEquals(user, contract.getUser());
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyContract() {
        Contract emptyContract = new Contract();
        
        assertNotNull(emptyContract);
        assertNull(emptyContract.getId());
        assertNull(emptyContract.getName());
    }

    @Test
    void allArgsConstructor_shouldCreateContractWithAllFields() {
        Contract newContract = new Contract(
                2L,
                "New Contract",
                "New Content",
                new BigDecimal("20000.00"),
                LocalDate.now(),
                LocalDate.now().plusMonths(6),
                Status.NEGOTIATED,
                customer,
                user
        );

        assertNotNull(newContract);
        assertEquals(2L, newContract.getId());
        assertEquals("New Contract", newContract.getName());
    }

    @Test
    void setAndGetId_shouldWorkCorrectly() {
        contract.setId(100L);
        assertEquals(100L, contract.getId());
    }

    @Test
    void setAndGetName_shouldWorkCorrectly() {
        contract.setName("Updated Contract");
        assertEquals("Updated Contract", contract.getName());
    }

    @Test
    void setAndGetContent_shouldWorkCorrectly() {
        contract.setContent("Updated Content");
        assertEquals("Updated Content", contract.getContent());
    }

    @Test
    void setAndGetValue_shouldWorkCorrectly() {
        BigDecimal newValue = new BigDecimal("50000.00");
        contract.setValue(newValue);
        assertEquals(newValue, contract.getValue());
    }

    @Test
    void setAndGetBeginDate_shouldWorkCorrectly() {
        LocalDate newDate = LocalDate.of(2025, 1, 1);
        contract.setBeginDate(newDate);
        assertEquals(newDate, contract.getBeginDate());
    }

    @Test
    void setAndGetEndDate_shouldWorkCorrectly() {
        LocalDate newDate = LocalDate.of(2025, 12, 31);
        contract.setEndDate(newDate);
        assertEquals(newDate, contract.getEndDate());
    }

    @Test
    void setAndGetStatus_shouldWorkCorrectly() {
        contract.setStatus(Status.DONE);
        assertEquals(Status.DONE, contract.getStatus());
    }

    @Test
    void setAndGetCustomer_shouldWorkCorrectly() {
        Customer newCustomer = Customer.builder().id(2L).name("New Company").build();
        contract.setCustomer(newCustomer);
        assertEquals(newCustomer, contract.getCustomer());
    }

    @Test
    void setAndGetUser_shouldWorkCorrectly() {
        User newUser = User.builder().id(2L).username("newuser").build();
        contract.setUser(newUser);
        assertEquals(newUser, contract.getUser());
    }

    @Test
    void contract_withNullValues_shouldHandleGracefully() {
        Contract nullContract = Contract.builder().build();
        
        assertNull(nullContract.getId());
        assertNull(nullContract.getName());
        assertNull(nullContract.getValue());
        assertNull(nullContract.getStatus());
    }

    @Test
    void contract_equalsAndHashCode_shouldWorkCorrectly() {
        Contract contract1 = Contract.builder()
                .id(1L)
                .name("Test")
                .build();

        Contract contract2 = Contract.builder()
                .id(1L)
                .name("Test")
                .build();

        assertEquals(contract1, contract2);
        assertEquals(contract1.hashCode(), contract2.hashCode());
    }

    @Test
    void contract_toString_shouldReturnString() {
        String result = contract.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("Test Contract"));
    }
}
