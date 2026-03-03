package crm.viewResolver;

import crm.view.PdfView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.View;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class PdfViewResolverTest {

    private PdfViewResolver pdfViewResolver;

    @BeforeEach
    void setUp() {
        pdfViewResolver = new PdfViewResolver();
    }

    @Test
    void resolveViewName_shouldReturnPdfView() throws Exception {
        View view = pdfViewResolver.resolveViewName("test", Locale.getDefault());

        assertNotNull(view);
        assertInstanceOf(PdfView.class, view);
    }

    @Test
    void resolveViewName_withNullViewName_shouldReturnPdfView() throws Exception {
        View view = pdfViewResolver.resolveViewName(null, Locale.getDefault());

        assertNotNull(view);
        assertInstanceOf(PdfView.class, view);
    }

    @Test
    void resolveViewName_withDifferentLocales_shouldReturnPdfView() throws Exception {
        View view1 = pdfViewResolver.resolveViewName("test", Locale.US);
        View view2 = pdfViewResolver.resolveViewName("test", Locale.UK);

        assertNotNull(view1);
        assertNotNull(view2);
        assertInstanceOf(PdfView.class, view1);
        assertInstanceOf(PdfView.class, view2);
    }
}
