package crm;

import crm.service.SpringDataUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    void securityConfig_shouldBeLoadedAsBean() {
        assertNotNull(securityConfig);
    }

    @Test
    void passwordEncoder_shouldBeConfigured() {
        BCryptPasswordEncoder encoder = applicationContext.getBean(BCryptPasswordEncoder.class);
        assertNotNull(encoder);
    }

    @Test
    void passwordEncoder_shouldEncodePasswords() {
        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();
        String rawPassword = "testPassword123";
        String encodedPassword = encoder.encode(rawPassword);
        
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(encoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void passwordEncoder_shouldProduceDifferentHashesForSamePassword() {
        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();
        String password = "samePassword";
        String hash1 = encoder.encode(password);
        String hash2 = encoder.encode(password);
        
        assertNotEquals(hash1, hash2);
        assertTrue(encoder.matches(password, hash1));
        assertTrue(encoder.matches(password, hash2));
    }

    @Test
    void customUserDetailsService_shouldBeConfigured() {
        SpringDataUserDetailsService service = applicationContext.getBean(SpringDataUserDetailsService.class);
        assertNotNull(service);
    }

    @Test
    void customUserDetailsService_shouldReturnNewInstance() {
        SpringDataUserDetailsService service = securityConfig.customUserDetailsService();
        assertNotNull(service);
    }

    @Test
    void authenticationManager_shouldBeConfigured() {
        AuthenticationManager authManager = applicationContext.getBean(AuthenticationManager.class);
        assertNotNull(authManager);
    }

    @Test
    void securityFilterChain_shouldBeConfigured() {
        SecurityFilterChain filterChain = applicationContext.getBean(SecurityFilterChain.class);
        assertNotNull(filterChain);
    }

    @Test
    void securityConfig_shouldHaveEnableWebSecurityAnnotation() {
        assertTrue(SecurityConfig.class.isAnnotationPresent(EnableWebSecurity.class));
    }

    @Test
    void securityConfig_shouldHaveEnableMethodSecurityAnnotation() {
        assertTrue(SecurityConfig.class.isAnnotationPresent(EnableMethodSecurity.class));
    }

    @Test
    void enableMethodSecurity_shouldHaveSecuredEnabled() {
        EnableMethodSecurity annotation = SecurityConfig.class.getAnnotation(EnableMethodSecurity.class);
        assertNotNull(annotation);
        assertTrue(annotation.securedEnabled());
    }

    @Test
    void passwordEncoder_shouldNotMatchWrongPassword() {
        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();
        String password = "correctPassword";
        String encodedPassword = encoder.encode(password);
        
        assertFalse(encoder.matches("wrongPassword", encodedPassword));
    }

    @Test
    void passwordEncoder_shouldHandleEmptyPassword() {
        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();
        String emptyPassword = "";
        String encodedPassword = encoder.encode(emptyPassword);
        
        assertNotNull(encodedPassword);
        assertTrue(encoder.matches(emptyPassword, encodedPassword));
    }
}
