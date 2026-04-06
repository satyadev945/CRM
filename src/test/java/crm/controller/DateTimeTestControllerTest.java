package crm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DateTimeTestControllerTest {

    @Mock
    private Model model;

    @InjectMocks
    private DateTimeTestController dateTimeTestController;

    @Test
    void dateTimeTest_shouldReturnTestView() {
        // Act
        String viewName = dateTimeTestController.dateTimeTest(model);

        // Assert
        assertEquals("date/test", viewName);
        verify(model).addAttribute(eq("standardDate"), any());
        verify(model).addAttribute(eq("localDateTime"), any());
        verify(model).addAttribute(eq("localDate"), any());
        verify(model).addAttribute(eq("timestamp"), any());
    }

    @Test
    void dateTimeTest_shouldAddAllDateAttributes() {
        // Act
        dateTimeTestController.dateTimeTest(model);

        // Assert
        verify(model, times(4)).addAttribute(anyString(), any());
    }

    @Test
    void dateTimeTestController_shouldHaveControllerAnnotation() {
        // Assert
        assertTrue(DateTimeTestController.class.isAnnotationPresent(org.springframework.stereotype.Controller.class));
    }

    @Test
    void dateTimeTestController_shouldHaveRequestMappingAnnotation() {
        // Assert
        assertTrue(DateTimeTestController.class.isAnnotationPresent(org.springframework.web.bind.annotation.RequestMapping.class));
    }
}
