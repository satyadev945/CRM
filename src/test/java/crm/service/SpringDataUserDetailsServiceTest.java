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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpringDataUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private SpringDataUserDetailsService userDetailsService;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        // Create test role
        testRole = new Role();
        testRole.setId(1);
        testRole.setName("ROLE_ADMIN");

        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setEnabled(1);
        testUser.setRole(testRole);
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsCurrentUser() {
        // Arrange
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof CurrentUser);

        CurrentUser currentUser = (CurrentUser) result;
        assertEquals(testUser, currentUser.getUser());
        assertEquals("testuser", currentUser.getUsername());
        assertEquals("password", currentUser.getPassword());

        // Verify authorities
        assertEquals(1, currentUser.getAuthorities().size());
        assertTrue(currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));

        // Verify default UserDetails implementations
        assertTrue(currentUser.isEnabled());
        assertTrue(currentUser.isAccountNonExpired());
        assertTrue(currentUser.isAccountNonLocked());
        assertTrue(currentUser.isCredentialsNonExpired());

        // Verify the service was called
        verify(userService, times(1)).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_UserDoesNotExist_ThrowsUsernameNotFoundException() {
        // Arrange
        when(userService.findByUsername("nonexistentuser")).thenReturn(null);

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername("nonexistentuser")
        );

        assertEquals("nonexistentuser", exception.getMessage());
        verify(userService, times(1)).findByUsername("nonexistentuser");
    }

    @Test
    void loadUserByUsername_WithMultipleAuthorities_ReturnsCorrectAuthorities() {
        // Arrange
        testRole.setName("ROLE_USER");
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        CurrentUser currentUser = (CurrentUser) result;

        assertEquals(1, currentUser.getAuthorities().size());

        boolean hasUserRole = false;
        for (GrantedAuthority authority : currentUser.getAuthorities()) {
            if ("ROLE_USER".equals(authority.getAuthority())) {
                hasUserRole = true;
                break;
            }
        }

        assertTrue(hasUserRole);
    }
}