package crm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for HTTP request logging in cloud environments.
 * Provides structured logging of incoming requests for monitoring and debugging.
 */
@Configuration
public class RequestLoggingConfig implements WebMvcConfigurer {

    /**
     * Configure request logging filter
     * This logs request details at DEBUG level
     */
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(false);
        filter.setIncludeHeaders(false);
        filter.setMaxPayloadLength(10000); // Limit payload size
        filter.setAfterMessagePrefix("REQUEST DATA: ");
        return filter;
    }
}