package crm.controller;

import crm.entity.Role;
import crm.entity.User;
import crm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private UserDetails currentUserDetails;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        Role testRole = new Role();
        testRole.setId(1);
        testRole.setName("ROLE_USER");

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .enabled(1)
                .role(testRole)
                .build();
    }

    @Test
    void testConstructor() {
        UserService mockService = mock(UserService.class);
        UserController controller = new UserController(mockService);
        assertNotNull(controller);
    }

    @Test
    void testShowAllUsers() {
        when(currentUserDetails.getUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.listAllUsers()).thenReturn(Arrays.asList(testUser));

        String viewName = userController.showAllUsers(model, currentUserDetails);

        assertEquals("user/list", viewName);
        verify(model).addAttribute("currentUser", testUser);
        verify(model).addAttribute(eq("users"), any());
        verify(userService).findByUsername("testuser");
        verify(userService).listAllUsers();
    }

    @Test
    void testShowFormEditUser() {
        when(userService.showUser(1L)).thenReturn(testUser);

        String viewName = userController.showFormEditUser(model, 1L);

        assertEquals("user/edit", viewName);
        verify(model).addAttribute("user", testUser);
        verify(userService).showUser(1L);
    }

    @Test
    void testProcessRequestEditUserSuccess() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = userController.processRequestEditUser(1L, testUser, bindingResult);

        assertEquals("redirect:/user/list", viewName);
        verify(userService).editUser(testUser);
    }

    @Test
    void testProcessRequestEditUserWithErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = userController.processRequestEditUser(1L, testUser, bindingResult);

        assertEquals("redirect:/user/edit/1", viewName);
        verify(userService, never()).editUser(any());
    }

    @Test
    void testDeleteUser() {
        when(userService.showUser(1L)).thenReturn(testUser);

        String viewName = userController.deleteUser(1L);

        assertEquals("redirect:/user/list", viewName);
        verify(userService).showUser(1L);
        verify(userService).deleteUser(testUser);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userService.showUser(999L)).thenReturn(null);

        String viewName = userController.deleteUser(999L);

        assertEquals("redirect:/user/list", viewName);
        verify(userService).deleteUser(null);
    }
}
