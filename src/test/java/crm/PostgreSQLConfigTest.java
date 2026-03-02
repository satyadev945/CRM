package crm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PostgreSQLConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void postgreSQLConfig_shouldBeLoadedAsBean() {
        PostgreSQLConfig config = applicationContext.getBean(PostgreSQLConfig.class);
        assertNotNull(config);
    }

    @Test
    void postgreSQLConfig_shouldHaveConfigurationAnnotation() {
        assertTrue(PostgreSQLConfig.class.isAnnotationPresent(Configuration.class));
    }

    @Test
    void postgreSQLConfig_shouldHaveEnableJpaRepositoriesAnnotation() {
        assertTrue(PostgreSQLConfig.class.isAnnotationPresent(EnableJpaRepositories.class));
    }

    @Test
    void postgreSQLConfig_shouldHaveEnableTransactionManagementAnnotation() {
        assertTrue(PostgreSQLConfig.class.isAnnotationPresent(EnableTransactionManagement.class));
    }

    @Test
    void enableJpaRepositories_shouldHaveCorrectBasePackages() {
        EnableJpaRepositories annotation = PostgreSQLConfig.class.getAnnotation(EnableJpaRepositories.class);
        assertNotNull(annotation);
        String[] basePackages = annotation.basePackages();
        assertEquals(1, basePackages.length);
        assertEquals("crm.repository", basePackages[0]);
    }

    @Test
    void postgreSQLConfig_shouldBeInstantiable() {
        assertDoesNotThrow(() -> new PostgreSQLConfig());
    }

    @Test
    void postgreSQLConfig_classNotNull() {
        assertNotNull(PostgreSQLConfig.class);
    }
}
