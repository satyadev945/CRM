package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CurrentUserTest {

    private CurrentUser currentUser;
    private User testUser;
    private Set<GrantedAuthority> authorities;

    @BeforeEach
    void setUp() {
        currentUser = new CurrentUser();
        
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        
        authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Test
    void currentUser_shouldImplementUserDetails() {
        assertTrue(currentUser instanceof UserDetails);
    }

    @Test
    void setUser_shouldSetUserValue() {
        currentUser.setUser(testUser);
        assertEquals(testUser, currentUser.getUser());
    }

    @Test
    void setAuthorities_shouldSetAuthoritiesValue() {
        currentUser.setAuthorities(authorities);
        assertEquals(authorities, currentUser.getAuthorities());
    }

    @Test
    void getAuthorities_shouldReturnAuthorities() {
        currentUser.setAuthorities(authorities);
        assertEquals(authorities, currentUser.getAuthorities());
        assertEquals(2, currentUser.getAuthorities().size());
    }

    @Test
    void getPassword_shouldReturnUserPassword() {
        currentUser.setUser(testUser);
        assertEquals("password123", currentUser.getPassword());
    }

    @Test
    void getUsername_shouldReturnUserUsername() {
        currentUser.setUser(testUser);
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
    void getUser_withNullUser_shouldReturnNull() {
        assertNull(currentUser.getUser());
    }

    @Test
    void getAuthorities_withNullAuthorities_shouldReturnNull() {
        assertNull(currentUser.getAuthorities());
    }

    @Test
    void getPassword_withNullUser_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            currentUser.getPassword();
        });
    }

    @Test
    void getUsername_withNullUser_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            currentUser.getUsername();
        });
    }

    @Test
    void setAuthorities_withEmptySet_shouldSetEmptySet() {
        Set<GrantedAuthority> emptyAuthorities = new HashSet<>();
        currentUser.setAuthorities(emptyAuthorities);
        
        assertNotNull(currentUser.getAuthorities());
        assertTrue(currentUser.getAuthorities().isEmpty());
    }

    @Test
    void currentUser_shouldBeInstantiable() {
        assertNotNull(currentUser);
    }

    @Test
    void equals_withSameValues_shouldReturnTrue() {
        CurrentUser cu1 = new CurrentUser();
        cu1.setUser(testUser);
        cu1.setAuthorities(authorities);
        
        CurrentUser cu2 = new CurrentUser();
        cu2.setUser(testUser);
        cu2.setAuthorities(authorities);
        
        assertEquals(cu1, cu2);
    }

    @Test
    void hashCode_withSameValues_shouldReturnSameHashCode() {
        CurrentUser cu1 = new CurrentUser();
        cu1.setUser(testUser);
        cu1.setAuthorities(authorities);
        
        CurrentUser cu2 = new CurrentUser();
        cu2.setUser(testUser);
        cu2.setAuthorities(authorities);
        
        assertEquals(cu1.hashCode(), cu2.hashCode());
    }

    @Test
    void toString_shouldContainFieldValues() {
        currentUser.setUser(testUser);
        currentUser.setAuthorities(authorities);
        
        String toString = currentUser.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("CurrentUser"));
    }
}
