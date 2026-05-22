package crm.dto;

import crm.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractSummaryDTO {

    private Long id;
    private String name;
    private String customerName;
    private Status status;
    private BigDecimal value;
    private LocalDate beginDate;

}
