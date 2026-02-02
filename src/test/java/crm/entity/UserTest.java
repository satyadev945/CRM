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
        role.setName("ROLE_ADMIN");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");
        user.setEnabled(1);
        user.setRole(role);
    }

    @Test
    void testUserCreation() {
        // Assert
        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("password", user.getPassword());
        assertEquals(1, user.getEnabled());
        assertEquals(role, user.getRole());
    }

    @Test
    void testBuilderPattern() {
        // Arrange
        Long id = 2L;
        String username = "builduser";
        String email = "build@example.com";
        String firstName = "Build";
        String lastName = "User";
        String password = "buildpw";
        int enabled = 1;

        // Act
        User builtUser = User.builder()
                .id(id)
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .password(password)
                .enabled(enabled)
                .role(role)
                .build();

        // Assert
        assertEquals(id, builtUser.getId());
        assertEquals(username, builtUser.getUsername());
        assertEquals(email, builtUser.getEmail());
        assertEquals(firstName, builtUser.getFirstName());
        assertEquals(lastName, builtUser.getLastName());
        assertEquals(password, builtUser.getPassword());
        assertEquals(enabled, builtUser.getEnabled());
        assertEquals(role, builtUser.getRole());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        Long id = 3L;
        String username = "allargs";
        String email = "allargs@example.com";
        String firstName = "All";
        String lastName = "Args";
        String password = "allargspw";
        int enabled = 1;

        // Act
        User allArgsUser = new User(id, username, email, firstName, lastName, password, enabled, role);

        // Assert
        assertEquals(id, allArgsUser.getId());
        assertEquals(username, allArgsUser.getUsername());
        assertEquals(email, allArgsUser.getEmail());
        assertEquals(firstName, allArgsUser.getFirstName());
        assertEquals(lastName, allArgsUser.getLastName());
        assertEquals(password, allArgsUser.getPassword());
        assertEquals(enabled, allArgsUser.getEnabled());
        assertEquals(role, allArgsUser.getRole());
    }

    @Test
    void testGetColumnCount() {
        // This might vary if fields are added or removed
        // Just testing that it returns a reasonable number
        int columnCount = user.getColumnCount();
        assertTrue(columnCount > 0);
    }

    @Test
    void testGetRole_id() {
        // Assert
        assertEquals(role.getId(), user.getRole_id());
    }

    @Test
    void testGetRole_name() {
        // Assert
        assertEquals(role.getName(), user.getRole_name());
    }

    @Test
    void testGetName() {
        // Assert
        assertEquals("John Doe", user.getName());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        User sameUser = new User();
        sameUser.setId(1L);
        sameUser.setUsername("testuser");
        sameUser.setEmail("test@example.com");

        User differentUser = new User();
        differentUser.setId(2L);
        differentUser.setUsername("different");
        differentUser.setEmail("different@example.com");

        // Assert
        assertEquals(user, sameUser);
        assertEquals(user.hashCode(), sameUser.hashCode());
        assertNotEquals(user, differentUser);
        assertNotEquals(user.hashCode(), differentUser.hashCode());
    }

    private void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Condition is not true");
        }
    }
}