package crm.controller;

import crm.entity.User;
import crm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private RegisterController registerController;

    private User testUser;

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
    }

    @Test
    void constructor_shouldInitializeUserService() {
        // Assert
        assertNotNull(registerController);
    }

    @Test
    void showRegistrationPage_shouldReturnRegisterView() {
        // Act
        String viewName = registerController.showRegistrationPage(model, testUser);

        // Assert
        assertEquals("register", viewName);
        verify(model).addAttribute("user", testUser);
    }

    @Test
    void processRegistrationForm_withNewUser_shouldReturnSuccessView() {
        // Arrange
        when(userService.findByUsername(testUser.getUsername())).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = registerController.processRegistrationForm(model, testUser, bindingResult);

        // Assert
        assertEquals("success", viewName);
        verify(userService).findByUsername(testUser.getUsername());
        verify(userService).saveUser(testUser);
    }

    @Test
    void processRegistrationForm_withExistingUser_shouldReturnRegisterView() {
        // Arrange
        User existingUser = User.builder()
                .id(2L)
                .username("testuser")
                .email("existing@example.com")
                .build();
        when(userService.findByUsername(testUser.getUsername())).thenReturn(existingUser);

        // Act
        String viewName = registerController.processRegistrationForm(model, testUser, bindingResult);

        // Assert
        assertEquals("register", viewName);
        verify(userService).findByUsername(testUser.getUsername());
        verify(userService, never()).saveUser(any());
        verify(model).addAttribute(eq("alreadyRegisteredMessage"), anyString());
        verify(bindingResult).reject("email");
    }

    @Test
    void processRegistrationForm_withValidationErrors_shouldRedirectToRegister() {
        // Arrange
        when(userService.findByUsername(testUser.getUsername())).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = registerController.processRegistrationForm(model, testUser, bindingResult);

        // Assert
        assertEquals("redirect:/register", viewName);
        verify(userService).findByUsername(testUser.getUsername());
        verify(userService, never()).saveUser(any());
    }

    @Test
    void processRegistrationForm_shouldCheckForExistingUserFirst() {
        // Arrange
        when(userService.findByUsername(testUser.getUsername())).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        registerController.processRegistrationForm(model, testUser, bindingResult);

        // Assert
        verify(userService).findByUsername(testUser.getUsername());
    }

    @Test
    void registerController_shouldHaveControllerAnnotation() {
        // Assert
        assertTrue(RegisterController.class.isAnnotationPresent(org.springframework.stereotype.Controller.class));
    }
}
