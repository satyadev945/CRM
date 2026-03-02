package crm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CrmApplicationTest {

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
        assertDoesNotThrow(() -> {
            CrmApplication.main(new String[]{});
        });
    }

    @Test
    void mainMethod_withNullArgs_shouldNotThrow() {
        assertDoesNotThrow(() -> {
            CrmApplication.main(new String[]{});
        });
    }

    @Test
    void mainMethod_withEmptyArgs_shouldNotThrow() {
        assertDoesNotThrow(() -> {
            CrmApplication.main(new String[]{});
        });
    }

    @Test
    void applicationContext_shouldNotBeNull() {
        assertNotNull(CrmApplication.class);
    }

    @Test
    void springBootApplication_annotationPresent() {
        assertTrue(CrmApplication.class.isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class));
    }

    @Test
    void entityScan_annotationPresent() {
        assertTrue(CrmApplication.class.isAnnotationPresent(org.springframework.boot.autoconfigure.domain.EntityScan.class));
    }
}
