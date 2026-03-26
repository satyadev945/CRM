package crm.service;

import crm.dto.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementation of StatusCheckService.
 * Handles checking the status of the /api/book endpoint.
 */
@Service
public class StatusCheckServiceImpl implements StatusCheckService {

    private static final Logger logger = LoggerFactory.getLogger(StatusCheckServiceImpl.class);
    private static final String API_BOOK_URL = "http://localhost:8080/api/book";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final RestTemplate restTemplate;

    /**
     * Constructor with RestTemplate dependency injection.
     * 
     * @param restTemplate the RestTemplate instance for making HTTP calls
     */
    @Autowired
    public StatusCheckServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Checks the status of the /api/book endpoint.
     * Attempts to call the endpoint and returns appropriate status message.
     * 
     * @return StatusResponse containing the status message and current timestamp
     */
    @Override
    public StatusResponse checkApiStatus() {
        String message;
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);

        try {
            logger.info("Checking status of API endpoint: {}", API_BOOK_URL);
            
            // Attempt to call the /api/book endpoint
            ResponseEntity<String> response = restTemplate.getForEntity(API_BOOK_URL, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                message = "API /api/book is operational";
                logger.info("API endpoint {} is operational. Status code: {}", API_BOOK_URL, response.getStatusCode());
            } else {
                message = "API /api/book returned status: " + response.getStatusCode();
                logger.warn("API endpoint {} returned non-OK status: {}", API_BOOK_URL, response.getStatusCode());
            }
            
        } catch (Exception e) {
            message = "API /api/book is not responding";
            logger.error("Failed to connect to API endpoint {}: {}", API_BOOK_URL, e.getMessage());
        }

        return new StatusResponse(message, timestamp);
    }
}
