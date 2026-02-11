package crm.service;

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
                .email("test@example.com")
                .password("encodedPassword")
                .enabled(1)
                .role(testRole)
                .build();
    }

    @Test
    void testLoadUserByUsername() {
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertNotNull(userDetails.getAuthorities());
        assertEquals(1, userDetails.getAuthorities().size());
        verify(userService).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userService.findByUsername("nonexistent")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent");
        });

        verify(userService).findByUsername("nonexistent");
    }

    @Test
    void testLoadUserByUsernameWithAdminRole() {
        testUser.setUsername("admin");
        testRole.setName("ROLE_ADMIN");
        when(userService.findByUsername("admin")).thenReturn(testUser);

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        verify(userService).findByUsername("admin");
    }

    @Test
    void testLoadUserByUsernameWithManagerRole() {
        testRole.setName("ROLE_MANAGER");
        when(userService.findByUsername("manager")).thenReturn(testUser);

        UserDetails userDetails = userDetailsService.loadUserByUsername("manager");

        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER")));
        verify(userService).findByUsername("manager");
    }

    @Test
    void testLoadUserByUsernameWithOwnerRole() {
        testRole.setName("ROLE_OWNER");
        when(userService.findByUsername("owner")).thenReturn(testUser);

        UserDetails userDetails = userDetailsService.loadUserByUsername("owner");

        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_OWNER")));
        verify(userService).findByUsername("owner");
    }

    @Test
    void testLoadUserByUsernameReturnsCurrentUser() {
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertTrue(userDetails instanceof crm.entity.CurrentUser);
        crm.entity.CurrentUser currentUser = (crm.entity.CurrentUser) userDetails;
        assertEquals(testUser, currentUser.getUser());
        verify(userService).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsernameNullUsername() {
        when(userService.findByUsername(null)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(null);
        });
    }

    @Test
    void testLoadUserByUsernameEmptyUsername() {
        when(userService.findByUsername("")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("");
        });
    }
}
