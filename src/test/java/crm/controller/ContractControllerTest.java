package crm.controller;

import crm.entity.*;
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
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

    private Contract contract;
    private Customer customer;
    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(role);

        customer = Customer.builder()
                .id(1L)
                .name("TestCo")
                .email("test@example.com")
                .enabled(1)
                .build();

        contract = Contract.builder()
                .id(1L)
                .name("Contract-001")
                .value(new BigDecimal("5000.00"))
                .beginDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .status(Status.PROPOSED)
                .customer(customer)
                .user(user)
                .build();
    }

    @Test
    void testConstructor() {
        ContractController controller = new ContractController(contractService, customerService, userService);
        assertNotNull(controller);
    }

    @Test
    void testShowAllContracts() {
        when(contractService.listAllContracts()).thenReturn(Arrays.asList(contract));
        String view = contractController.showAllContracts(model);
        assertEquals("contract/list", view);
        verify(model).addAttribute(eq("contracts"), any());
    }

    @Test
    void testShowFormAddContract() {
        when(customerService.findAllByEnabledTrue()).thenReturn(Arrays.asList(customer));
        when(userService.listAllUsers()).thenReturn(Arrays.asList(user));
        String view = contractController.showFormAddContract(model);
        assertEquals("contract/add", view);
    }

    @Test
    void testProcessRequestAddContract_withErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = contractController.processRequestAddContract(contract, bindingResult);
        assertEquals("redirect:/contract/add", view);
        verify(contractService, never()).saveContract(any());
    }

    @Test
    void testProcessRequestAddContract_withoutErrors() {
        when(bindingResult.hasErrors()).thenReturn(false);
        String view = contractController.processRequestAddContract(contract, bindingResult);
        assertEquals("contract/success", view);
        verify(contractService).saveContract(contract);
    }

    @Test
    void testShowFormEditContract() {
        when(contractService.showContract(1L)).thenReturn(contract);
        String view = contractController.showFormEditContract(model, 1L);
        assertEquals("contract/edit", view);
        verify(model).addAttribute(eq("contract"), eq(contract));
    }

    @Test
    void testProcessRequestEditContract_withErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = contractController.processRequestEditContract(1L, contract, bindingResult);
        assertEquals("redirect:/contract/edit/1", view);
        verify(contractService, never()).saveContract(any());
    }

    @Test
    void testProcessRequestEditContract_withoutErrors() {
        when(bindingResult.hasErrors()).thenReturn(false);
        String view = contractController.processRequestEditContract(1L, contract, bindingResult);
        assertEquals("redirect:/contract/list", view);
        verify(contractService).saveContract(contract);
    }

    @Test
    void testShowNameSearchForm() {
        String view = contractController.showNameSearchForm(model);
        assertEquals("contract/name-search", view);
    }

    @Test
    void testProcessRequestNameSearch() {
        when(contractService.findByName("Contract-001")).thenReturn(contract);
        String view = contractController.processRequestNameSearch(contract, model);
        assertEquals("contract/show-one", view);
    }

    @Test
    void testShowValueLessThanEqualSearchForm() {
        String view = contractController.showValueLeesThanEqualSearchForm(model);
        assertEquals("contract/value-le-search", view);
    }

    @Test
    void testProcessRequestValueLessThanEqualSearch() {
        when(contractService.findAllByValueLessThanEqual(any())).thenReturn(Arrays.asList(contract));
        String view = contractController.processRequestValueLessThanEqualSearch(contract, model);
        assertEquals("contract/show-list", view);
    }

    @Test
    void testShowValueGreaterThanEqualSearchForm() {
        String view = contractController.showValueGreaterThanEqualSearchForm(model);
        assertEquals("contract/value-ge-search", view);
    }

    @Test
    void testProcessRequestValueGreaterThanEqualSearch() {
        when(contractService.findAllByValueGreaterThanEqual(any())).thenReturn(Arrays.asList(contract));
        String view = contractController.processRequestValueGreaterThanEqualSearch(contract, model);
        assertEquals("contract/show-list", view);
    }

    @Test
    void testShowBeginDateSearchForm() {
        String view = contractController.showBeginDateSearchForm(model);
        assertEquals("contract/begin-date-search", view);
    }

    @Test
    void testProcessRequestBeginDateSearch() {
        when(contractService.findAllByBeginDate(any())).thenReturn(Arrays.asList(contract));
        String view = contractController.processRequestBeginDateSearch(contract, model);
        assertEquals("contract/show-list", view);
    }

    @Test
    void testShowBeginDateBeforeSearchForm() {
        String view = contractController.showBeginDateBeforeSearchForm(model);
        assertEquals("contract/begin-date-before-search", view);
    }

    @Test
    void testProcessRequestBeginDateBeforeSearch() {
        when(contractService.findAllByBeginDateBefore(any())).thenReturn(Arrays.asList(contract));
        String view = contractController.processRequestBeginDateBeforeSearch(contract, model);
        assertEquals("contract/show-list", view);
    }

    @Test
    void testShowBeginDateAfterSearchForm() {
        String view = contractController.showBeginDateAfterSearchForm(model);
        assertEquals("contract/begin-date-after-search", view);
    }

    @Test
    void testProcessRequestBeginDateAfterSearch() {
        when(contractService.findAllByBeginDateAfter(any())).thenReturn(Arrays.asList(contract));
        String view = contractController.processRequestBeginDateAfterSearch(contract, model);
        assertEquals("contract/show-list", view);
    }

    @Test
    void testShowEndDateSearchForm() {
        String view = contractController.showEndDateSearchForm(model);
        assertEquals("contract/end-date-search", view);
    }

    @Test
    void testProcessRequestEndDateSearch() {
        when(contractService.findAllByEndDate(any())).thenReturn(Arrays.asList(contract));
        String view = contractController.processRequestEndDateSearch(contract, model);
        assertEquals("contract/show-list", view);
    }

    @Test
    void testShowEndDateBeforeSearchForm() {
        String view = contractController.showEndDateBeforeSearchForm(model);
        assertEquals("contract/end-date-before-search", view);
    }

    @Test
    void testProcessRequestEndDateBeforeSearch() {
        when(contractService.findAllByEndDateBefore(any())).thenReturn(Arrays.asList(contract));
        String view = contractController.processRequestEndDateBeforeSearch(contract, model);
        assertEquals("contract/show-list", view);
    }

    @Test
    void testShowEndDateAfterSearchForm() {
        String view = contractController.showEndDateAfterSearchForm(model);
        assertEquals("contract/end-date-after-search", view);
    }

    @Test
    void testProcessRequestEndDateAfterSearch() {
        when(contractService.findAllByEndDateAfter(any())).thenReturn(Arrays.asList(contract));
        String view = contractController.processRequestEndDateAfterSearch(contract, model);
        assertEquals("contract/show-list", view);
    }

    @Test
    void testShowStatusSearchForm() {
        String view = contractController.showStatusSearchForm(model);
        assertEquals("contract/status-search", view);
    }

    @Test
    void testProcessRequestStatusSearch() {
        when(contractService.findAllByStatus(Status.PROPOSED)).thenReturn(Arrays.asList(contract));
        String view = contractController.processRequestStatusSearch(contract, model);
        assertEquals("contract/show-list", view);
    }

    @Test
    void testShowCustomerSearchForm() {
        when(customerService.findAllByEnabledTrue()).thenReturn(Arrays.asList(customer));
        String view = contractController.showCustomerSearchForm(model);
        assertEquals("contract/customer-search", view);
    }

    @Test
    void testProcessRequestCustomerSearch() {
        when(contractService.findAllByCustomer(any())).thenReturn(Arrays.asList(contract));
        String view = contractController.processRequestCustomerSearch(contract, model);
        assertEquals("contract/show-list", view);
    }

    @Test
    void testShowCustomerUserSearchForm() {
        when(customerService.findAllByEnabledTrue()).thenReturn(Arrays.asList(customer));
        when(userService.listAllUsers()).thenReturn(Arrays.asList(user));
        String view = contractController.showCustomerUserSearchForm(model);
        assertEquals("contract/customer-user-search", view);
    }

    @Test
    void testProcessRequestCustomerUserSearch() {
        when(contractService.findAllByCustomerAndUser(any(), any())).thenReturn(Arrays.asList(contract));
        String view = contractController.processRequestCustomerUserSearch(contract, model);
        assertEquals("contract/show-list", view);
    }

    @Test
    void testShowUserSearchForm() {
        when(userService.listAllUsers()).thenReturn(Arrays.asList(user));
        String view = contractController.showUserSearchForm(model);
        assertEquals("contract/user-search", view);
    }

    @Test
    void testProcessRequestUserSearch() {
        when(contractService.findAllByUser(any())).thenReturn(Arrays.asList(contract));
        String view = contractController.processRequestUserSearch(contract, model);
        assertEquals("contract/show-list", view);
    }
}
