package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CurrentUserTest {

    private CurrentUser currentUser;
    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEnabled(1);
        user.setRole(role);

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        currentUser = new CurrentUser();
        currentUser.setUser(user);
        currentUser.setAuthorities(authorities);
    }

    @Test
    void testDefaultConstructor() {
        CurrentUser cu = new CurrentUser();
        assertNotNull(cu);
    }

    @Test
    void testSetAndGetUser() {
        assertEquals(user, currentUser.getUser());
    }

    @Test
    void testSetAndGetAuthorities() {
        assertNotNull(currentUser.getAuthorities());
        assertEquals(1, currentUser.getAuthorities().size());
    }

    @Test
    void testGetPassword() {
        assertEquals("encodedPassword", currentUser.getPassword());
    }

    @Test
    void testGetUsername() {
        assertEquals("testuser", currentUser.getUsername());
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(currentUser.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(currentUser.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(currentUser.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(currentUser.isEnabled());
    }

    @Test
    void testAuthoritiesContainRole() {
        boolean hasRole = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
        assertTrue(hasRole);
    }

    @Test
    void testSetUserNull() {
        currentUser.setUser(null);
        assertNull(currentUser.getUser());
    }

    @Test
    void testSetAuthoritiesEmpty() {
        currentUser.setAuthorities(new HashSet<>());
        assertNotNull(currentUser.getAuthorities());
        assertTrue(currentUser.getAuthorities().isEmpty());
    }

    @Test
    void testSetAuthoritiesMultiple() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        currentUser.setAuthorities(authorities);
        assertEquals(2, currentUser.getAuthorities().size());
    }

    @Test
    void testGetAuthoritiesReturnsCollection() {
        assertNotNull(currentUser.getAuthorities());
        assertFalse(currentUser.getAuthorities().isEmpty());
    }

    @Test
    void testUserDetailsImplementation() {
        assertTrue(currentUser instanceof org.springframework.security.core.userdetails.UserDetails);
    }
}
