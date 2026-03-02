package crm.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleServiceTest {

    @Test
    void roleService_interfaceExists() {
        assertNotNull(RoleService.class);
    }

    @Test
    void roleService_isInterface() {
        assertTrue(RoleService.class.isInterface());
    }

    @Test
    void roleService_hasListAllRolesMethod() throws NoSuchMethodException {
        assertNotNull(RoleService.class.getMethod("listAllRoles"));
    }
}
