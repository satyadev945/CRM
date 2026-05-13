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
                .id(1L)
                .name("TestCo")
                .email("test@example.com")
                .phone(123456789)
                .firstName("John")
                .lastName("Doe")
                .city("NYC")
                .address("123 Main St")
                .enabled(1)
                .build();
    }

    @Test
    void testConstructor() {
        CustomerController controller = new CustomerController(customerService);
        assertNotNull(controller);
    }

    @Test
    void testShowAllCustomers() {
        when(customerService.listAllCustomers()).thenReturn(Arrays.asList(customer));
        String view = customerController.showAllCustomers(model);
        assertEquals("customer/list", view);
        verify(model).addAttribute(eq("customers"), any());
    }

    @Test
    void testShowFormAddCustomer() {
        String view = customerController.showFormAddCustomer(model);
        assertEquals("customer/add", view);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void testProcessRequestAddCustomer_withErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = customerController.processRequestAddCustomer(customer, bindingResult);
        assertEquals("redirect:/customer/add", view);
        verify(customerService, never()).saveCustomer(any());
    }

    @Test
    void testProcessRequestAddCustomer_withoutErrors() {
        when(bindingResult.hasErrors()).thenReturn(false);
        String view = customerController.processRequestAddCustomer(customer, bindingResult);
        assertEquals("customer/success", view);
        verify(customerService).saveCustomer(customer);
    }

    @Test
    void testShowFormEditCustomer() {
        when(customerService.showCustomer(1L)).thenReturn(customer);
        String view = customerController.showFormEditCustomer(model, 1L);
        assertEquals("customer/edit", view);
        verify(model).addAttribute(eq("customer"), eq(customer));
    }

    @Test
    void testProcessRequestEditCustomer_withErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = customerController.processRequestEditCustomer(1L, customer, bindingResult);
        assertEquals("redirect:/customer/edit/1", view);
        verify(customerService, never()).saveCustomer(any());
    }

    @Test
    void testProcessRequestEditCustomer_withoutErrors() {
        when(bindingResult.hasErrors()).thenReturn(false);
        String view = customerController.processRequestEditCustomer(1L, customer, bindingResult);
        assertEquals("redirect:/customer/list", view);
        verify(customerService).saveCustomer(customer);
    }

    @Test
    void testShowFormCreateCustomerBasedOnAnotherOne() {
        when(customerService.showCustomer(1L)).thenReturn(customer);
        String view = customerController.showFormCreateCustomerBasedOnAnotherOne(model, 1L);
        assertEquals("customer/add-customer-based-on-another-one", view);
    }

    @Test
    void testCreateCustomerBasedOnAnotherOne_withErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = customerController.createCustomerBasedOnAnotherOne(1L, customer, bindingResult);
        assertEquals("redirect:/customer/addCustomerBasedOnAnotherOne/1", view);
    }

    @Test
    void testCreateCustomerBasedOnAnotherOne_withoutErrors() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(customerService.getMaxId()).thenReturn(5L);
        String view = customerController.createCustomerBasedOnAnotherOne(1L, customer, bindingResult);
        assertEquals("redirect:/customer/list", view);
        verify(customerService).saveCustomer(any(Customer.class));
    }

    @Test
    void testShowNameSearchForm() {
        String view = customerController.showNameSearchForm(model);
        assertEquals("customer/name-search", view);
    }

    @Test
    void testProcessRequestNameSearch() {
        when(customerService.findOneByEnabledTrueAndName("TestCo")).thenReturn(customer);
        String view = customerController.processRequestNameSearch(customer, model);
        assertEquals("customer/show-one", view);
    }

    @Test
    void testShowEmailSearchForm() {
        String view = customerController.showEmailSearchForm(model);
        assertEquals("customer/email-search", view);
    }

    @Test
    void testProcessRequestEmailSearch() {
        when(customerService.findByEnabledTrueAndEmail("test@example.com")).thenReturn(Arrays.asList(customer));
        String view = customerController.processRequestEmailSearch(customer, model);
        assertEquals("customer/show-list", view);
    }

    @Test
    void testShowPhoneSearchForm() {
        String view = customerController.showPhoneSearchForm(model);
        assertEquals("customer/phone-search", view);
    }

    @Test
    void testProcessRequestPhoneSearch() {
        when(customerService.findByEnabledTrueAndPhone(123456789)).thenReturn(Arrays.asList(customer));
        String view = customerController.processRequestPhoneSearch(customer, model);
        assertEquals("customer/show-list", view);
    }

    @Test
    void testShowFirstNameSearchForm() {
        String view = customerController.showFirstNameSearchForm(model);
        assertEquals("customer/first-name-search", view);
    }

    @Test
    void testProcessRequestFirstNameSearch() {
        when(customerService.findByEnabledTrueAndFirstName("John")).thenReturn(Arrays.asList(customer));
        String view = customerController.processRequestFirstNameSearch(customer, model);
        assertEquals("customer/show-list", view);
    }

    @Test
    void testShowLastNameSearchForm() {
        String view = customerController.showLastNameSearchForm(model);
        assertEquals("customer/last-name-search", view);
    }

    @Test
    void testProcessRequestLastNameSearch() {
        when(customerService.findByEnabledTrueAndLastName("Doe")).thenReturn(Arrays.asList(customer));
        String view = customerController.processRequestLastNameSearch(customer, model);
        assertEquals("customer/show-list", view);
    }

    @Test
    void testShowFirstNameLastNameSearchForm() {
        String view = customerController.showFirstNameLastNameSearchForm(model);
        assertEquals("customer/first-name-last-name-search", view);
    }

    @Test
    void testProcessRequestFirstNameLastNameSearch() {
        when(customerService.findByEnabledTrueAndFirstNameAndLastName("John", "Doe")).thenReturn(Arrays.asList(customer));
        String view = customerController.processRequestFirstNameLastNameSearch(customer, model);
        assertEquals("customer/show-list", view);
    }

    @Test
    void testShowCitySearchForm() {
        String view = customerController.showCitySearchForm(model);
        assertEquals("customer/city-search", view);
    }

    @Test
    void testProcessRequestCitySearch() {
        when(customerService.findByEnabledTrueAndCity("NYC")).thenReturn(Arrays.asList(customer));
        String view = customerController.processRequestCitySearch(customer, model);
        assertEquals("customer/show-list", view);
    }

    @Test
    void testShowCityAddressSearchForm() {
        String view = customerController.showCityAddressSearchForm(model);
        assertEquals("customer/city-address-search", view);
    }

    @Test
    void testProcessRequestCityAddressSearch() {
        when(customerService.findByEnabledTrueAndCityAndAddress("NYC", "123 Main St")).thenReturn(Arrays.asList(customer));
        String view = customerController.processRequestCityAddressSearch(customer, model);
        assertEquals("customer/show-list", view);
    }
}
