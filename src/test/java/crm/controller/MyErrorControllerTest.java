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
    void error_shouldReturnErrorView() {
        String viewName = myErrorController.error();

        assertEquals("error", viewName);
    }

    @Test
    void myErrorController_shouldBeInstantiable() {
        assertNotNull(myErrorController);
    }

    @Test
    void myErrorController_shouldImplementErrorController() {
        assertTrue(myErrorController instanceof org.springframework.boot.web.servlet.error.ErrorController);
    }
}
