package crm;

import crm.controller.CSVController;
import crm.controller.ContractController;
import crm.controller.CustomerController;
import crm.controller.DateTimeTestController;
import crm.controller.Export;
import crm.controller.MyErrorController;
import crm.controller.PdfController;
import crm.controller.RegisterController;
import crm.controller.UserController;
import crm.entity.Category;
import crm.entity.Contract;
import crm.entity.CurrentUser;
import crm.entity.Customer;
import crm.entity.Pdf;
import crm.entity.Role;
import crm.entity.Status;
import crm.entity.User;
import crm.service.ContractService;
import crm.service.ContractServiceImpl;
import crm.service.CustomerService;
import crm.service.CustomerServiceImpl;
import crm.service.PdfService;
import crm.service.PdfServiceImpl;
import crm.service.RoleServiceImpl;
import crm.service.SpringDataUserDetailsService;
import crm.service.UserService;
import crm.service.UserServiceImpl;
import crm.utils.WriteCsvToResponse;
import crm.view.CsvView;
import crm.view.ExcelView;
import crm.view.PdfView;
import crm.viewResolver.CsvViewResolver;
import crm.viewResolver.ExcelViewResolver;
import crm.viewResolver.PdfViewResolver;
import crm.repository.ContractRepository;
import crm.repository.CustomerRepository;
import crm.repository.PdfRepository;
import crm.repository.RoleRepository;
import crm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GeneratedSmokeTest {

    @Test
    void entitiesAndEnums_shouldExposeBasicState() {
        Category category = new Category();
        category.setId(1L);
        category.setName("VIP");
        assertEquals(1L, category.getId());
        assertEquals("VIP", category.getName());

        Role role = new Role();
        role.setId(7);
        role.setName("ROLE_USER");

        User user = User.builder()
                .id(2L)
                .username("john")
                .email("john@example.com")
                .firstName("John")
                .lastName("Smith")
                .password("secret")
                .enabled(1)
                .role(role)
                .build();
        assertEquals("John Smith", user.getName());
        assertEquals(7, user.getRole_id());
        assertEquals("ROLE_USER", user.getRole_name());
        assertTrue(user.getColumnCount() > 0);

        Customer customer = Customer.builder()
                .id(3L)
                .name("Acme")
                .email("acme@example.com")
                .phone(123)
                .categories(Set.of(category))
                .firstName("Ann")
                .lastName("Lee")
                .city("NY")
                .address("Main")
                .enabled(1)
                .build();
        assertEquals("Acme", customer.getName());
        assertEquals(1, customer.getEnabled());

        Contract contract = Contract.builder()
                .id(4L)
                .name("C1")
                .content("Body")
                .value(BigDecimal.TEN)
                .beginDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .status(Status.DONE)
                .customer(customer)
                .user(user)
                .build();
        assertEquals("C1", contract.getName());
        assertEquals(Status.DONE, contract.getStatus());

        Pdf pdf = Pdf.builder().id(5L).name("file").content("content").build();
        assertEquals("file", pdf.getName());
        assertEquals("content", pdf.getContent());

        assertEquals(4, Status.ALL.length);
        assertEquals(Status.PROPOSED, Status.valueOf("PROPOSED"));
    }

    @Test
    void currentUser_shouldDelegateToWrappedUser() {
        Role role = new Role();
        role.setName("ROLE_USER");
        User user = User.builder().username("anna").password("pwd").role(role).build();
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUser(user);
        currentUser.setAuthorities(Set.of(new SimpleGrantedAuthority("ROLE_USER")));

        assertEquals("anna", currentUser.getUsername());
        assertEquals("pwd", currentUser.getPassword());
        assertEquals(1, currentUser.getAuthorities().size());
        assertTrue(currentUser.isAccountNonExpired());
        assertTrue(currentUser.isAccountNonLocked());
        assertTrue(currentUser.isCredentialsNonExpired());
        assertTrue(currentUser.isEnabled());
    }

    @Test
    void serviceImplementations_shouldDelegateToRepositories() {
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        CustomerServiceImpl customerService = new CustomerServiceImpl(customerRepository);
        Customer customer = Customer.builder().id(10L).name("Acme").build();
        when(customerRepository.findTopByOrderByIdDesc()).thenReturn(customer);
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));
        when(customerRepository.findAll()).thenReturn(List.of(customer));
        when(customerRepository.findAllByEnabled(1)).thenReturn(List.of(customer));
        when(customerRepository.findAllByEnabled(0)).thenReturn(List.of());
        when(customerRepository.findOneByEnabledAndName(1, "Acme")).thenReturn(customer);
        when(customerRepository.findOneByEnabledAndName(0, "Acme")).thenReturn(null);
        when(customerRepository.findOneByName("Acme")).thenReturn(customer);
        when(customerRepository.findByEnabledAndEmail(1, "a@b.com")).thenReturn(List.of(customer));
        when(customerRepository.findByEnabledAndEmail(0, "a@b.com")).thenReturn(List.of());
        when(customerRepository.findByEmail("a@b.com")).thenReturn(List.of(customer));
        when(customerRepository.findByEnabledAndPhone(1, 1)).thenReturn(List.of(customer));
        when(customerRepository.findByEnabledAndPhone(0, 1)).thenReturn(List.of());
        when(customerRepository.findByPhone(1)).thenReturn(List.of(customer));
        when(customerRepository.findByEnabledAndFirstName(1, "Ann")).thenReturn(List.of(customer));
        when(customerRepository.findByEnabledAndFirstName(0, "Ann")).thenReturn(List.of());
        when(customerRepository.findByFirstName("Ann")).thenReturn(List.of(customer));
        when(customerRepository.findByEnabledAndLastName(1, "Lee")).thenReturn(List.of(customer));
        when(customerRepository.findByEnabledAndLastName(0, "Lee")).thenReturn(List.of());
        when(customerRepository.findByLastName("Lee")).thenReturn(List.of(customer));
        when(customerRepository.findByEnabledAndFirstNameAndLastName(1, "Ann", "Lee")).thenReturn(List.of(customer));
        when(customerRepository.findByEnabledAndFirstNameAndLastName(0, "Ann", "Lee")).thenReturn(List.of());
        when(customerRepository.findByFirstNameAndLastName("Ann", "Lee")).thenReturn(List.of(customer));
        when(customerRepository.findByEnabledAndCity(1, "NY")).thenReturn(List.of(customer));
        when(customerRepository.findByEnabledAndCity(0, "NY")).thenReturn(List.of());
        when(customerRepository.findByCity("NY")).thenReturn(List.of(customer));
        when(customerRepository.findByEnabledAndCityAndAddress(1, "NY", "Main")).thenReturn(List.of(customer));
        when(customerRepository.findByEnabledAndCityAndAddress(0, "NY", "Main")).thenReturn(List.of());
        when(customerRepository.findByCityAndAddress("NY", "Main")).thenReturn(List.of(customer));
        Set<Category> categories = Set.of(new Category());
        when(customerRepository.findByEnabledAndCategories(1, categories)).thenReturn(List.of(customer));
        when(customerRepository.findByEnabledAndCategories(0, categories)).thenReturn(List.of());
        when(customerRepository.findByCategories(categories)).thenReturn(List.of(customer));

        assertEquals(10L, customerService.getMaxId());
        assertNotNull(customerService.listAllCustomers());
        assertEquals(customer, customerService.showCustomer(10L));
        assertNotNull(customerService.findAllByEnabledTrue());
        assertNotNull(customerService.findAllByEnabledFalse());
        assertEquals(customer, customerService.findOneByEnabledTrueAndName("Acme"));
        assertNull(customerService.findOneByEnabledFalseAndName("Acme"));
        assertEquals(customer, customerService.findOneByName("Acme"));
        assertNotNull(customerService.findByEnabledTrueAndEmail("a@b.com"));
        assertNotNull(customerService.findByEnabledFalseAndEmail("a@b.com"));
        assertNotNull(customerService.findByEmail("a@b.com"));
        assertNotNull(customerService.findByEnabledTrueAndPhone(1));
        assertNotNull(customerService.findByEnabledFalseAndPhone(1));
        assertNotNull(customerService.findByPhone(1));
        assertNotNull(customerService.findByEnabledTrueAndCategories(categories));
        assertNotNull(customerService.findByEnabledFalseAndCategories(categories));
        assertNotNull(customerService.findByCategories(categories));
        assertNotNull(customerService.findByEnabledTrueAndFirstName("Ann"));
        assertNotNull(customerService.findByEnabledFalseAndFirstName("Ann"));
        assertNotNull(customerService.findByFirstName("Ann"));
        assertNotNull(customerService.findByEnabledTrueAndLastName("Lee"));
        assertNotNull(customerService.findByEnabledFalseAndLastName("Lee"));
        assertNotNull(customerService.findByLastName("Lee"));
        assertNotNull(customerService.findByEnabledTrueAndFirstNameAndLastName("Ann", "Lee"));
        assertNotNull(customerService.findByEnabledFalseAndFirstNameAndLastName("Ann", "Lee"));
        assertNotNull(customerService.findByFirstNameAndLastName("Ann", "Lee"));
        assertNotNull(customerService.findByEnabledTrueAndCity("NY"));
        assertNotNull(customerService.findByEnabledFalseAndCity("NY"));
        assertNotNull(customerService.findByCity("NY"));
        assertNotNull(customerService.findByEnabledTrueAndCityAndAddress("NY", "Main"));
        assertNotNull(customerService.findByEnabledFalseAndCityAndAddress("NY", "Main"));
        assertNotNull(customerService.findByCityAndAddress("NY", "Main"));
        customerService.saveCustomer(customer);
        assertEquals(1, customer.getEnabled());
        verify(customerRepository).save(customer);

        ContractRepository contractRepository = mock(ContractRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        ContractServiceImpl contractService = new ContractServiceImpl(contractRepository, customerRepository, userRepository);
        Contract contract = Contract.builder().name("Deal").build();
        when(contractRepository.findByName("Deal")).thenReturn(contract);
        when(contractRepository.findAll()).thenReturn(List.of(contract));
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(contractRepository.findAllByValueLessThanEqual(BigDecimal.ONE)).thenReturn(List.of(contract));
        when(contractRepository.findAllByValueGreaterThanEqual(BigDecimal.ONE)).thenReturn(List.of(contract));
        LocalDate now = LocalDate.now();
        when(contractRepository.findAllByBeginDate(now)).thenReturn(List.of(contract));
        when(contractRepository.findAllByBeginDateBefore(now)).thenReturn(List.of(contract));
        when(contractRepository.findAllByBeginDateAfter(now)).thenReturn(List.of(contract));
        when(contractRepository.findAllByEndDate(now)).thenReturn(List.of(contract));
        when(contractRepository.findAllByEndDateBefore(now)).thenReturn(List.of(contract));
        when(contractRepository.findAllByEndDateAfter(now)).thenReturn(List.of(contract));
        when(contractRepository.findAllByStatus(Status.DONE)).thenReturn(List.of(contract));
        when(contractRepository.findAllByCustomer(customer)).thenReturn(List.of(contract));
        when(contractRepository.findAllByCustomerAndUser(any(), any())).thenReturn(List.of(contract));
        when(contractRepository.findAllByUser(any())).thenReturn(List.of(contract));
        assertEquals(contract, contractService.findByName("Deal"));
        assertNotNull(contractService.listAllContracts());
        assertEquals(contract, contractService.showContract(1L));
        assertNotNull(contractService.findAllByValueLessThanEqual(BigDecimal.ONE));
        assertNotNull(contractService.findAllByValueGreaterThanEqual(BigDecimal.ONE));
        assertNotNull(contractService.findAllByBeginDate(now));
        assertNotNull(contractService.findAllByBeginDateBefore(now));
        assertNotNull(contractService.findAllByBeginDateAfter(now));
        assertNotNull(contractService.findAllByEndDate(now));
        assertNotNull(contractService.findAllByEndDateBefore(now));
        assertNotNull(contractService.findAllByEndDateAfter(now));
        assertNotNull(contractService.findAllByStatus(Status.DONE));
        assertNotNull(contractService.findAllByCustomer(customer));
        User contractUser = User.builder().username("agent").build();
        assertNotNull(contractService.findAllByCustomerAndUser(customer, contractUser));
        assertNotNull(contractService.findAllByUser(contractUser));
        contractService.saveContract(contract);
        verify(contractRepository).save(contract);

        PdfRepository pdfRepository = mock(PdfRepository.class);
        PdfServiceImpl pdfService = new PdfServiceImpl(pdfRepository);
        Pdf pdf = Pdf.builder().name("f").build();
        when(pdfRepository.findByName("f")).thenReturn(pdf);
        assertEquals(pdf, pdfService.findByName("f"));
        pdfService.savePdf(pdf);
        verify(pdfRepository).save(pdf);

        RoleRepository roleRepository = mock(RoleRepository.class);
        RoleServiceImpl roleService = new RoleServiceImpl(roleRepository);
        Role listedRole = new Role();
        listedRole.setName("ROLE_LISTED");
        when(roleRepository.findAll()).thenReturn(List.of(listedRole));
        assertNotNull(roleService.listAllRoles());
    }

    @Test
    void springDataUserDetailsService_shouldHandleUserFoundAndMissing() throws Exception {
        SpringDataUserDetailsService service = new SpringDataUserDetailsService();
        UserService userService = mock(UserService.class);
        java.lang.reflect.Field field = SpringDataUserDetailsService.class.getDeclaredField("userService");
        field.setAccessible(true);
        field.set(service, userService);

        Role role = new Role();
        role.setName("ROLE_ADMIN");
        User user = User.builder().username("root").password("pw").role(role).build();
        when(userService.findByUsername("root")).thenReturn(user);
        assertEquals("root", service.loadUserByUsername("root").getUsername());
        assertThrows(Exception.class, () -> service.loadUserByUsername("missing"));
    }

    @Test
    void userServiceImpl_shouldSaveEditAndDeleteUser() {
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = mock(org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        SpringDataUserDetailsService detailsService = mock(SpringDataUserDetailsService.class);

        UserServiceImpl service = new UserServiceImpl();
        service.setUserRepository(userRepository);
        service.setRoleRepository(roleRepository);
        service.setPasswordEncoder((org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder) encoder);
        service.setAuthenticationManager(authenticationManager);
        service.setSpringDataUserDetailsService(detailsService);

        Role userRole = new Role();
        userRole.setId(1);
        userRole.setName("ROLE_USER");
        Role adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName("ROLE_ADMIN");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(userRole);
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(adminRole);
        when(encoder.encode("plain")).thenReturn("encoded");
        User user = User.builder().id(1L).username("john").password("plain").build();
        when(detailsService.loadUserByUsername("john")).thenReturn(new org.springframework.security.core.userdetails.User("john", "encoded", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
        when(authenticationManager.authenticate(any())).thenReturn(new TestingAuthenticationToken("john", "plain"));

        service.saveUser(user);
        assertEquals(1, user.getEnabled());
        assertEquals("encoded", user.getPassword());
        verify(userRepository, atLeastOnce()).save(user);

        when(userRepository.findByUsername("john")).thenReturn(user);
        when(userRepository.findAllByEnabled(1)).thenReturn(List.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertEquals(user, service.findByUsername("john"));
        assertNotNull(service.listAllUsers());
        assertEquals(user, service.showUser(1L));

        when(roleRepository.findById(2)).thenReturn(Optional.of(adminRole));
        user.setRole(adminRole);
        user.setPassword("plain");
        service.editUser(user);
        verify(userRepository, atLeast(2)).save(user);

        User noRoleUser = User.builder().password("plain").build();
        service.editUser(noRoleUser);
        assertEquals(userRole, noRoleUser.getRole());

        service.deleteUser(user);
        assertEquals(0, user.getEnabled());
        assertNull(user.getPassword());
    }

    @Test
    void controllers_shouldReturnExpectedViews() throws Exception {
        CustomerService customerService = mock(CustomerService.class);
        UserService userService = mock(UserService.class);
        ContractService contractService = mock(ContractService.class);
        PdfService pdfService = mock(PdfService.class);

        CustomerController customerController = new CustomerController(customerService);
        UserController userController = new UserController(userService);
        ContractController contractController = new ContractController(contractService, customerService, userService);
        CSVController csvController = new CSVController(customerService);
        DateTimeTestController dateController = new DateTimeTestController();
        Export exportController = new Export(userService);
        MyErrorController errorController = new MyErrorController();
        PdfController pdfController = new PdfController(pdfService);
        RegisterController registerController = new RegisterController(userService);

        Model model = new ConcurrentModel();
        when(customerService.listAllCustomers()).thenReturn(List.of());
        when(customerService.showCustomer(1L)).thenReturn(new Customer());
        when(customerService.getMaxId()).thenReturn(9L);
        when(customerService.findAllByEnabledTrue()).thenReturn(List.of());
        when(customerService.findOneByEnabledTrueAndName(anyString())).thenReturn(new Customer());
        when(customerService.findByEnabledTrueAndEmail(anyString())).thenReturn(List.of());
        when(customerService.findByEnabledTrueAndPhone(anyInt())).thenReturn(List.of());
        when(customerService.findByEnabledTrueAndFirstName(anyString())).thenReturn(List.of());
        when(customerService.findByEnabledTrueAndLastName(anyString())).thenReturn(List.of());
        when(customerService.findByEnabledTrueAndFirstNameAndLastName(anyString(), anyString())).thenReturn(List.of());
        when(customerService.findByEnabledTrueAndCity(anyString())).thenReturn(List.of());
        when(customerService.findByEnabledTrueAndCityAndAddress(anyString(), anyString())).thenReturn(List.of());
        when(userService.listAllUsers()).thenReturn(List.of());
        when(userService.showUser(1L)).thenReturn(User.builder().username("john").build());
        when(userService.findByUsername("john")).thenReturn(User.builder().username("john").build());
        when(contractService.listAllContracts()).thenReturn(List.of());
        when(contractService.showContract(1L)).thenReturn(new Contract());
        when(contractService.findByName(anyString())).thenReturn(new Contract());
        when(contractService.findAllByValueLessThanEqual(any())).thenReturn(List.of());
        when(contractService.findAllByValueGreaterThanEqual(any())).thenReturn(List.of());
        when(contractService.findAllByBeginDate(any())).thenReturn(List.of());
        when(contractService.findAllByBeginDateBefore(any())).thenReturn(List.of());
        when(contractService.findAllByBeginDateAfter(any())).thenReturn(List.of());
        when(contractService.findAllByEndDate(any())).thenReturn(List.of());
        when(contractService.findAllByEndDateBefore(any())).thenReturn(List.of());
        when(contractService.findAllByEndDateAfter(any())).thenReturn(List.of());
        when(contractService.findAllByStatus(any())).thenReturn(List.of());
        when(contractService.findAllByCustomer(any())).thenReturn(List.of());
        when(contractService.findAllByCustomerAndUser(any(), any())).thenReturn(List.of());
        when(contractService.findAllByUser(any())).thenReturn(List.of());

        assertEquals("customer/list", customerController.showAllCustomers(model));
        assertEquals("customer/add", customerController.showFormAddCustomer(model));
        assertEquals("customer/success", customerController.processRequestAddCustomer(new Customer(), new BeanPropertyBindingResult(new Customer(), "customer")));
        BindingResult errors = mock(BindingResult.class); when(errors.hasErrors()).thenReturn(true);
        assertEquals("redirect:/customer/add", customerController.processRequestAddCustomer(new Customer(), errors));
        assertEquals("customer/edit", customerController.showFormEditCustomer(model, 1L));
        assertEquals("redirect:/customer/list", customerController.processRequestEditCustomer(1L, new Customer(), new BeanPropertyBindingResult(new Customer(), "customer")));
        assertEquals("redirect:/customer/edit/1", customerController.processRequestEditCustomer(1L, new Customer(), errors));
        assertEquals("customer/add-customer-based-on-another-one", customerController.showFormCreateCustomerBasedOnAnotherOne(model, 1L));
        Customer sourceCustomer = Customer.builder().name("Acme").email("a@b.com").phone(1).categories(Set.of()).firstName("A").lastName("B").city("C").address("D").enabled(1).build();
        assertEquals("redirect:/customer/list", customerController.createCustomerBasedOnAnotherOne(1L, sourceCustomer, new BeanPropertyBindingResult(sourceCustomer, "customer")));
        assertEquals("redirect:/customer/addCustomerBasedOnAnotherOne/1", customerController.createCustomerBasedOnAnotherOne(1L, sourceCustomer, errors));
        assertEquals("customer/name-search", customerController.showNameSearchForm(model));
        assertEquals("customer/show-one", customerController.processRequestNameSearch(sourceCustomer, model));
        assertEquals("customer/email-search", customerController.showEmailSearchForm(model));
        assertEquals("customer/show-list", customerController.processRequestEmailSearch(sourceCustomer, model));
        assertEquals("customer/phone-search", customerController.showPhoneSearchForm(model));
        assertEquals("customer/show-list", customerController.processRequestPhoneSearch(sourceCustomer, model));
        assertEquals("customer/first-name-search", customerController.showFirstNameSearchForm(model));
        assertEquals("customer/show-list", customerController.processRequestFirstNameSearch(sourceCustomer, model));
        assertEquals("customer/last-name-search", customerController.showLastNameSearchForm(model));
        assertEquals("customer/show-list", customerController.processRequestLastNameSearch(sourceCustomer, model));
        assertEquals("customer/first-name-last-name-search", customerController.showFirstNameLastNameSearchForm(model));
        assertEquals("customer/show-list", customerController.processRequestFirstNameLastNameSearch(sourceCustomer, model));
        assertEquals("customer/city-search", customerController.showCitySearchForm(model));
        assertEquals("customer/show-list", customerController.processRequestCitySearch(sourceCustomer, model));
        assertEquals("customer/city-address-search", customerController.showCityAddressSearchForm(model));
        assertEquals("customer/show-list", customerController.processRequestCityAddressSearch(sourceCustomer, model));

        org.springframework.security.core.userdetails.UserDetails principal = new org.springframework.security.core.userdetails.User("john", "pw", List.of());
        assertEquals("user/list", userController.showAllUsers(model, principal));
        assertEquals("user/edit", userController.showFormEditUser(model, 1L));
        assertEquals("redirect:/user/list", userController.processRequestEditUser(1L, User.builder().build(), new BeanPropertyBindingResult(new User(), "user")));
        assertEquals("redirect:/user/edit/1", userController.processRequestEditUser(1L, User.builder().build(), errors));
        assertEquals("redirect:/user/list", userController.deleteUser(1L));

        assertEquals("contract/list", contractController.showAllContracts(model));
        assertEquals("contract/add", contractController.showFormAddContract(model));
        assertEquals("contract/success", contractController.processRequestAddContract(new Contract(), new BeanPropertyBindingResult(new Contract(), "contract")));
        assertEquals("redirect:/contract/add", contractController.processRequestAddContract(new Contract(), errors));
        assertEquals("contract/edit", contractController.showFormEditContract(model, 1L));
        assertEquals("redirect:/contract/list", contractController.processRequestEditContract(1L, new Contract(), new BeanPropertyBindingResult(new Contract(), "contract")));
        assertEquals("redirect:/contract/edit/1", contractController.processRequestEditContract(1L, new Contract(), errors));
        assertEquals("contract/name-search", contractController.showNameSearchForm(model));
        assertEquals("contract/show-one", contractController.processRequestNameSearch(new Contract(), model));
        assertEquals("contract/value-le-search", contractController.showValueLeesThanEqualSearchForm(model));
        assertEquals("contract/show-list", contractController.processRequestValueLessThanEqualSearch(new Contract(), model));
        assertEquals("contract/value-ge-search", contractController.showValueGreaterThanEqualSearchForm(model));
        assertEquals("contract/show-list", contractController.processRequestValueGreaterThanEqualSearch(new Contract(), model));
        assertEquals("contract/begin-date-search", contractController.showBeginDateSearchForm(model));
        assertEquals("contract/show-list", contractController.processRequestBeginDateSearch(new Contract(), model));
        assertEquals("contract/begin-date-before-search", contractController.showBeginDateBeforeSearchForm(model));
        assertEquals("contract/show-list", contractController.processRequestBeginDateBeforeSearch(new Contract(), model));
        assertEquals("contract/begin-date-after-search", contractController.showBeginDateAfterSearchForm(model));
        assertEquals("contract/show-list", contractController.processRequestBeginDateAfterSearch(new Contract(), model));
        assertEquals("contract/end-date-search", contractController.showEndDateSearchForm(model));
        assertEquals("contract/show-list", contractController.processRequestEndDateSearch(new Contract(), model));
        assertEquals("contract/end-date-before-search", contractController.showEndDateBeforeSearchForm(model));
        assertEquals("contract/show-list", contractController.processRequestEndDateBeforeSearch(new Contract(), model));
        assertEquals("contract/end-date-after-search", contractController.showEndDateAfterSearchForm(model));
        assertEquals("contract/show-list", contractController.processRequestEndDateAfterSearch(new Contract(), model));
        assertEquals("contract/status-search", contractController.showStatusSearchForm(model));
        assertEquals("contract/show-list", contractController.processRequestStatusSearch(new Contract(), model));
        assertEquals("contract/customer-search", contractController.showCustomerSearchForm(model));
        assertEquals("contract/show-list", contractController.processRequestCustomerSearch(new Contract(), model));
        assertEquals("contract/customer-user-search", contractController.showCustomerUserSearchForm(model));
        assertEquals("contract/show-list", contractController.processRequestCustomerUserSearch(new Contract(), model));
        assertEquals("contract/user-search", contractController.showUserSearchForm(model));
        assertEquals("contract/show-list", contractController.processRequestUserSearch(new Contract(), model));

        MockHttpServletResponse response = new MockHttpServletResponse();
        csvController.findCustomers(response);
        csvController.findCustomer(1L, response);
        assertEquals("date/test", dateController.dateTimeTest(model));
        assertEquals("excelView", exportController.download(model));
        assertThrows(ResponseStatusException.class, errorController::error);
        assertEquals("pdf/generator", pdfController.pdfGenerator(model));
        assertEquals("redirect:/pdf-generator", pdfController.generatePdf(new Pdf(), errors));
        assertEquals("register", registerController.showRegistrationPage(model, new User()));
        when(userService.findByUsername("new")).thenReturn(null);
        User newUser = User.builder().username("new").build();
        assertEquals("success", registerController.processRegistrationForm(model, newUser, new BeanPropertyBindingResult(newUser, "user")));
        when(userService.findByUsername("exists")).thenReturn(User.builder().username("exists").build());
        BeanPropertyBindingResult registerResult = new BeanPropertyBindingResult(User.builder().username("exists").build(), "user");
        assertEquals("register", registerController.processRegistrationForm(model, User.builder().username("exists").build(), registerResult));
    }

    @Test
    void configurationAndViews_shouldProduceBeansAndOutput(@TempDir Path tempDir) throws Exception {
        SecurityConfig securityConfig = new SecurityConfig();
        assertNotNull(securityConfig.passwordEncoder());
        assertNotNull(securityConfig.customUserDetailsService());
        assertNotNull(securityConfig.authenticationProvider());

        WebAppConfig webAppConfig = new WebAppConfig();
        assertNotNull(webAppConfig.templateResolver());
        assertNotNull(webAppConfig.templateEngine());
        assertNotNull(webAppConfig.viewResolver());
        assertNotNull(webAppConfig.csvViewResolver());
        assertNotNull(webAppConfig.excelViewResolver());
        assertNotNull(webAppConfig.pdfViewResolver());
        assertNotNull(new CsvViewResolver().resolveViewName("csvView", Locale.ENGLISH));
        assertNull(new CsvViewResolver().resolveViewName("other", Locale.ENGLISH));
        assertNotNull(new ExcelViewResolver().resolveViewName("excelView", Locale.ENGLISH));
        assertNull(new ExcelViewResolver().resolveViewName("other", Locale.ENGLISH));
        assertNotNull(new PdfViewResolver().resolveViewName("pdfView", Locale.ENGLISH));
        assertNull(new PdfViewResolver().resolveViewName("other", Locale.ENGLISH));

        Customer customer = Customer.builder().id(1L).name("Acme").email("a@b.com").phone(123).firstName("Ann").lastName("Lee").city("NY").address("Main").enabled(1).build();
        java.io.StringWriter sw = new java.io.StringWriter();
        WriteCsvToResponse.writeCustomer(new PrintWriter(sw), customer);
        assertTrue(sw.toString().contains("Acme"));

        java.io.StringWriter sw2 = new java.io.StringWriter();
        WriteCsvToResponse.writeCustomers(new PrintWriter(sw2), List.of(customer));
        assertTrue(sw2.toString().contains("Acme"));

        Role role = new Role(); role.setId(1); role.setName("ROLE_USER");
        User user = User.builder().firstName("John").lastName("Doe").username("jdoe").email("j@d.com").password("pw").enabled(1).role(role).build();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        new CsvView().render(java.util.Map.of("users", List.of(user)), request, response);
        assertTrue(response.getContentAsString().contains("jdoe"));

        MockHttpServletResponse excelResponse = new MockHttpServletResponse();
        new ExcelView().render(java.util.Map.of("users", List.of(user)), request, excelResponse);
        assertTrue(excelResponse.getContentAsByteArray().length > 0);

        MockHttpServletResponse pdfResponse = new MockHttpServletResponse();
        new PdfView().render(java.util.Map.of("users", List.of(user)), request, pdfResponse);
        assertTrue(pdfResponse.getContentAsByteArray().length > 0);

        Path pdfPath = tempDir.resolve("sample.pdf");
        PdfService pdfService = mock(PdfService.class);
        PdfController controller = new PdfController(pdfService);
        Pdf pdf = Pdf.builder().name(pdfPath.toString()).content("hello").build();
        assertEquals("pdf/success", controller.generatePdf(pdf, new BeanPropertyBindingResult(pdf, "pdf")));
        assertTrue(Files.exists(pdfPath));
    }

    @Test
    void mainClass_shouldStartViaMockedStatic() {
        try (var mocked = Mockito.mockStatic(org.springframework.boot.SpringApplication.class)) {
            CrmApplication.main(new String[]{});
            mocked.verify(() -> org.springframework.boot.SpringApplication.run(CrmApplication.class, new String[]{}));
        }
    }
}
