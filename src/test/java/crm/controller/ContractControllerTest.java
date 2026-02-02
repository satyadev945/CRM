package crm.controller;

import crm.entity.Contract;
import crm.entity.Customer;
import crm.entity.User;
import crm.service.ContractService;
import crm.service.CustomerService;
import crm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);
        customer.setName("Test Customer");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        contract = new Contract();
        contract.setId(1L);
        contract.setName("Test Contract");
        contract.setValue(new java.math.BigDecimal("1000.0"));
        contract.setBeginDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().plusMonths(6));
        contract.setCustomer(customer);
        contract.setUser(user);
    }

    @Test
    void showAllContractsShouldReturnListView() {
        // Arrange
        List<Contract> contracts = Arrays.asList(contract);
        when(contractService.listAllContracts()).thenReturn(contracts);

        // Act
        String viewName = contractController.showAllContracts(model);

        // Assert
        verify(contractService).listAllContracts();
        verify(model).addAttribute("contracts", contracts);
        assertEquals("contract/list", viewName);
    }

    @Test
    void showFormAddContractShouldReturnAddView() {
        // Arrange
        List<Customer> customers = Arrays.asList(customer);
        List<User> users = Arrays.asList(user);
        when(customerService.findAllByEnabledTrue()).thenReturn(customers);
        when(userService.listAllUsers()).thenReturn(users);

        // Act
        String viewName = contractController.showFormAddContract(model);

        // Assert
        verify(customerService).findAllByEnabledTrue();
        verify(userService).listAllUsers();
        verify(model).addAttribute(eq("contract"), any(Contract.class));
        verify(model).addAttribute("customers", customers);
        verify(model).addAttribute("users", users);
        assertEquals("contract/add", viewName);
    }

    @Test
    void processRequestAddContractShouldRedirectIfErrors() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = contractController.processRequestAddContract(contract, bindingResult);

        // Assert
        verify(bindingResult).hasErrors();
        assertEquals("redirect:/contract/add", viewName);
        verify(contractService, never()).saveContract(any(Contract.class));
    }

    @Test
    void processRequestAddContractShouldSaveAndReturnSuccessView() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = contractController.processRequestAddContract(contract, bindingResult);

        // Assert
        verify(bindingResult).hasErrors();
        verify(contractService).saveContract(contract);
        assertEquals("contract/success", viewName);
    }

    @Test
    void showFormEditContractShouldReturnEditView() {
        // Arrange
        Long id = 1L;
        when(contractService.showContract(id)).thenReturn(contract);

        // Act
        String viewName = contractController.showFormEditContract(model, id);

        // Assert
        verify(contractService).showContract(id);
        verify(model).addAttribute("contract", contract);
        assertEquals("contract/edit", viewName);
    }

    @Test
    void processRequestNameSearchShouldReturnShowOneView() {
        // Arrange
        String contractName = "Test Contract";
        contract.setName(contractName);
        when(contractService.findByName(contractName)).thenReturn(contract);

        // Act
        String viewName = contractController.processRequestNameSearch(contract, model);

        // Assert
        verify(contractService).findByName(contractName);
        verify(model).addAttribute("contract", contract);
        assertEquals("contract/show-one", viewName);
    }

    @Test
    void processRequestValueLessThanEqualSearchShouldReturnShowListView() {
        // Arrange
        java.math.BigDecimal value = new java.math.BigDecimal("1000.0");
        contract.setValue(value);
        List<Contract> contracts = Arrays.asList(contract);
        when(contractService.findAllByValueLessThanEqual(value)).thenReturn(contracts);

        // Act
        String viewName = contractController.processRequestValueLessThanEqualSearch(contract, model);

        // Assert
        verify(contractService).findAllByValueLessThanEqual(value);
        verify(model).addAttribute("contracts", contracts);
        assertEquals("contract/show-list", viewName);
    }

    @Test
    void processRequestValueGreaterThanEqualSearchShouldReturnShowListView() {
        // Arrange
        java.math.BigDecimal value = new java.math.BigDecimal("1000.0");
        contract.setValue(value);
        List<Contract> contracts = Arrays.asList(contract);
        when(contractService.findAllByValueGreaterThanEqual(value)).thenReturn(contracts);

        // Act
        String viewName = contractController.processRequestValueGreaterThanEqualSearch(contract, model);

        // Assert
        verify(contractService).findAllByValueGreaterThanEqual(value);
        verify(model).addAttribute("contracts", contracts);
        assertEquals("contract/show-list", viewName);
    }
}