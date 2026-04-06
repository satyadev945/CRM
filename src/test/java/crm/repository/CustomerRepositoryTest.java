package crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRepositoryTest {

    @Test
    void customerRepository_shouldExtendJpaRepository() {
        // Assert
        assertTrue(JpaRepository.class.isAssignableFrom(CustomerRepository.class));
    }

    @Test
    void customerRepository_shouldHaveRepositoryAnnotation() {
        // Assert
        assertTrue(CustomerRepository.class.isAnnotationPresent(org.springframework.stereotype.Repository.class));
    }

    @Test
    void customerRepository_shouldHaveGetMaxIdMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(CustomerRepository.class.getMethod("getMaxId"));
    }

    @Test
    void customerRepository_shouldHaveFindAllByEnabledMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(CustomerRepository.class.getMethod("findAllByEnabled", int.class));
    }

    @Test
    void customerRepository_shouldHaveFindOneByNameMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(CustomerRepository.class.getMethod("findOneByName", String.class));
    }

    @Test
    void customerRepository_shouldHaveFindByEmailMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(CustomerRepository.class.getMethod("findByEmail", String.class));
    }

    @Test
    void customerRepository_shouldHaveFindByPhoneMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(CustomerRepository.class.getMethod("findByPhone", int.class));
    }
}
