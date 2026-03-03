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

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder().id(1L).name("Test Company").build();
        testUser = User.builder().id(1L).username("testuser").build();

        testContract = Contract.builder()
                .id(1L)
                .name("Test Contract")
                .value(new BigDecimal("10000.00"))
                .beginDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(6))
                .status(Status.PROPOSED)
                .customer(testCustomer)
                .user(testUser)
                .build();
    }

    @Test
    void findByName_shouldReturnContract() {
        when(contractRepository.findByName("Test Contract")).thenReturn(testContract);

        Contract result = contractService.findByName("Test Contract");

        assertNotNull(result);
        assertEquals("Test Contract", result.getName());
        verify(contractRepository).findByName("Test Contract");
    }

    @Test
    void listAllContracts_shouldReturnAllContracts() {
        when(contractRepository.findAll()).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.listAllContracts();

        assertNotNull(result);
        verify(contractRepository).findAll();
    }

    @Test
    void showContract_shouldReturnContractById() {
        when(contractRepository.findById(1L)).thenReturn(Optional.of(testContract));

        Contract result = contractService.showContract(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(contractRepository).findById(1L);
    }

    @Test
    void showContract_withNonExistentId_shouldReturnNull() {
        when(contractRepository.findById(999L)).thenReturn(Optional.empty());

        Contract result = contractService.showContract(999L);

        assertNull(result);
        verify(contractRepository).findById(999L);
    }

    @Test
    void findAllByValueLessThanEqual_shouldReturnContracts() {
        BigDecimal value = new BigDecimal("15000.00");
        when(contractRepository.findAllByValueLessThanEqual(value)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByValueLessThanEqual(value);

        assertNotNull(result);
        verify(contractRepository).findAllByValueLessThanEqual(value);
    }

    @Test
    void findAllByValueGreaterThanEqual_shouldReturnContracts() {
        BigDecimal value = new BigDecimal("5000.00");
        when(contractRepository.findAllByValueGreaterThanEqual(value)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByValueGreaterThanEqual(value);

        assertNotNull(result);
        verify(contractRepository).findAllByValueGreaterThanEqual(value);
    }

    @Test
    void findAllByBeginDate_shouldReturnContracts() {
        LocalDate date = LocalDate.now();
        when(contractRepository.findAllByBeginDate(date)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByBeginDate(date);

        assertNotNull(result);
        verify(contractRepository).findAllByBeginDate(date);
    }

    @Test
    void findAllByBeginDateBefore_shouldReturnContracts() {
        LocalDate date = LocalDate.now().plusDays(1);
        when(contractRepository.findAllByBeginDateBefore(date)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByBeginDateBefore(date);

        assertNotNull(result);
        verify(contractRepository).findAllByBeginDateBefore(date);
    }

    @Test
    void findAllByBeginDateAfter_shouldReturnContracts() {
        LocalDate date = LocalDate.now().minusDays(1);
        when(contractRepository.findAllByBeginDateAfter(date)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByBeginDateAfter(date);

        assertNotNull(result);
        verify(contractRepository).findAllByBeginDateAfter(date);
    }

    @Test
    void findAllByEndDate_shouldReturnContracts() {
        LocalDate date = LocalDate.now().plusMonths(6);
        when(contractRepository.findAllByEndDate(date)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByEndDate(date);

        assertNotNull(result);
        verify(contractRepository).findAllByEndDate(date);
    }

    @Test
    void findAllByEndDateBefore_shouldReturnContracts() {
        LocalDate date = LocalDate.now().plusMonths(7);
        when(contractRepository.findAllByEndDateBefore(date)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByEndDateBefore(date);

        assertNotNull(result);
        verify(contractRepository).findAllByEndDateBefore(date);
    }

    @Test
    void findAllByEndDateAfter_shouldReturnContracts() {
        LocalDate date = LocalDate.now().plusMonths(5);
        when(contractRepository.findAllByEndDateAfter(date)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByEndDateAfter(date);

        assertNotNull(result);
        verify(contractRepository).findAllByEndDateAfter(date);
    }

    @Test
    void findAllByStatus_shouldReturnContracts() {
        when(contractRepository.findAllByStatus(Status.PROPOSED)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByStatus(Status.PROPOSED);

        assertNotNull(result);
        verify(contractRepository).findAllByStatus(Status.PROPOSED);
    }

    @Test
    void findAllByCustomer_shouldReturnContracts() {
        when(contractRepository.findAllByCustomer(testCustomer)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByCustomer(testCustomer);

        assertNotNull(result);
        verify(contractRepository).findAllByCustomer(testCustomer);
    }

    @Test
    void findAllByCustomerAndUser_shouldReturnContracts() {
        when(contractRepository.findAllByCustomerAndUser(testCustomer, testUser)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByCustomerAndUser(testCustomer, testUser);

        assertNotNull(result);
        verify(contractRepository).findAllByCustomerAndUser(testCustomer, testUser);
    }

    @Test
    void findAllByUser_shouldReturnContracts() {
        when(contractRepository.findAllByUser(testUser)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByUser(testUser);

        assertNotNull(result);
        verify(contractRepository).findAllByUser(testUser);
    }

    @Test
    void saveContract_shouldSaveContract() {
        when(contractRepository.save(testContract)).thenReturn(testContract);

        contractService.saveContract(testContract);

        verify(contractRepository).save(testContract);
    }
}
