package crm.viewResolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.View;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for ExcelViewResolver
 */
class ExcelViewResolverTest {

    private ExcelViewResolver excelViewResolver;

    @BeforeEach
    void setUp() {
        excelViewResolver = new ExcelViewResolver();
    }

    @Test
    void testExcelViewResolverCreation() {
        assertNotNull(excelViewResolver);
    }

    @Test
    void testExcelViewResolverImplementsViewResolver() {
        assertTrue(excelViewResolver instanceof org.springframework.web.servlet.ViewResolver);
    }

    @Test
    void testResolveViewName() throws Exception {
        View view = excelViewResolver.resolveViewName("testView", Locale.ENGLISH);

        assertNotNull(view);
    }

    @Test
    void testResolveViewNameWithDifferentLocale() throws Exception {
        View view = excelViewResolver.resolveViewName("testView", Locale.GERMAN);

        assertNotNull(view);
    }

    @Test
    void testResolveViewNameWithNullLocale() throws Exception {
        View view = excelViewResolver.resolveViewName("testView", null);

        assertNotNull(view);
    }

    @Test
    void testResolveViewNameWithEmptyString() throws Exception {
        View view = excelViewResolver.resolveViewName("", Locale.ENGLISH);

        assertNotNull(view);
    }
}
