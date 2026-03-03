package crm.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRepositoryTest {

    @Test
    void customerRepository_interfaceExists() {
        assertNotNull(CustomerRepository.class);
    }

    @Test
    void customerRepository_hasGetMaxIdMethod() throws NoSuchMethodException {
        assertNotNull(CustomerRepository.class.getMethod("getMaxId"));
    }

    @Test
    void customerRepository_hasFindOneByNameMethod() throws NoSuchMethodException {
        assertNotNull(CustomerRepository.class.getMethod("findOneByName", String.class));
    }
}
