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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
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
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1);
        testRole.setName("ROLE_USER");

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .enabled(1)
                .role(testRole)
                .build();

        testUsers = Arrays.asList(testUser);

        // Set dependencies using setters
        userService.setUserRepository(userRepository);
        userService.setRoleRepository(roleRepository);
        userService.setPasswordEncoder(passwordEncoder);
        userService.setAuthenticationManager(authenticationManager);
        userService.setSpringDataUserDetailsService(springDataUserDetailsService);
    }

    @Test
    void findByUsername_shouldReturnUser() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);

        // Act
        User result = userService.findByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void listAllUsers_shouldReturnEnabledUsers() {
        // Arrange
        when(userRepository.findAllByEnabled(1)).thenReturn(testUsers);

        // Act
        Iterable<User> result = userService.listAllUsers();

        // Assert
        assertNotNull(result);
        verify(userRepository).findAllByEnabled(1);
    }

    @Test
    void showUser_shouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.showUser(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void showUser_shouldReturnNull_whenNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        User result = userService.showUser(999L);

        // Assert
        assertNull(result);
        verify(userRepository).findById(999L);
    }

    @Test
    void saveUser_shouldEncodePasswordAndSave() {
        // Arrange
        User newUser = User.builder()
                .username("newuser")
                .password("plainpassword")
                .build();
        
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(testRole);
        when(passwordEncoder.encode("plainpassword")).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(springDataUserDetailsService.loadUserByUsername("newuser")).thenReturn(mockUserDetails);
        when(mockUserDetails.getAuthorities()).thenReturn(null);

        // Act
        userService.saveUser(newUser);

        // Assert
        assertEquals(1, newUser.getEnabled());
        verify(passwordEncoder).encode("plainpassword");
        verify(roleRepository).findByName("ROLE_USER");
        verify(userRepository, atLeastOnce()).save(newUser);
    }

    @Test
    void saveUser_shouldSetAdminRole_forFirstUser() {
        // Arrange
        User firstUser = User.builder()
                .id(1L)
                .username("firstuser")
                .password("password")
                .build();
        
        Role adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName("ROLE_ADMIN");
        
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(testRole);
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(adminRole);
        when(passwordEncoder.encode("password")).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(firstUser);
        when(springDataUserDetailsService.loadUserByUsername("firstuser")).thenReturn(mockUserDetails);
        when(mockUserDetails.getAuthorities()).thenReturn(null);

        // Act
        userService.saveUser(firstUser);

        // Assert
        verify(roleRepository).findByName("ROLE_ADMIN");
        verify(userRepository, times(2)).save(firstUser);
    }

    @Test
    void editUser_shouldEncodePasswordAndUpdate() {
        // Arrange
        testUser.setPassword("newpassword");
        when(passwordEncoder.encode("newpassword")).thenReturn("encodednewpassword");
        when(roleRepository.findById(1)).thenReturn(Optional.of(testRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.editUser(testUser);

        // Assert
        assertEquals(1, testUser.getEnabled());
        verify(passwordEncoder).encode("newpassword");
        verify(roleRepository).findById(1);
        verify(userRepository).save(testUser);
    }

    @Test
    void editUser_shouldHandleNullRole() {
        // Arrange
        testUser.setRole(null);
        testUser.setPassword("newpassword");
        when(passwordEncoder.encode("newpassword")).thenReturn("encodednewpassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(testRole);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.editUser(testUser);

        // Assert
        assertEquals(testRole, testUser.getRole());
        verify(roleRepository).findByName("ROLE_USER");
        verify(userRepository).save(testUser);
    }

    @Test
    void deleteUser_shouldDisableUserAndClearPassword() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.deleteUser(testUser);

        // Assert
        assertEquals(0, testUser.getEnabled());
        assertNull(testUser.getPassword());
        verify(userRepository).save(testUser);
    }

    @Test
    void deleteUser_shouldNotDeleteFromDatabase() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.deleteUser(testUser);

        // Assert
        verify(userRepository).save(testUser);
        verify(userRepository, never()).delete(any());
    }
}
