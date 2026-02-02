package crm.controller;

import crm.entity.User;
import crm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExportTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private Export export;

    private List<User> users;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        users = Arrays.asList(user1, user2);
    }

    @Test
    void downloadShouldAddUsersToModelAndReturnEmptyView() {
        // Arrange
        when(userService.listAllUsers()).thenReturn(users);

        // Act
        String viewName = export.download(model);

        // Assert
        verify(userService).listAllUsers();
        verify(model).addAttribute("users", users);
        assertEquals("", viewName);
    }
}