package crm.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContractRepositoryTest {

    @Test
    void contractRepository_interfaceExists() {
        assertNotNull(ContractRepository.class);
    }

    @Test
    void contractRepository_hasFindByNameMethod() throws NoSuchMethodException {
        assertNotNull(ContractRepository.class.getMethod("findByName", String.class));
    }
}
