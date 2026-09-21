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
import java.util.List;

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
        customer = Customer.builder().id(1L).name("Corp A").email("a@corp.com").enabled(1).build();

        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");

        user = User.builder().id(1L).username("johndoe").email("john@test.com")
                .firstName("John").lastName("Doe").enabled(1).role(role).build();

        contract = Contract.builder()
                .id(1L).name("Contract A").content("Content")
                .value(new BigDecimal("1000.00"))
                .beginDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .status(Status.PROPOSED)
                .customer(customer).user(user).build();
    }

    @Test
    void testShowAllContracts_ReturnsListView() {
        List<Contract> contracts = Collections.singletonList(contract);
        when(contractService.listAllContracts()).thenReturn(contracts);

        String view = contractController.showAllContracts(model);

        assertEquals("contract/list", view);
        verify(model).addAttribute("contracts", contracts);
    }

    @Test
    void testShowFormAddContract_ReturnsAddView() {
        List<Customer> customers = Collections.singletonList(customer);
        List<User> users = Collections.singletonList(user);
        when(customerService.findAllByEnabledTrue()).thenReturn(customers);
        when(userService.listAllUsers()).thenReturn(users);

        String view = contractController.showFormAddContract(model);

        assertEquals("contract/add", view);
        verify(model).addAttribute(eq("contract"), any(Contract.class));
        verify(model).addAttribute("customers", customers);
        verify(model).addAttribute("users", users);
    }

    @Test
    void testProcessRequestAddContract_NoErrors_ReturnsSuccessView() {
        when(bindingResult.hasErrors()).thenReturn(false);
        String view = contractController.processRequestAddContract(contract, bindingResult);
        assertEquals("contract/success", view);
        verify(contractService).saveContract(contract);
    }

    @Test
    void testProcessRequestAddContract_WithErrors_RedirectsToAdd() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = contractController.processRequestAddContract(contract, bindingResult);
        assertEquals("redirect:/contract/add", view);
        verify(contractService, never()).saveContract(any());
    }

    @Test
    void testShowFormEditContract_ReturnsEditView() {
        when(contractService.showContract(1L)).thenReturn(contract);
        String view = contractController.showFormEditContract(model, 1L);
        assertEquals("contract/edit", view);
        verify(model).addAttribute("contract", contract);
    }

    @Test
    void testProcessRequestEditContract_NoErrors_RedirectsToList() {
        when(bindingResult.hasErrors()).thenReturn(false);
        String view = contractController.processRequestEditContract(1L, contract, bindingResult);
        assertEquals("redirect:/contract/list", view);
        verify(contractService).saveContract(contract);
    }

    @Test
    void testProcessRequestEditContract_WithErrors_RedirectsToEdit() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = contractController.processRequestEditContract(1L, contract, bindingResult);
        assertEquals("redirect:/contract/edit/1", view);
        verify(contractService, never()).saveContract(any());
    }

    @Test
    void testShowNameSearchForm_ReturnsView() {
        String view = contractController.showNameSearchForm(model);
        assertEquals("contract/name-search", view);
        verify(model).addAttribute(eq("contract"), any(Contract.class));
    }

    @Test
    void testProcessRequestNameSearch_ReturnsShowOneView() {
        when(contractService.findByName("Contract A")).thenReturn(contract);
        String view = contractController.processRequestNameSearch(contract, model);
        assertEquals("contract/show-one", view);
        verify(model).addAttribute("contract", contract);
    }

    @Test
    void testShowValueLessThanEqualSearchForm_ReturnsView() {
        String view = contractController.showValueLeesThanEqualSearchForm(model);
        assertEquals("contract/value-le-search", view);
    }

    @Test
    void testProcessRequestValueLessThanEqualSearch_ReturnsShowListView() {
        List<Contract> contracts = Collections.singletonList(contract);
        when(contractService.findAllByValueLessThanEqual(new BigDecimal("1000.00"))).thenReturn(contracts);
        String view = contractController.processRequestValueLessThanEqualSearch(contract, model);
        assertEquals("contract/show-list", view);
        verify(model).addAttribute("contracts", contracts);
    }

    @Test
    void testShowValueGreaterThanEqualSearchForm_ReturnsView() {
        String view = contractController.showValueGreaterThanEqualSearchForm(model);
        assertEquals("contract/value-ge-search", view);
    }

    @Test
    void testProcessRequestValueGreaterThanEqualSearch_ReturnsShowListView() {
        List<Contract> contracts = Collections.singletonList(contract);
        when(contractService.findAllByValueGreaterThanEqual(new BigDecimal("1000.00"))).thenReturn(contracts);
        String view = contractController.processRequestValueGreaterThanEqualSearch(contract, model);
        assertEquals("contract/show-list", view);
        verify(model).addAttribute("contracts", contracts);
    }

    @Test
    void testShowBeginDateSearchForm_ReturnsView() {
        String view = contractController.showBeginDateSearchForm(model);
        assertEquals("contract/begin-date-search", view);
    }

    @Test
    void testProcessRequestBeginDateSearch_ReturnsShowListView() {
        List<Contract> contracts = Collections.singletonList(contract);
        LocalDate date = LocalDate.of(2023, 1, 1);
        when(contractService.findAllByBeginDate(date)).thenReturn(contracts);
        String view = contractController.processRequestBeginDateSearch(contract, model);
        assertEquals("contract/show-list", view);
        verify(model).addAttribute("contracts", contracts);
    }

    @Test
    void testShowBeginDateBeforeSearchForm_ReturnsView() {
        String view = contractController.showBeginDateBeforeSearchForm(model);
        assertEquals("contract/begin-date-before-search", view);
    }

    @Test
    void testProcessRequestBeginDateBeforeSearch_ReturnsShowListView() {
        List<Contract> contracts = Collections.singletonList(contract);
        LocalDate date = LocalDate.of(2023, 1, 1);
        when(contractService.findAllByBeginDateBefore(date)).thenReturn(contracts);
        String view = contractController.processRequestBeginDateBeforeSearch(contract, model);
        assertEquals("contract/show-list", view);
        verify(model).addAttribute("contracts", contracts);
    }

    @Test
    void testShowBeginDateAfterSearchForm_ReturnsView() {
        String view = contractController.showBeginDateAfterSearchForm(model);
        assertEquals("contract/begin-date-after-search", view);
    }

    @Test
    void testProcessRequestBeginDateAfterSearch_ReturnsShowListView() {
        List<Contract> contracts = Collections.singletonList(contract);
        LocalDate date = LocalDate.of(2023, 1, 1);
        when(contractService.findAllByBeginDateAfter(date)).thenReturn(contracts);
        String view = contractController.processRequestBeginDateAfterSearch(contract, model);
        assertEquals("contract/show-list", view);
        verify(model).addAttribute("contracts", contracts);
    }

    @Test
    void testShowEndDateSearchForm_ReturnsView() {
        String view = contractController.showEndDateSearchForm(model);
        assertEquals("contract/end-date-search", view);
    }

    @Test
    void testProcessRequestEndDateSearch_ReturnsShowListView() {
        List<Contract> contracts = Collections.singletonList(contract);
        LocalDate date = LocalDate.of(2023, 12, 31);
        when(contractService.findAllByEndDate(date)).thenReturn(contracts);
        String view = contractController.processRequestEndDateSearch(contract, model);
        assertEquals("contract/show-list", view);
        verify(model).addAttribute("contracts", contracts);
    }

    @Test
    void testShowEndDateBeforeSearchForm_ReturnsView() {
        String view = contractController.showEndDateBeforeSearchForm(model);
        assertEquals("contract/end-date-before-search", view);
    }

    @Test
    void testProcessRequestEndDateBeforeSearch_ReturnsShowListView() {
        List<Contract> contracts = Collections.singletonList(contract);
        LocalDate date = LocalDate.of(2023, 12, 31);
        when(contractService.findAllByEndDateBefore(date)).thenReturn(contracts);
        String view = contractController.processRequestEndDateBeforeSearch(contract, model);
        assertEquals("contract/show-list", view);
        verify(model).addAttribute("contracts", contracts);
    }

    @Test
    void testShowEndDateAfterSearchForm_ReturnsView() {
        String view = contractController.showEndDateAfterSearchForm(model);
        assertEquals("contract/end-date-after-search", view);
    }

    @Test
    void testProcessRequestEndDateAfterSearch_ReturnsShowListView() {
        List<Contract> contracts = Collections.singletonList(contract);
        LocalDate date = LocalDate.of(2023, 12, 31);
        when(contractService.findAllByEndDateAfter(date)).thenReturn(contracts);
        String view = contractController.processRequestEndDateAfterSearch(contract, model);
        assertEquals("contract/show-list", view);
        verify(model).addAttribute("contracts", contracts);
    }

    @Test
    void testShowStatusSearchForm_ReturnsView() {
        String view = contractController.showStatusSearchForm(model);
        assertEquals("contract/status-search", view);
    }

    @Test
    void testProcessRequestStatusSearch_ReturnsShowListView() {
        List<Contract> contracts = Collections.singletonList(contract);
        when(contractService.findAllByStatus(Status.PROPOSED)).thenReturn(contracts);
        String view = contractController.processRequestStatusSearch(contract, model);
        assertEquals("contract/show-list", view);
        verify(model).addAttribute("contracts", contracts);
    }

    @Test
    void testShowCustomerSearchForm_ReturnsView() {
        List<Customer> customers = Collections.singletonList(customer);
        when(customerService.findAllByEnabledTrue()).thenReturn(customers);
        String view = contractController.showCustomerSearchForm(model);
        assertEquals("contract/customer-search", view);
        verify(model).addAttribute("customers", customers);
    }

    @Test
    void testProcessRequestCustomerSearch_ReturnsShowListView() {
        List<Contract> contracts = Collections.singletonList(contract);
        when(contractService.findAllByCustomer(customer)).thenReturn(contracts);
        String view = contractController.processRequestCustomerSearch(contract, model);
        assertEquals("contract/show-list", view);
        verify(model).addAttribute("contracts", contracts);
    }

    @Test
    void testShowCustomerUserSearchForm_ReturnsView() {
        List<Customer> customers = Collections.singletonList(customer);
        List<User> users = Collections.singletonList(user);
        when(customerService.findAllByEnabledTrue()).thenReturn(customers);
        when(userService.listAllUsers()).thenReturn(users);
        String view = contractController.showCustomerUserSearchForm(model);
        assertEquals("contract/customer-user-search", view);
    }

    @Test
    void testProcessRequestCustomerUserSearch_ReturnsShowListView() {
        List<Contract> contracts = Collections.singletonList(contract);
        when(contractService.findAllByCustomerAndUser(customer, user)).thenReturn(contracts);
        String view = contractController.processRequestCustomerUserSearch(contract, model);
        assertEquals("contract/show-list", view);
        verify(model).addAttribute("contracts", contracts);
    }

    @Test
    void testShowUserSearchForm_ReturnsView() {
        List<User> users = Collections.singletonList(user);
        when(userService.listAllUsers()).thenReturn(users);
        String view = contractController.showUserSearchForm(model);
        assertEquals("contract/user-search", view);
        verify(model).addAttribute("users", users);
    }

    @Test
    void testProcessRequestUserSearch_ReturnsShowListView() {
        List<Contract> contracts = Collections.singletonList(contract);
        when(contractService.findAllByUser(user)).thenReturn(contracts);
        String view = contractController.processRequestUserSearch(contract, model);
        assertEquals("contract/show-list", view);
        verify(model).addAttribute("contracts", contracts);
    }

    @Test
    void testConstructor_WithAllServices() {
        ContractController controller = new ContractController(contractService, customerService, userService);
        assertNotNull(controller);
    }
}
