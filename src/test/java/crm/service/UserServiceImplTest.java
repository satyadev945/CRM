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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .enabled(1)
                .role(testRole)
                .build();

        userService.setUserRepository(userRepository);
        userService.setRoleRepository(roleRepository);
        userService.setPasswordEncoder(passwordEncoder);
        userService.setAuthenticationManager(authenticationManager);
        userService.setSpringDataUserDetailsService(springDataUserDetailsService);
    }

    @Test
    void testFindByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);

        User result = userService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testFindByUsernameNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        User result = userService.findByUsername("nonexistent");

        assertNull(result);
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void testListAllUsers() {
        when(userRepository.findAllByEnabled(1)).thenReturn(Arrays.asList(testUser));

        Iterable<User> result = userService.listAllUsers();

        assertNotNull(result);
        verify(userRepository).findAllByEnabled(1);
    }

    @Test
    void testShowUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.showUser(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void testShowUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        User result = userService.showUser(999L);

        assertNull(result);
        verify(userRepository).findById(999L);
    }

    @Test
    void testSaveUser() {
        UserDetails mockUserDetails = mock(UserDetails.class);
        UsernamePasswordAuthenticationToken mockAuth = mock(UsernamePasswordAuthenticationToken.class);

        when(roleRepository.findByName("ROLE_USER")).thenReturn(testRole);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(springDataUserDetailsService.loadUserByUsername("testuser")).thenReturn(mockUserDetails);
        when(mockUserDetails.getAuthorities()).thenReturn(null);

        userService.saveUser(testUser);

        verify(roleRepository).findByName("ROLE_USER");
        verify(passwordEncoder).encode("password123");
        verify(userRepository, atLeastOnce()).save(testUser);
        verify(springDataUserDetailsService).loadUserByUsername("testuser");
    }

    @Test
    void testSaveFirstUserAsAdmin() {
        testUser.setId(1L);
        Role adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName("ROLE_ADMIN");

        UserDetails mockUserDetails = mock(UserDetails.class);

        when(roleRepository.findByName("ROLE_USER")).thenReturn(testRole);
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(adminRole);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(springDataUserDetailsService.loadUserByUsername("testuser")).thenReturn(mockUserDetails);
        when(mockUserDetails.getAuthorities()).thenReturn(null);

        userService.saveUser(testUser);

        verify(roleRepository).findByName("ROLE_ADMIN");
        verify(userRepository, atLeast(2)).save(testUser);
    }

    @Test
    void testEditUser() {
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(testRole);
        when(roleRepository.findById(1)).thenReturn(Optional.of(testRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.editUser(testUser);

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(testUser);
        assertEquals(testRole, testUser.getRole());
        assertEquals(1, testUser.getEnabled());
    }

    @Test
    void testEditUserWithNullRole() {
        testUser.setRole(null);

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(testRole);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.editUser(testUser);

        verify(roleRepository, atLeastOnce()).findByName("ROLE_USER");
        verify(userRepository).save(testUser);
        assertEquals(testRole, testUser.getRole());
    }

    @Test
    void testDeleteUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.deleteUser(testUser);

        assertEquals(0, testUser.getEnabled());
        assertNull(testUser.getPassword());
        verify(userRepository).save(testUser);
    }

    @Test
    void testSetUserRepository() {
        UserRepository mockRepo = mock(UserRepository.class);
        userService.setUserRepository(mockRepo);
        // Verify setter works without exception
        assertNotNull(userService);
    }

    @Test
    void testSetRoleRepository() {
        RoleRepository mockRepo = mock(RoleRepository.class);
        userService.setRoleRepository(mockRepo);
        assertNotNull(userService);
    }

    @Test
    void testSetPasswordEncoder() {
        BCryptPasswordEncoder mockEncoder = mock(BCryptPasswordEncoder.class);
        userService.setPasswordEncoder(mockEncoder);
        assertNotNull(userService);
    }

    @Test
    void testSetAuthenticationManager() {
        AuthenticationManager mockManager = mock(AuthenticationManager.class);
        userService.setAuthenticationManager(mockManager);
        assertNotNull(userService);
    }

    @Test
    void testSetSpringDataUserDetailsService() {
        SpringDataUserDetailsService mockService = mock(SpringDataUserDetailsService.class);
        userService.setSpringDataUserDetailsService(mockService);
        assertNotNull(userService);
    }
}
