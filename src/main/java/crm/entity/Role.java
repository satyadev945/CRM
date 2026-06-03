package crm.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Data
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "role", unique = true)
    private String name;

}
