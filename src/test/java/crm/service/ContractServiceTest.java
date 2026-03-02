package crm.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContractServiceTest {

    @Test
    void contractService_interfaceExists() {
        assertNotNull(ContractService.class);
    }

    @Test
    void contractService_isInterface() {
        assertTrue(ContractService.class.isInterface());
    }

    @Test
    void contractService_hasListAllContractsMethod() throws NoSuchMethodException {
        assertNotNull(ContractService.class.getMethod("listAllContracts"));
    }

    @Test
    void contractService_hasShowContractMethod() throws NoSuchMethodException {
        assertNotNull(ContractService.class.getMethod("showContract", Long.class));
    }

    @Test
    void contractService_hasSaveContractMethod() throws NoSuchMethodException {
        assertNotNull(ContractService.class.getMethod("saveContract", crm.entity.Contract.class));
    }
}
