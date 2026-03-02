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

    private List<Role> testRoles;

    @BeforeEach
    void setUp() {
        Role role1 = new Role(1L, "ADMIN");
        Role role2 = new Role(2L, "USER");
        testRoles = Arrays.asList(role1, role2);
    }

    @Test
    void constructor_withRoleRepository_shouldCreateInstance() {
        RoleServiceImpl service = new RoleServiceImpl(roleRepository);
        assertNotNull(service);
    }

    @Test
    void listAllRoles_shouldReturnAllRoles() {
        when(roleRepository.findAll()).thenReturn(testRoles);

        Iterable<Role> roles = roleService.listAllRoles();

        assertNotNull(roles);
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void listAllRoles_withEmptyList_shouldReturnEmptyList() {
        when(roleRepository.findAll()).thenReturn(Arrays.asList());

        Iterable<Role> roles = roleService.listAllRoles();

        assertNotNull(roles);
        assertFalse(roles.iterator().hasNext());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void roleService_shouldImplementRoleService() {
        assertTrue(roleService instanceof RoleService);
    }
}
