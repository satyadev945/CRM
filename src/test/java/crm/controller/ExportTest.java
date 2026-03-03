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
    void download_shouldAddUsersToModel() {
        when(userService.listAllUsers()).thenReturn(testUsers);

        String viewName = export.download(model);

        assertEquals("", viewName);
        verify(model).addAttribute("users", testUsers);
        verify(userService).listAllUsers();
    }

    @Test
    void download_shouldHandleEmptyUserList() {
        when(userService.listAllUsers()).thenReturn(Arrays.asList());

        String viewName = export.download(model);

        assertEquals("", viewName);
        verify(model).addAttribute(eq("users"), anyIterable());
    }

    @Test
    void constructor_shouldInitializeUserService() {
        assertNotNull(export);
    }
}
