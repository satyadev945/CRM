package crm.view;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.view.AbstractView;

import static org.junit.jupiter.api.Assertions.*;

class AbstractCsvViewTest {

    @Test
    void abstractCsvView_shouldExtendAbstractView() {
        // Assert
        assertTrue(AbstractView.class.isAssignableFrom(AbstractCsvView.class));
    }

    @Test
    void abstractCsvView_shouldBeAbstract() {
        // Assert
        assertTrue(java.lang.reflect.Modifier.isAbstract(AbstractCsvView.class.getModifiers()));
    }

    @Test
    void abstractCsvView_shouldHaveSetUrlMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(AbstractCsvView.class.getMethod("setUrl", String.class));
    }

    @Test
    void abstractCsvView_shouldHaveGeneratesDownloadContentMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(AbstractCsvView.class.getDeclaredMethod("generatesDownloadContent"));
    }
}
