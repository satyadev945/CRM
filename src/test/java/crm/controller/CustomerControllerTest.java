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
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Company");
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setPhone("123-456-7890");
        testCustomer.setCity("New York");
        testCustomer.setAddress("123 Main St");
        testCustomer.setEnabled(true);

        testCustomers = Arrays.asList(testCustomer);
    }

    @Test
    void constructor_withCustomerService_shouldCreateInstance() {
        CustomerController controller = new CustomerController(customerService);
        assertNotNull(controller);
    }

    @Test
    void showAllCustomers_shouldReturnListView() {
        when(customerService.listAllCustomers()).thenReturn(testCustomers);

        String viewName = customerController.showAllCustomers(model);

        assertEquals("customer/list", viewName);
        verify(model).addAttribute("customers", testCustomers);
        verify(customerService).listAllCustomers();
    }

    @Test
    void showFormAddCustomer_shouldReturnAddView() {
        String viewName = customerController.showFormAddCustomer(model);

        assertEquals("customer/add", viewName);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void processRequestAddCustomer_withValidCustomer_shouldReturnSuccessView() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = customerController.processRequestAddCustomer(testCustomer, bindingResult);

        assertEquals("customer/success", viewName);
        verify(customerService).saveCustomer(testCustomer);
    }

    @Test
    void processRequestAddCustomer_withErrors_shouldRedirectToAddForm() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = customerController.processRequestAddCustomer(testCustomer, bindingResult);

        assertEquals("redirect:/customer/add", viewName);
        verify(customerService, never()).saveCustomer(any());
    }

    @Test
    void showFormEditCustomer_shouldReturnEditView() {
        Long customerId = 1L;
        when(customerService.showCustomer(customerId)).thenReturn(testCustomer);

        String viewName = customerController.showFormEditCustomer(model, customerId);

        assertEquals("customer/edit", viewName);
        verify(model).addAttribute("customer", testCustomer);
        verify(customerService).showCustomer(customerId);
    }

    @Test
    void processRequestEditCustomer_withValidCustomer_shouldRedirectToList() {
        Long customerId = 1L;
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = customerController.processRequestEditCustomer(customerId, testCustomer, bindingResult);

        assertEquals("redirect:/customer/list", viewName);
        verify(customerService).saveCustomer(testCustomer);
    }

    @Test
    void processRequestEditCustomer_withErrors_shouldRedirectToEditForm() {
        Long customerId = 1L;
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = customerController.processRequestEditCustomer(customerId, testCustomer, bindingResult);

        assertEquals("redirect:/customer/edit/" + customerId, viewName);
        verify(customerService, never()).saveCustomer(any());
    }

    @Test
    void showFormCreateCustomerBasedOnAnotherOne_shouldReturnView() {
        Long customerId = 1L;
        when(customerService.showCustomer(customerId)).thenReturn(testCustomer);

        String viewName = customerController.showFormCreateCustomerBasedOnAnotherOne(model, customerId);

        assertEquals("customer/add-customer-based-on-another-one", viewName);
        verify(model).addAttribute("customer", testCustomer);
        verify(customerService).showCustomer(customerId);
    }

    @Test
    void createCustomerBasedOnAnotherOne_withValidCustomer_shouldRedirectToList() {
        Long customerId = 1L;
        when(bindingResult.hasErrors()).thenReturn(false);
        when(customerService.getMaxId()).thenReturn(10L);

        String viewName = customerController.createCustomerBasedOnAnotherOne(customerId, testCustomer, bindingResult);

        assertEquals("redirect:/customer/list", viewName);
        verify(customerService).saveCustomer(any(Customer.class));
        verify(customerService).getMaxId();
    }

    @Test
    void createCustomerBasedOnAnotherOne_withErrors_shouldRedirectToForm() {
        Long customerId = 1L;
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = customerController.createCustomerBasedOnAnotherOne(customerId, testCustomer, bindingResult);

        assertEquals("redirect:/customer/addCustomerBasedOnAnotherOne/" + customerId, viewName);
        verify(customerService, never()).saveCustomer(any());
    }

    @Test
    void showNameSearchForm_shouldReturnNameSearchView() {
        String viewName = customerController.showNameSearchForm(model);

        assertEquals("customer/name-search", viewName);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void processRequestNameSearch_shouldReturnShowOneView() {
        when(customerService.findOneByEnabledTrueAndName(testCustomer.getName())).thenReturn(testCustomer);

        String viewName = customerController.processRequestNameSearch(testCustomer, model);

        assertEquals("customer/show-one", viewName);
        verify(model).addAttribute("customer", testCustomer);
        verify(customerService).findOneByEnabledTrueAndName(testCustomer.getName());
    }

    @Test
    void showEmailSearchForm_shouldReturnEmailSearchView() {
        String viewName = customerController.showEmailSearchForm(model);

        assertEquals("customer/email-search", viewName);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void processRequestEmailSearch_shouldReturnShowListView() {
        when(customerService.findByEnabledTrueAndEmail(testCustomer.getEmail())).thenReturn(testCustomers);

        String viewName = customerController.processRequestEmailSearch(testCustomer, model);

        assertEquals("customer/show-list", viewName);
        verify(model).addAttribute("customers", testCustomers);
        verify(customerService).findByEnabledTrueAndEmail(testCustomer.getEmail());
    }

    @Test
    void showPhoneSearchForm_shouldReturnPhoneSearchView() {
        String viewName = customerController.showPhoneSearchForm(model);

        assertEquals("customer/phone-search", viewName);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void processRequestPhoneSearch_shouldReturnShowListView() {
        when(customerService.findByEnabledTrueAndPhone(testCustomer.getPhone())).thenReturn(testCustomers);

        String viewName = customerController.processRequestPhoneSearch(testCustomer, model);

        assertEquals("customer/show-list", viewName);
        verify(model).addAttribute("customers", testCustomers);
        verify(customerService).findByEnabledTrueAndPhone(testCustomer.getPhone());
    }

    @Test
    void showFirstNameSearchForm_shouldReturnFirstNameSearchView() {
        String viewName = customerController.showFirstNameSearchForm(model);

        assertEquals("customer/first-name-search", viewName);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void processRequestFirstNameSearch_shouldReturnShowListView() {
        when(customerService.findByEnabledTrueAndFirstName(testCustomer.getFirstName())).thenReturn(testCustomers);

        String viewName = customerController.processRequestFirstNameSearch(testCustomer, model);

        assertEquals("customer/show-list", viewName);
        verify(model).addAttribute("customers", testCustomers);
        verify(customerService).findByEnabledTrueAndFirstName(testCustomer.getFirstName());
    }

    @Test
    void showLastNameSearchForm_shouldReturnLastNameSearchView() {
        String viewName = customerController.showLastNameSearchForm(model);

        assertEquals("customer/last-name-search", viewName);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void processRequestLastNameSearch_shouldReturnShowListView() {
        when(customerService.findByEnabledTrueAndLastName(testCustomer.getLastName())).thenReturn(testCustomers);

        String viewName = customerController.processRequestLastNameSearch(testCustomer, model);

        assertEquals("customer/show-list", viewName);
        verify(model).addAttribute("customers", testCustomers);
        verify(customerService).findByEnabledTrueAndLastName(testCustomer.getLastName());
    }

    @Test
    void showFirstNameLastNameSearchForm_shouldReturnView() {
        String viewName = customerController.showFirstNameLastNameSearchForm(model);

        assertEquals("customer/first-name-last-name-search", viewName);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void processRequestFirstNameLastNameSearch_shouldReturnShowListView() {
        when(customerService.findByEnabledTrueAndFirstNameAndLastName(
            testCustomer.getFirstName(), testCustomer.getLastName())).thenReturn(testCustomers);

        String viewName = customerController.processRequestFirstNameLastNameSearch(testCustomer, model);

        assertEquals("customer/show-list", viewName);
        verify(model).addAttribute("customers", testCustomers);
        verify(customerService).findByEnabledTrueAndFirstNameAndLastName(
            testCustomer.getFirstName(), testCustomer.getLastName());
    }

    @Test
    void showCitySearchForm_shouldReturnCitySearchView() {
        String viewName = customerController.showCitySearchForm(model);

        assertEquals("customer/city-search", viewName);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void processRequestCitySearch_shouldReturnShowListView() {
        when(customerService.findByEnabledTrueAndCity(testCustomer.getCity())).thenReturn(testCustomers);

        String viewName = customerController.processRequestCitySearch(testCustomer, model);

        assertEquals("customer/show-list", viewName);
        verify(model).addAttribute("customers", testCustomers);
        verify(customerService).findByEnabledTrueAndCity(testCustomer.getCity());
    }

    @Test
    void showCityAddressSearchForm_shouldReturnView() {
        String viewName = customerController.showCityAddressSearchForm(model);

        assertEquals("customer/city-address-search", viewName);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void processRequestCityAddressSearch_shouldReturnShowListView() {
        when(customerService.findByEnabledTrueAndCityAndAddress(
            testCustomer.getCity(), testCustomer.getAddress())).thenReturn(testCustomers);

        String viewName = customerController.processRequestCityAddressSearch(testCustomer, model);

        assertEquals("customer/show-list", viewName);
        verify(model).addAttribute("customers", testCustomers);
        verify(customerService).findByEnabledTrueAndCityAndAddress(
            testCustomer.getCity(), testCustomer.getAddress());
    }
}
