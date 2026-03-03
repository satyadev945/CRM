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
    void showRegistrationPage_shouldReturnRegisterView() {
        String viewName = registerController.showRegistrationPage(model, testUser);

        assertEquals("register", viewName);
        verify(model).addAttribute("user", testUser);
    }

    @Test
    void processRegistrationForm_withValidUser_shouldReturnSuccessView() {
        when(userService.findByUsername(testUser.getUsername())).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = registerController.processRegistrationForm(model, testUser, bindingResult);

        assertEquals("success", viewName);
        verify(userService).saveUser(testUser);
    }

    @Test
    void processRegistrationForm_withExistingUser_shouldReturnRegisterView() {
        User existingUser = User.builder().username("testuser").build();
        when(userService.findByUsername(testUser.getUsername())).thenReturn(existingUser);

        String viewName = registerController.processRegistrationForm(model, testUser, bindingResult);

        assertEquals("register", viewName);
        verify(model).addAttribute(eq("alreadyRegisteredMessage"), anyString());
        verify(bindingResult).reject("email");
        verify(userService, never()).saveUser(any());
    }

    @Test
    void processRegistrationForm_withErrors_shouldRedirectToRegister() {
        when(userService.findByUsername(testUser.getUsername())).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = registerController.processRegistrationForm(model, testUser, bindingResult);

        assertEquals("redirect:/register", viewName);
        verify(userService, never()).saveUser(any());
    }

    @Test
    void processRegistrationForm_withNullUsername_shouldHandleGracefully() {
        testUser.setUsername(null);
        when(userService.findByUsername(null)).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = registerController.processRegistrationForm(model, testUser, bindingResult);

        assertEquals("success", viewName);
    }

    @Test
    void constructor_shouldInitializeUserService() {
        assertNotNull(registerController);
    }
}
