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
    void constructor_shouldInitializeUserService() {
        // Assert
        assertNotNull(userController);
    }

    @Test
    void showAllUsers_shouldReturnListView() {
        // Arrange
        when(currentUserDetails.getUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.listAllUsers()).thenReturn(testUsers);

        // Act
        String viewName = userController.showAllUsers(model, currentUserDetails);

        // Assert
        assertEquals("user/list", viewName);
        verify(userService).findByUsername("testuser");
        verify(userService).listAllUsers();
        verify(model).addAttribute("currentUser", testUser);
        verify(model).addAttribute("users", testUsers);
    }

    @Test
    void showFormEditUser_shouldReturnEditView() {
        // Arrange
        Long userId = 1L;
        when(userService.showUser(userId)).thenReturn(testUser);

        // Act
        String viewName = userController.showFormEditUser(model, userId);

        // Assert
        assertEquals("user/edit", viewName);
        verify(userService).showUser(userId);
        verify(model).addAttribute("user", testUser);
    }

    @Test
    void processRequestEditUser_withValidUser_shouldRedirectToList() {
        // Arrange
        Long userId = 1L;
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = userController.processRequestEditUser(userId, testUser, bindingResult);

        // Assert
        assertEquals("redirect:/user/list", viewName);
        verify(userService).editUser(testUser);
    }

    @Test
    void processRequestEditUser_withInvalidUser_shouldRedirectToEditForm() {
        // Arrange
        Long userId = 1L;
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = userController.processRequestEditUser(userId, testUser, bindingResult);

        // Assert
        assertEquals("redirect:/user/edit/" + userId, viewName);
        verify(userService, never()).editUser(any());
    }

    @Test
    void deleteUser_shouldRedirectToList() {
        // Arrange
        Long userId = 1L;
        when(userService.showUser(userId)).thenReturn(testUser);

        // Act
        String viewName = userController.deleteUser(userId);

        // Assert
        assertEquals("redirect:/user/list", viewName);
        verify(userService).showUser(userId);
        verify(userService).deleteUser(testUser);
    }

    @Test
    void deleteUser_shouldCallShowUserBeforeDelete() {
        // Arrange
        Long userId = 1L;
        when(userService.showUser(userId)).thenReturn(testUser);

        // Act
        userController.deleteUser(userId);

        // Assert
        verify(userService).showUser(userId);
        verify(userService).deleteUser(testUser);
    }

    @Test
    void userController_shouldHaveControllerAnnotation() {
        // Assert
        assertTrue(UserController.class.isAnnotationPresent(org.springframework.stereotype.Controller.class));
    }

    @Test
    void userController_shouldHaveRequestMappingAnnotation() {
        // Assert
        assertTrue(UserController.class.isAnnotationPresent(org.springframework.web.bind.annotation.RequestMapping.class));
    }
}
