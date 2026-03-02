package crm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "role", indexes = {
    @Index(name = "idx_role_name", columnList = "role")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    // PostgreSQL IDENTITY generation strategy (recommended for PostgreSQL 10+)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "role", unique = true, nullable = false, length = 50)
    private String name;

}
