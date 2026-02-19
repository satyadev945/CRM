package crm.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

/**
 * Health check configuration for cloud deployment
 * Provides custom health indicators for monitoring
 */
@Configuration
@Slf4j
public class HealthCheckConfig {

    /**
     * Custom health indicator for application readiness
     * Used by cloud load balancers and orchestrators
     */
    @Bean
    public HealthIndicator customHealthIndicator() {
        return () -> {
            try {
                // Add custom health checks here
                // For example: check database connectivity, external services, etc.
                return Health.up()
                        .withDetail("application", "minicompcrm")
                        .withDetail("status", "running")
                        .build();
            } catch (Exception e) {
                log.error("Health check failed", e);
                return Health.down()
                        .withDetail("error", e.getMessage())
                        .build();
            }
        };
    }
}
