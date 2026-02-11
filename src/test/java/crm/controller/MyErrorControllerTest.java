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
    void testError() {
        String viewName = myErrorController.error();

        assertEquals("error", viewName);
    }

    @Test
    void testImplementsErrorController() {
        assertTrue(myErrorController instanceof org.springframework.boot.web.servlet.error.ErrorController);
    }

    @Test
    void testControllerCreation() {
        assertNotNull(myErrorController);
    }
}
