package crm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.error.ErrorController;

import static org.junit.jupiter.api.Assertions.*;

class MyErrorControllerTest {

    private MyErrorController myErrorController;

    @BeforeEach
    void setUp() {
        myErrorController = new MyErrorController();
    }

    @Test
    void myErrorController_shouldImplementErrorController() {
        assertTrue(myErrorController instanceof ErrorController);
    }

    @Test
    void error_shouldReturnErrorHandlingMessage() {
        String result = myErrorController.error();

        assertEquals("Error handling", result);
    }

    @Test
    void error_shouldNotReturnNull() {
        String result = myErrorController.error();

        assertNotNull(result);
    }

    @Test
    void error_shouldNotReturnEmptyString() {
        String result = myErrorController.error();

        assertFalse(result.isEmpty());
    }

    @Test
    void myErrorController_shouldBeInstantiable() {
        assertNotNull(myErrorController);
    }

    @Test
    void error_multipleInvocations_shouldReturnSameMessage() {
        String result1 = myErrorController.error();
        String result2 = myErrorController.error();

        assertEquals(result1, result2);
    }
}
