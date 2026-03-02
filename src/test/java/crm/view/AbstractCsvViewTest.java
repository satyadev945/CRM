package crm.view;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.view.AbstractView;

import static org.junit.jupiter.api.Assertions.*;

class AbstractCsvViewTest {

    @Test
    void abstractCsvView_shouldExtendAbstractView() {
        assertTrue(AbstractView.class.isAssignableFrom(AbstractCsvView.class));
    }

    @Test
    void abstractCsvView_classExists() {
        assertNotNull(AbstractCsvView.class);
    }
}
