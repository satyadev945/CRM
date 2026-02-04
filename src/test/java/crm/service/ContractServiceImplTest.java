package crm.service;

import crm.entity.*;
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
import java.util.*;

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
    private LocalDate beginDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        // Create role
        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_ADMIN");

        // Create user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRole(role);

        // Create customer
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Customer");
        testCustomer.setEmail("customer@test.com");

        // Create contract dates
        beginDate = LocalDate.of(2023, 1, 1);
        endDate = LocalDate.of(2023, 12, 31);

        // Create contract
        testContract = new Contract();
        testContract.setId(1L);
        testContract.setName("Test Contract");
        testContract.setContent("Contract content");
        testContract.setValue(new BigDecimal("1000.00"));
        testContract.setBeginDate(beginDate);
        testContract.setEndDate(endDate);
        testContract.setStatus(Status.PROPOSED);
        testContract.setCustomer(testCustomer);
        testContract.setUser(testUser);
    }

    @Test
    void findByName_ReturnsContract() {
        // Arrange
        when(contractRepository.findByName("Test Contract")).thenReturn(testContract);

        // Act
        Contract result = contractService.findByName("Test Contract");

        // Assert
        assertEquals(testContract, result);
        verify(contractRepository).findByName("Test Contract");
    }

    @Test
    void listAllContracts_ReturnsAllContracts() {
        // Arrange
        List<Contract> contracts = Collections.singletonList(testContract);
        when(contractRepository.findAll()).thenReturn(contracts);

        // Act
        Iterable<Contract> result = contractService.listAllContracts();

        // Assert
        assertEquals(contracts, result);
        verify(contractRepository).findAll();
    }

    @Test
    void showContract_ExistingContract_ReturnsContract() {
        // Arrange
        when(contractRepository.findById(1L)).thenReturn(Optional.of(testContract));

        // Act
        Contract result = contractService.showContract(1L);

        // Assert
        assertEquals(testContract, result);
        verify(contractRepository).findById(1L);
    }

    @Test
    void showContract_NonExistingContract_ReturnsNull() {
        // Arrange
        when(contractRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Contract result = contractService.showContract(999L);

        // Assert
        assertNull(result);
        verify(contractRepository).findById(999L);
    }

    @Test
    void findAllByValueLessThanEqual_ReturnsFilteredContracts() {
        // Arrange
        BigDecimal maxValue = new BigDecimal("2000.00");
        List<Contract> contracts = Collections.singletonList(testContract);
        when(contractRepository.findAllByValueLessThanEqual(maxValue)).thenReturn(contracts);

        // Act
        Iterable<Contract> result = contractService.findAllByValueLessThanEqual(maxValue);

        // Assert
        assertEquals(contracts, result);
        verify(contractRepository).findAllByValueLessThanEqual(maxValue);
    }

    @Test
    void findAllByValueGreaterThanEqual_ReturnsFilteredContracts() {
        // Arrange
        BigDecimal minValue = new BigDecimal("500.00");
        List<Contract> contracts = Collections.singletonList(testContract);
        when(contractRepository.findAllByValueGreaterThanEqual(minValue)).thenReturn(contracts);

        // Act
        Iterable<Contract> result = contractService.findAllByValueGreaterThanEqual(minValue);

        // Assert
        assertEquals(contracts, result);
        verify(contractRepository).findAllByValueGreaterThanEqual(minValue);
    }

    @Test
    void findAllByBeginDate_ReturnsFilteredContracts() {
        // Arrange
        List<Contract> contracts = Collections.singletonList(testContract);
        when(contractRepository.findAllByBeginDate(beginDate)).thenReturn(contracts);

        // Act
        Iterable<Contract> result = contractService.findAllByBeginDate(beginDate);

        // Assert
        assertEquals(contracts, result);
        verify(contractRepository).findAllByBeginDate(beginDate);
    }

    @Test
    void findAllByBeginDateBefore_ReturnsFilteredContracts() {
        // Arrange
        LocalDate date = LocalDate.of(2023, 2, 1);
        List<Contract> contracts = Collections.singletonList(testContract);
        when(contractRepository.findAllByBeginDateBefore(date)).thenReturn(contracts);

        // Act
        Iterable<Contract> result = contractService.findAllByBeginDateBefore(date);

        // Assert
        assertEquals(contracts, result);
        verify(contractRepository).findAllByBeginDateBefore(date);
    }

    @Test
    void findAllByBeginDateAfter_ReturnsFilteredContracts() {
        // Arrange
        LocalDate date = LocalDate.of(2022, 12, 1);
        List<Contract> contracts = Collections.singletonList(testContract);
        when(contractRepository.findAllByBeginDateAfter(date)).thenReturn(contracts);

        // Act
        Iterable<Contract> result = contractService.findAllByBeginDateAfter(date);

        // Assert
        assertEquals(contracts, result);
        verify(contractRepository).findAllByBeginDateAfter(date);
    }

    @Test
    void findAllByEndDate_ReturnsFilteredContracts() {
        // Arrange
        List<Contract> contracts = Collections.singletonList(testContract);
        when(contractRepository.findAllByEndDate(endDate)).thenReturn(contracts);

        // Act
        Iterable<Contract> result = contractService.findAllByEndDate(endDate);

        // Assert
        assertEquals(contracts, result);
        verify(contractRepository).findAllByEndDate(endDate);
    }

    @Test
    void findAllByEndDateBefore_ReturnsFilteredContracts() {
        // Arrange
        LocalDate date = LocalDate.of(2024, 1, 31);
        List<Contract> contracts = Collections.singletonList(testContract);
        when(contractRepository.findAllByEndDateBefore(date)).thenReturn(contracts);

        // Act
        Iterable<Contract> result = contractService.findAllByEndDateBefore(date);

        // Assert
        assertEquals(contracts, result);
        verify(contractRepository).findAllByEndDateBefore(date);
    }

    @Test
    void findAllByEndDateAfter_ReturnsFilteredContracts() {
        // Arrange
        LocalDate date = LocalDate.of(2023, 11, 30);
        List<Contract> contracts = Collections.singletonList(testContract);
        when(contractRepository.findAllByEndDateAfter(date)).thenReturn(contracts);

        // Act
        Iterable<Contract> result = contractService.findAllByEndDateAfter(date);

        // Assert
        assertEquals(contracts, result);
        verify(contractRepository).findAllByEndDateAfter(date);
    }

    @Test
    void findAllByStatus_ReturnsFilteredContracts() {
        // Arrange
        List<Contract> contracts = Collections.singletonList(testContract);
        when(contractRepository.findAllByStatus(Status.PROPOSED)).thenReturn(contracts);

        // Act
        Iterable<Contract> result = contractService.findAllByStatus(Status.PROPOSED);

        // Assert
        assertEquals(contracts, result);
        verify(contractRepository).findAllByStatus(Status.PROPOSED);
    }

    @Test
    void findAllByCustomer_ReturnsFilteredContracts() {
        // Arrange
        List<Contract> contracts = Collections.singletonList(testContract);
        when(contractRepository.findAllByCustomer(testCustomer)).thenReturn(contracts);

        // Act
        Iterable<Contract> result = contractService.findAllByCustomer(testCustomer);

        // Assert
        assertEquals(contracts, result);
        verify(contractRepository).findAllByCustomer(testCustomer);
    }

    @Test
    void findAllByUser_ReturnsFilteredContracts() {
        // Arrange
        List<Contract> contracts = Collections.singletonList(testContract);
        when(contractRepository.findAllByUser(testUser)).thenReturn(contracts);

        // Act
        Iterable<Contract> result = contractService.findAllByUser(testUser);

        // Assert
        assertEquals(contracts, result);
        verify(contractRepository).findAllByUser(testUser);
    }

    @Test
    void findAllByCustomerAndUser_ReturnsFilteredContracts() {
        // Arrange
        List<Contract> contracts = Collections.singletonList(testContract);
        when(contractRepository.findAllByCustomerAndUser(testCustomer, testUser)).thenReturn(contracts);

        // Act
        Iterable<Contract> result = contractService.findAllByCustomerAndUser(testCustomer, testUser);

        // Assert
        assertEquals(contracts, result);
        verify(contractRepository).findAllByCustomerAndUser(testCustomer, testUser);
    }

    @Test
    @SuppressWarnings("unchecked")
    void saveContract_SavesContract() {
        // Arrange
        List<Customer> customers = Collections.singletonList(testCustomer);
        List<User> users = Collections.singletonList(testUser);

        when(customerRepository.findAll()).thenReturn(customers);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        contractService.saveContract(testContract);

        // Assert
        verify(customerRepository).findAll();
        verify(userRepository).findAll();
        verify(customerRepository).saveAll(customers);
        verify(userRepository).saveAll(users);
        verify(contractRepository).save(testContract);
    }

    @Test
    void constructor_InitializesRepositories() {
        // Arrange
        ContractRepository mockContractRepo = mock(ContractRepository.class);
        CustomerRepository mockCustomerRepo = mock(CustomerRepository.class);
        UserRepository mockUserRepo = mock(UserRepository.class);

        // Act
        ContractServiceImpl service = new ContractServiceImpl(mockContractRepo, mockCustomerRepo, mockUserRepo);

        // Use reflection to check if repositories are set
        try {
            java.lang.reflect.Field contractRepoField = ContractServiceImpl.class.getDeclaredField("contractRepository");
            java.lang.reflect.Field customerRepoField = ContractServiceImpl.class.getDeclaredField("customerRepository");
            java.lang.reflect.Field userRepoField = ContractServiceImpl.class.getDeclaredField("userRepository");

            contractRepoField.setAccessible(true);
            customerRepoField.setAccessible(true);
            userRepoField.setAccessible(true);

            ContractRepository injectedContractRepo = (ContractRepository) contractRepoField.get(service);
            CustomerRepository injectedCustomerRepo = (CustomerRepository) customerRepoField.get(service);
            UserRepository injectedUserRepo = (UserRepository) userRepoField.get(service);

            // Assert
            assertEquals(mockContractRepo, injectedContractRepo);
            assertEquals(mockCustomerRepo, injectedCustomerRepo);
            assertEquals(mockUserRepo, injectedUserRepo);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // If reflection fails, this assertion will fail the test
            fail("Exception occurred during reflection: " + e.getMessage());
        }
    }
}