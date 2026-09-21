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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");
        user.setEnabled(1);
        user.setRole(role);
    }

    @Test
    void testConstructor() {
        UserController controller = new UserController(userService);
        assertNotNull(controller);
    }

    @Test
    void testShowAllUsers() {
        List<User> users = Arrays.asList(user);
        when(currentUserDetails.getUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(user);
        when(userService.listAllUsers()).thenReturn(users);

        String view = userController.showAllUsers(model, currentUserDetails);
        assertEquals("user/list", view);
        verify(model).addAttribute(eq("currentUser"), eq(user));
        verify(model).addAttribute(eq("users"), any());
    }

    @Test
    void testShowFormEditUser() {
        when(userService.showUser(1L)).thenReturn(user);
        String view = userController.showFormEditUser(model, 1L);
        assertEquals("user/edit", view);
        verify(model).addAttribute(eq("user"), eq(user));
    }

    @Test
    void testProcessRequestEditUser_noErrors() {
        when(bindingResult.hasErrors()).thenReturn(false);
        String view = userController.processRequestEditUser(1L, user, bindingResult);
        assertEquals("redirect:/user/list", view);
        verify(userService).editUser(user);
    }

    @Test
    void testProcessRequestEditUser_withErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = userController.processRequestEditUser(1L, user, bindingResult);
        assertEquals("redirect:/user/edit/1", view);
        verify(userService, never()).editUser(any());
    }

    @Test
    void testDeleteUser() {
        when(userService.showUser(1L)).thenReturn(user);
        String view = userController.deleteUser(1L);
        assertEquals("redirect:/user/list", view);
        verify(userService).showUser(1L);
        verify(userService).deleteUser(user);
    }

    @Test
    void testDeleteUser_callsShowUserFirst() {
        when(userService.showUser(2L)).thenReturn(user);
        userController.deleteUser(2L);
        verify(userService).showUser(2L);
    }

    @Test
    void testShowAllUsers_addsCurrentUserToModel() {
        when(currentUserDetails.getUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(user);
        when(userService.listAllUsers()).thenReturn(Arrays.asList(user));

        userController.showAllUsers(model, currentUserDetails);
        verify(model).addAttribute("currentUser", user);
    }

    @Test
    void testShowAllUsers_addsUsersToModel() {
        List<User> users = Arrays.asList(user);
        when(currentUserDetails.getUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(user);
        when(userService.listAllUsers()).thenReturn(users);

        userController.showAllUsers(model, currentUserDetails);
        verify(model).addAttribute("users", users);
    }
}
