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
    }

    @Test
    void testAllArgsConstructor() {
        User u = new User(1L, "alice", "alice@test.com", "Alice", "Smith", "pass", 1, role);
        assertNotNull(u);
        assertEquals(1L, u.getId());
        assertEquals("alice", u.getUsername());
        assertEquals("alice@test.com", u.getEmail());
        assertEquals("Alice", u.getFirstName());
        assertEquals("Smith", u.getLastName());
        assertEquals("pass", u.getPassword());
        assertEquals(1, u.getEnabled());
        assertEquals(role, u.getRole());
    }

    @Test
    void testBuilderPattern() {
        User u = User.builder()
                .id(2L)
                .username("bob")
                .email("bob@test.com")
                .firstName("Bob")
                .lastName("Builder")
                .password("pwd")
                .enabled(1)
                .role(role)
                .build();
        assertNotNull(u);
        assertEquals(2L, u.getId());
        assertEquals("bob", u.getUsername());
    }

    @Test
    void testSetAndGetId() {
        user.setId(5L);
        assertEquals(5L, user.getId());
    }

    @Test
    void testSetAndGetUsername() {
        user.setUsername("newuser");
        assertEquals("newuser", user.getUsername());
    }

    @Test
    void testSetAndGetEmail() {
        user.setEmail("new@example.com");
        assertEquals("new@example.com", user.getEmail());
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
    void testSetAndGetEnabled() {
        user.setEnabled(0);
        assertEquals(0, user.getEnabled());
    }

    @Test
    void testSetAndGetRole() {
        Role newRole = new Role();
        newRole.setId(2);
        newRole.setName("ROLE_ADMIN");
        user.setRole(newRole);
        assertEquals(newRole, user.getRole());
    }

    @Test
    void testGetName() {
        user.setFirstName("John");
        user.setLastName("Doe");
        assertEquals("John Doe", user.getName());
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
    void testSetUsernameNull() {
        user.setUsername(null);
        assertNull(user.getUsername());
    }

    @Test
    void testSetEmailNull() {
        user.setEmail(null);
        assertNull(user.getEmail());
    }

    @Test
    void testGetNameWithNullFirstName() {
        user.setFirstName(null);
        user.setLastName("Doe");
        String name = user.getName();
        assertNotNull(name);
        assertTrue(name.contains("Doe"));
    }
}
