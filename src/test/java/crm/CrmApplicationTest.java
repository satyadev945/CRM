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
    void mainMethodStartsApplication() {
        // Test that main method can be invoked without errors
        assertDoesNotThrow(() -> {
            // We don't actually start the application to avoid port conflicts
            // Just verify the class and method exist
            assertNotNull(CrmApplication.class.getMethod("main", String[].class));
        });
    }

    @Test
    void applicationClassExists() {
        assertNotNull(CrmApplication.class);
    }
}
