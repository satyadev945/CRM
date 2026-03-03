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
    private UserDetails currentUserDetails;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .enabled(1)
                .build();

        testUsers = Arrays.asList(testUser);
    }

    @Test
    void showAllUsers_shouldReturnUserListView() {
        when(currentUserDetails.getUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.listAllUsers()).thenReturn(testUsers);

        String viewName = userController.showAllUsers(model, currentUserDetails);

        assertEquals("user/list", viewName);
        verify(model).addAttribute("currentUser", testUser);
        verify(model).addAttribute("users", testUsers);
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
    void deleteUser_withNullUser_shouldHandleGracefully() {
        Long userId = 999L;
        when(userService.showUser(userId)).thenReturn(null);

        String viewName = userController.deleteUser(userId);

        assertEquals("redirect:/user/list", viewName);
        verify(userService).deleteUser(null);
    }

    @Test
    void constructor_shouldInitializeUserService() {
        assertNotNull(userController);
    }
}
