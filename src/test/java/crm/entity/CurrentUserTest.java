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
        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .enabled(1)
                .build();

        authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        currentUser = new CurrentUser();
        currentUser.setUser(user);
        currentUser.setAuthorities(authorities);
    }

    @Test
    void getAuthorities_shouldReturnAuthorities() {
        // Act
        var result = currentUser.getAuthorities();

        // Assert
        assertNotNull(result);
        assertEquals(authorities, result);
        assertEquals(1, result.size());
    }

    @Test
    void getPassword_shouldReturnUserPassword() {
        // Act
        String password = currentUser.getPassword();

        // Assert
        assertEquals("password123", password);
    }

    @Test
    void getUsername_shouldReturnUserUsername() {
        // Act
        String username = currentUser.getUsername();

        // Assert
        assertEquals("testuser", username);
    }

    @Test
    void isAccountNonExpired_shouldReturnTrue() {
        // Act
        boolean result = currentUser.isAccountNonExpired();

        // Assert
        assertTrue(result);
    }

    @Test
    void isAccountNonLocked_shouldReturnTrue() {
        // Act
        boolean result = currentUser.isAccountNonLocked();

        // Assert
        assertTrue(result);
    }

    @Test
    void isCredentialsNonExpired_shouldReturnTrue() {
        // Act
        boolean result = currentUser.isCredentialsNonExpired();

        // Assert
        assertTrue(result);
    }

    @Test
    void isEnabled_shouldReturnTrue() {
        // Act
        boolean result = currentUser.isEnabled();

        // Assert
        assertTrue(result);
    }

    @Test
    void setUser_shouldSetUser() {
        // Arrange
        User newUser = User.builder()
                .id(2L)
                .username("newuser")
                .password("newpass")
                .build();

        // Act
        currentUser.setUser(newUser);

        // Assert
        assertEquals(newUser, currentUser.getUser());
        assertEquals("newuser", currentUser.getUsername());
        assertEquals("newpass", currentUser.getPassword());
    }

    @Test
    void setAuthorities_shouldSetAuthorities() {
        // Arrange
        Set<GrantedAuthority> newAuthorities = new HashSet<>();
        newAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // Act
        currentUser.setAuthorities(newAuthorities);

        // Assert
        assertEquals(newAuthorities, currentUser.getAuthorities());
    }

    @Test
    void currentUser_shouldImplementUserDetails() {
        // Assert
        assertTrue(currentUser instanceof org.springframework.security.core.userdetails.UserDetails);
    }
}
