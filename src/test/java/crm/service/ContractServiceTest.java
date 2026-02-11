package crm.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for ContractService interface
 */
class ContractServiceTest {

    @Test
    void testContractServiceInterface() {
        // Verify interface exists
        assertTrue(ContractService.class.isInterface());

        try {
            ContractService.class.getMethod("listAllContracts");
            ContractService.class.getMethod("showContract", Long.class);
            ContractService.class.getMethod("findByName", String.class);
        } catch (NoSuchMethodException e) {
            fail("ContractService interface missing expected methods");
        }
    }
}
