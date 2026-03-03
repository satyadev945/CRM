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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    private List<Contract> testContracts;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .id(1L)
                .name("Test Company")
                .email("test@example.com")
                .enabled(1)
                .build();

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("user@example.com")
                .enabled(1)
                .build();

        testContract = Contract.builder()
                .id(1L)
                .name("Test Contract")
                .content("Test Content")
                .value(new BigDecimal("10000.00"))
                .beginDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(6))
                .status(Status.PROPOSED)
                .customer(testCustomer)
                .user(testUser)
                .build();

        testContracts = Arrays.asList(testContract);
    }

    @Test
    void showAllContracts_shouldReturnContractListView() {
        when(contractService.listAllContracts()).thenReturn(testContracts);

        String viewName = contractController.showAllContracts(model);

        assertEquals("contract/list", viewName);
        verify(model).addAttribute("contracts", testContracts);
        verify(contractService).listAllContracts();
    }

    @Test
    void showFormAddContract_shouldReturnAddView() {
        when(customerService.findAllByEnabledTrue()).thenReturn(Arrays.asList(testCustomer));
        when(userService.listAllUsers()).thenReturn(Arrays.asList(testUser));

        String viewName = contractController.showFormAddContract(model);

        assertEquals("contract/add", viewName);
        verify(model).addAttribute(eq("contract"), any(Contract.class));
        verify(model).addAttribute(eq("customers"), anyIterable());
        verify(model).addAttribute(eq("users"), anyIterable());
    }

    @Test
    void processRequestAddContract_withValidContract_shouldReturnSuccessView() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = contractController.processRequestAddContract(testContract, bindingResult);

        assertEquals("contract/success", viewName);
        verify(contractService).saveContract(testContract);
    }

    @Test
    void processRequestAddContract_withErrors_shouldRedirectToAddForm() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = contractController.processRequestAddContract(testContract, bindingResult);

        assertEquals("redirect:/contract/add", viewName);
        verify(contractService, never()).saveContract(any());
    }

    @Test
    void showFormEditContract_shouldReturnEditView() {
        Long contractId = 1L;
        when(contractService.showContract(contractId)).thenReturn(testContract);

        String viewName = contractController.showFormEditContract(model, contractId);

        assertEquals("contract/edit", viewName);
        verify(model).addAttribute("contract", testContract);
        verify(contractService).showContract(contractId);
    }

    @Test
    void processRequestEditContract_withValidContract_shouldRedirectToList() {
        Long contractId = 1L;
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = contractController.processRequestEditContract(contractId, testContract, bindingResult);

        assertEquals("redirect:/contract/list", viewName);
        verify(contractService).saveContract(testContract);
    }

    @Test
    void processRequestEditContract_withErrors_shouldRedirectToEditForm() {
        Long contractId = 1L;
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = contractController.processRequestEditContract(contractId, testContract, bindingResult);

        assertEquals("redirect:/contract/edit/" + contractId, viewName);
        verify(contractService, never()).saveContract(any());
    }

    @Test
    void showNameSearchForm_shouldReturnNameSearchView() {
        String viewName = contractController.showNameSearchForm(model);

        assertEquals("contract/name-search", viewName);
        verify(model).addAttribute(eq("contract"), any(Contract.class));
    }

    @Test
    void processRequestNameSearch_shouldReturnShowOneView() {
        when(contractService.findByName(testContract.getName())).thenReturn(testContract);

        String viewName = contractController.processRequestNameSearch(testContract, model);

        assertEquals("contract/show-one", viewName);
        verify(model).addAttribute("contract", testContract);
        verify(contractService).findByName(testContract.getName());
    }

    @Test
    void showValueLeesThanEqualSearchForm_shouldReturnValueLeSearchView() {
        String viewName = contractController.showValueLeesThanEqualSearchForm(model);

        assertEquals("contract/value-le-search", viewName);
        verify(model).addAttribute(eq("contract"), any(Contract.class));
    }

    @Test
    void processRequestValueLessThanEqualSearch_shouldReturnShowListView() {
        when(contractService.findAllByValueLessThanEqual(testContract.getValue())).thenReturn(testContracts);

        String viewName = contractController.processRequestValueLessThanEqualSearch(testContract, model);

        assertEquals("contract/show-list", viewName);
        verify(model).addAttribute("contracts", testContracts);
    }

    @Test
    void showValueGreaterThanEqualSearchForm_shouldReturnValueGeSearchView() {
        String viewName = contractController.showValueGreaterThanEqualSearchForm(model);

        assertEquals("contract/value-ge-search", viewName);
        verify(model).addAttribute(eq("contract"), any(Contract.class));
    }

    @Test
    void processRequestValueGreaterThanEqualSearch_shouldReturnShowListView() {
        when(contractService.findAllByValueGreaterThanEqual(testContract.getValue())).thenReturn(testContracts);

        String viewName = contractController.processRequestValueGreaterThanEqualSearch(testContract, model);

        assertEquals("contract/show-list", viewName);
        verify(model).addAttribute("contracts", testContracts);
    }

    @Test
    void showBeginDateSearchForm_shouldReturnBeginDateSearchView() {
        String viewName = contractController.showBeginDateSearchForm(model);

        assertEquals("contract/begin-date-search", viewName);
        verify(model).addAttribute(eq("contract"), any(Contract.class));
    }

    @Test
    void processRequestBeginDateSearch_shouldReturnShowListView() {
        when(contractService.findAllByBeginDate(testContract.getBeginDate())).thenReturn(testContracts);

        String viewName = contractController.processRequestBeginDateSearch(testContract, model);

        assertEquals("contract/show-list", viewName);
        verify(model).addAttribute("contracts", testContracts);
    }

    @Test
    void showStatusSearchForm_shouldReturnStatusSearchView() {
        String viewName = contractController.showStatusSearchForm(model);

        assertEquals("contract/status-search", viewName);
        verify(model).addAttribute(eq("contract"), any(Contract.class));
    }

    @Test
    void processRequestStatusSearch_shouldReturnShowListView() {
        when(contractService.findAllByStatus(testContract.getStatus())).thenReturn(testContracts);

        String viewName = contractController.processRequestStatusSearch(testContract, model);

        assertEquals("contract/show-list", viewName);
        verify(model).addAttribute("contracts", testContracts);
    }

    @Test
    void showCustomerSearchForm_shouldReturnCustomerSearchView() {
        when(customerService.findAllByEnabledTrue()).thenReturn(Arrays.asList(testCustomer));

        String viewName = contractController.showCustomerSearchForm(model);

        assertEquals("contract/customer-search", viewName);
        verify(model).addAttribute(eq("contract"), any(Contract.class));
        verify(model).addAttribute(eq("customers"), anyIterable());
    }

    @Test
    void processRequestCustomerSearch_shouldReturnShowListView() {
        when(contractService.findAllByCustomer(testContract.getCustomer())).thenReturn(testContracts);

        String viewName = contractController.processRequestCustomerSearch(testContract, model);

        assertEquals("contract/show-list", viewName);
        verify(model).addAttribute("contracts", testContracts);
    }

    @Test
    void showUserSearchForm_shouldReturnUserSearchView() {
        when(userService.listAllUsers()).thenReturn(Arrays.asList(testUser));

        String viewName = contractController.showUserSearchForm(model);

        assertEquals("contract/user-search", viewName);
        verify(model).addAttribute(eq("contract"), any(Contract.class));
        verify(model).addAttribute(eq("users"), anyIterable());
    }

    @Test
    void processRequestUserSearch_shouldReturnShowListView() {
        when(contractService.findAllByUser(testContract.getUser())).thenReturn(testContracts);

        String viewName = contractController.processRequestUserSearch(testContract, model);

        assertEquals("contract/show-list", viewName);
        verify(model).addAttribute("contracts", testContracts);
    }
}
