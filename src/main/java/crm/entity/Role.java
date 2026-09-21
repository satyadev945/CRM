package crm.entity;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    @SequenceGenerator(name = "role_seq", sequenceName = "role_seq", allocationSize = 1)
    @Column(name = "role_id")
    private int id;

    @Column(name = "role", unique = true)
    private String name;

}
