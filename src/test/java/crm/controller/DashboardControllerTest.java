package crm.controller;

import crm.dto.ContractSummaryDTO;
import crm.dto.CustomerSummaryDTO;
import crm.dto.DashboardStatsDTO;
import crm.entity.Status;
import crm.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DashboardController.class)
@Import(crm.SecurityConfig.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private crm.service.SpringDataUserDetailsService userDetailsService;

    private DashboardStatsDTO mockStats;

    @BeforeEach
    void setUp() {
        // Prepare mock data
        Map<Status, Long> contractsByStatus = new HashMap<>();
        contractsByStatus.put(Status.PROPOSED, 5L);
        contractsByStatus.put(Status.NEGOTIATED, 3L);
        contractsByStatus.put(Status.IMPLEMENTED, 2L);
        contractsByStatus.put(Status.DONE, 10L);

        List<ContractSummaryDTO> recentContracts = Arrays.asList(
                ContractSummaryDTO.builder()
                        .id(1L)
                        .name("Contract 1")
                        .customerName("Customer A")
                        .status(Status.PROPOSED)
                        .value(new BigDecimal("100000.00"))
                        .beginDate(LocalDate.of(2024, 1, 15))
                        .build(),
                ContractSummaryDTO.builder()
                        .id(2L)
                        .name("Contract 2")
                        .customerName("Customer B")
                        .status(Status.NEGOTIATED)
                        .value(new BigDecimal("250000.00"))
                        .beginDate(LocalDate.of(2024, 1, 10))
                        .build()
        );

        List<CustomerSummaryDTO> topCustomers = Arrays.asList(
                CustomerSummaryDTO.builder()
                        .id(1L)
                        .name("Customer A")
                        .email("customerA@example.com")
                        .contractCount(5L)
                        .build(),
                CustomerSummaryDTO.builder()
                        .id(2L)
                        .name("Customer B")
                        .email("customerB@example.com")
                        .contractCount(3L)
                        .build()
        );

        mockStats = DashboardStatsDTO.builder()
                .activeCustomersCount(15L)
                .totalContractsCount(20L)
                .contractsByStatus(contractsByStatus)
                .recentContracts(recentContracts)
                .topCustomers(topCustomers)
                .build();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetDashboardStats_Success() throws Exception {
        when(dashboardService.getDashboardStats()).thenReturn(mockStats);

        mockMvc.perform(get("/api/dashboard/stats")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.activeCustomersCount").value(15))
                .andExpect(jsonPath("$.totalContractsCount").value(20))
                .andExpect(jsonPath("$.contractsByStatus.PROPOSED").value(5))
                .andExpect(jsonPath("$.contractsByStatus.NEGOTIATED").value(3))
                .andExpect(jsonPath("$.contractsByStatus.IMPLEMENTED").value(2))
                .andExpect(jsonPath("$.contractsByStatus.DONE").value(10))
                .andExpect(jsonPath("$.recentContracts").isArray())
                .andExpect(jsonPath("$.recentContracts[0].name").value("Contract 1"))
                .andExpect(jsonPath("$.topCustomers").isArray())
                .andExpect(jsonPath("$.topCustomers[0].contractCount").value(5));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetDashboardStats_AsAdmin() throws Exception {
        when(dashboardService.getDashboardStats()).thenReturn(mockStats);

        mockMvc.perform(get("/api/dashboard/stats")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeCustomersCount").value(15));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void testGetDashboardStats_AsManager() throws Exception {
        when(dashboardService.getDashboardStats()).thenReturn(mockStats);

        mockMvc.perform(get("/api/dashboard/stats")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalContractsCount").value(20));
    }

    // Note: Authentication is enforced by Spring Security configuration
    // The endpoint requires authenticated users with roles: USER, MANAGER, OWNER, or ADMIN
    // Security configuration is tested through integration tests

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetDashboardStats_EmptyData() throws Exception {
        DashboardStatsDTO emptyStats = DashboardStatsDTO.builder()
                .activeCustomersCount(0L)
                .totalContractsCount(0L)
                .contractsByStatus(new HashMap<>())
                .recentContracts(new ArrayList<>())
                .topCustomers(new ArrayList<>())
                .build();

        when(dashboardService.getDashboardStats()).thenReturn(emptyStats);

        mockMvc.perform(get("/api/dashboard/stats")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeCustomersCount").value(0))
                .andExpect(jsonPath("$.totalContractsCount").value(0))
                .andExpect(jsonPath("$.recentContracts").isEmpty())
                .andExpect(jsonPath("$.topCustomers").isEmpty());
    }

}
