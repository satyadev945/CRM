package crm.service;

import crm.entity.Role;
import crm.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    private RoleServiceImpl roleService;

    @BeforeEach
    void setUp() {
        roleService = new RoleServiceImpl(roleRepository);
    }

    @Test
    void listAllRoles_ReturnsAllRoles() {
        // Arrange
        List<Role> roles = new ArrayList<>();

        Role userRole = new Role();
        userRole.setId(1);
        userRole.setName("ROLE_USER");

        Role adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName("ROLE_ADMIN");

        Role managerRole = new Role();
        managerRole.setId(3);
        managerRole.setName("ROLE_MANAGER");

        roles.add(userRole);
        roles.add(adminRole);
        roles.add(managerRole);

        when(roleRepository.findAll()).thenReturn(roles);

        // Act
        Iterable<Role> result = roleService.listAllRoles();

        // Assert
        assertEquals(roles, result);
        verify(roleRepository, times(1)).findAll();
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void listAllRoles_NoRoles_ReturnsEmptyList() {
        // Arrange
        List<Role> emptyRoles = new ArrayList<>();
        when(roleRepository.findAll()).thenReturn(emptyRoles);

        // Act
        Iterable<Role> result = roleService.listAllRoles();

        // Assert
        assertEquals(emptyRoles, result);
        verify(roleRepository, times(1)).findAll();
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void constructor_InitializesRepository() {
        // Arrange & Act
        RoleServiceImpl serviceWithMockedRepo = new RoleServiceImpl(roleRepository);

        // Use reflection to check if roleRepository is set
        try {
            java.lang.reflect.Field field = RoleServiceImpl.class.getDeclaredField("roleRepository");
            field.setAccessible(true);
            RoleRepository injectedRepository = (RoleRepository) field.get(serviceWithMockedRepo);

            // Assert
            assertEquals(roleRepository, injectedRepository);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // If reflection fails, this assertion will fail the test
            assertEquals("No exception expected", e.getMessage());
        }
    }
}