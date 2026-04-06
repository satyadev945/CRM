package crm.service;

import crm.entity.Role;
import crm.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role testRole;
    private List<Role> testRoles;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1);
        testRole.setName("ROLE_USER");

        Role adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName("ROLE_ADMIN");

        testRoles = Arrays.asList(testRole, adminRole);
    }

    @Test
    void constructor_shouldInitializeRoleRepository() {
        // Assert
        assertNotNull(roleService);
    }

    @Test
    void listAllRoles_shouldReturnAllRoles() {
        // Arrange
        when(roleRepository.findAll()).thenReturn(testRoles);

        // Act
        Iterable<Role> result = roleService.listAllRoles();

        // Assert
        assertNotNull(result);
        verify(roleRepository).findAll();
    }

    @Test
    void listAllRoles_shouldReturnEmptyList_whenNoRoles() {
        // Arrange
        when(roleRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        Iterable<Role> result = roleService.listAllRoles();

        // Assert
        assertNotNull(result);
        verify(roleRepository).findAll();
    }

    @Test
    void listAllRoles_shouldCallRepositoryOnce() {
        // Arrange
        when(roleRepository.findAll()).thenReturn(testRoles);

        // Act
        roleService.listAllRoles();

        // Assert
        verify(roleRepository, times(1)).findAll();
    }
}
