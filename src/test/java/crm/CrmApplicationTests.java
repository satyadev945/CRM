package crm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import crm.service.SpringDataUserDetailsService;
import crm.repository.UserRepository;
import crm.repository.RoleRepository;
import crm.repository.CustomerRepository;
import crm.repository.ContractRepository;
import crm.repository.PdfRepository;
import crm.repository.CategoryRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;NON_KEYWORDS=VALUE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.sql.init.mode=never"
})
public class CrmApplicationTests {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private ContractRepository contractRepository;

    @MockBean
    private PdfRepository pdfRepository;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private SpringDataUserDetailsService springDataUserDetailsService;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    public void contextLoads() {
        // Verifies that the Spring application context loads successfully with mocked beans
        assertTrue(true);
    }

}
