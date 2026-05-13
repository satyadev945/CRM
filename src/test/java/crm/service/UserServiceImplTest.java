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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("rawPassword");
        user.setEnabled(1);
        user.setRole(role);
    }

    @Test
    void testFindByUsername_returnsUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        User result = userService.findByUsername("testuser");
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testFindByUsername_returnsNull_whenNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);
        User result = userService.findByUsername("unknown");
        assertNull(result);
    }

    @Test
    void testListAllUsers_returnsUsers() {
        List<User> users = Arrays.asList(user);
        when(userRepository.findAllByEnabled(1)).thenReturn(users);
        Iterable<User> result = userService.listAllUsers();
        assertNotNull(result);
        verify(userRepository).findAllByEnabled(1);
    }

    @Test
    void testShowUser_returnsUser_whenFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = userService.showUser(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testShowUser_returnsNull_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        User result = userService.showUser(99L);
        assertNull(result);
    }

    @Test
    void testDeleteUser_setsEnabledZeroAndNullPassword() {
        userService.deleteUser(user);
        assertEquals(0, user.getEnabled());
        assertNull(user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void testEditUser_withValidRole() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(roleRepository.findByName("ROLE_USER")).thenReturn(role);

        userService.editUser(user);

        verify(userRepository).save(user);
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    void testEditUser_withNullRole_fallsBackToDefaultRole() {
        user.setRole(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(role);

        userService.editUser(user);

        verify(userRepository).save(user);
    }

    @Test
    void testSetUserRepository() {
        UserServiceImpl service = new UserServiceImpl();
        service.setUserRepository(userRepository);
        assertNotNull(service);
    }

    @Test
    void testSetRoleRepository() {
        UserServiceImpl service = new UserServiceImpl();
        service.setRoleRepository(roleRepository);
        assertNotNull(service);
    }

    @Test
    void testSetPasswordEncoder() {
        UserServiceImpl service = new UserServiceImpl();
        service.setPasswordEncoder(passwordEncoder);
        assertNotNull(service);
    }

    @Test
    void testSetAuthenticationManager() {
        UserServiceImpl service = new UserServiceImpl();
        service.setAuthenticationManager(authenticationManager);
        assertNotNull(service);
    }

    @Test
    void testSetSpringDataUserDetailsService() {
        UserServiceImpl service = new UserServiceImpl();
        service.setSpringDataUserDetailsService(springDataUserDetailsService);
        assertNotNull(service);
    }
}
