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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private Export export;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("johndoe")
                .email("john@test.com")
                .firstName("John")
                .lastName("Doe")
                .enabled(1)
                .build();
    }

    @Test
    void testDownload_ReturnsEmptyString() {
        List<User> users = Arrays.asList(user);
        when(userService.listAllUsers()).thenReturn(users);

        String view = export.download(model);

        assertEquals("", view);
    }

    @Test
    void testDownload_AddsUsersToModel() {
        List<User> users = Arrays.asList(user);
        when(userService.listAllUsers()).thenReturn(users);

        export.download(model);

        verify(model).addAttribute("users", users);
    }

    @Test
    void testDownload_CallsListAllUsers() {
        when(userService.listAllUsers()).thenReturn(Arrays.asList(user));
        export.download(model);
        verify(userService).listAllUsers();
    }

    @Test
    void testConstructor_WithUserService() {
        Export exportController = new Export(userService);
        assertNotNull(exportController);
    }
}
