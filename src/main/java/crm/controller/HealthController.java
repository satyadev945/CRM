package crm.controller;

import crm.dto.HealthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * REST Controller for health check endpoint
 * 
 * This controller provides a simple health check endpoint that returns JSON responses
 * indicating the application's health status. Unlike other controllers in this application
 * that use @Controller and return Thymeleaf views, this uses @RestController to return
 * JSON data directly.
 */
@RestController
@RequestMapping("/apii")
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    /**
     * Health check endpoint
     * 
     * GET /apii/health
     * 
     * This endpoint is publicly accessible (no authentication required) and returns
     * a JSON response with the application's health status and current timestamp.
     * 
     * @return HealthResponse object containing message and timestamp
     */
    @GetMapping("/health")
    public HealthResponse checkHealth() {
        logger.info("Health check endpoint accessed");
        
        String message = "Application is healthy";
        String timestamp = Instant.now().toString();
        
        HealthResponse response = new HealthResponse(message, timestamp);
        
        logger.debug("Health check response: message={}, timestamp={}", message, timestamp);
        
        return response;
    }
}
