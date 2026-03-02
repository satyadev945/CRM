package crm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "pdf", indexes = {
    @Index(name = "idx_pdf_name", columnList = "name")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pdf {

    @Id
    // PostgreSQL IDENTITY generation strategy (recommended for PostgreSQL 10+)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", length = 255)
    @Size(min = 2)
    private String name;

    @Transient
    private String content;

}
