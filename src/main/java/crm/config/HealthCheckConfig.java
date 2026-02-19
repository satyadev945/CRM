package crm.config;

import crm.service.S3StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Custom health checks for cloud deployment
 * Provides detailed health status for monitoring systems
 */
@Configuration
@Slf4j
public class HealthCheckConfig {

    /**
     * S3 Storage Health Indicator
     * Checks if S3 service is accessible
     */
    @Component("s3Storage")
    public static class S3StorageHealthIndicator implements HealthIndicator {

        @Autowired
        private S3StorageService s3StorageService;

        @Override
        public Health health() {
            try {
                boolean isAvailable = s3StorageService.isAvailable();
                
                if (isAvailable) {
                    return Health.up()
                            .withDetail("s3", "S3 storage is accessible")
                            .build();
                } else {
                    return Health.down()
                            .withDetail("s3", "S3 storage is not accessible")
                            .build();
                }
            } catch (Exception e) {
                log.error("S3 health check failed", e);
                return Health.down()
                        .withDetail("s3", "S3 health check failed")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        }
    }

    /**
     * Application Health Indicator
     * Provides custom application health status
     */
    @Component("application")
    public static class ApplicationHealthIndicator implements HealthIndicator {

        @Override
        public Health health() {
            // Add custom application health checks here
            // For example: check critical services, cache, etc.
            
            return Health.up()
                    .withDetail("app", "Application is running")
                    .withDetail("version", "0.0.1-SNAPSHOT")
                    .build();
        }
    }
}
