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
    private Set<GrantedAuthority> authorities;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password123");
        user.setEnabled(1);
        user.setRole(role);

        authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        currentUser = new CurrentUser();
        currentUser.setUser(user);
        currentUser.setAuthorities(authorities);
    }

    @Test
    void testCurrentUserDefaultConstructor() {
        CurrentUser newCurrentUser = new CurrentUser();
        assertNotNull(newCurrentUser);
    }

    @Test
    void testGetUser() {
        assertEquals(user, currentUser.getUser());
    }

    @Test
    void testSetUser() {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setUsername("newuser");
        newUser.setPassword("newpass");
        newUser.setRole(role);
        currentUser.setUser(newUser);
        assertEquals(newUser, currentUser.getUser());
    }

    @Test
    void testGetAuthorities() {
        assertNotNull(currentUser.getAuthorities());
        assertEquals(1, currentUser.getAuthorities().size());
    }

    @Test
    void testSetAuthorities() {
        Set<GrantedAuthority> newAuthorities = new HashSet<>();
        newAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        currentUser.setAuthorities(newAuthorities);
        assertEquals(newAuthorities, currentUser.getAuthorities());
    }

    @Test
    void testGetPassword() {
        assertEquals("password123", currentUser.getPassword());
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
    void testGetPasswordReturnsUserPassword() {
        user.setPassword("newpassword");
        assertEquals("newpassword", currentUser.getPassword());
    }

    @Test
    void testGetUsernameReturnsUserUsername() {
        user.setUsername("updateduser");
        assertEquals("updateduser", currentUser.getUsername());
    }

    @Test
    void testAuthoritiesContainRole() {
        boolean hasRole = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
        assertTrue(hasRole);
    }

    @Test
    void testMultipleAuthorities() {
        Set<GrantedAuthority> multiAuthorities = new HashSet<>();
        multiAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        multiAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        currentUser.setAuthorities(multiAuthorities);
        assertEquals(2, currentUser.getAuthorities().size());
    }

    @Test
    void testEqualsAndHashCode() {
        CurrentUser cu1 = new CurrentUser();
        cu1.setUser(user);
        cu1.setAuthorities(authorities);

        CurrentUser cu2 = new CurrentUser();
        cu2.setUser(user);
        cu2.setAuthorities(authorities);

        assertEquals(cu1, cu2);
        assertEquals(cu1.hashCode(), cu2.hashCode());
    }

    @Test
    void testToString() {
        String toString = currentUser.toString();
        assertNotNull(toString);
    }
}
