package crm.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * Configuration component that logs active profiles on startup.
 * Useful for verifying which environment configuration is active in cloud deployments.
 */
@Component
@Slf4j
public class ProfileConfig {

    private final Environment environment;

    @Autowired
    public ProfileConfig(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void logProfiles() {
        log.info("Active profiles: {}", Arrays.toString(environment.getActiveProfiles()));
        if (environment.getActiveProfiles().length == 0) {
            log.info("No active profiles set. Using default profile.");
        }

        // Log if running in cloud environment
        if (isCloudEnvironment()) {
            log.info("Application is running in cloud environment");
        } else {
            log.info("Application is running in local/development environment");
        }
    }

    /**
     * Check if the application is running in a cloud environment
     * by checking active profiles and environment variables
     */
    public boolean isCloudEnvironment() {
        // Check for cloud profiles
        for (String profile : environment.getActiveProfiles()) {
            if (profile.equals("cloud") || profile.equals("prod") || profile.equals("aws")) {
                return true;
            }
        }

        // Check for cloud platform environment variables
        return environment.containsProperty("DYNO") || // Heroku
               environment.containsProperty("VCAP_APPLICATION") || // Cloud Foundry
               environment.containsProperty("AWS_EXECUTION_ENV") || // AWS
               environment.containsProperty("K_SERVICE") || // Google Cloud Run
               environment.containsProperty("WEBSITE_SITE_NAME"); // Azure App Service
    }
}