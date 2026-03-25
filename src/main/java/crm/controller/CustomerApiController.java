package crm.controller;

import crm.dto.CustomerApiResponse;
import crm.entity.Customer;
import crm.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * REST Controller for Customer API endpoints
 * 
 * This controller provides RESTful API endpoints for customer-related operations.
 * All endpoints return JSON responses and require authentication.
 */
@RestController
@RequestMapping("/api")
public class CustomerApiController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerApiController.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CustomerService customerService;

    /**
     * Constructor injection for CustomerService
     * 
     * @param customerService the customer service to inject
     */
    public CustomerApiController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * GET /api/customer
     * 
     * Fetches all customer details from the database
     * 
     * @return ResponseEntity containing CustomerApiResponse with customer data and HTTP status
     */
    @GetMapping("/customer")
    public ResponseEntity<CustomerApiResponse> getCustomers() {
        logger.info("Received request to fetch customer details via API");
        
        try {
            // Fetch all customers from the database
            Iterable<Customer> customers = customerService.listAllCustomers();
            
            // Count customers for the response message
            long customerCount = 0;
            for (Customer customer : customers) {
                customerCount++;
            }
            
            logger.info("Successfully retrieved {} customers from database", customerCount);
            
            // Create response with success message and current timestamp
            String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
            CustomerApiResponse response = new CustomerApiResponse(
                "Customers retrieved successfully. Total count: " + customerCount,
                timestamp
            );
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error occurred while fetching customer details: {}", e.getMessage(), e);
            
            // Create error response with timestamp
            String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
            CustomerApiResponse errorResponse = new CustomerApiResponse(
                "Error retrieving customers: " + e.getMessage(),
                timestamp
            );
            
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
