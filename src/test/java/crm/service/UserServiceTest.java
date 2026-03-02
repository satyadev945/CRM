package crm.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void userService_interfaceExists() {
        assertNotNull(UserService.class);
    }

    @Test
    void userService_isInterface() {
        assertTrue(UserService.class.isInterface());
    }

    @Test
    void userService_hasListAllUsersMethod() throws NoSuchMethodException {
        assertNotNull(UserService.class.getMethod("listAllUsers"));
    }

    @Test
    void userService_hasShowUserMethod() throws NoSuchMethodException {
        assertNotNull(UserService.class.getMethod("showUser", Long.class));
    }

    @Test
    void userService_hasSaveUserMethod() throws NoSuchMethodException {
        assertNotNull(UserService.class.getMethod("saveUser", crm.entity.User.class));
    }

    @Test
    void userService_hasFindByUsernameMethod() throws NoSuchMethodException {
        assertNotNull(UserService.class.getMethod("findByUsername", String.class));
    }

    @Test
    void userService_hasEditUserMethod() throws NoSuchMethodException {
        assertNotNull(UserService.class.getMethod("editUser", crm.entity.User.class));
    }

    @Test
    void userService_hasDeleteUserMethod() throws NoSuchMethodException {
        assertNotNull(UserService.class.getMethod("deleteUser", crm.entity.User.class));
    }
}
