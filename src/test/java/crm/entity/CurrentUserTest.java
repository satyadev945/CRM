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
        assertEquals(authorities, currentUser.getAuthorities());
        assertEquals(1, currentUser.getAuthorities().size());
    }

    @Test
    void getPassword_shouldReturnUserPassword() {
        assertEquals("password123", currentUser.getPassword());
    }

    @Test
    void getUsername_shouldReturnUserUsername() {
        assertEquals("testuser", currentUser.getUsername());
    }

    @Test
    void isAccountNonExpired_shouldReturnTrue() {
        assertTrue(currentUser.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked_shouldReturnTrue() {
        assertTrue(currentUser.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired_shouldReturnTrue() {
        assertTrue(currentUser.isCredentialsNonExpired());
    }

    @Test
    void isEnabled_shouldReturnTrue() {
        assertTrue(currentUser.isEnabled());
    }

    @Test
    void setAndGetUser_shouldWorkCorrectly() {
        User newUser = User.builder()
                .id(2L)
                .username("newuser")
                .build();

        currentUser.setUser(newUser);

        assertEquals(newUser, currentUser.getUser());
        assertEquals("newuser", currentUser.getUsername());
    }

    @Test
    void setAndGetAuthorities_shouldWorkCorrectly() {
        Set<GrantedAuthority> newAuthorities = new HashSet<>();
        newAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        currentUser.setAuthorities(newAuthorities);

        assertEquals(newAuthorities, currentUser.getAuthorities());
        assertEquals(1, currentUser.getAuthorities().size());
    }

    @Test
    void currentUser_withNullUser_shouldHandleGracefully() {
        CurrentUser nullUser = new CurrentUser();
        nullUser.setUser(null);

        assertNull(nullUser.getUser());
        assertThrows(NullPointerException.class, () -> nullUser.getPassword());
    }

    @Test
    void currentUser_withEmptyAuthorities_shouldHandleGracefully() {
        currentUser.setAuthorities(new HashSet<>());

        assertNotNull(currentUser.getAuthorities());
        assertEquals(0, currentUser.getAuthorities().size());
    }

    @Test
    void currentUser_equalsAndHashCode_shouldWorkCorrectly() {
        CurrentUser currentUser1 = new CurrentUser();
        currentUser1.setUser(user);
        currentUser1.setAuthorities(authorities);

        CurrentUser currentUser2 = new CurrentUser();
        currentUser2.setUser(user);
        currentUser2.setAuthorities(authorities);

        assertEquals(currentUser1, currentUser2);
        assertEquals(currentUser1.hashCode(), currentUser2.hashCode());
    }
}
