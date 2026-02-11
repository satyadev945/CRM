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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private Export exportController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();
    }

    @Test
    void testConstructor() {
        UserService mockService = mock(UserService.class);
        Export controller = new Export(mockService);
        assertNotNull(controller);
    }

    @Test
    void testDownload() {
        when(userService.listAllUsers()).thenReturn(Arrays.asList(testUser));

        String viewName = exportController.download(model);

        assertEquals("", viewName);
        verify(model).addAttribute(eq("users"), any());
        verify(userService).listAllUsers();
    }
}
