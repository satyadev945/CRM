package crm.viewResolver;

import crm.view.ExcelView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.View;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class ExcelViewResolverTest {

    private ExcelViewResolver excelViewResolver;

    @BeforeEach
    void setUp() {
        excelViewResolver = new ExcelViewResolver();
    }

    @Test
    void resolveViewName_shouldReturnExcelView() throws Exception {
        View view = excelViewResolver.resolveViewName("test", Locale.getDefault());

        assertNotNull(view);
        assertInstanceOf(ExcelView.class, view);
    }

    @Test
    void resolveViewName_withNullViewName_shouldReturnExcelView() throws Exception {
        View view = excelViewResolver.resolveViewName(null, Locale.getDefault());

        assertNotNull(view);
        assertInstanceOf(ExcelView.class, view);
    }

    @Test
    void resolveViewName_withDifferentLocales_shouldReturnExcelView() throws Exception {
        View view1 = excelViewResolver.resolveViewName("test", Locale.US);
        View view2 = excelViewResolver.resolveViewName("test", Locale.UK);

        assertNotNull(view1);
        assertNotNull(view2);
        assertInstanceOf(ExcelView.class, view1);
        assertInstanceOf(ExcelView.class, view2);
    }
}
