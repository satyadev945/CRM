package crm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.springdata.SpringDataDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WebAppConfigTest {

    @Autowired
    private WebAppConfig webAppConfig;

    @Test
    void templateResolverShouldBeConfiguredCorrectly() {
        ClassLoaderTemplateResolver resolver = webAppConfig.templateResolver();

        assertNotNull(resolver);
        assertEquals("templates/", resolver.getPrefix());
        assertEquals(".html", resolver.getSuffix());
        assertEquals("HTML", resolver.getTemplateMode());
        assertEquals("UTF-8", resolver.getCharacterEncoding());
        assertFalse(resolver.isCacheable());
    }

    @Test
    void templateEngineShouldNotBeNull() {
        SpringTemplateEngine engine = webAppConfig.templateEngine();
        assertNotNull(engine);
    }

    @Test
    void viewResolverShouldBeConfiguredCorrectly() {
        ViewResolver resolver = webAppConfig.viewResolver();

        assertNotNull(resolver);
    }

    @Test
    void excelViewResolverShouldNotBeNull() {
        ViewResolver resolver = webAppConfig.excelViewResolver();
        assertNotNull(resolver);
    }

    @Test
    void csvViewResolverShouldNotBeNull() {
        ViewResolver resolver = webAppConfig.csvViewResolver();
        assertNotNull(resolver);
    }

    @Test
    void pdfViewResolverShouldNotBeNull() {
        ViewResolver resolver = webAppConfig.pdfViewResolver();
        assertNotNull(resolver);
    }

    @Test
    void springDataDialectShouldNotBeNull() {
        SpringDataDialect dialect = webAppConfig.springDataDialect();
        assertNotNull(dialect);
    }
}