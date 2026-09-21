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

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");

        user = User.builder()
                .id(1L)
                .username("johndoe")
                .email("john@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedpassword")
                .enabled(1)
                .role(role)
                .build();
    }

    @Test
    void testLoadUserByUsername_ValidUser_ReturnsCurrentUser() {
        when(userService.findByUsername("johndoe")).thenReturn(user);

        UserDetails result = springDataUserDetailsService.loadUserByUsername("johndoe");

        assertNotNull(result);
        assertTrue(result instanceof CurrentUser);
        verify(userService).findByUsername("johndoe");
    }

    @Test
    void testLoadUserByUsername_ValidUser_HasCorrectUsername() {
        when(userService.findByUsername("johndoe")).thenReturn(user);

        UserDetails result = springDataUserDetailsService.loadUserByUsername("johndoe");

        assertEquals("johndoe", result.getUsername());
    }

    @Test
    void testLoadUserByUsername_ValidUser_HasCorrectPassword() {
        when(userService.findByUsername("johndoe")).thenReturn(user);

        UserDetails result = springDataUserDetailsService.loadUserByUsername("johndoe");

        assertEquals("encodedpassword", result.getPassword());
    }

    @Test
    void testLoadUserByUsername_ValidUser_HasAuthorities() {
        when(userService.findByUsername("johndoe")).thenReturn(user);

        UserDetails result = springDataUserDetailsService.loadUserByUsername("johndoe");

        assertNotNull(result.getAuthorities());
        assertFalse(result.getAuthorities().isEmpty());
        assertEquals(1, result.getAuthorities().size());
    }

    @Test
    void testLoadUserByUsername_ValidUser_HasCorrectRole() {
        when(userService.findByUsername("johndoe")).thenReturn(user);

        UserDetails result = springDataUserDetailsService.loadUserByUsername("johndoe");

        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testLoadUserByUsername_NullUser_ThrowsUsernameNotFoundException() {
        when(userService.findByUsername("unknown")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> springDataUserDetailsService.loadUserByUsername("unknown"));
        verify(userService).findByUsername("unknown");
    }

    @Test
    void testLoadUserByUsername_AdminUser_HasAdminRole() {
        Role adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName("ROLE_ADMIN");

        User adminUser = User.builder()
                .id(2L)
                .username("admin")
                .email("admin@test.com")
                .password("adminpass")
                .enabled(1)
                .role(adminRole)
                .build();

        when(userService.findByUsername("admin")).thenReturn(adminUser);

        UserDetails result = springDataUserDetailsService.loadUserByUsername("admin");

        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testLoadUserByUsername_SetsUserOnCurrentUser() {
        when(userService.findByUsername("johndoe")).thenReturn(user);

        UserDetails result = springDataUserDetailsService.loadUserByUsername("johndoe");

        assertTrue(result instanceof CurrentUser);
        CurrentUser currentUser = (CurrentUser) result;
        assertEquals(user, currentUser.getUser());
    }

    @Test
    void testLoadUserByUsername_ExceptionMessage_ContainsUsername() {
        when(userService.findByUsername("missinguser")).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> springDataUserDetailsService.loadUserByUsername("missinguser"));

        assertEquals("missinguser", exception.getMessage());
    }
}
