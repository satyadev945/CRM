package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private Role testRole;

    @BeforeEach
    void setUp() {
        user = new User();
        testRole = new Role(1L, "ADMIN");
    }

    @Test
    void user_defaultConstructor_shouldCreateInstance() {
        assertNotNull(user);
    }

    @Test
    void user_allArgsConstructor_shouldCreateInstanceWithValues() {
        User u = new User(1L, "testuser", "test@example.com", "John", "Doe", "password", 1, testRole);

        assertNotNull(u);
        assertEquals(1L, u.getId());
        assertEquals("testuser", u.getUsername());
        assertEquals("test@example.com", u.getEmail());
        assertEquals("John", u.getFirstName());
        assertEquals("Doe", u.getLastName());
        assertEquals("password", u.getPassword());
        assertEquals(1, u.getEnabled());
        assertEquals(testRole, u.getRole());
    }

    @Test
    void user_builder_shouldCreateInstance() {
        User u = User.builder()
                .id(1L)
                .username("builderuser")
                .email("builder@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("pass123")
                .enabled(1)
                .role(testRole)
                .build();

        assertNotNull(u);
        assertEquals(1L, u.getId());
        assertEquals("builderuser", u.getUsername());
        assertEquals("builder@example.com", u.getEmail());
    }

    @Test
    void setId_shouldSetIdValue() {
        user.setId(10L);
        assertEquals(10L, user.getId());
    }

    @Test
    void setUsername_shouldSetUsernameValue() {
        user.setUsername("newuser");
        assertEquals("newuser", user.getUsername());
    }

    @Test
    void setEmail_shouldSetEmailValue() {
        user.setEmail("new@example.com");
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    void setFirstName_shouldSetFirstNameValue() {
        user.setFirstName("Alice");
        assertEquals("Alice", user.getFirstName());
    }

    @Test
    void setLastName_shouldSetLastNameValue() {
        user.setLastName("Johnson");
        assertEquals("Johnson", user.getLastName());
    }

    @Test
    void setPassword_shouldSetPasswordValue() {
        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());
    }

    @Test
    void setEnabled_shouldSetEnabledValue() {
        user.setEnabled(1);
        assertEquals(1, user.getEnabled());
    }

    @Test
    void setRole_shouldSetRoleValue() {
        user.setRole(testRole);
        assertEquals(testRole, user.getRole());
    }

    @Test
    void getColumnCount_shouldReturnNumberOfFields() {
        int count = user.getColumnCount();
        assertTrue(count > 0);
    }

    @Test
    void getRole_id_shouldReturnRoleId() {
        user.setRole(testRole);
        assertEquals(1L, user.getRole_id());
    }

    @Test
    void getRole_name_shouldReturnRoleName() {
        user.setRole(testRole);
        assertEquals("ADMIN", user.getRole_name());
    }

    @Test
    void getName_shouldReturnFullName() {
        user.setFirstName("John");
        user.setLastName("Doe");
        assertEquals("John Doe", user.getName());
    }

    @Test
    void getName_withNullFirstName_shouldHandleGracefully() {
        user.setFirstName(null);
        user.setLastName("Doe");
        assertEquals("null Doe", user.getName());
    }

    @Test
    void getName_withNullLastName_shouldHandleGracefully() {
        user.setFirstName("John");
        user.setLastName(null);
        assertEquals("John null", user.getName());
    }

    @Test
    void equals_withSameValues_shouldReturnTrue() {
        User u1 = new User(1L, "user", "email@test.com", "John", "Doe", "pass", 1, testRole);
        User u2 = new User(1L, "user", "email@test.com", "John", "Doe", "pass", 1, testRole);

        assertEquals(u1, u2);
    }

    @Test
    void hashCode_withSameValues_shouldReturnSameHashCode() {
        User u1 = new User(1L, "user", "email@test.com", "John", "Doe", "pass", 1, testRole);
        User u2 = new User(1L, "user", "email@test.com", "John", "Doe", "pass", 1, testRole);

        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    void toString_shouldContainFieldValues() {
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        String toString = user.toString();

        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("test@example.com"));
    }

    @Test
    void setEnabled_withZero_shouldSetZero() {
        user.setEnabled(0);
        assertEquals(0, user.getEnabled());
    }

    @Test
    void builder_withPartialFields_shouldCreateInstance() {
        User u = User.builder()
                .username("partial")
                .email("partial@test.com")
                .build();

        assertNotNull(u);
        assertEquals("partial", u.getUsername());
        assertNull(u.getFirstName());
    }
}
