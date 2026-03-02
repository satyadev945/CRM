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

    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        testUsers = Arrays.asList(user1, user2);
    }

    @Test
    void constructor_withUserService_shouldCreateInstance() {
        Export exportController = new Export(userService);
        assertNotNull(exportController);
    }

    @Test
    void download_shouldReturnEmptyString() {
        when(userService.listAllUsers()).thenReturn(testUsers);

        String viewName = export.download(model);

        assertEquals("", viewName);
    }

    @Test
    void download_shouldAddUsersToModel() {
        when(userService.listAllUsers()).thenReturn(testUsers);

        export.download(model);

        verify(model).addAttribute("users", testUsers);
        verify(userService).listAllUsers();
    }

    @Test
    void download_withEmptyUserList_shouldHandleGracefully() {
        when(userService.listAllUsers()).thenReturn(Arrays.asList());

        assertDoesNotThrow(() -> export.download(model));
        verify(model).addAttribute(eq("users"), anyList());
    }

    @Test
    void export_shouldNotBeNull() {
        assertNotNull(export);
    }
}
