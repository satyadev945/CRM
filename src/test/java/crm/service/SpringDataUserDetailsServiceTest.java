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
    private SpringDataUserDetailsService userDetailsService;

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
                .password("password123")
                .email("test@example.com")
                .enabled(1)
                .role(testRole)
                .build();
    }

    @Test
    void loadUserByUsername_withValidUsername_shouldReturnUserDetails() {
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertInstanceOf(CurrentUser.class, result);
        assertEquals("testuser", result.getUsername());
        assertEquals("password123", result.getPassword());
        assertNotNull(result.getAuthorities());
        assertEquals(1, result.getAuthorities().size());
        verify(userService).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_withNonExistentUsername_shouldThrowException() {
        when(userService.findByUsername("nonexistent")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, 
                () -> userDetailsService.loadUserByUsername("nonexistent"));
        verify(userService).findByUsername("nonexistent");
    }

    @Test
    void loadUserByUsername_withNullUsername_shouldThrowException() {
        when(userService.findByUsername(null)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, 
                () -> userDetailsService.loadUserByUsername(null));
    }

    @Test
    void loadUserByUsername_shouldSetCorrectAuthorities() {
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }
}
