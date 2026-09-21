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
    void testConstructor() {
        ContractServiceImpl service = new ContractServiceImpl(contractRepository, customerRepository, userRepository);
        assertNotNull(service);
    }

    @Test
    void testFindByName() {
        when(contractRepository.findByName("Contract-001")).thenReturn(contract);
        Contract result = contractService.findByName("Contract-001");
        assertEquals(contract, result);
        verify(contractRepository).findByName("Contract-001");
    }

    @Test
    void testFindByName_notFound() {
        when(contractRepository.findByName("NonExistent")).thenReturn(null);
        Contract result = contractService.findByName("NonExistent");
        assertNull(result);
        verify(contractRepository).findByName("NonExistent");
    }

    @Test
    void testListAllContracts() {
        List<Contract> contracts = Arrays.asList(contract);
        when(contractRepository.findAll()).thenReturn(contracts);
        Iterable<Contract> result = contractService.listAllContracts();
        assertNotNull(result);
        verify(contractRepository).findAll();
    }

    @Test
    void testShowContract_found() {
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        Contract result = contractService.showContract(1L);
        assertNotNull(result);
        assertEquals(contract, result);
        verify(contractRepository).findById(1L);
    }

    @Test
    void testShowContract_notFound() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());
        Contract result = contractService.showContract(99L);
        assertNull(result);
        verify(contractRepository).findById(99L);
    }

    @Test
    void testFindAllByValueLessThanEqual() {
        List<Contract> contracts = Arrays.asList(contract);
        BigDecimal value = new BigDecimal("15000.00");
        when(contractRepository.findAllByValueLessThanEqual(value)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByValueLessThanEqual(value);
        assertNotNull(result);
        verify(contractRepository).findAllByValueLessThanEqual(value);
    }

    @Test
    void testFindAllByValueGreaterThanEqual() {
        List<Contract> contracts = Arrays.asList(contract);
        BigDecimal value = new BigDecimal("5000.00");
        when(contractRepository.findAllByValueGreaterThanEqual(value)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByValueGreaterThanEqual(value);
        assertNotNull(result);
        verify(contractRepository).findAllByValueGreaterThanEqual(value);
    }

    @Test
    void testFindAllByBeginDate() {
        List<Contract> contracts = Arrays.asList(contract);
        LocalDate date = LocalDate.of(2024, 1, 1);
        when(contractRepository.findAllByBeginDate(date)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByBeginDate(date);
        assertNotNull(result);
        verify(contractRepository).findAllByBeginDate(date);
    }

    @Test
    void testFindAllByBeginDateBefore() {
        List<Contract> contracts = Arrays.asList(contract);
        LocalDate date = LocalDate.of(2024, 6, 1);
        when(contractRepository.findAllByBeginDateBefore(date)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByBeginDateBefore(date);
        assertNotNull(result);
        verify(contractRepository).findAllByBeginDateBefore(date);
    }

    @Test
    void testFindAllByBeginDateAfter() {
        List<Contract> contracts = new ArrayList<>();
        LocalDate date = LocalDate.of(2024, 6, 1);
        when(contractRepository.findAllByBeginDateAfter(date)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByBeginDateAfter(date);
        assertNotNull(result);
        verify(contractRepository).findAllByBeginDateAfter(date);
    }

    @Test
    void testFindAllByEndDate() {
        List<Contract> contracts = Arrays.asList(contract);
        LocalDate date = LocalDate.of(2024, 12, 31);
        when(contractRepository.findAllByEndDate(date)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByEndDate(date);
        assertNotNull(result);
        verify(contractRepository).findAllByEndDate(date);
    }

    @Test
    void testFindAllByEndDateBefore() {
        List<Contract> contracts = Arrays.asList(contract);
        LocalDate date = LocalDate.of(2025, 1, 1);
        when(contractRepository.findAllByEndDateBefore(date)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByEndDateBefore(date);
        assertNotNull(result);
        verify(contractRepository).findAllByEndDateBefore(date);
    }

    @Test
    void testFindAllByEndDateAfter() {
        List<Contract> contracts = new ArrayList<>();
        LocalDate date = LocalDate.of(2024, 6, 1);
        when(contractRepository.findAllByEndDateAfter(date)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByEndDateAfter(date);
        assertNotNull(result);
        verify(contractRepository).findAllByEndDateAfter(date);
    }

    @Test
    void testFindAllByStatus_Proposed() {
        List<Contract> contracts = Arrays.asList(contract);
        when(contractRepository.findAllByStatus(Status.PROPOSED)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByStatus(Status.PROPOSED);
        assertNotNull(result);
        verify(contractRepository).findAllByStatus(Status.PROPOSED);
    }

    @Test
    void testFindAllByStatus_Done() {
        List<Contract> contracts = new ArrayList<>();
        when(contractRepository.findAllByStatus(Status.DONE)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByStatus(Status.DONE);
        assertNotNull(result);
        verify(contractRepository).findAllByStatus(Status.DONE);
    }

    @Test
    void testFindAllByCustomer() {
        List<Contract> contracts = Arrays.asList(contract);
        when(contractRepository.findAllByCustomer(customer)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByCustomer(customer);
        assertNotNull(result);
        verify(contractRepository).findAllByCustomer(customer);
    }

    @Test
    void testFindAllByCustomerAndUser() {
        List<Contract> contracts = Arrays.asList(contract);
        when(contractRepository.findAllByCustomerAndUser(customer, user)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByCustomerAndUser(customer, user);
        assertNotNull(result);
        verify(contractRepository).findAllByCustomerAndUser(customer, user);
    }

    @Test
    void testFindAllByUser() {
        List<Contract> contracts = Arrays.asList(contract);
        when(contractRepository.findAllByUser(user)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByUser(user);
        assertNotNull(result);
        verify(contractRepository).findAllByUser(user);
    }

    @Test
    void testSaveContract() {
        List<Customer> customers = Arrays.asList(customer);
        List<User> users = Arrays.asList(user);
        when(customerRepository.findAll()).thenReturn(customers);
        when(userRepository.findAll()).thenReturn(users);
        when(customerRepository.saveAll(customers)).thenReturn(customers);
        when(userRepository.saveAll(users)).thenReturn(users);

        contractService.saveContract(contract);

        verify(customerRepository).findAll();
        verify(userRepository).findAll();
        verify(contractRepository).save(contract);
    }
}
