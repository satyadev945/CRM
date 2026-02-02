package crm.controller;

import crm.entity.Customer;
import crm.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private CustomerController customerController;

    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        customer.setPhone(123456789);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setCity("Test City");
        customer.setAddress("Test Address");
        customer.setEnabled(1);
    }

    @Test
    void showAllCustomersShouldReturnListView() {
        // Arrange
        List<Customer> customers = Arrays.asList(customer);
        when(customerService.listAllCustomers()).thenReturn(customers);

        // Act
        String viewName = customerController.showAllCustomers(model);

        // Assert
        verify(customerService).listAllCustomers();
        verify(model).addAttribute("customers", customers);
        assertEquals("customer/list", viewName);
    }

    @Test
    void showFormAddCustomerShouldReturnAddView() {
        // Act
        String viewName = customerController.showFormAddCustomer(model);

        // Assert
        verify(model).addAttribute(eq("customer"), any(Customer.class));
        assertEquals("customer/add", viewName);
    }

    @Test
    void processRequestAddCustomerShouldRedirectIfErrors() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = customerController.processRequestAddCustomer(customer, bindingResult);

        // Assert
        verify(bindingResult).hasErrors();
        assertEquals("redirect:/customer/add", viewName);
        verify(customerService, never()).saveCustomer(any(Customer.class));
    }

    @Test
    void processRequestAddCustomerShouldSaveAndReturnSuccessView() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = customerController.processRequestAddCustomer(customer, bindingResult);

        // Assert
        verify(bindingResult).hasErrors();
        verify(customerService).saveCustomer(customer);
        assertEquals("customer/success", viewName);
    }

    @Test
    void showFormEditCustomerShouldReturnEditView() {
        // Arrange
        Long id = 1L;
        when(customerService.showCustomer(id)).thenReturn(customer);

        // Act
        String viewName = customerController.showFormEditCustomer(model, id);

        // Assert
        verify(customerService).showCustomer(id);
        verify(model).addAttribute("customer", customer);
        assertEquals("customer/edit", viewName);
    }

    @Test
    void processRequestNameSearchShouldReturnShowOneView() {
        // Arrange
        String customerName = "Test Customer";
        customer.setName(customerName);
        when(customerService.findOneByEnabledTrueAndName(customerName)).thenReturn(customer);

        // Act
        String viewName = customerController.processRequestNameSearch(customer, model);

        // Assert
        verify(customerService).findOneByEnabledTrueAndName(customerName);
        verify(model).addAttribute("customer", customer);
        assertEquals("customer/show-one", viewName);
    }

    @Test
    void processRequestEmailSearchShouldReturnShowListView() {
        // Arrange
        String email = "test@example.com";
        customer.setEmail(email);
        List<Customer> customers = Collections.singletonList(customer);
        when(customerService.findByEnabledTrueAndEmail(email)).thenReturn(customers);

        // Act
        String viewName = customerController.processRequestEmailSearch(customer, model);

        // Assert
        verify(customerService).findByEnabledTrueAndEmail(email);
        verify(model).addAttribute("customers", customers);
        assertEquals("customer/show-list", viewName);
    }

    @Test
    void createCustomerBasedOnAnotherOneShouldSaveNewCustomerAndRedirect() {
        // Arrange
        Long id = 1L;
        Long newId = 2L;
        when(bindingResult.hasErrors()).thenReturn(false);
        when(customerService.getMaxId()).thenReturn(newId - 1);

        // Act
        String viewName = customerController.createCustomerBasedOnAnotherOne(id, customer, bindingResult);

        // Assert
        verify(bindingResult).hasErrors();
        verify(customerService).getMaxId();
        verify(customerService).saveCustomer(any(Customer.class));
        assertEquals("redirect:/customer/list", viewName);
    }

    @Test
    void processRequestFirstNameLastNameSearchShouldReturnShowListView() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        List<Customer> customers = Collections.singletonList(customer);
        when(customerService.findByEnabledTrueAndFirstNameAndLastName(firstName, lastName)).thenReturn(customers);

        // Act
        String viewName = customerController.processRequestFirstNameLastNameSearch(customer, model);

        // Assert
        verify(customerService).findByEnabledTrueAndFirstNameAndLastName(firstName, lastName);
        verify(model).addAttribute("customers", customers);
        assertEquals("customer/show-list", viewName);
    }
}