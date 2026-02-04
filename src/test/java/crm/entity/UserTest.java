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
    void testGetColumnCount() {
        // This will get the number of declared fields in the User class
        int count = user.getColumnCount();
        // User class has 7 declared fields (id, username, email, firstName, lastName, password, enabled, role)
        assertTrue(count > 0);
    }

    @Test
    void testGetRole_id() {
        assertEquals(1, user.getRole_id());
    }

    @Test
    void testGetRole_name() {
        assertEquals("ROLE_ADMIN", user.getRole_name());
    }

    @Test
    void testGetName() {
        assertEquals("John Doe", user.getName());
    }

    @Test
    void testBuilderPattern() {
        User builtUser = User.builder()
                .id(2L)
                .username("builder")
                .email("builder@example.com")
                .firstName("Built")
                .lastName("User")
                .password("builderpass")
                .enabled(1)
                .role(role)
                .build();

        assertEquals(2L, builtUser.getId());
        assertEquals("builder", builtUser.getUsername());
        assertEquals("builder@example.com", builtUser.getEmail());
        assertEquals("Built", builtUser.getFirstName());
        assertEquals("User", builtUser.getLastName());
        assertEquals("builderpass", builtUser.getPassword());
        assertEquals(1, builtUser.getEnabled());
        assertEquals(role, builtUser.getRole());
    }

    @Test
    void testEqualsAndHashCode() {
        User sameUser = new User();
        sameUser.setId(1L);
        sameUser.setUsername("testuser");
        sameUser.setEmail("test@example.com");
        sameUser.setFirstName("John");
        sameUser.setLastName("Doe");
        sameUser.setPassword("password");
        sameUser.setEnabled(1);
        sameUser.setRole(role);

        assertEquals(user, sameUser);
        assertEquals(user.hashCode(), sameUser.hashCode());
    }

    @Test
    void testNoArgsConstructor() {
        User emptyUser = new User();
        assertNull(emptyUser.getId());
        assertNull(emptyUser.getUsername());
        assertNull(emptyUser.getEmail());
        assertNull(emptyUser.getFirstName());
        assertNull(emptyUser.getLastName());
        assertNull(emptyUser.getPassword());
        assertEquals(0, emptyUser.getEnabled());
        assertNull(emptyUser.getRole());
    }
}