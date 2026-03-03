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
        testRole = new Role();
        testRole.setId(1);
        testRole.setName("ROLE_USER");

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .enabled(1)
                .role(testRole)
                .build();
    }

    @Test
    void findByUsername_shouldReturnUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);

        User result = userService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void findByUsername_withNonExistentUsername_shouldReturnNull() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        User result = userService.findByUsername("nonexistent");

        assertNull(result);
    }

    @Test
    void listAllUsers_shouldReturnEnabledUsers() {
        when(userRepository.findAllByEnabled(1)).thenReturn(Arrays.asList(testUser));

        Iterable<User> result = userService.listAllUsers();

        assertNotNull(result);
        verify(userRepository).findAllByEnabled(1);
    }

    @Test
    void showUser_shouldReturnUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.showUser(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void showUser_withNonExistentId_shouldReturnNull() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        User result = userService.showUser(999L);

        assertNull(result);
    }

    @Test
    void editUser_shouldEncodePasswordAndSave() {
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findById(1)).thenReturn(Optional.of(testRole));
        when(userRepository.save(testUser)).thenReturn(testUser);

        userService.editUser(testUser);

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(testUser);
        assertEquals(1, testUser.getEnabled());
    }

    @Test
    void editUser_withNullRole_shouldUseDefaultRole() {
        testUser.setRole(null);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(testRole);
        when(userRepository.save(testUser)).thenReturn(testUser);

        userService.editUser(testUser);

        verify(roleRepository).findByName("ROLE_USER");
        verify(userRepository).save(testUser);
    }

    @Test
    void deleteUser_shouldSetEnabledToZeroAndNullPassword() {
        when(userRepository.save(testUser)).thenReturn(testUser);

        userService.deleteUser(testUser);

        assertEquals(0, testUser.getEnabled());
        assertNull(testUser.getPassword());
        verify(userRepository).save(testUser);
    }

    @Test
    void deleteUser_withAlreadyDisabledUser_shouldStillSave() {
        testUser.setEnabled(0);
        when(userRepository.save(testUser)).thenReturn(testUser);

        userService.deleteUser(testUser);

        assertEquals(0, testUser.getEnabled());
        verify(userRepository).save(testUser);
    }
}
