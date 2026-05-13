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
import java.util.Arrays;
import java.util.Collections;
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

        contract = Contract.builder()
                .id(1L)
                .name("Contract-001")
                .value(new BigDecimal("5000.00"))
                .beginDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .status(Status.PROPOSED)
                .customer(customer)
                .user(user)
                .build();
    }

    @Test
    void testConstructor() {
        ContractServiceImpl service = new ContractServiceImpl(contractRepository, customerRepository, userRepository);
        assertNotNull(service);
    }

    @Test
    void testFindByName_returnsContract() {
        when(contractRepository.findByName("Contract-001")).thenReturn(contract);
        Contract result = contractService.findByName("Contract-001");
        assertNotNull(result);
        assertEquals("Contract-001", result.getName());
    }

    @Test
    void testFindByName_returnsNull_whenNotFound() {
        when(contractRepository.findByName("Unknown")).thenReturn(null);
        Contract result = contractService.findByName("Unknown");
        assertNull(result);
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
    void testShowContract_returnsContract_whenFound() {
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        Contract result = contractService.showContract(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testShowContract_returnsNull_whenNotFound() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());
        Contract result = contractService.showContract(99L);
        assertNull(result);
    }

    @Test
    void testFindAllByValueLessThanEqual() {
        BigDecimal value = new BigDecimal("6000.00");
        when(contractRepository.findAllByValueLessThanEqual(value)).thenReturn(Arrays.asList(contract));
        Iterable<Contract> result = contractService.findAllByValueLessThanEqual(value);
        assertNotNull(result);
        verify(contractRepository).findAllByValueLessThanEqual(value);
    }

    @Test
    void testFindAllByValueGreaterThanEqual() {
        BigDecimal value = new BigDecimal("1000.00");
        when(contractRepository.findAllByValueGreaterThanEqual(value)).thenReturn(Arrays.asList(contract));
        Iterable<Contract> result = contractService.findAllByValueGreaterThanEqual(value);
        assertNotNull(result);
        verify(contractRepository).findAllByValueGreaterThanEqual(value);
    }

    @Test
    void testFindAllByBeginDate() {
        LocalDate date = LocalDate.of(2023, 1, 1);
        when(contractRepository.findAllByBeginDate(date)).thenReturn(Arrays.asList(contract));
        Iterable<Contract> result = contractService.findAllByBeginDate(date);
        assertNotNull(result);
        verify(contractRepository).findAllByBeginDate(date);
    }

    @Test
    void testFindAllByBeginDateBefore() {
        LocalDate date = LocalDate.of(2023, 6, 1);
        when(contractRepository.findAllByBeginDateBefore(date)).thenReturn(Arrays.asList(contract));
        Iterable<Contract> result = contractService.findAllByBeginDateBefore(date);
        assertNotNull(result);
        verify(contractRepository).findAllByBeginDateBefore(date);
    }

    @Test
    void testFindAllByBeginDateAfter() {
        LocalDate date = LocalDate.of(2022, 12, 31);
        when(contractRepository.findAllByBeginDateAfter(date)).thenReturn(Arrays.asList(contract));
        Iterable<Contract> result = contractService.findAllByBeginDateAfter(date);
        assertNotNull(result);
        verify(contractRepository).findAllByBeginDateAfter(date);
    }

    @Test
    void testFindAllByEndDate() {
        LocalDate date = LocalDate.of(2023, 12, 31);
        when(contractRepository.findAllByEndDate(date)).thenReturn(Arrays.asList(contract));
        Iterable<Contract> result = contractService.findAllByEndDate(date);
        assertNotNull(result);
        verify(contractRepository).findAllByEndDate(date);
    }

    @Test
    void testFindAllByEndDateBefore() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        when(contractRepository.findAllByEndDateBefore(date)).thenReturn(Arrays.asList(contract));
        Iterable<Contract> result = contractService.findAllByEndDateBefore(date);
        assertNotNull(result);
        verify(contractRepository).findAllByEndDateBefore(date);
    }

    @Test
    void testFindAllByEndDateAfter() {
        LocalDate date = LocalDate.of(2023, 6, 1);
        when(contractRepository.findAllByEndDateAfter(date)).thenReturn(Arrays.asList(contract));
        Iterable<Contract> result = contractService.findAllByEndDateAfter(date);
        assertNotNull(result);
        verify(contractRepository).findAllByEndDateAfter(date);
    }

    @Test
    void testFindAllByStatus() {
        when(contractRepository.findAllByStatus(Status.PROPOSED)).thenReturn(Arrays.asList(contract));
        Iterable<Contract> result = contractService.findAllByStatus(Status.PROPOSED);
        assertNotNull(result);
        verify(contractRepository).findAllByStatus(Status.PROPOSED);
    }

    @Test
    void testFindAllByCustomer() {
        when(contractRepository.findAllByCustomer(customer)).thenReturn(Arrays.asList(contract));
        Iterable<Contract> result = contractService.findAllByCustomer(customer);
        assertNotNull(result);
        verify(contractRepository).findAllByCustomer(customer);
    }

    @Test
    void testFindAllByCustomerAndUser() {
        when(contractRepository.findAllByCustomerAndUser(customer, user)).thenReturn(Arrays.asList(contract));
        Iterable<Contract> result = contractService.findAllByCustomerAndUser(customer, user);
        assertNotNull(result);
        verify(contractRepository).findAllByCustomerAndUser(customer, user);
    }

    @Test
    void testFindAllByUser() {
        when(contractRepository.findAllByUser(user)).thenReturn(Arrays.asList(contract));
        Iterable<Contract> result = contractService.findAllByUser(user);
        assertNotNull(result);
        verify(contractRepository).findAllByUser(user);
    }

    @Test
    void testSaveContract() {
        List<Customer> customers = Collections.emptyList();
        List<User> users = Collections.emptyList();
        when(customerRepository.findAll()).thenReturn(customers);
        when(userRepository.findAll()).thenReturn(users);
        when(customerRepository.saveAll(customers)).thenReturn(customers);
        when(userRepository.saveAll(users)).thenReturn(users);

        contractService.saveContract(contract);

        verify(contractRepository).save(contract);
    }

    @Test
    void testFindAllByStatusNegotiated() {
        when(contractRepository.findAllByStatus(Status.NEGOTIATED)).thenReturn(Arrays.asList(contract));
        Iterable<Contract> result = contractService.findAllByStatus(Status.NEGOTIATED);
        assertNotNull(result);
    }

    @Test
    void testFindAllByStatusDone() {
        when(contractRepository.findAllByStatus(Status.DONE)).thenReturn(Collections.emptyList());
        Iterable<Contract> result = contractService.findAllByStatus(Status.DONE);
        assertNotNull(result);
    }
}
