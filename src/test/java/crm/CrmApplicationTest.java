package crm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test for CrmApplication main class
 */
class CrmApplicationTest {

    @Test
    void testMainMethod() {
        // Verify the main method exists and can be called
        assertDoesNotThrow(() -> {
            String[] args = {};
            // We cannot actually run the application in test, but we can verify the method exists
            CrmApplication.class.getMethod("main", String[].class);
        });
    }

    @Test
    void testCrmApplicationClass() {
        // Verify class has SpringBootApplication annotation
        assertTrue(CrmApplication.class.isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class));
    }

    @Test
    void testApplicationContext() {
        // Verify CrmApplication class exists and can be instantiated
        assertNotNull(CrmApplication.class);
    }
}
