package crm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import jakarta.persistence.*;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_username", columnList = "username"),
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_enabled", columnList = "enabled"),
    @Index(name = "idx_user_role", columnList = "role_role_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    // PostgreSQL IDENTITY generation strategy (recommended for PostgreSQL 10+)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    @Email(message = "Please provide a valid e-mail")
    @NotEmpty(message = "Please provide an e-mail")
    private String email;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "enabled", nullable = false)
    private int enabled;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_role_id", foreignKey = @ForeignKey(name = "fk_user_role"))
    private Role role;

    public int getColumnCount() {
        return getClass().getDeclaredFields().length;
    }

    public Long getRole_id() {
        return role.getId();
    }

    public String getRole_name() {
        return role.getName();
    }

    public String getName() {
        return firstName + " " + lastName;
    }

}
