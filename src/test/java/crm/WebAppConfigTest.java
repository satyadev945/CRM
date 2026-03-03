package crm;

import crm.viewResolver.CsvViewResolver;
import crm.viewResolver.ExcelViewResolver;
import crm.viewResolver.PdfViewResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
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
    void addViewControllers_shouldConfigureViewControllers() {
        ViewControllerRegistry registry = mock(ViewControllerRegistry.class);
        
        webAppConfig.addViewControllers(registry);
        
        verify(registry, atLeastOnce()).addViewController(anyString());
    }

    @Test
    void configureContentNegotiation_shouldConfigureMediaTypes() {
        ContentNegotiationConfigurer configurer = mock(ContentNegotiationConfigurer.class);
        
        webAppConfig.configureContentNegotiation(configurer);
        
        verify(configurer).ignoreAcceptHeader(false);
        verify(configurer).defaultContentType(MediaType.APPLICATION_JSON);
        verify(configurer).mediaTypes(anyMap());
    }

    @Test
    void templateResolver_shouldReturnClassLoaderTemplateResolver() {
        ClassLoaderTemplateResolver resolver = webAppConfig.templateResolver();
        
        assertNotNull(resolver);
        assertInstanceOf(ClassLoaderTemplateResolver.class, resolver);
    }

    @Test
    void templateEngine_shouldReturnSpringTemplateEngine() {
        SpringTemplateEngine engine = webAppConfig.templateEngine();
        
        assertNotNull(engine);
        assertInstanceOf(SpringTemplateEngine.class, engine);
    }

    @Test
    void viewResolver_shouldReturnThymeleafViewResolver() {
        ViewResolver resolver = webAppConfig.viewResolver();
        
        assertNotNull(resolver);
        assertInstanceOf(ThymeleafViewResolver.class, resolver);
    }

    @Test
    void excelViewResolver_shouldReturnExcelViewResolver() {
        ViewResolver resolver = webAppConfig.excelViewResolver();
        
        assertNotNull(resolver);
        assertInstanceOf(ExcelViewResolver.class, resolver);
    }

    @Test
    void csvViewResolver_shouldReturnCsvViewResolver() {
        ViewResolver resolver = webAppConfig.csvViewResolver();
        
        assertNotNull(resolver);
        assertInstanceOf(CsvViewResolver.class, resolver);
    }

    @Test
    void pdfViewResolver_shouldReturnPdfViewResolver() {
        ViewResolver resolver = webAppConfig.pdfViewResolver();
        
        assertNotNull(resolver);
        assertInstanceOf(PdfViewResolver.class, resolver);
    }

    @Test
    void contentNegotiatingViewResolver_shouldReturnContentNegotiatingViewResolver() {
        ContentNegotiationManager manager = mock(ContentNegotiationManager.class);
        
        ViewResolver resolver = webAppConfig.contentNegotiatingViewResolver(manager);
        
        assertNotNull(resolver);
        assertInstanceOf(ContentNegotiatingViewResolver.class, resolver);
    }
}
