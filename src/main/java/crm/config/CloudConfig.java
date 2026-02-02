package crm.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import javax.sql.DataSource;

/**
 * Cloud-specific configuration for AWS deployments.
 */
@Configuration
@Profile({"cloud", "aws"})
@Slf4j
public class CloudConfig {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    /**
     * Configure a HikariCP datasource for cloud RDS connections
     * Only used if no other DataSource is explicitly configured
     */
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.type", havingValue = "com.zaxxer.hikari.HikariDataSource", matchIfMissing = true)
    public DataSource dataSource() {
        log.info("Configuring HikariCP DataSource for cloud environment");

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(datasourceUrl);
        dataSource.setUsername(datasourceUsername);
        dataSource.setPassword(datasourcePassword);

        // Cloud-optimized connection pool settings
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(5);
        dataSource.setIdleTimeout(300000); // 5 minutes
        dataSource.setMaxLifetime(1200000); // 20 minutes
        dataSource.setConnectionTimeout(20000); // 20 seconds
        dataSource.setPoolName("CloudHikariPool");

        // Enable JMX monitoring
        dataSource.setRegisterMbeans(true);

        // Connection health testing
        dataSource.setConnectionTestQuery("SELECT 1");
        dataSource.setValidationTimeout(5000); // 5 seconds

        return dataSource;
    }
}