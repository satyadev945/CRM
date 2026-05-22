package crm.service;

import crm.dto.ContractSummaryDTO;
import crm.dto.CustomerSummaryDTO;
import crm.dto.DashboardStatsDTO;
import crm.entity.*;
import crm.repository.ContractRepository;
import crm.repository.CustomerRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private List<Customer> mockCustomers;
    private List<Contract> mockContracts;

    @BeforeEach
    void setUp() {
        // Setup mock customers
        Customer customer1 = Customer.builder()
                .id(1L)
                .name("Customer A")
                .email("customerA@example.com")
                .enabled(1)
                .build();

        Customer customer2 = Customer.builder()
                .id(2L)
                .name("Customer B")
                .email("customerB@example.com")
                .enabled(1)
                .build();

        Customer customer3 = Customer.builder()
                .id(3L)
                .name("Customer C")
                .email("customerC@example.com")
                .enabled(1)
                .build();

        mockCustomers = Arrays.asList(customer1, customer2, customer3);

        // Setup mock contracts
        mockContracts = Arrays.asList(
                Contract.builder()
                        .id(1L)
                        .name("Contract 1")
                        .customer(customer1)
                        .status(Status.PROPOSED)
                        .value(new BigDecimal("100000.00"))
                        .beginDate(LocalDate.of(2024, 1, 15))
                        .build(),
                Contract.builder()
                        .id(2L)
                        .name("Contract 2")
                        .customer(customer1)
                        .status(Status.NEGOTIATED)
                        .value(new BigDecimal("150000.00"))
                        .beginDate(LocalDate.of(2024, 1, 20))
                        .build(),
                Contract.builder()
                        .id(3L)
                        .name("Contract 3")
                        .customer(customer2)
                        .status(Status.IMPLEMENTED)
                        .value(new BigDecimal("200000.00"))
                        .beginDate(LocalDate.of(2024, 1, 10))
                        .build(),
                Contract.builder()
                        .id(4L)
                        .name("Contract 4")
                        .customer(customer2)
                        .status(Status.DONE)
                        .value(new BigDecimal("300000.00"))
                        .beginDate(LocalDate.of(2024, 1, 5))
                        .build(),
                Contract.builder()
                        .id(5L)
                        .name("Contract 5")
                        .customer(customer3)
                        .status(Status.DONE)
                        .value(new BigDecimal("50000.00"))
                        .beginDate(LocalDate.of(2024, 1, 1))
                        .build()
        );
    }

    @Test
    void testGetDashboardStats_Success() {
        when(customerRepository.findAllByEnabled(1)).thenReturn(mockCustomers);
        when(contractRepository.findAll()).thenReturn(mockContracts);

        DashboardStatsDTO result = dashboardService.getDashboardStats();

        assertNotNull(result);
        assertEquals(3L, result.getActiveCustomersCount());
        assertEquals(5L, result.getTotalContractsCount());
        assertNotNull(result.getContractsByStatus());
        assertEquals(1L, result.getContractsByStatus().get(Status.PROPOSED));
        assertEquals(1L, result.getContractsByStatus().get(Status.NEGOTIATED));
        assertEquals(1L, result.getContractsByStatus().get(Status.IMPLEMENTED));
        assertEquals(2L, result.getContractsByStatus().get(Status.DONE));
        assertEquals(5, result.getRecentContracts().size());
        assertEquals(3, result.getTopCustomers().size());
    }

    @Test
    void testGetDashboardStats_EmptyData() {
        when(customerRepository.findAllByEnabled(1)).thenReturn(Collections.emptyList());
        when(contractRepository.findAll()).thenReturn(Collections.emptyList());

        DashboardStatsDTO result = dashboardService.getDashboardStats();

        assertNotNull(result);
        assertEquals(0L, result.getActiveCustomersCount());
        assertEquals(0L, result.getTotalContractsCount());
        assertTrue(result.getRecentContracts().isEmpty());
        assertTrue(result.getTopCustomers().isEmpty());
    }

    @Test
    void testGetDashboardStats_ContractsByStatus_AllStatusesPresent() {
        when(customerRepository.findAllByEnabled(1)).thenReturn(mockCustomers);
        when(contractRepository.findAll()).thenReturn(mockContracts);

        DashboardStatsDTO result = dashboardService.getDashboardStats();

        Map<Status, Long> statusMap = result.getContractsByStatus();
        assertNotNull(statusMap);
        // Verify all statuses are present
        assertTrue(statusMap.containsKey(Status.PROPOSED));
        assertTrue(statusMap.containsKey(Status.NEGOTIATED));
        assertTrue(statusMap.containsKey(Status.IMPLEMENTED));
        assertTrue(statusMap.containsKey(Status.DONE));
    }

    @Test
    void testGetDashboardStats_RecentContracts_SortedByDate() {
        when(customerRepository.findAllByEnabled(1)).thenReturn(mockCustomers);
        when(contractRepository.findAll()).thenReturn(mockContracts);

        DashboardStatsDTO result = dashboardService.getDashboardStats();

        List<ContractSummaryDTO> recentContracts = result.getRecentContracts();
        assertNotNull(recentContracts);
        assertFalse(recentContracts.isEmpty());

        // Verify most recent contract is first
        assertEquals("Contract 2", recentContracts.get(0).getName());
        assertEquals(LocalDate.of(2024, 1, 20), recentContracts.get(0).getBeginDate());
    }

    @Test
    void testGetDashboardStats_TopCustomers_SortedByContractCount() {
        when(customerRepository.findAllByEnabled(1)).thenReturn(mockCustomers);
        when(contractRepository.findAll()).thenReturn(mockContracts);

        DashboardStatsDTO result = dashboardService.getDashboardStats();

        List<CustomerSummaryDTO> topCustomers = result.getTopCustomers();
        assertNotNull(topCustomers);
        assertEquals(3, topCustomers.size());

        // Verify sorted by contract count descending
        assertTrue(topCustomers.get(0).getContractCount() >= topCustomers.get(1).getContractCount());
        assertTrue(topCustomers.get(1).getContractCount() >= topCustomers.get(2).getContractCount());

        // Customer A and B should have 2 contracts each, Customer C has 1
        CustomerSummaryDTO firstCustomer = topCustomers.get(0);
        assertEquals(2L, firstCustomer.getContractCount());
    }

    @Test
    void testGetDashboardStats_RecentContracts_LimitedToFive() {
        // Create more than 5 contracts
        List<Contract> manyContracts = new ArrayList<>();
        Customer customer = mockCustomers.get(0);

        for (int i = 0; i < 10; i++) {
            manyContracts.add(Contract.builder()
                    .id((long) i)
                    .name("Contract " + i)
                    .customer(customer)
                    .status(Status.PROPOSED)
                    .value(new BigDecimal("10000.00"))
                    .beginDate(LocalDate.of(2024, 1, i + 1))
                    .build());
        }

        when(customerRepository.findAllByEnabled(1)).thenReturn(mockCustomers);
        when(contractRepository.findAll()).thenReturn(manyContracts);

        DashboardStatsDTO result = dashboardService.getDashboardStats();

        assertEquals(5, result.getRecentContracts().size());
    }

    @Test
    void testGetDashboardStats_TopCustomers_LimitedToFive() {
        // Create 7 customers with varying contract counts
        List<Customer> manyCustomers = new ArrayList<>();
        List<Contract> manyContracts = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            Customer customer = Customer.builder()
                    .id((long) i)
                    .name("Customer " + i)
                    .email("customer" + i + "@example.com")
                    .enabled(1)
                    .build();
            manyCustomers.add(customer);

            // Create varying number of contracts per customer
            for (int j = 0; j <= i; j++) {
                manyContracts.add(Contract.builder()
                        .id((long) (i * 10 + j))
                        .name("Contract " + i + "-" + j)
                        .customer(customer)
                        .status(Status.PROPOSED)
                        .value(new BigDecimal("10000.00"))
                        .beginDate(LocalDate.of(2024, 1, 1))
                        .build());
            }
        }

        when(customerRepository.findAllByEnabled(1)).thenReturn(manyCustomers);
        when(contractRepository.findAll()).thenReturn(manyContracts);

        DashboardStatsDTO result = dashboardService.getDashboardStats();

        assertEquals(5, result.getTopCustomers().size());
    }

    @Test
    void testGetDashboardStats_ContractWithNullCustomer() {
        Contract contractWithoutCustomer = Contract.builder()
                .id(6L)
                .name("Orphan Contract")
                .customer(null)
                .status(Status.PROPOSED)
                .value(new BigDecimal("10000.00"))
                .beginDate(LocalDate.of(2024, 1, 25))
                .build();

        List<Contract> contractsWithNull = new ArrayList<>(mockContracts);
        contractsWithNull.add(contractWithoutCustomer);

        when(customerRepository.findAllByEnabled(1)).thenReturn(mockCustomers);
        when(contractRepository.findAll()).thenReturn(contractsWithNull);

        DashboardStatsDTO result = dashboardService.getDashboardStats();

        assertNotNull(result);
        assertEquals(6L, result.getTotalContractsCount());

        // Verify the orphan contract appears in recent contracts with "N/A" customer name
        ContractSummaryDTO orphanContract = result.getRecentContracts().stream()
                .filter(c -> c.getName().equals("Orphan Contract"))
                .findFirst()
                .orElse(null);

        assertNotNull(orphanContract);
        assertEquals("N/A", orphanContract.getCustomerName());
    }

}
