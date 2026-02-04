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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Role userRole;
    private Role adminRole;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Create roles
        userRole = new Role();
        userRole.setId(1);
        userRole.setName("ROLE_USER");

        adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName("ROLE_ADMIN");

        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEnabled(1);
        testUser.setRole(userRole);

        // Setup mock userDetails
        userDetails = mock(UserDetails.class);

        // Don't mock authorities - we'll just verify login happens

        // Setup SecurityContextHolder mock
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void findByUsername_ReturnsUser() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);

        // Act
        User result = userService.findByUsername("testuser");

        // Assert
        assertEquals(testUser, result);
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void listAllUsers_ReturnsEnabledUsers() {
        // Arrange
        ArrayList<User> users = new ArrayList<>();
        users.add(testUser);
        when(userRepository.findAllByEnabled(1)).thenReturn(users);

        // Act
        Iterable<User> result = userService.listAllUsers();

        // Assert
        assertEquals(users, result);
        verify(userRepository).findAllByEnabled(1);
    }

    @Test
    void showUser_ExistingUser_ReturnsUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.showUser(1L);

        // Assert
        assertEquals(testUser, result);
        verify(userRepository).findById(1L);
    }

    @Test
    void showUser_NonExistingUser_ReturnsNull() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        User result = userService.showUser(999L);

        // Assert
        assertNull(result);
        verify(userRepository).findById(999L);
    }

    @Test
    void saveUser_RegularUser_SavesWithUserRole() {
        // Arrange
        User newUser = new User();
        newUser.setId(2L);
        newUser.setUsername("newuser");
        newUser.setPassword("password");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(userRole);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(springDataUserDetailsService.loadUserByUsername("newuser")).thenReturn(userDetails);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        // Act
        userService.saveUser(newUser);

        // Assert
        assertEquals(userRole, newUser.getRole());
        assertEquals(1, newUser.getEnabled());
        assertEquals("encodedPassword", newUser.getPassword());

        verify(userRepository).save(newUser);
        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(roleRepository, never()).findByName("ROLE_ADMIN");
        verify(passwordEncoder).encode("password");
        verify(springDataUserDetailsService).loadUserByUsername("newuser");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(securityContext).setAuthentication(any(Authentication.class));
    }

    @Test
    void saveUser_AdminUser_SavesWithAdminRole() {
        // Arrange
        when(roleRepository.findByName("ROLE_USER")).thenReturn(userRole);
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(adminRole);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(springDataUserDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        // Act
        userService.saveUser(testUser); // testUser has ID=1L

        // Assert
        assertEquals(adminRole, testUser.getRole());
        assertEquals(1, testUser.getEnabled());
        assertEquals("encodedPassword", testUser.getPassword());

        verify(userRepository, times(2)).save(testUser); // Once for initial save, once for role update
        verify(roleRepository).findByName("ROLE_USER");
        verify(roleRepository).findByName("ROLE_ADMIN");
        verify(passwordEncoder).encode("password");
        verify(springDataUserDetailsService).loadUserByUsername("testuser");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(securityContext).setAuthentication(any(Authentication.class));
    }

    @Test
    void editUser_WithValidRole_UpdatesUser() {
        // Arrange
        when(passwordEncoder.encode("newpassword")).thenReturn("newEncodedPassword");
        when(roleRepository.findById(1)).thenReturn(Optional.of(userRole));

        testUser.setPassword("newpassword");

        // Act
        userService.editUser(testUser);

        // Assert
        assertEquals("newEncodedPassword", testUser.getPassword());
        assertEquals(userRole, testUser.getRole());
        assertEquals(1, testUser.getEnabled());

        verify(userRepository).save(testUser);
        verify(passwordEncoder).encode("newpassword");
        verify(roleRepository).findById(1);
        verify(roleRepository, never()).findByName("ROLE_USER"); // Should not need fallback
    }

    @Test
    void editUser_WithNullRole_UsesFallbackRole() {
        // Arrange
        when(passwordEncoder.encode("newpassword")).thenReturn("newEncodedPassword");
        testUser.setRole(null);
        testUser.setPassword("newpassword");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(userRole);

        // Act
        userService.editUser(testUser);

        // Assert
        assertEquals("newEncodedPassword", testUser.getPassword());
        assertEquals(userRole, testUser.getRole());
        assertEquals(1, testUser.getEnabled());

        verify(userRepository).save(testUser);
        verify(passwordEncoder).encode("newpassword");
        verify(roleRepository, never()).findById(anyInt());
        verify(roleRepository).findByName("ROLE_USER"); // Should use fallback
    }

    @Test
    void deleteUser_DisablesAndClearsPassword() {
        // Act
        userService.deleteUser(testUser);

        // Assert
        assertEquals(0, testUser.getEnabled());
        assertNull(testUser.getPassword());

        verify(userRepository).save(testUser);
    }
}