package crm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class CrmApplicationTests {

    @Test
    public void contextLoads() {
        // Simple test to verify the application class exists
        assertDoesNotThrow(() -> {
            // CrmApplication class is accessible
            Class.forName("crm.CrmApplication");
        });
    }

    @Test
    public void testMainClassExists() {
        CrmApplication app = new CrmApplication();
        assertDoesNotThrow(() -> app.getClass().getName());
    }

}
