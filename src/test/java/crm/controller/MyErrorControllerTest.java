package crm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyErrorControllerTest {

    private MyErrorController myErrorController;

    @BeforeEach
    void setUp() {
        myErrorController = new MyErrorController();
    }

    @Test
    void error_shouldReturnErrorHandlingMessage() {
        // Act
        String result = myErrorController.error();

        // Assert
        assertEquals("Error handling", result);
    }

    @Test
    void myErrorController_shouldImplementErrorController() {
        // Assert
        assertTrue(myErrorController instanceof org.springframework.boot.web.servlet.error.ErrorController);
    }

    @Test
    void myErrorController_shouldHaveRestControllerAnnotation() {
        // Assert
        assertTrue(MyErrorController.class.isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class));
    }

    @Test
    void error_shouldNotReturnNull() {
        // Act
        String result = myErrorController.error();

        // Assert
        assertNotNull(result);
    }

    @Test
    void error_shouldReturnNonEmptyString() {
        // Act
        String result = myErrorController.error();

        // Assert
        assertFalse(result.isEmpty());
    }
}
