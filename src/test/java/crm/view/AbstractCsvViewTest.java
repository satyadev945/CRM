package crm.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for AbstractCsvView class
 */
class AbstractCsvViewTest {

    @Test
    void testAbstractCsvViewClassExists() {
        assertNotNull(AbstractCsvView.class);
    }

    @Test
    void testAbstractCsvViewIsAbstract() {
        assertTrue(java.lang.reflect.Modifier.isAbstract(AbstractCsvView.class.getModifiers()));
    }
}
