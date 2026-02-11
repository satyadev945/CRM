package crm.service;

import crm.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for UserService interface
 */
class UserServiceTest {

    @Test
    void testUserServiceInterface() {
        // Verify interface exists and has expected methods
        assertTrue(UserService.class.isInterface());

        try {
            UserService.class.getMethod("findByUsername", String.class);
            UserService.class.getMethod("listAllUsers");
            UserService.class.getMethod("showUser", Long.class);
            UserService.class.getMethod("saveUser", User.class);
            UserService.class.getMethod("editUser", User.class);
            UserService.class.getMethod("deleteUser", User.class);
        } catch (NoSuchMethodException e) {
            fail("UserService interface missing expected methods");
        }
    }
}
