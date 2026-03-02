package crm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "category", indexes = {
    @Index(name = "idx_category_name", columnList = "category")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    // PostgreSQL IDENTITY generation strategy (recommended for PostgreSQL 10+)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "category", length = 100)
    private String name;

}
