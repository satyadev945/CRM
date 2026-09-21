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
        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEnabled(1);
        user.setRole(role);

        authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        currentUser = new CurrentUser();
        currentUser.setUser(user);
        currentUser.setAuthorities(authorities);
    }

    @Test
    void testDefaultConstructor() {
        CurrentUser cu = new CurrentUser();
        assertNotNull(cu);
        assertNull(cu.getUser());
        assertNull(cu.getAuthorities());
    }

    @Test
    void testSetAndGetUser() {
        assertEquals(user, currentUser.getUser());
    }

    @Test
    void testSetAndGetAuthorities() {
        assertEquals(authorities, currentUser.getAuthorities());
    }

    @Test
    void testGetPassword_ReturnsUserPassword() {
        assertEquals("testpassword", currentUser.getPassword());
    }

    @Test
    void testGetUsername_ReturnsUserUsername() {
        assertEquals("testuser", currentUser.getUsername());
    }

    @Test
    void testIsAccountNonExpired_ReturnsTrue() {
        assertTrue(currentUser.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked_ReturnsTrue() {
        assertTrue(currentUser.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired_ReturnsTrue() {
        assertTrue(currentUser.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled_ReturnsTrue() {
        assertTrue(currentUser.isEnabled());
    }

    @Test
    void testGetAuthorities_ReturnsCorrectAuthorities() {
        assertNotNull(currentUser.getAuthorities());
        assertEquals(1, currentUser.getAuthorities().size());
        assertTrue(currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
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
        Set<GrantedAuthority> multiAuth = new HashSet<>();
        multiAuth.add(new SimpleGrantedAuthority("ROLE_USER"));
        multiAuth.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        currentUser.setAuthorities(multiAuth);
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
        String str = currentUser.toString();
        assertNotNull(str);
    }
}
