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
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("secret");
        user.setEnabled(1);
        user.setRole(role);
    }

    @Test
    void testDefaultConstructor() {
        User u = new User();
        assertNotNull(u);
        assertNull(u.getId());
        assertNull(u.getUsername());
        assertNull(u.getEmail());
        assertNull(u.getFirstName());
        assertNull(u.getLastName());
        assertNull(u.getPassword());
        assertEquals(0, u.getEnabled());
        assertNull(u.getRole());
    }

    @Test
    void testAllArgsConstructor() {
        Role r = new Role();
        r.setId(2);
        r.setName("ROLE_ADMIN");
        User u = new User(2L, "admin", "admin@test.com", "Admin", "User", "pass", 1, r);
        assertEquals(2L, u.getId());
        assertEquals("admin", u.getUsername());
        assertEquals("admin@test.com", u.getEmail());
        assertEquals("Admin", u.getFirstName());
        assertEquals("User", u.getLastName());
        assertEquals("pass", u.getPassword());
        assertEquals(1, u.getEnabled());
        assertEquals(r, u.getRole());
    }

    @Test
    void testBuilderPattern() {
        User u = User.builder()
                .id(3L)
                .username("builder_user")
                .email("builder@test.com")
                .firstName("Builder")
                .lastName("Test")
                .password("builderpass")
                .enabled(1)
                .role(role)
                .build();

        assertEquals(3L, u.getId());
        assertEquals("builder_user", u.getUsername());
        assertEquals("builder@test.com", u.getEmail());
        assertEquals("Builder", u.getFirstName());
        assertEquals("Test", u.getLastName());
        assertEquals("builderpass", u.getPassword());
        assertEquals(1, u.getEnabled());
        assertEquals(role, u.getRole());
    }

    @Test
    void testSetAndGetId() {
        user.setId(99L);
        assertEquals(99L, user.getId());
    }

    @Test
    void testSetAndGetUsername() {
        user.setUsername("newuser");
        assertEquals("newuser", user.getUsername());
    }

    @Test
    void testSetAndGetEmail() {
        user.setEmail("new@email.com");
        assertEquals("new@email.com", user.getEmail());
    }

    @Test
    void testSetAndGetFirstName() {
        user.setFirstName("Jane");
        assertEquals("Jane", user.getFirstName());
    }

    @Test
    void testSetAndGetLastName() {
        user.setLastName("Smith");
        assertEquals("Smith", user.getLastName());
    }

    @Test
    void testSetAndGetPassword() {
        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());
    }

    @Test
    void testSetAndGetEnabled_Active() {
        user.setEnabled(1);
        assertEquals(1, user.getEnabled());
    }

    @Test
    void testSetAndGetEnabled_Inactive() {
        user.setEnabled(0);
        assertEquals(0, user.getEnabled());
    }

    @Test
    void testSetAndGetRole() {
        Role newRole = new Role();
        newRole.setId(3);
        newRole.setName("ROLE_MANAGER");
        user.setRole(newRole);
        assertEquals(newRole, user.getRole());
    }

    @Test
    void testGetName_ReturnsFullName() {
        user.setFirstName("John");
        user.setLastName("Doe");
        assertEquals("John Doe", user.getName());
    }

    @Test
    void testGetName_WithDifferentNames() {
        user.setFirstName("Alice");
        user.setLastName("Wonder");
        assertEquals("Alice Wonder", user.getName());
    }

    @Test
    void testGetRole_id() {
        assertEquals(1, user.getRole_id());
    }

    @Test
    void testGetRole_name() {
        assertEquals("ROLE_USER", user.getRole_name());
    }

    @Test
    void testGetColumnCount() {
        int count = user.getColumnCount();
        assertTrue(count > 0);
    }

    @Test
    void testEqualsAndHashCode() {
        User u1 = User.builder().id(1L).username("user1").email("u1@test.com").build();
        User u2 = User.builder().id(1L).username("user1").email("u1@test.com").build();
        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    void testNotEquals() {
        User u1 = User.builder().id(1L).username("user1").build();
        User u2 = User.builder().id(2L).username("user2").build();
        assertNotEquals(u1, u2);
    }

    @Test
    void testToString() {
        String str = user.toString();
        assertNotNull(str);
        assertTrue(str.contains("johndoe"));
    }

    @Test
    void testSetNullPassword() {
        user.setPassword(null);
        assertNull(user.getPassword());
    }

    @Test
    void testSetNullUsername() {
        user.setUsername(null);
        assertNull(user.getUsername());
    }
}
