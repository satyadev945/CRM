package crm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    void passwordEncoderShouldNotBeNull() {
        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder);
    }

    @Test
    void passwordEncoderShouldEncodePassword() {
        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();
        String password = "testPassword";
        String encodedPassword = encoder.encode(password);

        assertNotNull(encodedPassword);
        assertNotEquals(password, encodedPassword);
        assertTrue(encoder.matches(password, encodedPassword));
    }

    @Test
    void customUserDetailsServiceShouldNotBeNull() {
        assertNotNull(securityConfig.customUserDetailsService());
    }
}