package crm;

import crm.viewResolver.CsvViewResolver;
import crm.viewResolver.ExcelViewResolver;
import crm.viewResolver.PdfViewResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
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

@SpringBootTest
@ActiveProfiles("test")
class WebAppConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private WebAppConfig webAppConfig;

    private ViewControllerRegistry mockRegistry;
    private ContentNegotiationConfigurer mockConfigurer;

    @BeforeEach
    void setUp() {
        mockRegistry = mock(ViewControllerRegistry.class);
        mockConfigurer = mock(ContentNegotiationConfigurer.class);
    }

    @Test
    void webAppConfig_shouldBeLoadedAsBean() {
        assertNotNull(webAppConfig);
    }

    @Test
    void addViewControllers_shouldConfigureViewControllers() {
        assertDoesNotThrow(() -> webAppConfig.addViewControllers(mockRegistry));
        verify(mockRegistry, atLeastOnce()).addViewController(anyString());
    }

    @Test
    void configureContentNegotiation_shouldConfigureMediaTypes() {
        assertDoesNotThrow(() -> webAppConfig.configureContentNegotiation(mockConfigurer));
    }

    @Test
    void templateResolver_shouldBeConfigured() {
        ClassLoaderTemplateResolver resolver = webAppConfig.templateResolver();
        assertNotNull(resolver);
    }

    @Test
    void templateResolver_shouldHaveCorrectPrefix() {
        ClassLoaderTemplateResolver resolver = webAppConfig.templateResolver();
        assertEquals("templates/", resolver.getPrefix());
    }

    @Test
    void templateResolver_shouldHaveCorrectSuffix() {
        ClassLoaderTemplateResolver resolver = webAppConfig.templateResolver();
        assertEquals(".html", resolver.getSuffix());
    }

    @Test
    void templateResolver_shouldHaveCorrectCharacterEncoding() {
        ClassLoaderTemplateResolver resolver = webAppConfig.templateResolver();
        assertEquals("UTF-8", resolver.getCharacterEncoding());
    }

    @Test
    void templateResolver_shouldNotBeCacheable() {
        ClassLoaderTemplateResolver resolver = webAppConfig.templateResolver();
        assertFalse(resolver.isCacheable());
    }

    @Test
    void templateEngine_shouldBeConfigured() {
        SpringTemplateEngine engine = webAppConfig.templateEngine();
        assertNotNull(engine);
    }

    @Test
    void templateEngine_shouldHaveTemplateResolver() {
        SpringTemplateEngine engine = webAppConfig.templateEngine();
        assertNotNull(engine.getTemplateResolvers());
        assertFalse(engine.getTemplateResolvers().isEmpty());
    }

    @Test
    void templateEngine_shouldHaveDialects() {
        SpringTemplateEngine engine = webAppConfig.templateEngine();
        assertNotNull(engine.getDialects());
        assertTrue(engine.getDialects().size() >= 2); // SpringSecurityDialect and Java8TimeDialect
    }

    @Test
    void viewResolver_shouldBeConfigured() {
        ViewResolver resolver = webAppConfig.viewResolver();
        assertNotNull(resolver);
        assertTrue(resolver instanceof ThymeleafViewResolver);
    }

    @Test
    void viewResolver_shouldHaveCorrectCharacterEncoding() {
        ThymeleafViewResolver resolver = (ThymeleafViewResolver) webAppConfig.viewResolver();
        assertEquals("UTF-8", resolver.getCharacterEncoding());
    }

    @Test
    void excelViewResolver_shouldBeConfigured() {
        ViewResolver resolver = webAppConfig.excelViewResolver();
        assertNotNull(resolver);
        assertTrue(resolver instanceof ExcelViewResolver);
    }

    @Test
    void csvViewResolver_shouldBeConfigured() {
        ViewResolver resolver = webAppConfig.csvViewResolver();
        assertNotNull(resolver);
        assertTrue(resolver instanceof CsvViewResolver);
    }

    @Test
    void pdfViewResolver_shouldBeConfigured() {
        ViewResolver resolver = webAppConfig.pdfViewResolver();
        assertNotNull(resolver);
        assertTrue(resolver instanceof PdfViewResolver);
    }

    @Test
    void contentNegotiatingViewResolver_shouldBeConfigured() {
        ContentNegotiationManager manager = mock(ContentNegotiationManager.class);
        ViewResolver resolver = webAppConfig.contentNegotiatingViewResolver(manager);
        assertNotNull(resolver);
        assertTrue(resolver instanceof ContentNegotiatingViewResolver);
    }

    @Test
    void contentNegotiatingViewResolver_shouldHaveViewResolvers() {
        ContentNegotiationManager manager = mock(ContentNegotiationManager.class);
        ContentNegotiatingViewResolver resolver = 
            (ContentNegotiatingViewResolver) webAppConfig.contentNegotiatingViewResolver(manager);
        assertNotNull(resolver.getViewResolvers());
        assertFalse(resolver.getViewResolvers().isEmpty());
    }

    @Test
    void contentNegotiatingViewResolver_shouldHaveContentNegotiationManager() {
        ContentNegotiationManager manager = mock(ContentNegotiationManager.class);
        ContentNegotiatingViewResolver resolver = 
            (ContentNegotiatingViewResolver) webAppConfig.contentNegotiatingViewResolver(manager);
        assertNotNull(resolver.getContentNegotiationManager());
    }

    @Test
    void allViewResolvers_shouldBeAvailableInContext() {
        assertNotNull(applicationContext.getBean("excelViewResolver", ViewResolver.class));
        assertNotNull(applicationContext.getBean("csvViewResolver", ViewResolver.class));
        assertNotNull(applicationContext.getBean("pdfViewResolver", ViewResolver.class));
        assertNotNull(applicationContext.getBean("viewResolver", ViewResolver.class));
    }

    @Test
    void templateEngine_shouldBeAvailableInContext() {
        assertNotNull(applicationContext.getBean(SpringTemplateEngine.class));
    }

    @Test
    void templateResolver_shouldBeAvailableInContext() {
        assertNotNull(applicationContext.getBean(ClassLoaderTemplateResolver.class));
    }
}
