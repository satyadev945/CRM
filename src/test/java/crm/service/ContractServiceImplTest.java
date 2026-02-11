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
import static org.mockito.ArgumentMatchers.any;
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
        testCustomer = Customer.builder().id(1L).name("Acme Corp").build();
        testUser = User.builder().id(1L).username("testuser").build();

        testContract = Contract.builder()
                .id(1L)
                .name("Service Contract 2024")
                .content("Annual service agreement")
                .value(new BigDecimal("50000.00"))
                .beginDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .status(Status.PROPOSED)
                .customer(testCustomer)
                .user(testUser)
                .build();
    }

    @Test
    void testFindByName() {
        when(contractRepository.findByName("Service Contract 2024")).thenReturn(testContract);

        Contract result = contractService.findByName("Service Contract 2024");

        assertNotNull(result);
        assertEquals("Service Contract 2024", result.getName());
        verify(contractRepository).findByName("Service Contract 2024");
    }

    @Test
    void testListAllContracts() {
        when(contractRepository.findAll()).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.listAllContracts();

        assertNotNull(result);
        verify(contractRepository).findAll();
    }

    @Test
    void testShowContract() {
        when(contractRepository.findById(1L)).thenReturn(Optional.of(testContract));

        Contract result = contractService.showContract(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(contractRepository).findById(1L);
    }

    @Test
    void testShowContractNotFound() {
        when(contractRepository.findById(999L)).thenReturn(Optional.empty());

        Contract result = contractService.showContract(999L);

        assertNull(result);
        verify(contractRepository).findById(999L);
    }

    @Test
    void testFindAllByValueLessThanEqual() {
        BigDecimal value = new BigDecimal("60000.00");
        when(contractRepository.findAllByValueLessThanEqual(value)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByValueLessThanEqual(value);

        assertNotNull(result);
        verify(contractRepository).findAllByValueLessThanEqual(value);
    }

    @Test
    void testFindAllByValueGreaterThanEqual() {
        BigDecimal value = new BigDecimal("40000.00");
        when(contractRepository.findAllByValueGreaterThanEqual(value)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByValueGreaterThanEqual(value);

        assertNotNull(result);
        verify(contractRepository).findAllByValueGreaterThanEqual(value);
    }

    @Test
    void testFindAllByBeginDate() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        when(contractRepository.findAllByBeginDate(date)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByBeginDate(date);

        assertNotNull(result);
        verify(contractRepository).findAllByBeginDate(date);
    }

    @Test
    void testFindAllByBeginDateBefore() {
        LocalDate date = LocalDate.of(2024, 6, 1);
        when(contractRepository.findAllByBeginDateBefore(date)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByBeginDateBefore(date);

        assertNotNull(result);
        verify(contractRepository).findAllByBeginDateBefore(date);
    }

    @Test
    void testFindAllByBeginDateAfter() {
        LocalDate date = LocalDate.of(2023, 12, 1);
        when(contractRepository.findAllByBeginDateAfter(date)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByBeginDateAfter(date);

        assertNotNull(result);
        verify(contractRepository).findAllByBeginDateAfter(date);
    }

    @Test
    void testFindAllByEndDate() {
        LocalDate date = LocalDate.of(2024, 12, 31);
        when(contractRepository.findAllByEndDate(date)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByEndDate(date);

        assertNotNull(result);
        verify(contractRepository).findAllByEndDate(date);
    }

    @Test
    void testFindAllByEndDateBefore() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        when(contractRepository.findAllByEndDateBefore(date)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByEndDateBefore(date);

        assertNotNull(result);
        verify(contractRepository).findAllByEndDateBefore(date);
    }

    @Test
    void testFindAllByEndDateAfter() {
        LocalDate date = LocalDate.of(2024, 6, 1);
        when(contractRepository.findAllByEndDateAfter(date)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByEndDateAfter(date);

        assertNotNull(result);
        verify(contractRepository).findAllByEndDateAfter(date);
    }

    @Test
    void testFindAllByStatus() {
        when(contractRepository.findAllByStatus(Status.PROPOSED)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByStatus(Status.PROPOSED);

        assertNotNull(result);
        verify(contractRepository).findAllByStatus(Status.PROPOSED);
    }

    @Test
    void testFindAllByCustomer() {
        when(contractRepository.findAllByCustomer(testCustomer)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByCustomer(testCustomer);

        assertNotNull(result);
        verify(contractRepository).findAllByCustomer(testCustomer);
    }

    @Test
    void testFindAllByCustomerAndUser() {
        when(contractRepository.findAllByCustomerAndUser(testCustomer, testUser)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByCustomerAndUser(testCustomer, testUser);

        assertNotNull(result);
        verify(contractRepository).findAllByCustomerAndUser(testCustomer, testUser);
    }

    @Test
    void testFindAllByUser() {
        when(contractRepository.findAllByUser(testUser)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> result = contractService.findAllByUser(testUser);

        assertNotNull(result);
        verify(contractRepository).findAllByUser(testUser);
    }

    @Test
    void testSaveContract() {
        when(customerRepository.findAll()).thenReturn(Arrays.asList(testCustomer));
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        when(customerRepository.saveAll(any())).thenReturn(Arrays.asList(testCustomer));
        when(userRepository.saveAll(any())).thenReturn(Arrays.asList(testUser));
        when(contractRepository.save(any(Contract.class))).thenReturn(testContract);

        contractService.saveContract(testContract);

        verify(customerRepository).saveAll(any());
        verify(userRepository).saveAll(any());
        verify(contractRepository).save(testContract);
    }

    @Test
    void testSaveContractWithNullCustomer() {
        testContract.setCustomer(null);
        when(customerRepository.findAll()).thenReturn(Arrays.asList());
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        when(customerRepository.saveAll(any())).thenReturn(Arrays.asList());
        when(userRepository.saveAll(any())).thenReturn(Arrays.asList(testUser));
        when(contractRepository.save(any(Contract.class))).thenReturn(testContract);

        contractService.saveContract(testContract);

        verify(contractRepository).save(testContract);
    }

    @Test
    void testSaveContractWithNullUser() {
        testContract.setUser(null);
        when(customerRepository.findAll()).thenReturn(Arrays.asList(testCustomer));
        when(userRepository.findAll()).thenReturn(Arrays.asList());
        when(customerRepository.saveAll(any())).thenReturn(Arrays.asList(testCustomer));
        when(userRepository.saveAll(any())).thenReturn(Arrays.asList());
        when(contractRepository.save(any(Contract.class))).thenReturn(testContract);

        contractService.saveContract(testContract);

        verify(contractRepository).save(testContract);
    }
}
