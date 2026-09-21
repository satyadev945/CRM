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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

    private User user;
    private Role userRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(1);
        userRole.setName("ROLE_USER");

        adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName("ROLE_ADMIN");

        user = User.builder()
                .id(1L)
                .username("johndoe")
                .email("john@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("plainpassword")
                .enabled(1)
                .role(userRole)
                .build();
    }

    @Test
    void testFindByUsername_ReturnsUser() {
        when(userRepository.findByUsername("johndoe")).thenReturn(user);
        User result = userService.findByUsername("johndoe");
        assertEquals(user, result);
        verify(userRepository).findByUsername("johndoe");
    }

    @Test
    void testFindByUsername_NotFound_ReturnsNull() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);
        User result = userService.findByUsername("unknown");
        assertNull(result);
        verify(userRepository).findByUsername("unknown");
    }

    @Test
    void testListAllUsers_ReturnsEnabledUsers() {
        List<User> users = Arrays.asList(user);
        when(userRepository.findAllByEnabled(1)).thenReturn(users);
        Iterable<User> result = userService.listAllUsers();
        assertNotNull(result);
        verify(userRepository).findAllByEnabled(1);
    }

    @Test
    void testShowUser_ExistingId_ReturnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = userService.showUser(1L);
        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findById(1L);
    }

    @Test
    void testShowUser_NonExistingId_ReturnsNull() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        User result = userService.showUser(99L);
        assertNull(result);
        verify(userRepository).findById(99L);
    }

    @Test
    void testSaveUser_SetsEnabledAndEncodesPassword() {
        // Arrange
        String rawPassword = "plainpassword";
        String encodedPassword = "$2a$10$encodedpassword";

        when(roleRepository.findByName("ROLE_USER")).thenReturn(userRole);
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(adminRole);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("johndoe");
        when(mockUserDetails.getAuthorities()).thenReturn(new java.util.HashSet<>());
        when(springDataUserDetailsService.loadUserByUsername("johndoe")).thenReturn(mockUserDetails);
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.saveUser(user);

        // Assert
        assertEquals(1, user.getEnabled());
        assertEquals(encodedPassword, user.getPassword());
        verify(userRepository, atLeastOnce()).save(user);
    }

    @Test
    void testSaveUser_FirstUser_GetsAdminRole() {
        // Arrange - user.getId() == 1L triggers admin role assignment
        String rawPassword = "plainpassword";
        String encodedPassword = "$2a$10$encodedpassword";

        when(roleRepository.findByName("ROLE_USER")).thenReturn(userRole);
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(adminRole);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("johndoe");
        when(mockUserDetails.getAuthorities()).thenReturn(new java.util.HashSet<>());
        when(springDataUserDetailsService.loadUserByUsername("johndoe")).thenReturn(mockUserDetails);
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.saveUser(user);

        // Assert - admin role should be set for user with id=1
        verify(roleRepository).findByName("ROLE_ADMIN");
        verify(userRepository, times(2)).save(user);
    }

    @Test
    void testSaveUser_NonFirstUser_DoesNotGetAdminRole() {
        // Arrange - user with id=2 should NOT get admin role
        User user2 = User.builder()
                .id(2L)
                .username("user2")
                .email("user2@test.com")
                .firstName("User")
                .lastName("Two")
                .password("password2")
                .enabled(1)
                .role(userRole)
                .build();

        String rawPassword = "password2";
        String encodedPassword = "$2a$10$encoded2";

        when(roleRepository.findByName("ROLE_USER")).thenReturn(userRole);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("user2");
        when(mockUserDetails.getAuthorities()).thenReturn(new java.util.HashSet<>());
        when(springDataUserDetailsService.loadUserByUsername("user2")).thenReturn(mockUserDetails);
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(user2);

        // Act
        userService.saveUser(user2);

        // Assert - admin role should NOT be fetched for user with id=2
        verify(roleRepository, never()).findByName("ROLE_ADMIN");
        verify(userRepository, times(1)).save(user2);
    }

    @Test
    void testEditUser_EncodesPasswordAndSaves() {
        String rawPassword = "newpassword";
        String encodedPassword = "$2a$10$newencoded";
        user.setPassword(rawPassword);

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(userRole);
        when(roleRepository.findById(userRole.getId())).thenReturn(Optional.of(userRole));

        userService.editUser(user);

        assertEquals(encodedPassword, user.getPassword());
        assertEquals(1, user.getEnabled());
        verify(userRepository).save(user);
    }

    @Test
    void testEditUser_NullRole_UsesDefaultRole() {
        user.setRole(null);
        String rawPassword = "password";
        user.setPassword(rawPassword);

        when(passwordEncoder.encode(rawPassword)).thenReturn("encoded");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(userRole);

        userService.editUser(user);

        assertEquals(userRole, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void testDeleteUser_SetsEnabledZeroAndNullPassword() {
        userService.deleteUser(user);

        assertEquals(0, user.getEnabled());
        assertNull(user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void testDeleteUser_SavesUser() {
        userService.deleteUser(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testSetUserRepository_ViaAutowired() {
        UserServiceImpl service = new UserServiceImpl();
        service.setUserRepository(userRepository);
        assertNotNull(service);
    }

    @Test
    void testSetRoleRepository_ViaAutowired() {
        UserServiceImpl service = new UserServiceImpl();
        service.setRoleRepository(roleRepository);
        assertNotNull(service);
    }

    @Test
    void testSetPasswordEncoder_ViaAutowired() {
        UserServiceImpl service = new UserServiceImpl();
        service.setPasswordEncoder(passwordEncoder);
        assertNotNull(service);
    }

    @Test
    void testSetAuthenticationManager_ViaAutowired() {
        UserServiceImpl service = new UserServiceImpl();
        service.setAuthenticationManager(authenticationManager);
        assertNotNull(service);
    }

    @Test
    void testSetSpringDataUserDetailsService_ViaAutowired() {
        UserServiceImpl service = new UserServiceImpl();
        service.setSpringDataUserDetailsService(springDataUserDetailsService);
        assertNotNull(service);
    }
}
