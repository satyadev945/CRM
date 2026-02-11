package crm.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for CustomerService interface
 */
class CustomerServiceTest {

    @Test
    void testCustomerServiceInterface() {
        // Verify interface exists
        assertTrue(CustomerService.class.isInterface());

        try {
            CustomerService.class.getMethod("listAllCustomers");
            CustomerService.class.getMethod("showCustomer", Long.class);
            CustomerService.class.getMethod("findAllByEnabledTrue");
            CustomerService.class.getMethod("findAllByEnabledFalse");
            CustomerService.class.getMethod("getMaxId");
        } catch (NoSuchMethodException e) {
            fail("CustomerService interface missing expected methods");
        }
    }
}
