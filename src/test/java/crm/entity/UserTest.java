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

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password123");
        user.setEnabled(1);
        user.setRole(role);
    }

    @Test
    void testUserDefaultConstructor() {
        User newUser = new User();
        assertNotNull(newUser);
    }

    @Test
    void testUserAllArgsConstructor() {
        User newUser = new User(1L, "username", "email@test.com", "First", "Last", "pass", 1, role);
        assertNotNull(newUser);
        assertEquals(1L, newUser.getId());
        assertEquals("username", newUser.getUsername());
        assertEquals("email@test.com", newUser.getEmail());
        assertEquals("First", newUser.getFirstName());
        assertEquals("Last", newUser.getLastName());
        assertEquals("pass", newUser.getPassword());
        assertEquals(1, newUser.getEnabled());
        assertEquals(role, newUser.getRole());
    }

    @Test
    void testUserBuilder() {
        User builtUser = User.builder()
                .id(2L)
                .username("builderuser")
                .email("builder@test.com")
                .firstName("Builder")
                .lastName("User")
                .password("builderpass")
                .enabled(1)
                .role(role)
                .build();
        assertNotNull(builtUser);
        assertEquals(2L, builtUser.getId());
        assertEquals("builderuser", builtUser.getUsername());
    }

    @Test
    void testGetId() {
        assertEquals(1L, user.getId());
    }

    @Test
    void testSetId() {
        user.setId(99L);
        assertEquals(99L, user.getId());
    }

    @Test
    void testGetUsername() {
        assertEquals("testuser", user.getUsername());
    }

    @Test
    void testSetUsername() {
        user.setUsername("newuser");
        assertEquals("newuser", user.getUsername());
    }

    @Test
    void testGetEmail() {
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void testSetEmail() {
        user.setEmail("new@example.com");
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    void testGetFirstName() {
        assertEquals("John", user.getFirstName());
    }

    @Test
    void testSetFirstName() {
        user.setFirstName("Jane");
        assertEquals("Jane", user.getFirstName());
    }

    @Test
    void testGetLastName() {
        assertEquals("Doe", user.getLastName());
    }

    @Test
    void testSetLastName() {
        user.setLastName("Smith");
        assertEquals("Smith", user.getLastName());
    }

    @Test
    void testGetPassword() {
        assertEquals("password123", user.getPassword());
    }

    @Test
    void testSetPassword() {
        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());
    }

    @Test
    void testGetEnabled() {
        assertEquals(1, user.getEnabled());
    }

    @Test
    void testSetEnabled() {
        user.setEnabled(0);
        assertEquals(0, user.getEnabled());
    }

    @Test
    void testGetRole() {
        assertEquals(role, user.getRole());
    }

    @Test
    void testSetRole() {
        Role newRole = new Role();
        newRole.setId(2);
        newRole.setName("ROLE_ADMIN");
        user.setRole(newRole);
        assertEquals(newRole, user.getRole());
    }

    @Test
    void testGetName() {
        assertEquals("John Doe", user.getName());
    }

    @Test
    void testGetName_withDifferentNames() {
        user.setFirstName("Alice");
        user.setLastName("Wonderland");
        assertEquals("Alice Wonderland", user.getName());
    }

    @Test
    void testGetRoleId() {
        assertEquals(1, user.getRole_id());
    }

    @Test
    void testGetRoleName() {
        assertEquals("ROLE_USER", user.getRole_name());
    }

    @Test
    void testGetColumnCount() {
        int count = user.getColumnCount();
        assertTrue(count > 0);
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .enabled(1)
                .role(role)
                .build();

        User user2 = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .enabled(1)
                .role(role)
                .build();

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testToString() {
        String toString = user.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("testuser"));
    }

    @Test
    void testUserWithNullPassword() {
        user.setPassword(null);
        assertNull(user.getPassword());
    }

    @Test
    void testUserEnabledZero() {
        user.setEnabled(0);
        assertEquals(0, user.getEnabled());
    }
}
