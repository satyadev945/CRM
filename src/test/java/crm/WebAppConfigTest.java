package crm;

import crm.viewResolver.CsvViewResolver;
import crm.viewResolver.ExcelViewResolver;
import crm.viewResolver.PdfViewResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebAppConfigTest {

    @InjectMocks
    private WebAppConfig webAppConfig;

    @Test
    void testAddViewControllers() {
        ViewControllerRegistry registry = mock(ViewControllerRegistry.class, RETURNS_DEEP_STUBS);

        webAppConfig.addViewControllers(registry);

        verify(registry, atLeastOnce()).addViewController(anyString());
        verify(registry).setOrder(anyInt());
    }

    @Test
    void testConfigureContentNegotiation() {
        ContentNegotiationConfigurer configurer = mock(ContentNegotiationConfigurer.class);
        when(configurer.favorParameter(anyBoolean())).thenReturn(configurer);
        when(configurer.ignoreAcceptHeader(anyBoolean())).thenReturn(configurer);
        when(configurer.defaultContentType(any())).thenReturn(configurer);
        when(configurer.mediaTypes(any())).thenReturn(configurer);

        webAppConfig.configureContentNegotiation(configurer);

        verify(configurer).favorParameter(true);
        verify(configurer).ignoreAcceptHeader(false);
        verify(configurer).defaultContentType(any());
        verify(configurer).mediaTypes(any());
    }

    @Test
    void testContentNegotiatingViewResolver() {
        ContentNegotiationManager manager = mock(ContentNegotiationManager.class);

        ViewResolver resolver = webAppConfig.contentNegotiatingViewResolver(manager);

        assertNotNull(resolver);
    }

    @Test
    void testTemplateResolver() {
        ClassLoaderTemplateResolver resolver = webAppConfig.templateResolver();

        assertNotNull(resolver);
        assertEquals("UTF-8", resolver.getCharacterEncoding());
    }

    @Test
    void testTemplateEngine() {
        SpringTemplateEngine engine = webAppConfig.templateEngine();

        assertNotNull(engine);
    }

    @Test
    void testViewResolver() {
        ViewResolver resolver = webAppConfig.viewResolver();

        assertNotNull(resolver);
    }

    @Test
    void testExcelViewResolver() {
        ViewResolver resolver = webAppConfig.excelViewResolver();

        assertNotNull(resolver);
        assertTrue(resolver instanceof ExcelViewResolver);
    }

    @Test
    void testCsvViewResolver() {
        ViewResolver resolver = webAppConfig.csvViewResolver();

        assertNotNull(resolver);
        assertTrue(resolver instanceof CsvViewResolver);
    }

    @Test
    void testPdfViewResolver() {
        ViewResolver resolver = webAppConfig.pdfViewResolver();

        assertNotNull(resolver);
        assertTrue(resolver instanceof PdfViewResolver);
    }

    @Test
    void testConfigurationAnnotation() {
        assertTrue(WebAppConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
    }

    @Test
    void testWebMvcConfigurerImplementation() {
        assertTrue(org.springframework.web.servlet.config.annotation.WebMvcConfigurer.class.isAssignableFrom(WebAppConfig.class));
    }
}
