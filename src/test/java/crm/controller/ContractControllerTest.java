package crm.controller;

import crm.entity.Contract;
import crm.entity.Customer;
import crm.entity.Status;
import crm.entity.User;
import crm.service.ContractService;
import crm.service.CustomerService;
import crm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractControllerTest {

    @Mock
    private ContractService contractService;

    @Mock
    private CustomerService customerService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private ContractController contractController;

    private Contract testContract;
    private Customer testCustomer;
    private User testUser;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder().id(1L).name("Acme Corp").build();
        testUser = User.builder().id(1L).username("testuser").build();

        testContract = Contract.builder()
                .id(1L)
                .name("Service Contract 2024")
                .value(new BigDecimal("50000.00"))
                .beginDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .status(Status.PROPOSED)
                .customer(testCustomer)
                .user(testUser)
                .build();
    }

    @Test
    void testShowAllContracts() {
        when(contractService.listAllContracts()).thenReturn(Arrays.asList(testContract));

        String viewName = contractController.showAllContracts(model);

        assertEquals("contract/list", viewName);
        verify(model).addAttribute(any(), any());
    }

    @Test
    void testShowFormAddContract() {
        when(customerService.findAllByEnabledTrue()).thenReturn(Arrays.asList(testCustomer));
        when(userService.listAllUsers()).thenReturn(Arrays.asList(testUser));

        String viewName = contractController.showFormAddContract(model);

        assertEquals("contract/add", viewName);
        verify(model, atLeast(3)).addAttribute(any(), any());
    }

    @Test
    void testProcessRequestAddContractSuccess() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = contractController.processRequestAddContract(testContract, bindingResult);

        assertEquals("contract/success", viewName);
        verify(contractService).saveContract(testContract);
    }

    @Test
    void testProcessRequestAddContractWithErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = contractController.processRequestAddContract(testContract, bindingResult);

        assertEquals("redirect:/contract/add", viewName);
    }

    @Test
    void testShowNameSearchForm() {
        String viewName = contractController.showNameSearchForm(model);

        assertEquals("contract/name-search", viewName);
    }

    @Test
    void testProcessRequestNameSearch() {
        when(contractService.findByName("Service Contract 2024")).thenReturn(testContract);

        String viewName = contractController.processRequestNameSearch(testContract, model);

        assertEquals("contract/show-one", viewName);
    }
}
