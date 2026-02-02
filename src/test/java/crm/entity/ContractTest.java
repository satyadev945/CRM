package crm.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ContractTest {

    @Test
    void testContractCreation() {
        // Arrange
        Contract contract = new Contract();
        Long id = 1L;
        String name = "Test Contract";
        String content = "Contract details";
        BigDecimal value = new BigDecimal("1000.50");
        LocalDate beginDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        Status status = Status.PROPOSED;
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Test Customer");
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        // Act
        contract.setId(id);
        contract.setName(name);
        contract.setContent(content);
        contract.setValue(value);
        contract.setBeginDate(beginDate);
        contract.setEndDate(endDate);
        contract.setStatus(status);
        contract.setCustomer(customer);
        contract.setUser(user);

        // Assert
        assertEquals(id, contract.getId());
        assertEquals(name, contract.getName());
        assertEquals(content, contract.getContent());
        assertEquals(value, contract.getValue());
        assertEquals(beginDate, contract.getBeginDate());
        assertEquals(endDate, contract.getEndDate());
        assertEquals(status, contract.getStatus());
        assertEquals(customer, contract.getCustomer());
        assertEquals(user, contract.getUser());
    }

    @Test
    void testBuilderPattern() {
        // Arrange
        Long id = 1L;
        String name = "Test Contract";
        String content = "Contract details";
        BigDecimal value = new BigDecimal("1000.50");
        LocalDate beginDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        Status status = Status.PROPOSED;
        Customer customer = new Customer();
        User user = new User();

        // Act
        Contract contract = Contract.builder()
                .id(id)
                .name(name)
                .content(content)
                .value(value)
                .beginDate(beginDate)
                .endDate(endDate)
                .status(status)
                .customer(customer)
                .user(user)
                .build();

        // Assert
        assertEquals(id, contract.getId());
        assertEquals(name, contract.getName());
        assertEquals(content, contract.getContent());
        assertEquals(value, contract.getValue());
        assertEquals(beginDate, contract.getBeginDate());
        assertEquals(endDate, contract.getEndDate());
        assertEquals(status, contract.getStatus());
        assertEquals(customer, contract.getCustomer());
        assertEquals(user, contract.getUser());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Contract contract1 = Contract.builder()
                .id(1L)
                .name("Test Contract")
                .build();

        Contract contract2 = Contract.builder()
                .id(1L)
                .name("Test Contract")
                .build();

        Contract contract3 = Contract.builder()
                .id(2L)
                .name("Another Contract")
                .build();

        // Assert
        assertEquals(contract1, contract2);
        assertEquals(contract1.hashCode(), contract2.hashCode());
        assertNotEquals(contract1, contract3);
        assertNotEquals(contract1.hashCode(), contract3.hashCode());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        Long id = 1L;
        String name = "Test Contract";
        String content = "Content";
        BigDecimal value = new BigDecimal("100");
        LocalDate beginDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        Status status = Status.PROPOSED;
        Customer customer = new Customer();
        User user = new User();

        // Act
        Contract contract = new Contract(id, name, content, value, beginDate, endDate, status, customer, user);

        // Assert
        assertEquals(id, contract.getId());
        assertEquals(name, contract.getName());
        assertEquals(content, contract.getContent());
        assertEquals(value, contract.getValue());
        assertEquals(beginDate, contract.getBeginDate());
        assertEquals(endDate, contract.getEndDate());
        assertEquals(status, contract.getStatus());
        assertEquals(customer, contract.getCustomer());
        assertEquals(user, contract.getUser());
    }
}