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

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");

        user = User.builder()
                .id(1L)
                .username("johndoe")
                .email("john@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .enabled(1)
                .role(role)
                .build();
    }

    @Test
    void testShowAllUsers_ReturnsListView() {
        List<User> users = Arrays.asList(user);
        when(currentUserDetails.getUsername()).thenReturn("johndoe");
        when(userService.findByUsername("johndoe")).thenReturn(user);
        when(userService.listAllUsers()).thenReturn(users);

        String view = userController.showAllUsers(model, currentUserDetails);

        assertEquals("user/list", view);
        verify(model).addAttribute("currentUser", user);
        verify(model).addAttribute("users", users);
    }

    @Test
    void testShowFormEditUser_ReturnsEditView() {
        when(userService.showUser(1L)).thenReturn(user);
        String view = userController.showFormEditUser(model, 1L);
        assertEquals("user/edit", view);
        verify(model).addAttribute("user", user);
    }

    @Test
    void testProcessRequestEditUser_NoErrors_RedirectsToList() {
        when(bindingResult.hasErrors()).thenReturn(false);
        String view = userController.processRequestEditUser(1L, user, bindingResult);
        assertEquals("redirect:/user/list", view);
        verify(userService).editUser(user);
    }

    @Test
    void testProcessRequestEditUser_WithErrors_RedirectsToEdit() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = userController.processRequestEditUser(1L, user, bindingResult);
        assertEquals("redirect:/user/edit/1", view);
        verify(userService, never()).editUser(any());
    }

    @Test
    void testDeleteUser_RedirectsToList() {
        when(userService.showUser(1L)).thenReturn(user);
        String view = userController.deleteUser(1L);
        assertEquals("redirect:/user/list", view);
        verify(userService).deleteUser(user);
    }

    @Test
    void testDeleteUser_CallsShowUserFirst() {
        when(userService.showUser(2L)).thenReturn(user);
        userController.deleteUser(2L);
        verify(userService).showUser(2L);
        verify(userService).deleteUser(user);
    }

    @Test
    void testConstructor_WithUserService() {
        UserController controller = new UserController(userService);
        assertNotNull(controller);
    }

    @Test
    void testShowAllUsers_AddsCurrentUserToModel() {
        when(currentUserDetails.getUsername()).thenReturn("johndoe");
        when(userService.findByUsername("johndoe")).thenReturn(user);
        when(userService.listAllUsers()).thenReturn(Arrays.asList(user));

        userController.showAllUsers(model, currentUserDetails);

        verify(model).addAttribute("currentUser", user);
    }

    @Test
    void testProcessRequestEditUser_WithErrors_DoesNotCallEditUser() {
        when(bindingResult.hasErrors()).thenReturn(true);
        userController.processRequestEditUser(5L, user, bindingResult);
        verify(userService, never()).editUser(any(User.class));
    }

    @Test
    void testProcessRequestEditUser_WithErrors_ReturnsCorrectRedirect() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = userController.processRequestEditUser(5L, user, bindingResult);
        assertEquals("redirect:/user/edit/5", view);
    }
}
