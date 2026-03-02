package crm.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTest {

    @Test
    void customerService_interfaceExists() {
        assertNotNull(CustomerService.class);
    }

    @Test
    void customerService_isInterface() {
        assertTrue(CustomerService.class.isInterface());
    }

    @Test
    void customerService_hasListAllCustomersMethod() throws NoSuchMethodException {
        assertNotNull(CustomerService.class.getMethod("listAllCustomers"));
    }

    @Test
    void customerService_hasShowCustomerMethod() throws NoSuchMethodException {
        assertNotNull(CustomerService.class.getMethod("showCustomer", Long.class));
    }

    @Test
    void customerService_hasSaveCustomerMethod() throws NoSuchMethodException {
        assertNotNull(CustomerService.class.getMethod("saveCustomer", crm.entity.Customer.class));
    }

    @Test
    void customerService_hasGetMaxIdMethod() throws NoSuchMethodException {
        assertNotNull(CustomerService.class.getMethod("getMaxId"));
    }
}
