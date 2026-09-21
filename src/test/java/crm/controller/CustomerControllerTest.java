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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L).name("Corp A").email("a@corp.com")
                .phone(111222333).firstName("John").lastName("Doe")
                .city("NYC").address("5th Ave").enabled(1).build();
    }

    @Test
    void testShowAllCustomers_ReturnsListView() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerService.listAllCustomers()).thenReturn(customers);

        String view = customerController.showAllCustomers(model);

        assertEquals("customer/list", view);
        verify(model).addAttribute("customers", customers);
    }

    @Test
    void testShowFormAddCustomer_ReturnsAddView() {
        String view = customerController.showFormAddCustomer(model);
        assertEquals("customer/add", view);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void testProcessRequestAddCustomer_NoErrors_ReturnsSuccessView() {
        when(bindingResult.hasErrors()).thenReturn(false);
        String view = customerController.processRequestAddCustomer(customer, bindingResult);
        assertEquals("customer/success", view);
        verify(customerService).saveCustomer(customer);
    }

    @Test
    void testProcessRequestAddCustomer_WithErrors_RedirectsToAdd() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = customerController.processRequestAddCustomer(customer, bindingResult);
        assertEquals("redirect:/customer/add", view);
        verify(customerService, never()).saveCustomer(any());
    }

    @Test
    void testShowFormEditCustomer_ReturnsEditView() {
        when(customerService.showCustomer(1L)).thenReturn(customer);
        String view = customerController.showFormEditCustomer(model, 1L);
        assertEquals("customer/edit", view);
        verify(model).addAttribute("customer", customer);
    }

    @Test
    void testProcessRequestEditCustomer_NoErrors_RedirectsToList() {
        when(bindingResult.hasErrors()).thenReturn(false);
        String view = customerController.processRequestEditCustomer(1L, customer, bindingResult);
        assertEquals("redirect:/customer/list", view);
        verify(customerService).saveCustomer(customer);
    }

    @Test
    void testProcessRequestEditCustomer_WithErrors_RedirectsToEdit() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = customerController.processRequestEditCustomer(1L, customer, bindingResult);
        assertEquals("redirect:/customer/edit/1", view);
        verify(customerService, never()).saveCustomer(any());
    }

    @Test
    void testShowFormCreateCustomerBasedOnAnotherOne_ReturnsView() {
        when(customerService.showCustomer(1L)).thenReturn(customer);
        String view = customerController.showFormCreateCustomerBasedOnAnotherOne(model, 1L);
        assertEquals("customer/add-customer-based-on-another-one", view);
        verify(model).addAttribute("customer", customer);
    }

    @Test
    void testCreateCustomerBasedOnAnotherOne_NoErrors_RedirectsToList() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(customerService.getMaxId()).thenReturn(5L);

        String view = customerController.createCustomerBasedOnAnotherOne(1L, customer, bindingResult);

        assertEquals("redirect:/customer/list", view);
        verify(customerService).saveCustomer(any(Customer.class));
    }

    @Test
    void testCreateCustomerBasedOnAnotherOne_WithErrors_RedirectsToForm() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = customerController.createCustomerBasedOnAnotherOne(1L, customer, bindingResult);
        assertEquals("redirect:/customer/addCustomerBasedOnAnotherOne/1", view);
        verify(customerService, never()).saveCustomer(any());
    }

    @Test
    void testShowNameSearchForm_ReturnsView() {
        String view = customerController.showNameSearchForm(model);
        assertEquals("customer/name-search", view);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void testProcessRequestNameSearch_ReturnsShowOneView() {
        when(customerService.findOneByEnabledTrueAndName("Corp A")).thenReturn(customer);
        String view = customerController.processRequestNameSearch(customer, model);
        assertEquals("customer/show-one", view);
        verify(model).addAttribute("customer", customer);
    }

    @Test
    void testShowEmailSearchForm_ReturnsView() {
        String view = customerController.showEmailSearchForm(model);
        assertEquals("customer/email-search", view);
    }

    @Test
    void testProcessRequestEmailSearch_ReturnsShowListView() {
        List<Customer> customers = Collections.singletonList(customer);
        when(customerService.findByEnabledTrueAndEmail("a@corp.com")).thenReturn(customers);
        String view = customerController.processRequestEmailSearch(customer, model);
        assertEquals("customer/show-list", view);
        verify(model).addAttribute("customers", customers);
    }

    @Test
    void testShowPhoneSearchForm_ReturnsView() {
        String view = customerController.showPhoneSearchForm(model);
        assertEquals("customer/phone-search", view);
    }

    @Test
    void testProcessRequestPhoneSearch_ReturnsShowListView() {
        List<Customer> customers = Collections.singletonList(customer);
        when(customerService.findByEnabledTrueAndPhone(111222333)).thenReturn(customers);
        String view = customerController.processRequestPhoneSearch(customer, model);
        assertEquals("customer/show-list", view);
        verify(model).addAttribute("customers", customers);
    }

    @Test
    void testShowFirstNameSearchForm_ReturnsView() {
        String view = customerController.showFirstNameSearchForm(model);
        assertEquals("customer/first-name-search", view);
    }

    @Test
    void testProcessRequestFirstNameSearch_ReturnsShowListView() {
        List<Customer> customers = Collections.singletonList(customer);
        when(customerService.findByEnabledTrueAndFirstName("John")).thenReturn(customers);
        String view = customerController.processRequestFirstNameSearch(customer, model);
        assertEquals("customer/show-list", view);
        verify(model).addAttribute("customers", customers);
    }

    @Test
    void testShowLastNameSearchForm_ReturnsView() {
        String view = customerController.showLastNameSearchForm(model);
        assertEquals("customer/last-name-search", view);
    }

    @Test
    void testProcessRequestLastNameSearch_ReturnsShowListView() {
        List<Customer> customers = Collections.singletonList(customer);
        when(customerService.findByEnabledTrueAndLastName("Doe")).thenReturn(customers);
        String view = customerController.processRequestLastNameSearch(customer, model);
        assertEquals("customer/show-list", view);
        verify(model).addAttribute("customers", customers);
    }

    @Test
    void testShowFirstNameLastNameSearchForm_ReturnsView() {
        String view = customerController.showFirstNameLastNameSearchForm(model);
        assertEquals("customer/first-name-last-name-search", view);
    }

    @Test
    void testProcessRequestFirstNameLastNameSearch_ReturnsShowListView() {
        List<Customer> customers = Collections.singletonList(customer);
        when(customerService.findByEnabledTrueAndFirstNameAndLastName("John", "Doe")).thenReturn(customers);
        String view = customerController.processRequestFirstNameLastNameSearch(customer, model);
        assertEquals("customer/show-list", view);
        verify(model).addAttribute("customers", customers);
    }

    @Test
    void testShowCitySearchForm_ReturnsView() {
        String view = customerController.showCitySearchForm(model);
        assertEquals("customer/city-search", view);
    }

    @Test
    void testProcessRequestCitySearch_ReturnsShowListView() {
        List<Customer> customers = Collections.singletonList(customer);
        when(customerService.findByEnabledTrueAndCity("NYC")).thenReturn(customers);
        String view = customerController.processRequestCitySearch(customer, model);
        assertEquals("customer/show-list", view);
        verify(model).addAttribute("customers", customers);
    }

    @Test
    void testShowCityAddressSearchForm_ReturnsView() {
        String view = customerController.showCityAddressSearchForm(model);
        assertEquals("customer/city-address-search", view);
    }

    @Test
    void testProcessRequestCityAddressSearch_ReturnsShowListView() {
        List<Customer> customers = Collections.singletonList(customer);
        when(customerService.findByEnabledTrueAndCityAndAddress("NYC", "5th Ave")).thenReturn(customers);
        String view = customerController.processRequestCityAddressSearch(customer, model);
        assertEquals("customer/show-list", view);
        verify(model).addAttribute("customers", customers);
    }

    @Test
    void testConstructor_WithCustomerService() {
        CustomerController controller = new CustomerController(customerService);
        assertNotNull(controller);
    }
}
