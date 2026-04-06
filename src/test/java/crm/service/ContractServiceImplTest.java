package crm.service;

import crm.entity.Contract;
import crm.entity.Customer;
import crm.entity.Status;
import crm.entity.User;
import crm.repository.ContractRepository;
import crm.repository.CustomerRepository;
import crm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceImplTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ContractServiceImpl contractService;

    private Contract testContract;
    private Customer testCustomer;
    private User testUser;
    private List<Contract> testContracts;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .id(1L)
                .name("Test Company")
                .build();

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        testContract = Contract.builder()
                .id(1L)
                .name("Test Contract")
                .value(new BigDecimal("10000.00"))
                .beginDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .status(Status.PROPOSED)
                .customer(testCustomer)
                .user(testUser)
                .build();

        testContracts = Arrays.asList(testContract);
    }

    @Test
    void findByName_shouldReturnContract() {
        // Arrange
        when(contractRepository.findByName("Test Contract")).thenReturn(testContract);

        // Act
        Contract result = contractService.findByName("Test Contract");

        // Assert
        assertNotNull(result);
        assertEquals("Test Contract", result.getName());
        verify(contractRepository).findByName("Test Contract");
    }

    @Test
    void listAllContracts_shouldReturnAllContracts() {
        // Arrange
        when(contractRepository.findAll()).thenReturn(testContracts);

        // Act
        Iterable<Contract> result = contractService.listAllContracts();

        // Assert
        assertNotNull(result);
        verify(contractRepository).findAll();
    }

    @Test
    void showContract_shouldReturnContract() {
        // Arrange
        when(contractRepository.findById(1L)).thenReturn(Optional.of(testContract));

        // Act
        Contract result = contractService.showContract(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(contractRepository).findById(1L);
    }

    @Test
    void showContract_shouldReturnNull_whenNotFound() {
        // Arrange
        when(contractRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Contract result = contractService.showContract(999L);

        // Assert
        assertNull(result);
        verify(contractRepository).findById(999L);
    }

    @Test
    void findAllByValueLessThanEqual_shouldReturnContracts() {
        // Arrange
        BigDecimal value = new BigDecimal("15000.00");
        when(contractRepository.findAllByValueLessThanEqual(value)).thenReturn(testContracts);

        // Act
        Iterable<Contract> result = contractService.findAllByValueLessThanEqual(value);

        // Assert
        assertNotNull(result);
        verify(contractRepository).findAllByValueLessThanEqual(value);
    }

    @Test
    void findAllByValueGreaterThanEqual_shouldReturnContracts() {
        // Arrange
        BigDecimal value = new BigDecimal("5000.00");
        when(contractRepository.findAllByValueGreaterThanEqual(value)).thenReturn(testContracts);

        // Act
        Iterable<Contract> result = contractService.findAllByValueGreaterThanEqual(value);

        // Assert
        assertNotNull(result);
        verify(contractRepository).findAllByValueGreaterThanEqual(value);
    }

    @Test
    void findAllByBeginDate_shouldReturnContracts() {
        // Arrange
        LocalDate beginDate = LocalDate.of(2024, 1, 1);
        when(contractRepository.findAllByBeginDate(beginDate)).thenReturn(testContracts);

        // Act
        Iterable<Contract> result = contractService.findAllByBeginDate(beginDate);

        // Assert
        assertNotNull(result);
        verify(contractRepository).findAllByBeginDate(beginDate);
    }

    @Test
    void findAllByBeginDateBefore_shouldReturnContracts() {
        // Arrange
        LocalDate beforeDate = LocalDate.of(2024, 6, 1);
        when(contractRepository.findAllByBeginDateBefore(beforeDate)).thenReturn(testContracts);

        // Act
        Iterable<Contract> result = contractService.findAllByBeginDateBefore(beforeDate);

        // Assert
        assertNotNull(result);
        verify(contractRepository).findAllByBeginDateBefore(beforeDate);
    }

    @Test
    void findAllByBeginDateAfter_shouldReturnContracts() {
        // Arrange
        LocalDate afterDate = LocalDate.of(2023, 12, 1);
        when(contractRepository.findAllByBeginDateAfter(afterDate)).thenReturn(testContracts);

        // Act
        Iterable<Contract> result = contractService.findAllByBeginDateAfter(afterDate);

        // Assert
        assertNotNull(result);
        verify(contractRepository).findAllByBeginDateAfter(afterDate);
    }

    @Test
    void findAllByEndDate_shouldReturnContracts() {
        // Arrange
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        when(contractRepository.findAllByEndDate(endDate)).thenReturn(testContracts);

        // Act
        Iterable<Contract> result = contractService.findAllByEndDate(endDate);

        // Assert
        assertNotNull(result);
        verify(contractRepository).findAllByEndDate(endDate);
    }

    @Test
    void findAllByEndDateBefore_shouldReturnContracts() {
        // Arrange
        LocalDate beforeDate = LocalDate.of(2025, 1, 1);
        when(contractRepository.findAllByEndDateBefore(beforeDate)).thenReturn(testContracts);

        // Act
        Iterable<Contract> result = contractService.findAllByEndDateBefore(beforeDate);

        // Assert
        assertNotNull(result);
        verify(contractRepository).findAllByEndDateBefore(beforeDate);
    }

    @Test
    void findAllByEndDateAfter_shouldReturnContracts() {
        // Arrange
        LocalDate afterDate = LocalDate.of(2024, 6, 1);
        when(contractRepository.findAllByEndDateAfter(afterDate)).thenReturn(testContracts);

        // Act
        Iterable<Contract> result = contractService.findAllByEndDateAfter(afterDate);

        // Assert
        assertNotNull(result);
        verify(contractRepository).findAllByEndDateAfter(afterDate);
    }

    @Test
    void findAllByStatus_shouldReturnContracts() {
        // Arrange
        when(contractRepository.findAllByStatus(Status.PROPOSED)).thenReturn(testContracts);

        // Act
        Iterable<Contract> result = contractService.findAllByStatus(Status.PROPOSED);

        // Assert
        assertNotNull(result);
        verify(contractRepository).findAllByStatus(Status.PROPOSED);
    }

    @Test
    void findAllByCustomer_shouldReturnContracts() {
        // Arrange
        when(contractRepository.findAllByCustomer(testCustomer)).thenReturn(testContracts);

        // Act
        Iterable<Contract> result = contractService.findAllByCustomer(testCustomer);

        // Assert
        assertNotNull(result);
        verify(contractRepository).findAllByCustomer(testCustomer);
    }

    @Test
    void findAllByCustomerAndUser_shouldReturnContracts() {
        // Arrange
        when(contractRepository.findAllByCustomerAndUser(testCustomer, testUser)).thenReturn(testContracts);

        // Act
        Iterable<Contract> result = contractService.findAllByCustomerAndUser(testCustomer, testUser);

        // Assert
        assertNotNull(result);
        verify(contractRepository).findAllByCustomerAndUser(testCustomer, testUser);
    }

    @Test
    void findAllByUser_shouldReturnContracts() {
        // Arrange
        when(contractRepository.findAllByUser(testUser)).thenReturn(testContracts);

        // Act
        Iterable<Contract> result = contractService.findAllByUser(testUser);

        // Assert
        assertNotNull(result);
        verify(contractRepository).findAllByUser(testUser);
    }

    @Test
    void saveContract_shouldSaveContract() {
        // Arrange
        when(customerRepository.findAll()).thenReturn(Arrays.asList(testCustomer));
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        when(contractRepository.save(testContract)).thenReturn(testContract);

        // Act
        contractService.saveContract(testContract);

        // Assert
        verify(customerRepository).saveAll(any());
        verify(userRepository).saveAll(any());
        verify(contractRepository).save(testContract);
    }
}
