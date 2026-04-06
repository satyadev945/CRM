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
                .enabled(1)
                .build();

        testUsers = Arrays.asList(testUser);
    }

    @Test
    void constructor_shouldInitializeUserService() {
        // Assert
        assertNotNull(export);
    }

    @Test
    void download_shouldReturnEmptyString() {
        // Arrange
        when(userService.listAllUsers()).thenReturn(testUsers);

        // Act
        String viewName = export.download(model);

        // Assert
        assertEquals("", viewName);
        verify(userService).listAllUsers();
        verify(model).addAttribute("users", testUsers);
    }

    @Test
    void download_shouldAddUsersToModel() {
        // Arrange
        when(userService.listAllUsers()).thenReturn(testUsers);

        // Act
        export.download(model);

        // Assert
        verify(model).addAttribute("users", testUsers);
    }

    @Test
    void download_shouldHandleEmptyUserList() {
        // Arrange
        when(userService.listAllUsers()).thenReturn(Arrays.asList());

        // Act
        String viewName = export.download(model);

        // Assert
        assertEquals("", viewName);
        verify(userService).listAllUsers();
    }

    @Test
    void export_shouldHaveControllerAnnotation() {
        // Assert
        assertTrue(Export.class.isAnnotationPresent(org.springframework.stereotype.Controller.class));
    }
}
