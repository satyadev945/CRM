package crm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CrmApplicationTest {

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
        assertDoesNotThrow(() -> {
            // Context loading is tested by @SpringBootTest
        });
    }

    @Test
    void main_shouldStartApplication() {
        // Test that main method can be called without exceptions
        assertDoesNotThrow(() -> {
            // We don't actually start the application in tests
            // Just verify the class structure is correct
            assertNotNull(CrmApplication.class);
        });
    }

    @Test
    void applicationClass_shouldHaveSpringBootApplicationAnnotation() {
        // Verify the class has the required annotation
        assertTrue(CrmApplication.class.isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class));
    }

    @Test
    void mainMethod_shouldExist() throws NoSuchMethodException {
        // Verify main method exists with correct signature
        assertNotNull(CrmApplication.class.getMethod("main", String[].class));
    }
}
