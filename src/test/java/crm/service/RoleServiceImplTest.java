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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1);
        testRole.setName("ROLE_USER");
    }

    @Test
    void listAllRoles_shouldReturnAllRoles() {
        when(roleRepository.findAll()).thenReturn(Arrays.asList(testRole));

        Iterable<Role> result = roleService.listAllRoles();

        assertNotNull(result);
        verify(roleRepository).findAll();
    }

    @Test
    void listAllRoles_withEmptyList_shouldReturnEmptyIterable() {
        when(roleRepository.findAll()).thenReturn(Arrays.asList());

        Iterable<Role> result = roleService.listAllRoles();

        assertNotNull(result);
        verify(roleRepository).findAll();
    }
}
