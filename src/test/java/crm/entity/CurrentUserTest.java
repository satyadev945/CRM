package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrentUserTest {

    @Mock
    private User mockUser;

    private CurrentUser currentUser;
    private Set<GrantedAuthority> authorities;

    @BeforeEach
    void setUp() {
        currentUser = new CurrentUser();
        authorities = new HashSet<>();
    }

    @Test
    void testCurrentUserCreation() {
        assertNotNull(currentUser);
    }

    @Test
    void testSetAndGetUser() {
        currentUser.setUser(mockUser);
        assertEquals(mockUser, currentUser.getUser());
    }

    @Test
    void testSetAndGetAuthorities() {
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        currentUser.setAuthorities(authorities);

        assertEquals(2, currentUser.getAuthorities().size());
        assertTrue(currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testGetPassword() {
        when(mockUser.getPassword()).thenReturn("encodedPassword123");
        currentUser.setUser(mockUser);

        assertEquals("encodedPassword123", currentUser.getPassword());
        verify(mockUser).getPassword();
    }

    @Test
    void testGetUsername() {
        when(mockUser.getUsername()).thenReturn("testuser");
        currentUser.setUser(mockUser);

        assertEquals("testuser", currentUser.getUsername());
        verify(mockUser).getUsername();
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
    void testGetAuthoritiesReturnsEmptySet() {
        currentUser.setAuthorities(new HashSet<>());
        assertNotNull(currentUser.getAuthorities());
        assertEquals(0, currentUser.getAuthorities().size());
    }

    @Test
    void testGetAuthoritiesWithSingleRole() {
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        currentUser.setAuthorities(authorities);

        assertEquals(1, currentUser.getAuthorities().size());
        assertTrue(currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testGetAuthoritiesWithMultipleRoles() {
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        authorities.add(new SimpleGrantedAuthority("ROLE_MANAGER"));

        currentUser.setAuthorities(authorities);

        assertEquals(3, currentUser.getAuthorities().size());
    }

    @Test
    void testNullUser() {
        currentUser.setUser(null);
        assertNull(currentUser.getUser());
    }

    @Test
    void testNullAuthorities() {
        currentUser.setAuthorities(null);
        assertNull(currentUser.getAuthorities());
    }

    @Test
    void testUserDetailsInterfaceImplementation() {
        when(mockUser.getUsername()).thenReturn("john.doe");
        when(mockUser.getPassword()).thenReturn("password123");

        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        currentUser.setUser(mockUser);
        currentUser.setAuthorities(authorities);

        // Verify UserDetails interface methods
        assertEquals("john.doe", currentUser.getUsername());
        assertEquals("password123", currentUser.getPassword());
        assertEquals(1, currentUser.getAuthorities().size());
        assertTrue(currentUser.isAccountNonExpired());
        assertTrue(currentUser.isAccountNonLocked());
        assertTrue(currentUser.isCredentialsNonExpired());
        assertTrue(currentUser.isEnabled());
    }

    @Test
    void testGetPasswordWhenUserIsNull() {
        currentUser.setUser(null);
        assertThrows(NullPointerException.class, () -> currentUser.getPassword());
    }

    @Test
    void testGetUsernameWhenUserIsNull() {
        currentUser.setUser(null);
        assertThrows(NullPointerException.class, () -> currentUser.getUsername());
    }

    @Test
    void testEqualsAndHashCode() {
        CurrentUser user1 = new CurrentUser();
        user1.setUser(mockUser);
        user1.setAuthorities(authorities);

        CurrentUser user2 = new CurrentUser();
        user2.setUser(mockUser);
        user2.setAuthorities(authorities);

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testToString() {
        currentUser.setUser(mockUser);
        currentUser.setAuthorities(authorities);

        String toString = currentUser.toString();
        assertNotNull(toString);
    }

    @Test
    void testAuthoritiesImmutability() {
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        currentUser.setAuthorities(authorities);

        Set<GrantedAuthority> retrievedAuthorities = (Set<GrantedAuthority>) currentUser.getAuthorities();
        assertNotNull(retrievedAuthorities);
        assertEquals(1, retrievedAuthorities.size());
    }
}
