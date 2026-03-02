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
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
    }

    @Test
    void constructor_withUserService_shouldCreateInstance() {
        RegisterController controller = new RegisterController(userService);
        assertNotNull(controller);
    }

    @Test
    void showRegistrationPage_shouldReturnRegisterView() {
        String viewName = registerController.showRegistrationPage(model, testUser);

        assertEquals("register", viewName);
        verify(model).addAttribute("user", testUser);
    }

    @Test
    void processRegistrationForm_withNewUser_shouldReturnSuccessView() {
        when(userService.findByUsername(testUser.getUsername())).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = registerController.processRegistrationForm(model, testUser, bindingResult);

        assertEquals("success", viewName);
        verify(userService).saveUser(testUser);
    }

    @Test
    void processRegistrationForm_withExistingUser_shouldReturnRegisterView() {
        User existingUser = new User();
        existingUser.setUsername(testUser.getUsername());
        when(userService.findByUsername(testUser.getUsername())).thenReturn(existingUser);

        String viewName = registerController.processRegistrationForm(model, testUser, bindingResult);

        assertEquals("register", viewName);
        verify(model).addAttribute(eq("alreadyRegisteredMessage"), anyString());
        verify(bindingResult).reject("email");
        verify(userService, never()).saveUser(any());
    }

    @Test
    void processRegistrationForm_withValidationErrors_shouldRedirectToRegister() {
        when(userService.findByUsername(testUser.getUsername())).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = registerController.processRegistrationForm(model, testUser, bindingResult);

        assertEquals("redirect:/register", viewName);
        verify(userService, never()).saveUser(any());
    }

    @Test
    void processRegistrationForm_withExistingUser_shouldNotSaveUser() {
        User existingUser = new User();
        existingUser.setUsername(testUser.getUsername());
        when(userService.findByUsername(testUser.getUsername())).thenReturn(existingUser);

        registerController.processRegistrationForm(model, testUser, bindingResult);

        verify(userService, never()).saveUser(testUser);
    }

    @Test
    void showRegistrationPage_withNullUser_shouldHandleGracefully() {
        assertDoesNotThrow(() -> registerController.showRegistrationPage(model, null));
        verify(model).addAttribute("user", null);
    }

    @Test
    void registerController_shouldNotBeNull() {
        assertNotNull(registerController);
    }

    @Test
    void processRegistrationForm_withNullUsername_shouldCheckDatabase() {
        testUser.setUsername(null);
        when(userService.findByUsername(null)).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(false);

        registerController.processRegistrationForm(model, testUser, bindingResult);

        verify(userService).findByUsername(null);
    }

    @Test
    void showRegistrationPage_multipleInvocations_shouldAddAttributeEachTime() {
        registerController.showRegistrationPage(model, testUser);
        registerController.showRegistrationPage(model, testUser);

        verify(model, times(2)).addAttribute("user", testUser);
    }
}
