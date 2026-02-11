package crm.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for AbstractPdfView class
 */
class AbstractPdfViewTest {

    @Test
    void testAbstractPdfViewClassExists() {
        assertNotNull(AbstractPdfView.class);
    }

    @Test
    void testAbstractPdfViewIsAbstract() {
        assertTrue(java.lang.reflect.Modifier.isAbstract(AbstractPdfView.class.getModifiers()));
    }
}
