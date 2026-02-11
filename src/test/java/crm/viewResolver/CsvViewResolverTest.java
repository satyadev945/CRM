package crm.viewResolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.View;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for CsvViewResolver
 */
class CsvViewResolverTest {

    private CsvViewResolver csvViewResolver;

    @BeforeEach
    void setUp() {
        csvViewResolver = new CsvViewResolver();
    }

    @Test
    void testCsvViewResolverCreation() {
        assertNotNull(csvViewResolver);
    }

    @Test
    void testCsvViewResolverImplementsViewResolver() {
        assertTrue(csvViewResolver instanceof org.springframework.web.servlet.ViewResolver);
    }

    @Test
    void testResolveViewName() throws Exception {
        View view = csvViewResolver.resolveViewName("testView", Locale.ENGLISH);

        assertNotNull(view);
    }

    @Test
    void testResolveViewNameWithDifferentLocale() throws Exception {
        View view = csvViewResolver.resolveViewName("testView", Locale.FRENCH);

        assertNotNull(view);
    }

    @Test
    void testResolveViewNameWithNullLocale() throws Exception {
        View view = csvViewResolver.resolveViewName("testView", null);

        assertNotNull(view);
    }

    @Test
    void testResolveViewNameWithEmptyString() throws Exception {
        View view = csvViewResolver.resolveViewName("", Locale.ENGLISH);

        assertNotNull(view);
    }
}
