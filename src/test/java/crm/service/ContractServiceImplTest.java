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

    private Contract contract1;
    private Contract contract2;
    private Customer customer;
    private User user;

    @BeforeEach
    void setUp() {
        customer = Customer.builder().id(1L).name("Corp A").email("a@corp.com").enabled(1).build();

        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");

        user = User.builder().id(1L).username("johndoe").email("john@test.com")
                .firstName("John").lastName("Doe").enabled(1).role(role).build();

        contract1 = Contract.builder()
                .id(1L).name("Contract A").content("Content A")
                .value(new BigDecimal("1000.00"))
                .beginDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .status(Status.PROPOSED)
                .customer(customer).user(user).build();

        contract2 = Contract.builder()
                .id(2L).name("Contract B").content("Content B")
                .value(new BigDecimal("5000.00"))
                .beginDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .status(Status.DONE)
                .customer(customer).user(user).build();
    }

    @Test
    void testFindByName_ReturnsContract() {
        when(contractRepository.findByName("Contract A")).thenReturn(contract1);
        Contract result = contractService.findByName("Contract A");
        assertEquals(contract1, result);
        verify(contractRepository).findByName("Contract A");
    }

    @Test
    void testFindByName_NotFound_ReturnsNull() {
        when(contractRepository.findByName("Unknown")).thenReturn(null);
        Contract result = contractService.findByName("Unknown");
        assertNull(result);
        verify(contractRepository).findByName("Unknown");
    }

    @Test
    void testListAllContracts_ReturnsAllContracts() {
        List<Contract> contracts = Arrays.asList(contract1, contract2);
        when(contractRepository.findAll()).thenReturn(contracts);
        Iterable<Contract> result = contractService.listAllContracts();
        assertNotNull(result);
        verify(contractRepository).findAll();
    }

    @Test
    void testShowContract_ExistingId_ReturnsContract() {
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract1));
        Contract result = contractService.showContract(1L);
        assertNotNull(result);
        assertEquals(contract1, result);
        verify(contractRepository).findById(1L);
    }

    @Test
    void testShowContract_NonExistingId_ReturnsNull() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());
        Contract result = contractService.showContract(99L);
        assertNull(result);
        verify(contractRepository).findById(99L);
    }

    @Test
    void testFindAllByValueLessThanEqual_ReturnsContracts() {
        BigDecimal value = new BigDecimal("2000.00");
        List<Contract> contracts = Collections.singletonList(contract1);
        when(contractRepository.findAllByValueLessThanEqual(value)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByValueLessThanEqual(value);
        assertNotNull(result);
        verify(contractRepository).findAllByValueLessThanEqual(value);
    }

    @Test
    void testFindAllByValueGreaterThanEqual_ReturnsContracts() {
        BigDecimal value = new BigDecimal("3000.00");
        List<Contract> contracts = Collections.singletonList(contract2);
        when(contractRepository.findAllByValueGreaterThanEqual(value)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByValueGreaterThanEqual(value);
        assertNotNull(result);
        verify(contractRepository).findAllByValueGreaterThanEqual(value);
    }

    @Test
    void testFindAllByBeginDate_ReturnsContracts() {
        LocalDate date = LocalDate.of(2023, 1, 1);
        List<Contract> contracts = Collections.singletonList(contract1);
        when(contractRepository.findAllByBeginDate(date)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByBeginDate(date);
        assertNotNull(result);
        verify(contractRepository).findAllByBeginDate(date);
    }

    @Test
    void testFindAllByBeginDateBefore_ReturnsContracts() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        List<Contract> contracts = Collections.singletonList(contract1);
        when(contractRepository.findAllByBeginDateBefore(date)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByBeginDateBefore(date);
        assertNotNull(result);
        verify(contractRepository).findAllByBeginDateBefore(date);
    }

    @Test
    void testFindAllByBeginDateAfter_ReturnsContracts() {
        LocalDate date = LocalDate.of(2022, 1, 1);
        List<Contract> contracts = Arrays.asList(contract1, contract2);
        when(contractRepository.findAllByBeginDateAfter(date)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByBeginDateAfter(date);
        assertNotNull(result);
        verify(contractRepository).findAllByBeginDateAfter(date);
    }

    @Test
    void testFindAllByEndDate_ReturnsContracts() {
        LocalDate date = LocalDate.of(2023, 12, 31);
        List<Contract> contracts = Collections.singletonList(contract1);
        when(contractRepository.findAllByEndDate(date)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByEndDate(date);
        assertNotNull(result);
        verify(contractRepository).findAllByEndDate(date);
    }

    @Test
    void testFindAllByEndDateBefore_ReturnsContracts() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        List<Contract> contracts = Arrays.asList(contract1, contract2);
        when(contractRepository.findAllByEndDateBefore(date)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByEndDateBefore(date);
        assertNotNull(result);
        verify(contractRepository).findAllByEndDateBefore(date);
    }

    @Test
    void testFindAllByEndDateAfter_ReturnsContracts() {
        LocalDate date = LocalDate.of(2022, 1, 1);
        List<Contract> contracts = Arrays.asList(contract1, contract2);
        when(contractRepository.findAllByEndDateAfter(date)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByEndDateAfter(date);
        assertNotNull(result);
        verify(contractRepository).findAllByEndDateAfter(date);
    }

    @Test
    void testFindAllByStatus_Proposed_ReturnsContracts() {
        List<Contract> contracts = Collections.singletonList(contract1);
        when(contractRepository.findAllByStatus(Status.PROPOSED)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByStatus(Status.PROPOSED);
        assertNotNull(result);
        verify(contractRepository).findAllByStatus(Status.PROPOSED);
    }

    @Test
    void testFindAllByStatus_Done_ReturnsContracts() {
        List<Contract> contracts = Collections.singletonList(contract2);
        when(contractRepository.findAllByStatus(Status.DONE)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByStatus(Status.DONE);
        assertNotNull(result);
        verify(contractRepository).findAllByStatus(Status.DONE);
    }

    @Test
    void testFindAllByCustomer_ReturnsContracts() {
        List<Contract> contracts = Arrays.asList(contract1, contract2);
        when(contractRepository.findAllByCustomer(customer)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByCustomer(customer);
        assertNotNull(result);
        verify(contractRepository).findAllByCustomer(customer);
    }

    @Test
    void testFindAllByCustomerAndUser_ReturnsContracts() {
        List<Contract> contracts = Arrays.asList(contract1, contract2);
        when(contractRepository.findAllByCustomerAndUser(customer, user)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByCustomerAndUser(customer, user);
        assertNotNull(result);
        verify(contractRepository).findAllByCustomerAndUser(customer, user);
    }

    @Test
    void testFindAllByUser_ReturnsContracts() {
        List<Contract> contracts = Arrays.asList(contract1, contract2);
        when(contractRepository.findAllByUser(user)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByUser(user);
        assertNotNull(result);
        verify(contractRepository).findAllByUser(user);
    }

    @Test
    void testSaveContract_SavesSuccessfully() {
        List<Customer> customers = Collections.singletonList(customer);
        List<User> users = Collections.singletonList(user);
        when(customerRepository.findAll()).thenReturn(customers);
        when(customerRepository.saveAll(customers)).thenReturn(customers);
        when(userRepository.findAll()).thenReturn(users);
        when(userRepository.saveAll(users)).thenReturn(users);

        contractService.saveContract(contract1);

        verify(customerRepository).findAll();
        verify(customerRepository).saveAll(customers);
        verify(userRepository).findAll();
        verify(userRepository).saveAll(users);
        verify(contractRepository).save(contract1);
    }

    @Test
    void testFindAllByStatus_Negotiated_ReturnsContracts() {
        List<Contract> contracts = new ArrayList<>();
        when(contractRepository.findAllByStatus(Status.NEGOTIATED)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByStatus(Status.NEGOTIATED);
        assertNotNull(result);
        verify(contractRepository).findAllByStatus(Status.NEGOTIATED);
    }

    @Test
    void testFindAllByStatus_Implemented_ReturnsContracts() {
        List<Contract> contracts = new ArrayList<>();
        when(contractRepository.findAllByStatus(Status.IMPLEMENTED)).thenReturn(contracts);
        Iterable<Contract> result = contractService.findAllByStatus(Status.IMPLEMENTED);
        assertNotNull(result);
        verify(contractRepository).findAllByStatus(Status.IMPLEMENTED);
    }
}
