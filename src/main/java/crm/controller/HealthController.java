package crm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller providing custom health check endpoints.
 * These complement the standard Spring Boot Actuator endpoints.
 */
@RestController
@Slf4j
public class HealthController {

    private final JdbcTemplate jdbcTemplate;
    private final HealthIndicator dbHealthIndicator;

    @Autowired
    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.dbHealthIndicator = () -> {
            try {
                // Simple database connectivity check
                int result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                if (result == 1) {
                    return Health.up().withDetail("message", "Database connection is healthy").build();
                }
                return Health.down().withDetail("message", "Database connectivity test failed").build();
            } catch (Exception e) {
                log.error("Database health check failed: {}", e.getMessage(), e);
                return Health.down()
                        .withDetail("message", "Database connectivity error")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        };
    }

    /**
     * Basic health check endpoint
     * This endpoint can be used by load balancers and monitoring systems
     */
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return healthInfo;
    }

    /**
     * Detailed health check with component status
     */
    @GetMapping("/health/detail")
    public Map<String, Object> detailedHealthCheck() {
        Map<String, Object> healthInfo = new HashMap<>();

        // Check database connectivity
        Health dbHealth = dbHealthIndicator.health();
        boolean isHealthy = dbHealth.getStatus() == Status.UP;

        healthInfo.put("status", isHealthy ? "UP" : "DOWN");
        healthInfo.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        healthInfo.put("components", Map.of(
            "database", Map.of(
                "status", dbHealth.getStatus().toString(),
                "details", dbHealth.getDetails()
            )
        ));

        return healthInfo;
    }
}