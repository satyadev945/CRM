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
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");
        user.setEnabled(1);
        user.setRole(role);
    }

    @Test
    void testConstructor() {
        RegisterController controller = new RegisterController(userService);
        assertNotNull(controller);
    }

    @Test
    void testShowRegistrationPage() {
        String view = registerController.showRegistrationPage(model, user);
        assertEquals("register", view);
        verify(model).addAttribute(eq("user"), eq(user));
    }

    @Test
    void testProcessRegistrationForm_userAlreadyExists() {
        when(userService.findByUsername("testuser")).thenReturn(user);
        String view = registerController.processRegistrationForm(model, user, bindingResult);
        assertEquals("register", view);
        verify(model).addAttribute(eq("alreadyRegisteredMessage"), anyString());
        verify(bindingResult).reject("email");
        verify(userService, never()).saveUser(any());
    }

    @Test
    void testProcessRegistrationForm_newUser_noErrors() {
        when(userService.findByUsername("testuser")).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(false);
        String view = registerController.processRegistrationForm(model, user, bindingResult);
        assertEquals("success", view);
        verify(userService).saveUser(user);
    }

    @Test
    void testProcessRegistrationForm_newUser_withErrors() {
        when(userService.findByUsername("testuser")).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = registerController.processRegistrationForm(model, user, bindingResult);
        assertEquals("redirect:/register", view);
        verify(userService, never()).saveUser(any());
    }

    @Test
    void testProcessRegistrationForm_existingUser_doesNotSave() {
        when(userService.findByUsername("testuser")).thenReturn(user);
        registerController.processRegistrationForm(model, user, bindingResult);
        verify(userService, never()).saveUser(any());
    }

    @Test
    void testProcessRegistrationForm_existingUser_addsErrorMessage() {
        when(userService.findByUsername("testuser")).thenReturn(user);
        registerController.processRegistrationForm(model, user, bindingResult);
        verify(model).addAttribute(eq("alreadyRegisteredMessage"),
                eq("Oops!  There is already a user registered with the email provided."));
    }

    @Test
    void testShowRegistrationPage_returnsRegisterView() {
        User newUser = new User();
        String view = registerController.showRegistrationPage(model, newUser);
        assertEquals("register", view);
    }
}
