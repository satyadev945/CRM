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
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password123");
        user.setEnabled(1);
        user.setRole(role);
    }

    @Test
    void testFindByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        User result = userService.findByUsername("testuser");
        assertEquals(user, result);
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testFindByUsername_notFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);
        User result = userService.findByUsername("unknown");
        assertNull(result);
        verify(userRepository).findByUsername("unknown");
    }

    @Test
    void testListAllUsers() {
        List<User> users = Arrays.asList(user);
        when(userRepository.findAllByEnabled(1)).thenReturn(users);
        Iterable<User> result = userService.listAllUsers();
        assertNotNull(result);
        verify(userRepository).findAllByEnabled(1);
    }

    @Test
    void testShowUser_found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = userService.showUser(1L);
        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findById(1L);
    }

    @Test
    void testShowUser_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        User result = userService.showUser(99L);
        assertNull(result);
        verify(userRepository).findById(99L);
    }

    @Test
    void testSaveUser_regularUser() {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setPassword("rawpassword");
        newUser.setEnabled(0);

        Role userRole = new Role();
        userRole.setId(1);
        userRole.setName("ROLE_USER");

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("newuser");
        when(mockUserDetails.getAuthorities()).thenReturn(new java.util.HashSet<>());

        when(roleRepository.findByName("ROLE_USER")).thenReturn(userRole);
        when(passwordEncoder.encode("rawpassword")).thenReturn("encodedpassword");
        when(springDataUserDetailsService.loadUserByUsername("newuser")).thenReturn(mockUserDetails);
        when(authenticationManager.authenticate(any())).thenReturn(null);

        userService.saveUser(newUser);

        assertEquals("encodedpassword", newUser.getPassword());
        assertEquals(1, newUser.getEnabled());
        verify(userRepository, atLeastOnce()).save(newUser);
    }

    @Test
    void testSaveUser_firstUser_becomesAdmin() {
        User firstUser = new User();
        firstUser.setId(1L);
        firstUser.setUsername("admin");
        firstUser.setEmail("admin@example.com");
        firstUser.setFirstName("Admin");
        firstUser.setLastName("User");
        firstUser.setPassword("adminpass");
        firstUser.setEnabled(0);

        Role userRole = new Role();
        userRole.setId(1);
        userRole.setName("ROLE_USER");

        Role adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName("ROLE_ADMIN");

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("admin");
        when(mockUserDetails.getAuthorities()).thenReturn(new java.util.HashSet<>());

        when(roleRepository.findByName("ROLE_USER")).thenReturn(userRole);
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(adminRole);
        when(passwordEncoder.encode("adminpass")).thenReturn("encodedadminpass");
        when(springDataUserDetailsService.loadUserByUsername("admin")).thenReturn(mockUserDetails);
        when(authenticationManager.authenticate(any())).thenReturn(null);

        userService.saveUser(firstUser);

        assertEquals(adminRole, firstUser.getRole());
        verify(userRepository, times(2)).save(firstUser);
    }

    @Test
    void testEditUser_withValidRole() {
        Role existingRole = new Role();
        existingRole.setId(2);
        existingRole.setName("ROLE_ADMIN");
        user.setRole(existingRole);

        Role defaultRole = new Role();
        defaultRole.setId(1);
        defaultRole.setName("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(defaultRole);
        when(roleRepository.findById(2)).thenReturn(Optional.of(existingRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedpassword");

        userService.editUser(user);

        assertEquals("encodedpassword", user.getPassword());
        assertEquals(1, user.getEnabled());
        verify(userRepository).save(user);
    }

    @Test
    void testEditUser_withNullRole() {
        user.setRole(null);

        Role defaultRole = new Role();
        defaultRole.setId(1);
        defaultRole.setName("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(defaultRole);
        when(passwordEncoder.encode("password123")).thenReturn("encodedpassword");

        userService.editUser(user);

        assertEquals(defaultRole, user.getRole());
        assertEquals(1, user.getEnabled());
        verify(userRepository).save(user);
    }

    @Test
    void testDeleteUser() {
        userService.deleteUser(user);

        assertEquals(0, user.getEnabled());
        assertNull(user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void testDeleteUser_setsEnabledToZero() {
        user.setEnabled(1);
        userService.deleteUser(user);
        assertEquals(0, user.getEnabled());
    }

    @Test
    void testDeleteUser_setsPasswordToNull() {
        user.setPassword("somepassword");
        userService.deleteUser(user);
        assertNull(user.getPassword());
    }

    @Test
    void testSetUserRepository() {
        userService.setUserRepository(userRepository);
        assertNotNull(userService);
    }

    @Test
    void testSetRoleRepository() {
        userService.setRoleRepository(roleRepository);
        assertNotNull(userService);
    }

    @Test
    void testSetPasswordEncoder() {
        userService.setPasswordEncoder(passwordEncoder);
        assertNotNull(userService);
    }

    @Test
    void testSetAuthenticationManager() {
        userService.setAuthenticationManager(authenticationManager);
        assertNotNull(userService);
    }

    @Test
    void testSetSpringDataUserDetailsService() {
        userService.setSpringDataUserDetailsService(springDataUserDetailsService);
        assertNotNull(userService);
    }
}
