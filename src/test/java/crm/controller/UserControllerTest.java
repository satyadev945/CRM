package crm.controller;

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
    private UserDetails userDetails;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");

        testUsers = Arrays.asList(testUser);
    }

    @Test
    void constructor_withUserService_shouldCreateInstance() {
        UserController controller = new UserController(userService);
        assertNotNull(controller);
    }

    @Test
    void showAllUsers_shouldReturnListView() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.listAllUsers()).thenReturn(testUsers);

        String viewName = userController.showAllUsers(model, userDetails);

        assertEquals("user/list", viewName);
        verify(model).addAttribute("currentUser", testUser);
        verify(model).addAttribute("users", testUsers);
        verify(userService).findByUsername("testuser");
        verify(userService).listAllUsers();
    }

    @Test
    void showFormEditUser_shouldReturnEditView() {
        Long userId = 1L;
        when(userService.showUser(userId)).thenReturn(testUser);

        String viewName = userController.showFormEditUser(model, userId);

        assertEquals("user/edit", viewName);
        verify(model).addAttribute("user", testUser);
        verify(userService).showUser(userId);
    }

    @Test
    void processRequestEditUser_withValidUser_shouldRedirectToList() {
        Long userId = 1L;
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = userController.processRequestEditUser(userId, testUser, bindingResult);

        assertEquals("redirect:/user/list", viewName);
        verify(userService).editUser(testUser);
    }

    @Test
    void processRequestEditUser_withErrors_shouldRedirectToEditForm() {
        Long userId = 1L;
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = userController.processRequestEditUser(userId, testUser, bindingResult);

        assertEquals("redirect:/user/edit/" + userId, viewName);
        verify(userService, never()).editUser(any());
    }

    @Test
    void deleteUser_shouldRedirectToList() {
        Long userId = 1L;
        when(userService.showUser(userId)).thenReturn(testUser);

        String viewName = userController.deleteUser(userId);

        assertEquals("redirect:/user/list", viewName);
        verify(userService).showUser(userId);
        verify(userService).deleteUser(testUser);
    }

    @Test
    void deleteUser_withNonExistentUser_shouldCallService() {
        Long userId = 999L;
        when(userService.showUser(userId)).thenReturn(null);

        userController.deleteUser(userId);

        verify(userService).showUser(userId);
        verify(userService).deleteUser(null);
    }

    @Test
    void showAllUsers_withNullCurrentUser_shouldHandleGracefully() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(null);
        when(userService.listAllUsers()).thenReturn(testUsers);

        assertDoesNotThrow(() -> userController.showAllUsers(model, userDetails));
        verify(model).addAttribute("currentUser", null);
    }

    @Test
    void userController_shouldNotBeNull() {
        assertNotNull(userController);
    }

    @Test
    void showFormEditUser_withDifferentIds_shouldCallServiceWithCorrectId() {
        Long userId1 = 1L;
        Long userId2 = 2L;

        when(userService.showUser(userId1)).thenReturn(testUser);
        when(userService.showUser(userId2)).thenReturn(testUser);

        userController.showFormEditUser(model, userId1);
        verify(userService, times(1)).showUser(userId1);

        userController.showFormEditUser(model, userId2);
        verify(userService, times(1)).showUser(userId2);
    }

    @Test
    void processRequestEditUser_multipleInvocations_shouldCallServiceEachTime() {
        Long userId = 1L;
        when(bindingResult.hasErrors()).thenReturn(false);

        userController.processRequestEditUser(userId, testUser, bindingResult);
        userController.processRequestEditUser(userId, testUser, bindingResult);

        verify(userService, times(2)).editUser(testUser);
    }
}
