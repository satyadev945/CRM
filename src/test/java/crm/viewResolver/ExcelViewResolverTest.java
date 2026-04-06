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
        // Assert
        assertTrue(excelViewResolver instanceof ViewResolver);
    }

    @Test
    void resolveViewName_shouldReturnExcelView() throws Exception {
        // Act
        View view = excelViewResolver.resolveViewName("test", Locale.getDefault());

        // Assert
        assertNotNull(view);
        assertTrue(view instanceof ExcelView);
    }

    @Test
    void resolveViewName_shouldReturnExcelView_forAnyViewName() throws Exception {
        // Act
        View view1 = excelViewResolver.resolveViewName("view1", Locale.US);
        View view2 = excelViewResolver.resolveViewName("view2", Locale.UK);

        // Assert
        assertNotNull(view1);
        assertNotNull(view2);
        assertTrue(view1 instanceof ExcelView);
        assertTrue(view2 instanceof ExcelView);
    }

    @Test
    void resolveViewName_shouldHandleNullViewName() throws Exception {
        // Act
        View view = excelViewResolver.resolveViewName(null, Locale.getDefault());

        // Assert
        assertNotNull(view);
        assertTrue(view instanceof ExcelView);
    }
}
