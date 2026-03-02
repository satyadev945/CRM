package crm.service;

import crm.entity.Role;
import crm.entity.User;
import crm.repository.RoleRepository;
import crm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SpringDataUserDetailsService springDataUserDetailsService;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role(1L, "USER");
        testUser = new User(1L, "testuser", "test@example.com", "John", "Doe", "password", 1, testRole);
    }

    @Test
    void findByUsername_shouldReturnUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);

        User found = userService.findByUsername("testuser");

        assertNotNull(found);
        assertEquals("testuser", found.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void listAllUsers_shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));

        Iterable<User> users = userService.listAllUsers();

        assertNotNull(users);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void showUser_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User found = userService.showUser(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void deleteUser_shouldCallRepository() {
        userService.deleteUser(testUser);

        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void userService_shouldImplementUserService() {
        assertTrue(userService instanceof UserService);
    }
}
