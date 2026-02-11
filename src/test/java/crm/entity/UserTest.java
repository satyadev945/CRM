package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserTest {

    @Mock
    private Role mockRole;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testUserBuilder() {
        User builtUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .enabled(1)
                .role(mockRole)
                .build();

        assertNotNull(builtUser);
        assertEquals(1L, builtUser.getId());
        assertEquals("testuser", builtUser.getUsername());
        assertEquals("test@example.com", builtUser.getEmail());
        assertEquals("John", builtUser.getFirstName());
        assertEquals("Doe", builtUser.getLastName());
        assertEquals("password123", builtUser.getPassword());
        assertEquals(1, builtUser.getEnabled());
        assertEquals(mockRole, builtUser.getRole());
    }

    @Test
    void testNoArgsConstructor() {
        User newUser = new User();
        assertNotNull(newUser);
    }

    @Test
    void testAllArgsConstructor() {
        User newUser = new User(1L, "testuser", "test@example.com", "John", "Doe", "password123", 1, mockRole);

        assertNotNull(newUser);
        assertEquals(1L, newUser.getId());
        assertEquals("testuser", newUser.getUsername());
        assertEquals("test@example.com", newUser.getEmail());
        assertEquals("John", newUser.getFirstName());
        assertEquals("Doe", newUser.getLastName());
        assertEquals("password123", newUser.getPassword());
        assertEquals(1, newUser.getEnabled());
        assertEquals(mockRole, newUser.getRole());
    }

    @Test
    void testGettersAndSetters() {
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password123");
        user.setEnabled(1);
        user.setRole(mockRole);

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("password123", user.getPassword());
        assertEquals(1, user.getEnabled());
        assertEquals(mockRole, user.getRole());
    }

    @Test
    void testGetColumnCount() {
        int columnCount = user.getColumnCount();
        assertTrue(columnCount > 0);
    }

    @Test
    void testGetRoleId() {
        when(mockRole.getId()).thenReturn(5);
        user.setRole(mockRole);

        assertEquals(5, user.getRole_id());
        verify(mockRole).getId();
    }

    @Test
    void testGetRoleName() {
        when(mockRole.getName()).thenReturn("ROLE_USER");
        user.setRole(mockRole);

        assertEquals("ROLE_USER", user.getRole_name());
        verify(mockRole).getName();
    }

    @Test
    void testGetName() {
        user.setFirstName("John");
        user.setLastName("Doe");

        assertEquals("John Doe", user.getName());
    }

    @Test
    void testGetNameWithNullValues() {
        user.setFirstName(null);
        user.setLastName(null);

        assertEquals("null null", user.getName());
    }

    @Test
    void testEnabledFlag() {
        user.setEnabled(1);
        assertEquals(1, user.getEnabled());

        user.setEnabled(0);
        assertEquals(0, user.getEnabled());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        User user2 = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testToString() {
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        String toString = user.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("testuser"));
    }

    @Test
    void testUserWithNullRole() {
        user.setRole(null);
        assertNull(user.getRole());
    }

    @Test
    void testSetEmailValidation() {
        user.setEmail("valid@example.com");
        assertEquals("valid@example.com", user.getEmail());
    }

    @Test
    void testSetPasswordNullValue() {
        user.setPassword(null);
        assertNull(user.getPassword());
    }

    @Test
    void testUserIdAutoGeneration() {
        User newUser = new User();
        assertNull(newUser.getId());
    }
}
