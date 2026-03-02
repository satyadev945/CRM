package crm.viewResolver;

import crm.view.ExcelView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class ExcelViewResolverTest {

    private ExcelViewResolver excelViewResolver;

    @BeforeEach
    void setUp() {
        excelViewResolver = new ExcelViewResolver();
    }

    @Test
    void excelViewResolver_shouldImplementViewResolver() {
        assertTrue(excelViewResolver instanceof ViewResolver);
    }

    @Test
    void resolveViewName_shouldReturnExcelView() throws Exception {
        View view = excelViewResolver.resolveViewName("test", Locale.getDefault());

        assertNotNull(view);
        assertTrue(view instanceof ExcelView);
    }

    @Test
    void resolveViewName_withDifferentLocales_shouldReturnExcelView() throws Exception {
        View view1 = excelViewResolver.resolveViewName("test", Locale.US);
        View view2 = excelViewResolver.resolveViewName("test", Locale.UK);

        assertNotNull(view1);
        assertNotNull(view2);
        assertTrue(view1 instanceof ExcelView);
        assertTrue(view2 instanceof ExcelView);
    }

    @Test
    void excelViewResolver_shouldNotBeNull() {
        assertNotNull(excelViewResolver);
    }
}
