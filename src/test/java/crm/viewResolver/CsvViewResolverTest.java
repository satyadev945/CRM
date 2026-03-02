package crm.viewResolver;

import crm.view.CsvView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class CsvViewResolverTest {

    private CsvViewResolver csvViewResolver;

    @BeforeEach
    void setUp() {
        csvViewResolver = new CsvViewResolver();
    }

    @Test
    void csvViewResolver_shouldImplementViewResolver() {
        assertTrue(csvViewResolver instanceof ViewResolver);
    }

    @Test
    void resolveViewName_shouldReturnCsvView() throws Exception {
        View view = csvViewResolver.resolveViewName("test", Locale.getDefault());

        assertNotNull(view);
        assertTrue(view instanceof CsvView);
    }

    @Test
    void resolveViewName_withDifferentLocales_shouldReturnCsvView() throws Exception {
        View view1 = csvViewResolver.resolveViewName("test", Locale.US);
        View view2 = csvViewResolver.resolveViewName("test", Locale.UK);

        assertNotNull(view1);
        assertNotNull(view2);
        assertTrue(view1 instanceof CsvView);
        assertTrue(view2 instanceof CsvView);
    }

    @Test
    void csvViewResolver_shouldNotBeNull() {
        assertNotNull(csvViewResolver);
    }
}
