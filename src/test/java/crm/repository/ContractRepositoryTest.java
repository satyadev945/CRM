package crm.repository;

import crm.entity.Contract;
import crm.entity.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ContractRepositoryTest {

    @Autowired
    private ContractRepository contractRepository;

    @Test
    void contractRepository_shouldNotBeNull() {
        assertNotNull(contractRepository);
    }

    @Test
    void save_shouldPersistContract() {
        Contract contract = new Contract();
        contract.setName("Test Contract");
        contract.setValue(new BigDecimal("10000.00"));
        contract.setBeginDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().plusMonths(6));
        contract.setStatus(Status.PROPOSED);

        Contract saved = contractRepository.save(contract);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Test Contract", saved.getName());
    }

    @Test
    void findByName_withExistingName_shouldReturnContract() {
        Contract contract = new Contract();
        contract.setName("Unique Contract");
        contract.setValue(new BigDecimal("5000.00"));
        contract.setStatus(Status.ACTIVE);
        contractRepository.save(contract);

        Contract found = contractRepository.findByName("Unique Contract");

        assertNotNull(found);
        assertEquals("Unique Contract", found.getName());
    }

    @Test
    void findAllByValueLessThanEqual_shouldReturnContracts() {
        Contract contract = new Contract();
        contract.setName("Low Value Contract");
        contract.setValue(new BigDecimal("1000.00"));
        contract.setStatus(Status.PROPOSED);
        contractRepository.save(contract);

        Iterable<Contract> found = contractRepository.findAllByValueLessThanEqual(new BigDecimal("2000.00"));

        assertNotNull(found);
        assertTrue(found.iterator().hasNext());
    }

    @Test
    void findAllByValueGreaterThanEqual_shouldReturnContracts() {
        Contract contract = new Contract();
        contract.setName("High Value Contract");
        contract.setValue(new BigDecimal("50000.00"));
        contract.setStatus(Status.ACTIVE);
        contractRepository.save(contract);

        Iterable<Contract> found = contractRepository.findAllByValueGreaterThanEqual(new BigDecimal("40000.00"));

        assertNotNull(found);
        assertTrue(found.iterator().hasNext());
    }

    @Test
    void findAllByBeginDate_shouldReturnContracts() {
        LocalDate today = LocalDate.now();
        Contract contract = new Contract();
        contract.setName("Today Contract");
        contract.setBeginDate(today);
        contract.setStatus(Status.PROPOSED);
        contractRepository.save(contract);

        Iterable<Contract> found = contractRepository.findAllByBeginDate(today);

        assertNotNull(found);
        assertTrue(found.iterator().hasNext());
    }

    @Test
    void findAllByStatus_shouldReturnContracts() {
        Contract contract = new Contract();
        contract.setName("Active Contract");
        contract.setStatus(Status.ACTIVE);
        contractRepository.save(contract);

        Iterable<Contract> found = contractRepository.findAllByStatus(Status.ACTIVE);

        assertNotNull(found);
        assertTrue(found.iterator().hasNext());
    }

    @Test
    void delete_shouldRemoveContract() {
        Contract contract = new Contract();
        contract.setName("Delete Contract");
        contract.setStatus(Status.PROPOSED);
        Contract saved = contractRepository.save(contract);

        contractRepository.delete(saved);

        assertFalse(contractRepository.findById(saved.getId()).isPresent());
    }
}
