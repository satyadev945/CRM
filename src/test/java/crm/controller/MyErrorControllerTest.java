package crm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyErrorControllerTest {

    @InjectMocks
    private MyErrorController myErrorController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void errorShouldReturnErrorHandlingMessage() {
        // Act
        String result = myErrorController.error();

        // Assert
        assertEquals("Error handling", result);
    }

    // The getErrorPath method has been removed from the ErrorController interface
    // in Spring Boot 2.3.0+, so we removed this test
}