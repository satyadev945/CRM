package crm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contract", indexes = {
    @Index(name = "idx_contract_name", columnList = "name"),
    @Index(name = "idx_contract_status", columnList = "status"),
    @Index(name = "idx_contract_customer", columnList = "customer_id"),
    @Index(name = "idx_contract_user", columnList = "user_id"),
    @Index(name = "idx_contract_dates", columnList = "begin_date, end_date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contract {

    @Id
    // PostgreSQL IDENTITY generation strategy (recommended for PostgreSQL 10+)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 255)
    private String name;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "value", precision = 19, scale = 2)
    private BigDecimal value;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "begin_date")
    private LocalDate beginDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", foreignKey = @ForeignKey(name = "fk_contract_customer"))
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_contract_user"))
    private User user;

}
