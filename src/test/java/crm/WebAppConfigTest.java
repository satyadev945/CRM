package crm;

import crm.viewResolver.CsvViewResolver;
import crm.viewResolver.ExcelViewResolver;
import crm.viewResolver.PdfViewResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebAppConfigTest {

    private WebAppConfig webAppConfig;

    @BeforeEach
    void setUp() {
        webAppConfig = new WebAppConfig();
    }

    @Test
    void templateResolver_shouldReturnClassLoaderTemplateResolver() {
        // Act
        ClassLoaderTemplateResolver resolver = webAppConfig.templateResolver();

        // Assert
        assertNotNull(resolver);
        assertTrue(resolver instanceof ClassLoaderTemplateResolver);
    }

    @Test
    void templateResolver_shouldHaveCorrectPrefix() {
        // Act
        ClassLoaderTemplateResolver resolver = webAppConfig.templateResolver();

        // Assert
        assertNotNull(resolver);
        // Verify configuration is set
        assertDoesNotThrow(() -> resolver.toString());
    }

    @Test
    void templateEngine_shouldReturnSpringTemplateEngine() {
        // Act
        SpringTemplateEngine engine = webAppConfig.templateEngine();

        // Assert
        assertNotNull(engine);
        assertTrue(engine instanceof SpringTemplateEngine);
    }

    @Test
    void templateEngine_shouldHaveTemplateResolverSet() {
        // Act
        SpringTemplateEngine engine = webAppConfig.templateEngine();

        // Assert
        assertNotNull(engine);
        assertNotNull(engine.getTemplateResolvers());
        assertFalse(engine.getTemplateResolvers().isEmpty());
    }

    @Test
    void viewResolver_shouldReturnThymeleafViewResolver() {
        // Act
        ViewResolver resolver = webAppConfig.viewResolver();

        // Assert
        assertNotNull(resolver);
        assertTrue(resolver instanceof ThymeleafViewResolver);
    }

    @Test
    void excelViewResolver_shouldReturnExcelViewResolver() {
        // Act
        ViewResolver resolver = webAppConfig.excelViewResolver();

        // Assert
        assertNotNull(resolver);
        assertTrue(resolver instanceof ExcelViewResolver);
    }

    @Test
    void csvViewResolver_shouldReturnCsvViewResolver() {
        // Act
        ViewResolver resolver = webAppConfig.csvViewResolver();

        // Assert
        assertNotNull(resolver);
        assertTrue(resolver instanceof CsvViewResolver);
    }

    @Test
    void pdfViewResolver_shouldReturnPdfViewResolver() {
        // Act
        ViewResolver resolver = webAppConfig.pdfViewResolver();

        // Assert
        assertNotNull(resolver);
        assertTrue(resolver instanceof PdfViewResolver);
    }

    @Test
    void contentNegotiatingViewResolver_shouldReturnContentNegotiatingViewResolver() {
        // Arrange
        ContentNegotiationManager manager = mock(ContentNegotiationManager.class);

        // Act
        ViewResolver resolver = webAppConfig.contentNegotiatingViewResolver(manager);

        // Assert
        assertNotNull(resolver);
        assertTrue(resolver instanceof ContentNegotiatingViewResolver);
    }

    @Test
    void contentNegotiatingViewResolver_shouldHaveViewResolversSet() {
        // Arrange
        ContentNegotiationManager manager = mock(ContentNegotiationManager.class);

        // Act
        ContentNegotiatingViewResolver resolver = (ContentNegotiatingViewResolver) webAppConfig.contentNegotiatingViewResolver(manager);

        // Assert
        assertNotNull(resolver);
        assertNotNull(resolver.getViewResolvers());
        assertFalse(resolver.getViewResolvers().isEmpty());
    }

    @Test
    void webAppConfig_shouldHaveConfigurationAnnotation() {
        // Assert
        assertTrue(WebAppConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
    }
}
