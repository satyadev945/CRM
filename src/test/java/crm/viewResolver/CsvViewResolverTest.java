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
        // Assert
        assertTrue(csvViewResolver instanceof ViewResolver);
    }

    @Test
    void resolveViewName_shouldReturnCsvView() throws Exception {
        // Act
        View view = csvViewResolver.resolveViewName("test", Locale.getDefault());

        // Assert
        assertNotNull(view);
        assertTrue(view instanceof CsvView);
    }

    @Test
    void resolveViewName_shouldReturnCsvView_forAnyViewName() throws Exception {
        // Act
        View view1 = csvViewResolver.resolveViewName("view1", Locale.US);
        View view2 = csvViewResolver.resolveViewName("view2", Locale.UK);

        // Assert
        assertNotNull(view1);
        assertNotNull(view2);
        assertTrue(view1 instanceof CsvView);
        assertTrue(view2 instanceof CsvView);
    }

    @Test
    void resolveViewName_shouldHandleNullViewName() throws Exception {
        // Act
        View view = csvViewResolver.resolveViewName(null, Locale.getDefault());

        // Assert
        assertNotNull(view);
        assertTrue(view instanceof CsvView);
    }
}
