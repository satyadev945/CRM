package crm.view;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.view.AbstractView;

import static org.junit.jupiter.api.Assertions.*;

class AbstractPdfViewTest {

    @Test
    void abstractPdfView_shouldExtendAbstractView() {
        assertTrue(AbstractView.class.isAssignableFrom(AbstractPdfView.class));
    }

    @Test
    void abstractPdfView_classExists() {
        assertNotNull(AbstractPdfView.class);
    }
}
