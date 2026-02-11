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
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .id(1L)
                .name("Acme Corp")
                .email("contact@acme.com")
                .phone(123456789)
                .firstName("John")
                .lastName("Smith")
                .city("New York")
                .address("123 Main St")
                .enabled(1)
                .categories(new HashSet<>())
                .build();
    }

    @Test
    void testShowAllCustomers() {
        when(customerService.listAllCustomers()).thenReturn(Arrays.asList(testCustomer));

        String viewName = customerController.showAllCustomers(model);

        assertEquals("customer/list", viewName);
        verify(model).addAttribute(eq("customers"), any());
    }

    @Test
    void testShowFormAddCustomer() {
        String viewName = customerController.showFormAddCustomer(model);

        assertEquals("customer/add", viewName);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void testProcessRequestAddCustomerSuccess() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = customerController.processRequestAddCustomer(testCustomer, bindingResult);

        assertEquals("customer/success", viewName);
        verify(customerService).saveCustomer(testCustomer);
    }

    @Test
    void testProcessRequestAddCustomerWithErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = customerController.processRequestAddCustomer(testCustomer, bindingResult);

        assertEquals("redirect:/customer/add", viewName);
        verify(customerService, never()).saveCustomer(any());
    }

    @Test
    void testShowFormEditCustomer() {
        when(customerService.showCustomer(1L)).thenReturn(testCustomer);

        String viewName = customerController.showFormEditCustomer(model, 1L);

        assertEquals("customer/edit", viewName);
        verify(model).addAttribute("customer", testCustomer);
    }

    @Test
    void testProcessRequestEditCustomerSuccess() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = customerController.processRequestEditCustomer(1L, testCustomer, bindingResult);

        assertEquals("redirect:/customer/list", viewName);
        verify(customerService).saveCustomer(testCustomer);
    }

    @Test
    void testShowFormCreateCustomerBasedOnAnotherOne() {
        when(customerService.showCustomer(1L)).thenReturn(testCustomer);

        String viewName = customerController.showFormCreateCustomerBasedOnAnotherOne(model, 1L);

        assertEquals("customer/add-customer-based-on-another-one", viewName);
        verify(model).addAttribute("customer", testCustomer);
    }

    @Test
    void testCreateCustomerBasedOnAnotherOneSuccess() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(customerService.getMaxId()).thenReturn(10L);

        String viewName = customerController.createCustomerBasedOnAnotherOne(1L, testCustomer, bindingResult);

        assertEquals("redirect:/customer/list", viewName);
        verify(customerService).saveCustomer(any(Customer.class));
    }

    @Test
    void testShowNameSearchForm() {
        String viewName = customerController.showNameSearchForm(model);

        assertEquals("customer/name-search", viewName);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void testProcessRequestNameSearch() {
        when(customerService.findOneByEnabledTrueAndName("Acme Corp")).thenReturn(testCustomer);

        String viewName = customerController.processRequestNameSearch(testCustomer, model);

        assertEquals("customer/show-one", viewName);
        verify(model).addAttribute("customer", testCustomer);
    }

    @Test
    void testShowEmailSearchForm() {
        String viewName = customerController.showEmailSearchForm(model);

        assertEquals("customer/email-search", viewName);
    }

    @Test
    void testProcessRequestEmailSearch() {
        when(customerService.findByEnabledTrueAndEmail("contact@acme.com")).thenReturn(Arrays.asList(testCustomer));

        String viewName = customerController.processRequestEmailSearch(testCustomer, model);

        assertEquals("customer/show-list", viewName);
        verify(model).addAttribute(eq("customers"), any());
    }

    @Test
    void testShowPhoneSearchForm() {
        String viewName = customerController.showPhoneSearchForm(model);

        assertEquals("customer/phone-search", viewName);
    }

    @Test
    void testProcessRequestPhoneSearch() {
        when(customerService.findByEnabledTrueAndPhone(123456789)).thenReturn(Arrays.asList(testCustomer));

        String viewName = customerController.processRequestPhoneSearch(testCustomer, model);

        assertEquals("customer/show-list", viewName);
    }

    @Test
    void testShowCitySearchForm() {
        String viewName = customerController.showCitySearchForm(model);

        assertEquals("customer/city-search", viewName);
    }

    @Test
    void testProcessRequestCitySearch() {
        when(customerService.findByEnabledTrueAndCity("New York")).thenReturn(Arrays.asList(testCustomer));

        String viewName = customerController.processRequestCitySearch(testCustomer, model);

        assertEquals("customer/show-list", viewName);
    }
}
