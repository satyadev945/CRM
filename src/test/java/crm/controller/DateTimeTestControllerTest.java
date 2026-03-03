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
    void dateTimeTest_shouldReturnDateTestView() {
        String viewName = dateTimeTestController.dateTimeTest(model);

        assertEquals("date/test", viewName);
        verify(model).addAttribute(eq("standardDate"), any());
        verify(model).addAttribute(eq("localDateTime"), any());
        verify(model).addAttribute(eq("localDate"), any());
        verify(model).addAttribute(eq("timestamp"), any());
    }

    @Test
    void dateTimeTest_shouldAddAllDateTimeAttributes() {
        dateTimeTestController.dateTimeTest(model);

        verify(model, times(4)).addAttribute(anyString(), any());
    }
}
