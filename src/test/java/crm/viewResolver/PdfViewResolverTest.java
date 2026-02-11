package crm.viewResolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.View;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for PdfViewResolver
 */
class PdfViewResolverTest {

    private PdfViewResolver pdfViewResolver;

    @BeforeEach
    void setUp() {
        pdfViewResolver = new PdfViewResolver();
    }

    @Test
    void testPdfViewResolverCreation() {
        assertNotNull(pdfViewResolver);
    }

    @Test
    void testPdfViewResolverImplementsViewResolver() {
        assertTrue(pdfViewResolver instanceof org.springframework.web.servlet.ViewResolver);
    }

    @Test
    void testResolveViewName() throws Exception {
        View view = pdfViewResolver.resolveViewName("testView", Locale.ENGLISH);

        assertNotNull(view);
    }

    @Test
    void testResolveViewNameWithDifferentLocale() throws Exception {
        View view = pdfViewResolver.resolveViewName("testView", Locale.JAPANESE);

        assertNotNull(view);
    }

    @Test
    void testResolveViewNameWithNullLocale() throws Exception {
        View view = pdfViewResolver.resolveViewName("testView", null);

        assertNotNull(view);
    }

    @Test
    void testResolveViewNameWithEmptyString() throws Exception {
        View view = pdfViewResolver.resolveViewName("", Locale.ENGLISH);

        assertNotNull(view);
    }
}
