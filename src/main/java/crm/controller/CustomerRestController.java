package crm.controller;

import crm.entity.Customer;
import crm.service.CustomerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for customer-related API endpoints
 * Returns JSON responses for programmatic access
 */
@RestController
@RequestMapping("/api")
public class CustomerRestController {

    private CustomerService customerService;

    public CustomerRestController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * GET /api/customers
     * <p>
     * Retrieves all customer records as JSON array
     *
     * @return List of all customers (empty array if no customers exist)
     */
    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return (List<Customer>) customerService.listAllCustomers();
    }

}
