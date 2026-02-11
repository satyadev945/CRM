package crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for CustomerRepository interface
 */
class CustomerRepositoryTest {

    @Test
    void testCustomerRepositoryInterface() {
        // Verify interface exists and extends JpaRepository
        assertTrue(CustomerRepository.class.isInterface());
        assertTrue(JpaRepository.class.isAssignableFrom(CustomerRepository.class));
    }

    @Test
    void testCustomerRepositoryMethods() {
        try {
            CustomerRepository.class.getMethod("getMaxId");
            CustomerRepository.class.getMethod("findAllByEnabled", int.class);
            CustomerRepository.class.getMethod("findOneByEnabledAndName", int.class, String.class);
            CustomerRepository.class.getMethod("findOneByName", String.class);
            CustomerRepository.class.getMethod("findByEmail", String.class);
            CustomerRepository.class.getMethod("findByPhone", int.class);
            CustomerRepository.class.getMethod("findByCity", String.class);
            CustomerRepository.class.getMethod("findByFirstName", String.class);
            CustomerRepository.class.getMethod("findByLastName", String.class);
        } catch (NoSuchMethodException e) {
            fail("CustomerRepository interface missing expected methods: " + e.getMessage());
        }
    }

    @Test
    void testRepositoryAnnotation() {
        assertTrue(CustomerRepository.class.isAnnotationPresent(org.springframework.stereotype.Repository.class));
    }
}
