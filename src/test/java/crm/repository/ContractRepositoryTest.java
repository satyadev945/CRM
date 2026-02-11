package crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for ContractRepository interface
 */
class ContractRepositoryTest {

    @Test
    void testContractRepositoryInterface() {
        // Verify interface exists and extends JpaRepository
        assertTrue(ContractRepository.class.isInterface());
        assertTrue(JpaRepository.class.isAssignableFrom(ContractRepository.class));
    }

    @Test
    void testContractRepositoryMethods() {
        try {
            ContractRepository.class.getMethod("findByName", String.class);
        } catch (NoSuchMethodException e) {
            fail("ContractRepository interface missing expected methods");
        }
    }

    @Test
    void testRepositoryAnnotation() {
        assertTrue(ContractRepository.class.isAnnotationPresent(org.springframework.stereotype.Repository.class));
    }
}
