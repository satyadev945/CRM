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
    void noArgsConstructor_shouldCreateEmptyUser() {
        User emptyUser = new User();
        
        assertNotNull(emptyUser);
        assertNull(emptyUser.getId());
        assertNull(emptyUser.getUsername());
    }

    @Test
    void allArgsConstructor_shouldCreateUserWithAllFields() {
        User newUser = new User(
                2L,
                "newuser",
                "new@example.com",
                "Jane",
                "Smith",
                "newpassword",
                1,
                role
        );

        assertNotNull(newUser);
        assertEquals(2L, newUser.getId());
        assertEquals("newuser", newUser.getUsername());
    }

    @Test
    void setAndGetId_shouldWorkCorrectly() {
        user.setId(100L);
        assertEquals(100L, user.getId());
    }

    @Test
    void setAndGetUsername_shouldWorkCorrectly() {
        user.setUsername("updateduser");
        assertEquals("updateduser", user.getUsername());
    }

    @Test
    void setAndGetEmail_shouldWorkCorrectly() {
        user.setEmail("updated@example.com");
        assertEquals("updated@example.com", user.getEmail());
    }

    @Test
    void setAndGetFirstName_shouldWorkCorrectly() {
        user.setFirstName("Jane");
        assertEquals("Jane", user.getFirstName());
    }

    @Test
    void setAndGetLastName_shouldWorkCorrectly() {
        user.setLastName("Smith");
        assertEquals("Smith", user.getLastName());
    }

    @Test
    void setAndGetPassword_shouldWorkCorrectly() {
        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());
    }

    @Test
    void setAndGetEnabled_shouldWorkCorrectly() {
        user.setEnabled(0);
        assertEquals(0, user.getEnabled());
    }

    @Test
    void setAndGetRole_shouldWorkCorrectly() {
        Role newRole = new Role();
        newRole.setId(2);
        newRole.setName("ROLE_ADMIN");

        user.setRole(newRole);
        assertEquals(newRole, user.getRole());
    }

    @Test
    void getColumnCount_shouldReturnNumberOfFields() {
        int columnCount = user.getColumnCount();
        assertTrue(columnCount > 0);
    }

    @Test
    void getRole_id_shouldReturnRoleId() {
        assertEquals(1, user.getRole_id());
    }

    @Test
    void getRole_name_shouldReturnRoleName() {
        assertEquals("ROLE_USER", user.getRole_name());
    }

    @Test
    void getName_shouldReturnFullName() {
        assertEquals("John Doe", user.getName());
    }

    @Test
    void getName_withNullFirstName_shouldHandleGracefully() {
        user.setFirstName(null);
        String name = user.getName();
        assertTrue(name.contains("Doe"));
    }

    @Test
    void getName_withNullLastName_shouldHandleGracefully() {
        user.setLastName(null);
        String name = user.getName();
        assertTrue(name.contains("John"));
    }

    @Test
    void user_withNullValues_shouldHandleGracefully() {
        User nullUser = User.builder().build();
        
        assertNull(nullUser.getId());
        assertNull(nullUser.getUsername());
        assertNull(nullUser.getEmail());
        assertEquals(0, nullUser.getEnabled());
    }

    @Test
    void user_withNullRole_shouldHandleGetRoleIdGracefully() {
        user.setRole(null);
        assertThrows(NullPointerException.class, () -> user.getRole_id());
    }

    @Test
    void user_withNullRole_shouldHandleGetRoleNameGracefully() {
        user.setRole(null);
        assertThrows(NullPointerException.class, () -> user.getRole_name());
    }

    @Test
    void user_equalsAndHashCode_shouldWorkCorrectly() {
        User user1 = User.builder()
                .id(1L)
                .username("test")
                .email("test@example.com")
                .build();

        User user2 = User.builder()
                .id(1L)
                .username("test")
                .email("test@example.com")
                .build();

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void user_toString_shouldReturnString() {
        String result = user.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("testuser"));
    }
}
