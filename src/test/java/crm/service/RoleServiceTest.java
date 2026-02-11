package crm.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for RoleService interface
 */
class RoleServiceTest {

    @Test
    void testRoleServiceInterface() {
        // Verify interface exists
        assertTrue(RoleService.class.isInterface());

        try {
            RoleService.class.getMethod("listAllRoles");
        } catch (NoSuchMethodException e) {
            fail("RoleService interface missing expected methods");
        }
    }
}
