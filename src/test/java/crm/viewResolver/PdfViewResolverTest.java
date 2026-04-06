package crm.viewResolver;

import crm.view.PdfView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class PdfViewResolverTest {

    private PdfViewResolver pdfViewResolver;

    @BeforeEach
    void setUp() {
        pdfViewResolver = new PdfViewResolver();
    }

    @Test
    void pdfViewResolver_shouldImplementViewResolver() {
        // Assert
        assertTrue(pdfViewResolver instanceof ViewResolver);
    }

    @Test
    void resolveViewName_shouldReturnPdfView() throws Exception {
        // Act
        View view = pdfViewResolver.resolveViewName("test", Locale.getDefault());

        // Assert
        assertNotNull(view);
        assertTrue(view instanceof PdfView);
    }

    @Test
    void resolveViewName_shouldReturnPdfView_forAnyViewName() throws Exception {
        // Act
        View view1 = pdfViewResolver.resolveViewName("view1", Locale.US);
        View view2 = pdfViewResolver.resolveViewName("view2", Locale.UK);

        // Assert
        assertNotNull(view1);
        assertNotNull(view2);
        assertTrue(view1 instanceof PdfView);
        assertTrue(view2 instanceof PdfView);
    }

    @Test
    void resolveViewName_shouldHandleNullViewName() throws Exception {
        // Act
        View view = pdfViewResolver.resolveViewName(null, Locale.getDefault());

        // Assert
        assertNotNull(view);
        assertTrue(view instanceof PdfView);
    }
}
