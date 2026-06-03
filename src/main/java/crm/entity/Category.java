package crm.entity;

import lombok.Data;

import jakarta.persistence.*;

@Entity
@Data
@Table(name = "category")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category")
    private String name;

}
