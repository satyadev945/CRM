package crm.controller;

import crm.entity.User;
import crm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RegisterControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private RegisterController registerController;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
    }

    @Test
    void showRegistrationPageShouldAddUserToModelAndReturnRegisterView() {
        // Act
        String viewName = registerController.showRegistrationPage(model, user);

        // Assert
        verify(model).addAttribute("user", user);
        assertEquals("register", viewName);
    }

    @Test
    void processRegistrationFormShouldReturnRegisterViewWhenUserAlreadyExists() {
        // Arrange
        when(userService.findByUsername(user.getUsername())).thenReturn(user);

        // Act
        String viewName = registerController.processRegistrationForm(model, user, bindingResult);

        // Assert
        verify(userService).findByUsername(user.getUsername());
        verify(model).addAttribute(eq("alreadyRegisteredMessage"), anyString());
        verify(bindingResult).reject("email");
        assertEquals("register", viewName);
        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    void processRegistrationFormShouldRedirectWhenBindingResultHasErrors() {
        // Arrange
        when(userService.findByUsername(user.getUsername())).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = registerController.processRegistrationForm(model, user, bindingResult);

        // Assert
        verify(userService).findByUsername(user.getUsername());
        verify(bindingResult).hasErrors();
        assertEquals("redirect:/register", viewName);
        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    void processRegistrationFormShouldSaveUserAndReturnSuccessView() {
        // Arrange
        when(userService.findByUsername(user.getUsername())).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = registerController.processRegistrationForm(model, user, bindingResult);

        // Assert
        verify(userService).findByUsername(user.getUsername());
        verify(bindingResult).hasErrors();
        verify(userService).saveUser(user);
        assertEquals("success", viewName);
    }
}