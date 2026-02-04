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
    private Set<GrantedAuthority> authorities;

    @BeforeEach
    void setUp() {
        // Create role
        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_ADMIN");

        // Create user
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setRole(role);
        user.setEnabled(1);

        // Create authorities
        authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // Create current user
        currentUser = new CurrentUser();
        currentUser.setUser(user);
        currentUser.setAuthorities(authorities);
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
        newUser.setPassword("newpassword");

        currentUser.setUser(newUser);
        assertEquals(newUser, currentUser.getUser());
    }

    @Test
    void testGetAuthorities() {
        assertEquals(authorities, currentUser.getAuthorities());
        assertEquals(1, currentUser.getAuthorities().size());
        assertTrue(currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testSetAuthorities() {
        Set<GrantedAuthority> newAuthorities = new HashSet<>();
        newAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        newAuthorities.add(new SimpleGrantedAuthority("ROLE_EDITOR"));

        currentUser.setAuthorities(newAuthorities);

        assertEquals(newAuthorities, currentUser.getAuthorities());
        assertEquals(2, currentUser.getAuthorities().size());
        assertTrue(currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EDITOR")));
        assertFalse(currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
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
    void testEqualsAndHashCode() {
        CurrentUser sameUser = new CurrentUser();
        sameUser.setUser(user);
        sameUser.setAuthorities(authorities);

        assertEquals(currentUser, sameUser);
        assertEquals(currentUser.hashCode(), sameUser.hashCode());
    }

    @Test
    void testToString() {
        String toString = currentUser.toString();
        assertTrue(toString.contains("user=" + user.toString()));
        assertTrue(toString.contains("authorities=" + authorities.toString()));
    }
}