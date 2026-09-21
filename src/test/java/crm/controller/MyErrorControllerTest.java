package crm.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MyErrorControllerTest {

    @InjectMocks
    private MyErrorController myErrorController;

    @Test
    void testHandleError_ReturnsErrorView() {
        String view = myErrorController.handleError();
        assertEquals("error", view);
    }

    @Test
    void testHandleError_NotNull() {
        String view = myErrorController.handleError();
        assertNotNull(view);
    }

    @Test
    void testHandleError_ReturnsCorrectString() {
        String view = myErrorController.handleError();
        assertEquals("error", view);
        assertFalse(view.isEmpty());
    }

    @Test
    void testConstructor_DefaultConstructor() {
        MyErrorController controller = new MyErrorController();
        assertNotNull(controller);
    }
}
