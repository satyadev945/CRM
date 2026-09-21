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
import static org.mockito.ArgumentMatchers.*;
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

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("newuser")
                .email("new@test.com")
                .firstName("New")
                .lastName("User")
                .password("password")
                .enabled(1)
                .build();
    }

    @Test
    void testShowRegistrationPage_ReturnsRegisterView() {
        String view = registerController.showRegistrationPage(model, user);
        assertEquals("register", view);
        verify(model).addAttribute("user", user);
    }

    @Test
    void testProcessRegistrationForm_UserAlreadyExists_ReturnsRegisterView() {
        when(userService.findByUsername("newuser")).thenReturn(user);

        String view = registerController.processRegistrationForm(model, user, bindingResult);

        assertEquals("register", view);
        verify(model).addAttribute(eq("alreadyRegisteredMessage"), anyString());
        verify(bindingResult).reject("email");
        verify(userService, never()).saveUser(any());
    }

    @Test
    void testProcessRegistrationForm_NewUser_NoErrors_ReturnsSuccessView() {
        when(userService.findByUsername("newuser")).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = registerController.processRegistrationForm(model, user, bindingResult);

        assertEquals("success", view);
        verify(userService).saveUser(user);
    }

    @Test
    void testProcessRegistrationForm_NewUser_WithErrors_RedirectsToRegister() {
        when(userService.findByUsername("newuser")).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = registerController.processRegistrationForm(model, user, bindingResult);

        assertEquals("redirect:/register", view);
        verify(userService, never()).saveUser(any());
    }

    @Test
    void testProcessRegistrationForm_ExistingUser_DoesNotSave() {
        when(userService.findByUsername("newuser")).thenReturn(user);

        registerController.processRegistrationForm(model, user, bindingResult);

        verify(userService, never()).saveUser(any());
    }

    @Test
    void testProcessRegistrationForm_ExistingUser_AddsAlreadyRegisteredMessage() {
        when(userService.findByUsername("newuser")).thenReturn(user);

        registerController.processRegistrationForm(model, user, bindingResult);

        verify(model).addAttribute(eq("alreadyRegisteredMessage"),
                eq("Oops!  There is already a user registered with the email provided."));
    }

    @Test
    void testConstructor_WithUserService() {
        RegisterController controller = new RegisterController(userService);
        assertNotNull(controller);
    }

    @Test
    void testShowRegistrationPage_WithNewUser_AddsUserToModel() {
        User newUser = new User();
        registerController.showRegistrationPage(model, newUser);
        verify(model).addAttribute("user", newUser);
    }

    @Test
    void testProcessRegistrationForm_NewUser_NoErrors_CallsSaveUser() {
        when(userService.findByUsername("newuser")).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(false);

        registerController.processRegistrationForm(model, user, bindingResult);

        verify(userService, times(1)).saveUser(user);
    }
}
