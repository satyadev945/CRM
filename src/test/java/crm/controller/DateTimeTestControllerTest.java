package crm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

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
        String viewName = dateTimeTestController.dateTimeTest(model);

        assertEquals("date/test", viewName);
    }

    @Test
    void dateTimeTest_shouldAddStandardDateAttribute() {
        dateTimeTestController.dateTimeTest(model);

        verify(model).addAttribute(eq("standardDate"), any(Date.class));
    }

    @Test
    void dateTimeTest_shouldAddLocalDateTimeAttribute() {
        dateTimeTestController.dateTimeTest(model);

        verify(model).addAttribute(eq("localDateTime"), any(LocalDateTime.class));
    }

    @Test
    void dateTimeTest_shouldAddLocalDateAttribute() {
        dateTimeTestController.dateTimeTest(model);

        verify(model).addAttribute(eq("localDate"), any(LocalDate.class));
    }

    @Test
    void dateTimeTest_shouldAddTimestampAttribute() {
        dateTimeTestController.dateTimeTest(model);

        verify(model).addAttribute(eq("timestamp"), any(Instant.class));
    }

    @Test
    void dateTimeTest_shouldAddAllDateAttributes() {
        dateTimeTestController.dateTimeTest(model);

        verify(model, times(4)).addAttribute(anyString(), any());
    }

    @Test
    void dateTimeTestController_shouldNotBeNull() {
        assertNotNull(dateTimeTestController);
    }

    @Test
    void dateTimeTest_multipleInvocations_shouldAddAttributesEachTime() {
        dateTimeTestController.dateTimeTest(model);
        dateTimeTestController.dateTimeTest(model);

        verify(model, times(8)).addAttribute(anyString(), any());
    }
}
