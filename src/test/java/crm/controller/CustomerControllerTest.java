package crm.controller;

import crm.entity.Customer;
import crm.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private CustomerController customerController;

    private Customer testCustomer;
    private List<Customer> testCustomers;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .id(1L)
                .name("Test Company")
                .email("test@example.com")
                .phone(123456789)
                .firstName("John")
                .lastName("Doe")
                .city("New York")
                .address("123 Main St")
                .enabled(1)
                .build();

        testCustomers = Arrays.asList(testCustomer);
    }

    @Test
    void showAllCustomers_shouldReturnListView() {
        // Arrange
        when(customerService.listAllCustomers()).thenReturn(testCustomers);

        // Act
        String viewName = customerController.showAllCustomers(model);

        // Assert
        assertEquals("customer/list", viewName);
        verify(customerService).listAllCustomers();
        verify(model).addAttribute("customers", testCustomers);
    }

    @Test
    void showFormAddCustomer_shouldReturnAddView() {
        // Act
        String viewName = customerController.showFormAddCustomer(model);

        // Assert
        assertEquals("customer/add", viewName);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void processRequestAddCustomer_withValidCustomer_shouldReturnSuccessView() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = customerController.processRequestAddCustomer(testCustomer, bindingResult);

        // Assert
        assertEquals("customer/success", viewName);
        verify(customerService).saveCustomer(testCustomer);
    }

    @Test
    void processRequestAddCustomer_withInvalidCustomer_shouldRedirectToAddForm() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = customerController.processRequestAddCustomer(testCustomer, bindingResult);

        // Assert
        assertEquals("redirect:/customer/add", viewName);
        verify(customerService, never()).saveCustomer(any());
    }

    @Test
    void showFormEditCustomer_shouldReturnEditView() {
        // Arrange
        Long customerId = 1L;
        when(customerService.showCustomer(customerId)).thenReturn(testCustomer);

        // Act
        String viewName = customerController.showFormEditCustomer(model, customerId);

        // Assert
        assertEquals("customer/edit", viewName);
        verify(customerService).showCustomer(customerId);
        verify(model).addAttribute("customer", testCustomer);
    }

    @Test
    void processRequestEditCustomer_withValidCustomer_shouldRedirectToList() {
        // Arrange
        Long customerId = 1L;
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = customerController.processRequestEditCustomer(customerId, testCustomer, bindingResult);

        // Assert
        assertEquals("redirect:/customer/list", viewName);
        verify(customerService).saveCustomer(testCustomer);
    }

    @Test
    void processRequestEditCustomer_withInvalidCustomer_shouldRedirectToEditForm() {
        // Arrange
        Long customerId = 1L;
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = customerController.processRequestEditCustomer(customerId, testCustomer, bindingResult);

        // Assert
        assertEquals("redirect:/customer/edit/" + customerId, viewName);
        verify(customerService, never()).saveCustomer(any());
    }

    @Test
    void showFormCreateCustomerBasedOnAnotherOne_shouldReturnAddBasedOnAnotherOneView() {
        // Arrange
        Long customerId = 1L;
        when(customerService.showCustomer(customerId)).thenReturn(testCustomer);

        // Act
        String viewName = customerController.showFormCreateCustomerBasedOnAnotherOne(model, customerId);

        // Assert
        assertEquals("customer/add-customer-based-on-another-one", viewName);
        verify(customerService).showCustomer(customerId);
        verify(model).addAttribute("customer", testCustomer);
    }

    @Test
    void createCustomerBasedOnAnotherOne_withValidCustomer_shouldRedirectToList() {
        // Arrange
        Long customerId = 1L;
        when(bindingResult.hasErrors()).thenReturn(false);
        when(customerService.getMaxId()).thenReturn(10L);

        // Act
        String viewName = customerController.createCustomerBasedOnAnotherOne(customerId, testCustomer, bindingResult);

        // Assert
        assertEquals("redirect:/customer/list", viewName);
        verify(customerService).getMaxId();
        verify(customerService).saveCustomer(any(Customer.class));
    }

    @Test
    void createCustomerBasedOnAnotherOne_withInvalidCustomer_shouldRedirectToForm() {
        // Arrange
        Long customerId = 1L;
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = customerController.createCustomerBasedOnAnotherOne(customerId, testCustomer, bindingResult);

        // Assert
        assertEquals("redirect:/customer/addCustomerBasedOnAnotherOne/" + customerId, viewName);
        verify(customerService, never()).saveCustomer(any());
    }

    @Test
    void showNameSearchForm_shouldReturnNameSearchView() {
        // Act
        String viewName = customerController.showNameSearchForm(model);

        // Assert
        assertEquals("customer/name-search", viewName);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void processRequestNameSearch_shouldReturnShowOneView() {
        // Arrange
        when(customerService.findOneByEnabledTrueAndName(testCustomer.getName())).thenReturn(testCustomer);

        // Act
        String viewName = customerController.processRequestNameSearch(testCustomer, model);

        // Assert
        assertEquals("customer/show-one", viewName);
        verify(customerService).findOneByEnabledTrueAndName(testCustomer.getName());
        verify(model).addAttribute("customer", testCustomer);
    }

    @Test
    void showEmailSearchForm_shouldReturnEmailSearchView() {
        // Act
        String viewName = customerController.showEmailSearchForm(model);

        // Assert
        assertEquals("customer/email-search", viewName);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void processRequestEmailSearch_shouldReturnShowListView() {
        // Arrange
        when(customerService.findByEnabledTrueAndEmail(testCustomer.getEmail())).thenReturn(testCustomers);

        // Act
        String viewName = customerController.processRequestEmailSearch(testCustomer, model);

        // Assert
        assertEquals("customer/show-list", viewName);
        verify(customerService).findByEnabledTrueAndEmail(testCustomer.getEmail());
        verify(model).addAttribute("customers", testCustomers);
    }

    @Test
    void showPhoneSearchForm_shouldReturnPhoneSearchView() {
        // Act
        String viewName = customerController.showPhoneSearchForm(model);

        // Assert
        assertEquals("customer/phone-search", viewName);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void processRequestPhoneSearch_shouldReturnShowListView() {
        // Arrange
        when(customerService.findByEnabledTrueAndPhone(testCustomer.getPhone())).thenReturn(testCustomers);

        // Act
        String viewName = customerController.processRequestPhoneSearch(testCustomer, model);

        // Assert
        assertEquals("customer/show-list", viewName);
        verify(customerService).findByEnabledTrueAndPhone(testCustomer.getPhone());
        verify(model).addAttribute("customers", testCustomers);
    }

    @Test
    void showFirstNameSearchForm_shouldReturnFirstNameSearchView() {
        // Act
        String viewName = customerController.showFirstNameSearchForm(model);

        // Assert
        assertEquals("customer/first-name-search", viewName);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void processRequestFirstNameSearch_shouldReturnShowListView() {
        // Arrange
        when(customerService.findByEnabledTrueAndFirstName(testCustomer.getFirstName())).thenReturn(testCustomers);

        // Act
        String viewName = customerController.processRequestFirstNameSearch(testCustomer, model);

        // Assert
        assertEquals("customer/show-list", viewName);
        verify(customerService).findByEnabledTrueAndFirstName(testCustomer.getFirstName());
        verify(model).addAttribute("customers", testCustomers);
    }

    @Test
    void showLastNameSearchForm_shouldReturnLastNameSearchView() {
        // Act
        String viewName = customerController.showLastNameSearchForm(model);

        // Assert
        assertEquals("customer/last-name-search", viewName);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void processRequestLastNameSearch_shouldReturnShowListView() {
        // Arrange
        when(customerService.findByEnabledTrueAndLastName(testCustomer.getLastName())).thenReturn(testCustomers);

        // Act
        String viewName = customerController.processRequestLastNameSearch(testCustomer, model);

        // Assert
        assertEquals("customer/show-list", viewName);
        verify(customerService).findByEnabledTrueAndLastName(testCustomer.getLastName());
        verify(model).addAttribute("customers", testCustomers);
    }

    @Test
    void showCitySearchForm_shouldReturnCitySearchView() {
        // Act
        String viewName = customerController.showCitySearchForm(model);

        // Assert
        assertEquals("customer/city-search", viewName);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void processRequestCitySearch_shouldReturnShowListView() {
        // Arrange
        when(customerService.findByEnabledTrueAndCity(testCustomer.getCity())).thenReturn(testCustomers);

        // Act
        String viewName = customerController.processRequestCitySearch(testCustomer, model);

        // Assert
        assertEquals("customer/show-list", viewName);
        verify(customerService).findByEnabledTrueAndCity(testCustomer.getCity());
        verify(model).addAttribute("customers", testCustomers);
    }
}
