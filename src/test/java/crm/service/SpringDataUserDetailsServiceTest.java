package crm.service;

import crm.entity.CurrentUser;
import crm.entity.Role;
import crm.entity.User;
import crm.repository.UserRepository;
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
    private UserRepository userRepository;

    @InjectMocks
    private SpringDataUserDetailsService userDetailsService;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role(1L, "USER");
        testUser = new User(1L, "testuser", "test@example.com", "John", "Doe", "password", 1, testRole);
    }

    @Test
    void loadUserByUsername_withExistingUser_shouldReturnUserDetails() {
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertTrue(userDetails instanceof CurrentUser);
        assertEquals("testuser", userDetails.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_withNonExistingUser_shouldThrowException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent");
        });
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void springDataUserDetailsService_shouldNotBeNull() {
        assertNotNull(userDetailsService);
    }
}
