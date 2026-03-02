package crm.service;

import crm.entity.Contract;
import crm.entity.Status;
import crm.repository.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceImplTest {

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private ContractServiceImpl contractService;

    private Contract testContract;

    @BeforeEach
    void setUp() {
        testContract = new Contract();
        testContract.setId(1L);
        testContract.setName("Test Contract");
        testContract.setValue(new BigDecimal("10000.00"));
        testContract.setStatus(Status.ACTIVE);
    }

    @Test
    void listAllContracts_shouldReturnAllContracts() {
        when(contractRepository.findAll()).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> contracts = contractService.listAllContracts();

        assertNotNull(contracts);
        verify(contractRepository, times(1)).findAll();
    }

    @Test
    void showContract_shouldReturnContract() {
        when(contractRepository.findById(1L)).thenReturn(Optional.of(testContract));

        Contract found = contractService.showContract(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        verify(contractRepository, times(1)).findById(1L);
    }

    @Test
    void findByName_shouldReturnContract() {
        when(contractRepository.findByName("Test Contract")).thenReturn(testContract);

        Contract found = contractService.findByName("Test Contract");

        assertNotNull(found);
        assertEquals("Test Contract", found.getName());
        verify(contractRepository, times(1)).findByName("Test Contract");
    }

    @Test
    void saveContract_shouldCallRepository() {
        contractService.saveContract(testContract);

        verify(contractRepository, times(1)).save(testContract);
    }

    @Test
    void findAllByStatus_shouldReturnContracts() {
        when(contractRepository.findAllByStatus(Status.ACTIVE)).thenReturn(Arrays.asList(testContract));

        Iterable<Contract> contracts = contractService.findAllByStatus(Status.ACTIVE);

        assertNotNull(contracts);
        verify(contractRepository, times(1)).findAllByStatus(Status.ACTIVE);
    }

    @Test
    void contractService_shouldImplementContractService() {
        assertTrue(contractService instanceof ContractService);
    }
}
