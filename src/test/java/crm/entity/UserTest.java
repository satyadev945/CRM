package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .enabled(1)
                .role(role)
                .build();
    }

    @Test
    void builder_shouldCreateUserWithAllFields() {
        // Assert
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("password123", user.getPassword());
        assertEquals(1, user.getEnabled());
        assertEquals(role, user.getRole());
    }

    @Test
    void setUsername_shouldSetUsername() {
        // Arrange
        String newUsername = "newuser";

        // Act
        user.setUsername(newUsername);

        // Assert
        assertEquals(newUsername, user.getUsername());
    }

    @Test
    void setEmail_shouldSetEmail() {
        // Arrange
        String newEmail = "newemail@example.com";

        // Act
        user.setEmail(newEmail);

        // Assert
        assertEquals(newEmail, user.getEmail());
    }

    @Test
    void setFirstName_shouldSetFirstName() {
        // Arrange
        String newFirstName = "Jane";

        // Act
        user.setFirstName(newFirstName);

        // Assert
        assertEquals(newFirstName, user.getFirstName());
    }

    @Test
    void setLastName_shouldSetLastName() {
        // Arrange
        String newLastName = "Smith";

        // Act
        user.setLastName(newLastName);

        // Assert
        assertEquals(newLastName, user.getLastName());
    }

    @Test
    void setPassword_shouldSetPassword() {
        // Arrange
        String newPassword = "newpassword456";

        // Act
        user.setPassword(newPassword);

        // Assert
        assertEquals(newPassword, user.getPassword());
    }

    @Test
    void setEnabled_shouldSetEnabled() {
        // Arrange
        int newEnabled = 0;

        // Act
        user.setEnabled(newEnabled);

        // Assert
        assertEquals(newEnabled, user.getEnabled());
    }

    @Test
    void setRole_shouldSetRole() {
        // Arrange
        Role newRole = new Role();
        newRole.setId(2);
        newRole.setName("ROLE_ADMIN");

        // Act
        user.setRole(newRole);

        // Assert
        assertEquals(newRole, user.getRole());
    }

    @Test
    void getColumnCount_shouldReturnNumberOfFields() {
        // Act
        int columnCount = user.getColumnCount();

        // Assert
        assertTrue(columnCount > 0);
    }

    @Test
    void getRole_id_shouldReturnRoleId() {
        // Act
        int roleId = user.getRole_id();

        // Assert
        assertEquals(1, roleId);
    }

    @Test
    void getRole_name_shouldReturnRoleName() {
        // Act
        String roleName = user.getRole_name();

        // Assert
        assertEquals("ROLE_USER", roleName);
    }

    @Test
    void getName_shouldReturnFullName() {
        // Act
        String fullName = user.getName();

        // Assert
        assertEquals("John Doe", fullName);
    }

    @Test
    void user_shouldHaveEntityAnnotation() {
        // Assert
        assertTrue(User.class.isAnnotationPresent(javax.persistence.Entity.class));
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyUser() {
        // Act
        User emptyUser = new User();

        // Assert
        assertNotNull(emptyUser);
        assertNull(emptyUser.getId());
    }

    @Test
    void allArgsConstructor_shouldCreateUserWithAllFields() {
        // Act
        User newUser = new User(
                2L, "anotheruser", "another@example.com",
                "Alice", "Brown", "pass789", 1, role
        );

        // Assert
        assertNotNull(newUser);
        assertEquals(2L, newUser.getId());
        assertEquals("anotheruser", newUser.getUsername());
    }
}
