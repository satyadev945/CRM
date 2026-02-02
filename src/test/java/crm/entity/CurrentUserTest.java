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
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        currentUser = new CurrentUser();
        currentUser.setUser(user);
        currentUser.setAuthorities(authorities);
    }

    @Test
    void getAuthoritiesShouldReturnAuthorities() {
        // Assert
        assertEquals(authorities, currentUser.getAuthorities());
    }

    @Test
    void getPasswordShouldReturnUserPassword() {
        // Assert
        assertEquals(user.getPassword(), currentUser.getPassword());
    }

    @Test
    void getUsernameShouldReturnUserUsername() {
        // Assert
        assertEquals(user.getUsername(), currentUser.getUsername());
    }

    @Test
    void isAccountNonExpiredShouldReturnTrue() {
        // Assert
        assertTrue(currentUser.isAccountNonExpired());
    }

    @Test
    void isAccountNonLockedShouldReturnTrue() {
        // Assert
        assertTrue(currentUser.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpiredShouldReturnTrue() {
        // Assert
        assertTrue(currentUser.isCredentialsNonExpired());
    }

    @Test
    void isEnabledShouldReturnTrue() {
        // Assert
        assertTrue(currentUser.isEnabled());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        CurrentUser sameCurrentUser = new CurrentUser();
        sameCurrentUser.setUser(user);
        sameCurrentUser.setAuthorities(authorities);

        User differentUser = new User();
        differentUser.setUsername("different");
        differentUser.setPassword("different");

        CurrentUser differentCurrentUser = new CurrentUser();
        differentCurrentUser.setUser(differentUser);
        differentCurrentUser.setAuthorities(authorities);

        // Assert
        assertEquals(currentUser, sameCurrentUser);
        assertEquals(currentUser.hashCode(), sameCurrentUser.hashCode());
        assertNotEquals(currentUser, differentCurrentUser);
        assertNotEquals(currentUser.hashCode(), differentCurrentUser.hashCode());
    }
}