package crm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CrmApplicationTest {

    @Test
    void contextLoads() {
        // Tests that the application context loads successfully
    }

    @Test
    void applicationStarts() {
        // Tests that the main method executes without throwing exceptions
        CrmApplication.main(new String[]{});
    }
}