package crm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

class DateTimeTestControllerTest {

    @Mock
    private Model model;

    @InjectMocks
    private DateTimeTestController dateTimeTestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void dateTimeTestShouldAddDateAttributesToModelAndReturnView() {
        // Act
        String viewName = dateTimeTestController.dateTimeTest(model);

        // Assert
        verify(model).addAttribute(eq("standardDate"), any(Date.class));
        verify(model).addAttribute(eq("localDateTime"), any(LocalDateTime.class));
        verify(model).addAttribute(eq("localDate"), any(LocalDate.class));
        verify(model).addAttribute(eq("timestamp"), any(Instant.class));
        assertEquals("date/test", viewName);
    }
}