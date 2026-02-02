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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContractServiceImplTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ContractServiceImpl contractService;

    private Contract contract;
    private Customer customer;
    private User user;
    private LocalDate beginDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);
        customer.setName("Test Customer");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        beginDate = LocalDate.of(2023, 1, 1);
        endDate = LocalDate.of(2023, 12, 31);

        contract = new Contract();
        contract.setId(1L);
        contract.setName("Test Contract");
        contract.setContent("Contract content");
        contract.setValue(new BigDecimal("1000.00"));
        contract.setBeginDate(beginDate);
        contract.setEndDate(endDate);
        contract.setStatus(Status.PROPOSED);
        contract.setCustomer(customer);
        contract.setUser(user);
    }

    @Test
    void findByNameShouldReturnContract() {
        // Arrange
        when(contractRepository.findByName("Test Contract")).thenReturn(contract);

        // Act
        Contract result = contractService.findByName("Test Contract");

        // Assert
        assertEquals(contract, result);
        verify(contractRepository).findByName("Test Contract");
    }

    @Test
    void listAllContractsShouldReturnAllContracts() {
        // Arrange
        List<Contract> contracts = Arrays.asList(contract);
        when(contractRepository.findAll()).thenReturn(contracts);

        // Act
        Iterable<Contract> result = contractService.listAllContracts();

        // Assert
        assertEquals(contracts, result);
        verify(contractRepository).findAll();
    }

    @Test
    void showContractShouldReturnContractWhenFound() {
        // Arrange
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));

        // Act
        Contract result = contractService.showContract(1L);

        // Assert
        assertEquals(contract, result);
        verify(contractRepository).findById(1L);
    }

    @Test
    void showContractShouldReturnNullWhenNotFound() {
        // Arrange
        when(contractRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
        Contract result = contractService.showContract(2L);

        // Assert
        assertNull(result);
        verify(contractRepository).findById(2L);
    }

    @Test
    void saveContractShouldSaveContract() {
        // Arrange
        when(contractRepository.save(contract)).thenReturn(contract);

        // Act
        contractService.saveContract(contract);

        // Assert
        verify(contractRepository).save(contract);
        verify(customerRepository, never()).findAll();
        verify(customerRepository, never()).save(any());
        verify(userRepository, never()).findAll();
        verify(userRepository, never()).save(any());
    }

    @Test
    void findAllByValueLessThanEqualShouldReturnContracts() {
        // Arrange
        BigDecimal value = new BigDecimal("1000.00");
        List<Contract> contracts = Arrays.asList(contract);
        when(contractRepository.findAllByValueLessThanEqual(value)).thenReturn(contracts);

        // Act
        Iterable<Contract> result = contractService.findAllByValueLessThanEqual(value);

        // Assert
        assertEquals(contracts, result);
        verify(contractRepository).findAllByValueLessThanEqual(value);
    }

    @Test
    void findAllByStatusShouldReturnContracts() {
        // Arrange
        Status status = Status.PROPOSED;
        List<Contract> contracts = Arrays.asList(contract);
        when(contractRepository.findAllByStatus(status)).thenReturn(contracts);

        // Act
        Iterable<Contract> result = contractService.findAllByStatus(status);

        // Assert
        assertEquals(contracts, result);
        verify(contractRepository).findAllByStatus(status);
    }

    @Test
    void findAllByCustomerShouldReturnContracts() {
        // Arrange
        List<Contract> contracts = Arrays.asList(contract);
        when(contractRepository.findAllByCustomer(customer)).thenReturn(contracts);

        // Act
        Iterable<Contract> result = contractService.findAllByCustomer(customer);

        // Assert
        assertEquals(contracts, result);
        verify(contractRepository).findAllByCustomer(customer);
    }
}