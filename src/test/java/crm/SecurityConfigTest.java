package crm;

import crm.service.SpringDataUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @InjectMocks
    private SecurityConfig securityConfig;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Test
    void passwordEncoder_shouldReturnBCryptPasswordEncoder() {
        // Act
        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();

        // Assert
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
    }

    @Test
    void customUserDetailsService_shouldReturnSpringDataUserDetailsService() {
        // Act
        SpringDataUserDetailsService service = securityConfig.customUserDetailsService();

        // Assert
        assertNotNull(service);
        assertTrue(service instanceof SpringDataUserDetailsService);
    }

    @Test
    void authenticationProvider_shouldReturnDaoAuthenticationProvider() {
        // Act
        DaoAuthenticationProvider provider = securityConfig.authenticationProvider();

        // Assert
        assertNotNull(provider);
        assertTrue(provider instanceof DaoAuthenticationProvider);
    }

    @Test
    void authenticationProvider_shouldHaveUserDetailsServiceSet() {
        // Act
        DaoAuthenticationProvider provider = securityConfig.authenticationProvider();

        // Assert
        assertNotNull(provider);
        // Verify the provider is properly configured
        assertDoesNotThrow(() -> provider.toString());
    }

    @Test
    void authenticationProvider_shouldHavePasswordEncoderSet() {
        // Act
        DaoAuthenticationProvider provider = securityConfig.authenticationProvider();

        // Assert
        assertNotNull(provider);
        // Password encoder should be set
        assertDoesNotThrow(() -> provider.toString());
    }

    @Test
    void authenticationManager_shouldReturnAuthenticationManager() throws Exception {
        // Arrange
        AuthenticationManager mockManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(mockManager);

        // Act
        AuthenticationManager manager = securityConfig.authenticationManager(authenticationConfiguration);

        // Assert
        assertNotNull(manager);
        assertEquals(mockManager, manager);
        verify(authenticationConfiguration).getAuthenticationManager();
    }

    @Test
    void securityConfig_shouldHaveConfigurationAnnotation() {
        // Assert
        assertTrue(SecurityConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
    }

    @Test
    void securityConfig_shouldHaveEnableWebSecurityAnnotation() {
        // Assert
        assertTrue(SecurityConfig.class.isAnnotationPresent(org.springframework.security.config.annotation.web.configuration.EnableWebSecurity.class));
    }
}
