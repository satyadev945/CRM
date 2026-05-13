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
    void testError_returnsErrorHandlingString() {
        String result = myErrorController.error();
        assertNotNull(result);
        assertEquals("Error handling", result);
    }

    @Test
    void testError_returnsNonEmptyString() {
        String result = myErrorController.error();
        assertFalse(result.isEmpty());
    }

    @Test
    void testError_returnsExpectedMessage() {
        String result = myErrorController.error();
        assertTrue(result.contains("Error"));
    }

    @Test
    void testDefaultConstructor() {
        MyErrorController controller = new MyErrorController();
        assertNotNull(controller);
    }
}
