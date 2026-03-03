package crm.viewResolver;

import crm.view.CsvView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.View;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class CsvViewResolverTest {

    private CsvViewResolver csvViewResolver;

    @BeforeEach
    void setUp() {
        csvViewResolver = new CsvViewResolver();
    }

    @Test
    void resolveViewName_shouldReturnCsvView() throws Exception {
        View view = csvViewResolver.resolveViewName("test", Locale.getDefault());

        assertNotNull(view);
        assertInstanceOf(CsvView.class, view);
    }

    @Test
    void resolveViewName_withNullViewName_shouldReturnCsvView() throws Exception {
        View view = csvViewResolver.resolveViewName(null, Locale.getDefault());

        assertNotNull(view);
        assertInstanceOf(CsvView.class, view);
    }

    @Test
    void resolveViewName_withDifferentLocales_shouldReturnCsvView() throws Exception {
        View view1 = csvViewResolver.resolveViewName("test", Locale.US);
        View view2 = csvViewResolver.resolveViewName("test", Locale.UK);

        assertNotNull(view1);
        assertNotNull(view2);
        assertInstanceOf(CsvView.class, view1);
        assertInstanceOf(CsvView.class, view2);
    }
}
