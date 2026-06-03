package crm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity(name = "pdf")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Size(min = 2)
    private String name;

    @Transient
    private String content;

}
