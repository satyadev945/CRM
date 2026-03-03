package crm;

import crm.service.SpringDataUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void passwordEncoder_shouldReturnBCryptPasswordEncoder() {
        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();
        
        assertNotNull(encoder);
        assertInstanceOf(BCryptPasswordEncoder.class, encoder);
    }

    @Test
    void passwordEncoder_shouldEncodePassword() {
        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();
        String rawPassword = "testPassword123";
        
        String encodedPassword = encoder.encode(rawPassword);
        
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(encoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void customUserDetailsService_shouldReturnSpringDataUserDetailsService() {
        SpringDataUserDetailsService service = securityConfig.customUserDetailsService();
        
        assertNotNull(service);
        assertInstanceOf(SpringDataUserDetailsService.class, service);
    }

    @Test
    void securityConfig_shouldBeInstantiable() {
        assertNotNull(securityConfig);
    }
}
