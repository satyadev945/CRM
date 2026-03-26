package crm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for RestTemplate bean.
 * Provides RestTemplate instance for making HTTP calls to external/internal APIs.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates and configures a RestTemplate bean with appropriate timeout settings.
     * 
     * @return RestTemplate instance configured with connection and read timeouts
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // Set connection timeout to 5 seconds
        factory.setConnectTimeout(5000);
        // Set read timeout to 5 seconds
        factory.setReadTimeout(5000);
        return new RestTemplate(factory);
    }
}
