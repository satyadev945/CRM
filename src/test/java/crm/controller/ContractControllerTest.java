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
                .build();

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("user@example.com")
                .build();

        testContract = Contract.builder()
                .id(1L)
                .name("Test Contract")
                .content("Contract content")
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
    void showAllContracts_shouldReturnListView() {
        // Arrange
        when(contractService.listAllContracts()).thenReturn(testContracts);

        // Act
        String viewName = contractController.showAllContracts(model);

        // Assert
        assertEquals("contract/list", viewName);
        verify(contractService).listAllContracts();
        verify(model).addAttribute("contracts", testContracts);
    }

    @Test
    void showFormAddContract_shouldReturnAddView() {
        // Arrange
        when(customerService.findAllByEnabledTrue()).thenReturn(Arrays.asList(testCustomer));
        when(userService.listAllUsers()).thenReturn(Arrays.asList(testUser));

        // Act
        String viewName = contractController.showFormAddContract(model);

        // Assert
        assertEquals("contract/add", viewName);
        verify(customerService).findAllByEnabledTrue();
        verify(userService).listAllUsers();
        verify(model).addAttribute(eq("contract"), any(Contract.class));
        verify(model).addAttribute("customers", Arrays.asList(testCustomer));
        verify(model).addAttribute("users", Arrays.asList(testUser));
    }

    @Test
    void processRequestAddContract_withValidContract_shouldReturnSuccessView() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = contractController.processRequestAddContract(testContract, bindingResult);

        // Assert
        assertEquals("contract/success", viewName);
        verify(contractService).saveContract(testContract);
    }

    @Test
    void processRequestAddContract_withInvalidContract_shouldRedirectToAddForm() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = contractController.processRequestAddContract(testContract, bindingResult);

        // Assert
        assertEquals("redirect:/contract/add", viewName);
        verify(contractService, never()).saveContract(any());
    }

    @Test
    void showFormEditContract_shouldReturnEditView() {
        // Arrange
        Long contractId = 1L;
        when(contractService.showContract(contractId)).thenReturn(testContract);

        // Act
        String viewName = contractController.showFormEditContract(model, contractId);

        // Assert
        assertEquals("contract/edit", viewName);
        verify(contractService).showContract(contractId);
        verify(model).addAttribute("contract", testContract);
    }

    @Test
    void processRequestEditContract_withValidContract_shouldRedirectToList() {
        // Arrange
        Long contractId = 1L;
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = contractController.processRequestEditContract(contractId, testContract, bindingResult);

        // Assert
        assertEquals("redirect:/contract/list", viewName);
        verify(contractService).saveContract(testContract);
    }

    @Test
    void processRequestEditContract_withInvalidContract_shouldRedirectToEditForm() {
        // Arrange
        Long contractId = 1L;
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = contractController.processRequestEditContract(contractId, testContract, bindingResult);

        // Assert
        assertEquals("redirect:/contract/edit/" + contractId, viewName);
        verify(contractService, never()).saveContract(any());
    }

    @Test
    void showNameSearchForm_shouldReturnNameSearchView() {
        // Act
        String viewName = contractController.showNameSearchForm(model);

        // Assert
        assertEquals("contract/name-search", viewName);
        verify(model).addAttribute(eq("contract"), any(Contract.class));
    }

    @Test
    void processRequestNameSearch_shouldReturnShowOneView() {
        // Arrange
        when(contractService.findByName(testContract.getName())).thenReturn(testContract);

        // Act
        String viewName = contractController.processRequestNameSearch(testContract, model);

        // Assert
        assertEquals("contract/show-one", viewName);
        verify(contractService).findByName(testContract.getName());
        verify(model).addAttribute("contract", testContract);
    }

    @Test
    void showValueLeesThanEqualSearchForm_shouldReturnValueLeSearchView() {
        // Act
        String viewName = contractController.showValueLeesThanEqualSearchForm(model);

        // Assert
        assertEquals("contract/value-le-search", viewName);
        verify(model).addAttribute(eq("contract"), any(Contract.class));
    }

    @Test
    void processRequestValueLessThanEqualSearch_shouldReturnShowListView() {
        // Arrange
        when(contractService.findAllByValueLessThanEqual(testContract.getValue())).thenReturn(testContracts);

        // Act
        String viewName = contractController.processRequestValueLessThanEqualSearch(testContract, model);

        // Assert
        assertEquals("contract/show-list", viewName);
        verify(contractService).findAllByValueLessThanEqual(testContract.getValue());
        verify(model).addAttribute("contracts", testContracts);
    }

    @Test
    void showValueGreaterThanEqualSearchForm_shouldReturnValueGeSearchView() {
        // Act
        String viewName = contractController.showValueGreaterThanEqualSearchForm(model);

        // Assert
        assertEquals("contract/value-ge-search", viewName);
        verify(model).addAttribute(eq("contract"), any(Contract.class));
    }

    @Test
    void processRequestValueGreaterThanEqualSearch_shouldReturnShowListView() {
        // Arrange
        when(contractService.findAllByValueGreaterThanEqual(testContract.getValue())).thenReturn(testContracts);

        // Act
        String viewName = contractController.processRequestValueGreaterThanEqualSearch(testContract, model);

        // Assert
        assertEquals("contract/show-list", viewName);
        verify(contractService).findAllByValueGreaterThanEqual(testContract.getValue());
        verify(model).addAttribute("contracts", testContracts);
    }

    @Test
    void showBeginDateSearchForm_shouldReturnBeginDateSearchView() {
        // Act
        String viewName = contractController.showBeginDateSearchForm(model);

        // Assert
        assertEquals("contract/begin-date-search", viewName);
        verify(model).addAttribute(eq("contract"), any(Contract.class));
    }

    @Test
    void processRequestBeginDateSearch_shouldReturnShowListView() {
        // Arrange
        when(contractService.findAllByBeginDate(testContract.getBeginDate())).thenReturn(testContracts);

        // Act
        String viewName = contractController.processRequestBeginDateSearch(testContract, model);

        // Assert
        assertEquals("contract/show-list", viewName);
        verify(contractService).findAllByBeginDate(testContract.getBeginDate());
        verify(model).addAttribute("contracts", testContracts);
    }

    @Test
    void showStatusSearchForm_shouldReturnStatusSearchView() {
        // Act
        String viewName = contractController.showStatusSearchForm(model);

        // Assert
        assertEquals("contract/status-search", viewName);
        verify(model).addAttribute(eq("contract"), any(Contract.class));
    }

    @Test
    void processRequestStatusSearch_shouldReturnShowListView() {
        // Arrange
        when(contractService.findAllByStatus(testContract.getStatus())).thenReturn(testContracts);

        // Act
        String viewName = contractController.processRequestStatusSearch(testContract, model);

        // Assert
        assertEquals("contract/show-list", viewName);
        verify(contractService).findAllByStatus(testContract.getStatus());
        verify(model).addAttribute("contracts", testContracts);
    }

    @Test
    void showCustomerSearchForm_shouldReturnCustomerSearchView() {
        // Arrange
        when(customerService.findAllByEnabledTrue()).thenReturn(Arrays.asList(testCustomer));

        // Act
        String viewName = contractController.showCustomerSearchForm(model);

        // Assert
        assertEquals("contract/customer-search", viewName);
        verify(customerService).findAllByEnabledTrue();
        verify(model).addAttribute(eq("contract"), any(Contract.class));
        verify(model).addAttribute("customers", Arrays.asList(testCustomer));
    }

    @Test
    void processRequestCustomerSearch_shouldReturnShowListView() {
        // Arrange
        when(contractService.findAllByCustomer(testContract.getCustomer())).thenReturn(testContracts);

        // Act
        String viewName = contractController.processRequestCustomerSearch(testContract, model);

        // Assert
        assertEquals("contract/show-list", viewName);
        verify(contractService).findAllByCustomer(testContract.getCustomer());
        verify(model).addAttribute("contracts", testContracts);
    }

    @Test
    void showUserSearchForm_shouldReturnUserSearchView() {
        // Arrange
        when(userService.listAllUsers()).thenReturn(Arrays.asList(testUser));

        // Act
        String viewName = contractController.showUserSearchForm(model);

        // Assert
        assertEquals("contract/user-search", viewName);
        verify(userService).listAllUsers();
        verify(model).addAttribute(eq("contract"), any(Contract.class));
        verify(model).addAttribute("users", Arrays.asList(testUser));
    }

    @Test
    void processRequestUserSearch_shouldReturnShowListView() {
        // Arrange
        when(contractService.findAllByUser(testContract.getUser())).thenReturn(testContracts);

        // Act
        String viewName = contractController.processRequestUserSearch(testContract, model);

        // Assert
        assertEquals("contract/show-list", viewName);
        verify(contractService).findAllByUser(testContract.getUser());
        verify(model).addAttribute("contracts", testContracts);
    }
}
