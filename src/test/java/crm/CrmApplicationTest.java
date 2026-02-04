package crm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CrmApplicationTest {

    @Test
    void contextLoads() {
        // Verify the application context loads successfully
    }

    @Test
    void applicationStarts() {
        // Test that the main method runs without throwing exceptions
        CrmApplication.main(new String[]{});
    }
}