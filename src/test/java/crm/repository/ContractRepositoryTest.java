package crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ContractRepositoryTest {

    @Test
    void contractRepository_shouldExtendJpaRepository() {
        // Assert
        assertTrue(JpaRepository.class.isAssignableFrom(ContractRepository.class));
    }

    @Test
    void contractRepository_shouldHaveRepositoryAnnotation() {
        // Assert
        assertTrue(ContractRepository.class.isAnnotationPresent(org.springframework.stereotype.Repository.class));
    }

    @Test
    void contractRepository_shouldHaveFindByNameMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(ContractRepository.class.getMethod("findByName", String.class));
    }

    @Test
    void contractRepository_shouldHaveFindAllByValueLessThanEqualMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(ContractRepository.class.getMethod("findAllByValueLessThanEqual", BigDecimal.class));
    }

    @Test
    void contractRepository_shouldHaveFindAllByValueGreaterThanEqualMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(ContractRepository.class.getMethod("findAllByValueGreaterThanEqual", BigDecimal.class));
    }

    @Test
    void contractRepository_shouldHaveFindAllByBeginDateMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(ContractRepository.class.getMethod("findAllByBeginDate", LocalDate.class));
    }

    @Test
    void contractRepository_shouldHaveFindAllByEndDateMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(ContractRepository.class.getMethod("findAllByEndDate", LocalDate.class));
    }
}
