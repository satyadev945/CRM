package crm.controller;

import crm.dto.StatusResponse;
import crm.service.StatusCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for status checking endpoints.
 * Provides public API endpoint to check the status of other API endpoints.
 */
@RestController
public class StatusController {

    private static final Logger logger = LoggerFactory.getLogger(StatusController.class);

    private final StatusCheckService statusCheckService;

    /**
     * Constructor with StatusCheckService dependency injection.
     * 
     * @param statusCheckService the service for checking API status
     */
    public StatusController(StatusCheckService statusCheckService) {
        this.statusCheckService = statusCheckService;
    }

    /**
     * GET /getStatus
     * 
     * Checks the status of the /api/book endpoint and returns a structured response.
     * This endpoint is publicly accessible without authentication.
     * 
     * @return StatusResponse containing status message and timestamp
     */
    @GetMapping("/getStatus")
    public StatusResponse getStatus() {
        logger.info("Received request to /getStatus endpoint");
        StatusResponse response = statusCheckService.checkApiStatus();
        logger.info("Returning status response: message='{}', timestamp='{}'", 
                    response.getMessage(), response.getTimestamp());
        return response;
    }
}
