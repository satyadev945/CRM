package crm.dto;

import crm.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    private Long activeCustomersCount;
    private Long totalContractsCount;
    private Map<Status, Long> contractsByStatus;
    private List<ContractSummaryDTO> recentContracts;
    private List<CustomerSummaryDTO> topCustomers;

}
