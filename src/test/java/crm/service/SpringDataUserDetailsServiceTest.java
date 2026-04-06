package crm.service;

import crm.entity.CurrentUser;
import crm.entity.Role;
import crm.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpringDataUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private SpringDataUserDetailsService springDataUserDetailsService;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1);
        testRole.setName("ROLE_USER");

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .enabled(1)
                .role(testRole)
                .build();
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        // Arrange
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // Act
        UserDetails result = springDataUserDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof CurrentUser);
        assertEquals("testuser", result.getUsername());
        assertEquals("password123", result.getPassword());
        assertNotNull(result.getAuthorities());
        assertFalse(result.getAuthorities().isEmpty());
        verify(userService).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        // Arrange
        when(userService.findByUsername("nonexistent")).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            springDataUserDetailsService.loadUserByUsername("nonexistent");
        });
        verify(userService).findByUsername("nonexistent");
    }

    @Test
    void loadUserByUsername_shouldSetCorrectAuthorities() {
        // Arrange
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // Act
        UserDetails result = springDataUserDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(result.getAuthorities());
        assertEquals(1, result.getAuthorities().size());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_shouldHandleAdminRole() {
        // Arrange
        Role adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName("ROLE_ADMIN");
        testUser.setRole(adminRole);
        when(userService.findByUsername("adminuser")).thenReturn(testUser);

        // Act
        UserDetails result = springDataUserDetailsService.loadUserByUsername("adminuser");

        // Assert
        assertNotNull(result.getAuthorities());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_shouldReturnCurrentUserInstance() {
        // Arrange
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // Act
        UserDetails result = springDataUserDetailsService.loadUserByUsername("testuser");

        // Assert
        assertTrue(result instanceof CurrentUser);
        CurrentUser currentUser = (CurrentUser) result;
        assertEquals(testUser, currentUser.getUser());
    }
}
