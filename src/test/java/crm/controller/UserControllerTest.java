package crm.controller;

import crm.entity.User;
import crm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private UserDetails currentUser;

    @InjectMocks
    private UserController userController;

    private User user;
    private List<User> users;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("testuser2");

        users = Arrays.asList(user, user2);

        when(currentUser.getUsername()).thenReturn("testuser");
    }

    @Test
    void showAllUsersShouldAddCurrentUserAndAllUsersToModelAndReturnListView() {
        // Arrange
        when(userService.findByUsername("testuser")).thenReturn(user);
        when(userService.listAllUsers()).thenReturn(users);

        // Act
        String viewName = userController.showAllUsers(model, currentUser);

        // Assert
        verify(userService).findByUsername("testuser");
        verify(userService).listAllUsers();
        verify(model).addAttribute("currentUser", user);
        verify(model).addAttribute("users", users);
        assertEquals("user/list", viewName);
    }

    @Test
    void showFormEditUserShouldAddUserToModelAndReturnEditView() {
        // Arrange
        Long id = 1L;
        when(userService.showUser(id)).thenReturn(user);

        // Act
        String viewName = userController.showFormEditUser(model, id);

        // Assert
        verify(userService).showUser(id);
        verify(model).addAttribute("user", user);
        assertEquals("user/edit", viewName);
    }

    @Test
    void processRequestEditUserShouldRedirectIfErrors() {
        // Arrange
        Long id = 1L;
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = userController.processRequestEditUser(id, user, bindingResult);

        // Assert
        verify(bindingResult).hasErrors();
        assertEquals("redirect:/user/edit/" + id, viewName);
        verify(userService, never()).editUser(any(User.class));
    }

    @Test
    void processRequestEditUserShouldEditUserAndRedirectToList() {
        // Arrange
        Long id = 1L;
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = userController.processRequestEditUser(id, user, bindingResult);

        // Assert
        verify(bindingResult).hasErrors();
        verify(userService).editUser(user);
        assertEquals("redirect:/user/list", viewName);
    }

    @Test
    void deleteUserShouldDeleteUserAndRedirectToList() {
        // Arrange
        Long id = 1L;
        when(userService.showUser(id)).thenReturn(user);

        // Act
        String viewName = userController.deleteUser(id);

        // Assert
        verify(userService).showUser(id);
        verify(userService).deleteUser(user);
        assertEquals("redirect:/user/list", viewName);
    }
}