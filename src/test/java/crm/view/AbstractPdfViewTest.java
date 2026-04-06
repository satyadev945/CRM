package crm.view;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.view.AbstractView;

import static org.junit.jupiter.api.Assertions.*;

class AbstractPdfViewTest {

    @Test
    void abstractPdfView_shouldExtendAbstractView() {
        // Assert
        assertTrue(AbstractView.class.isAssignableFrom(AbstractPdfView.class));
    }

    @Test
    void abstractPdfView_shouldBeAbstract() {
        // Assert
        assertTrue(java.lang.reflect.Modifier.isAbstract(AbstractPdfView.class.getModifiers()));
    }

    @Test
    void abstractPdfView_shouldHaveGeneratesDownloadContentMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(AbstractPdfView.class.getDeclaredMethod("generatesDownloadContent"));
    }

    @Test
    void abstractPdfView_shouldHaveGetViewerPreferencesMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(AbstractPdfView.class.getDeclaredMethod("getViewerPreferences"));
    }
}
