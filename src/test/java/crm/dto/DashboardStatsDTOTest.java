package crm.dto;

import crm.entity.Status;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DashboardStatsDTOTest {

    @Test
    void testDashboardStatsDTOBuilder() {
        Map<Status, Long> statusMap = new HashMap<>();
        statusMap.put(Status.PROPOSED, 5L);
        statusMap.put(Status.DONE, 10L);

        List<ContractSummaryDTO> contracts = Arrays.asList(
                ContractSummaryDTO.builder()
                        .id(1L)
                        .name("Test Contract")
                        .customerName("Test Customer")
                        .status(Status.PROPOSED)
                        .value(new BigDecimal("100000.00"))
                        .beginDate(LocalDate.now())
                        .build()
        );

        List<CustomerSummaryDTO> customers = Arrays.asList(
                CustomerSummaryDTO.builder()
                        .id(1L)
                        .name("Test Customer")
                        .email("test@example.com")
                        .contractCount(5L)
                        .build()
        );

        DashboardStatsDTO dto = DashboardStatsDTO.builder()
                .activeCustomersCount(15L)
                .totalContractsCount(20L)
                .contractsByStatus(statusMap)
                .recentContracts(contracts)
                .topCustomers(customers)
                .build();

        assertNotNull(dto);
        assertEquals(15L, dto.getActiveCustomersCount());
        assertEquals(20L, dto.getTotalContractsCount());
        assertEquals(2, dto.getContractsByStatus().size());
        assertEquals(1, dto.getRecentContracts().size());
        assertEquals(1, dto.getTopCustomers().size());
    }

    @Test
    void testContractSummaryDTOBuilder() {
        ContractSummaryDTO dto = ContractSummaryDTO.builder()
                .id(1L)
                .name("Test Contract")
                .customerName("Test Customer")
                .status(Status.NEGOTIATED)
                .value(new BigDecimal("250000.50"))
                .beginDate(LocalDate.of(2024, 1, 15))
                .build();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test Contract", dto.getName());
        assertEquals("Test Customer", dto.getCustomerName());
        assertEquals(Status.NEGOTIATED, dto.getStatus());
        assertEquals(new BigDecimal("250000.50"), dto.getValue());
        assertEquals(LocalDate.of(2024, 1, 15), dto.getBeginDate());
    }

    @Test
    void testCustomerSummaryDTOBuilder() {
        CustomerSummaryDTO dto = CustomerSummaryDTO.builder()
                .id(1L)
                .name("Test Customer")
                .email("test@example.com")
                .contractCount(10L)
                .build();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test Customer", dto.getName());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals(10L, dto.getContractCount());
    }

}
