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
import static org.mockito.ArgumentMatchers.any;
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
                .username("newuser")
                .email("newuser@example.com")
                .password("password")
                .build();
    }

    @Test
    void testShowRegistrationPage() {
        String viewName = registerController.showRegistrationPage(model, testUser);

        assertEquals("register", viewName);
        verify(model).addAttribute("user", testUser);
    }

    @Test
    void testProcessRegistrationFormSuccess() {
        when(userService.findByUsername("newuser")).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = registerController.processRegistrationForm(model, testUser, bindingResult);

        assertEquals("success", viewName);
        verify(userService).saveUser(testUser);
    }

    @Test
    void testProcessRegistrationFormUserAlreadyExists() {
        when(userService.findByUsername("newuser")).thenReturn(testUser);

        String viewName = registerController.processRegistrationForm(model, testUser, bindingResult);

        assertEquals("register", viewName);
        verify(model).addAttribute(eq("alreadyRegisteredMessage"), any());
        verify(bindingResult).reject("email");
        verify(userService, never()).saveUser(any());
    }

    @Test
    void testProcessRegistrationFormWithErrors() {
        when(userService.findByUsername("newuser")).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = registerController.processRegistrationForm(model, testUser, bindingResult);

        assertEquals("redirect:/register", viewName);
        verify(userService, never()).saveUser(any());
    }
}
