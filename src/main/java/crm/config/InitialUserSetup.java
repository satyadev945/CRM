package crm.config;

import crm.entity.Role;
import crm.entity.User;
import crm.repository.RoleRepository;
import crm.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class to set up initial admin user when application starts.
 * The admin credentials are loaded from environment variables for cloud security.
 * This only runs when the "prod" or "cloud" profiles are active.
 */
@Configuration
@Slf4j
@Profile({"prod", "cloud", "aws"})
public class InitialUserSetup {

    @Value("${admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.password:}")
    private String adminPassword;

    @Value("${admin.firstname:Admin}")
    private String adminFirstName;

    @Value("${admin.lastname:User}")
    private String adminLastName;

    @Bean
    public CommandLineRunner setupAdminUser(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            if (adminPassword.isEmpty()) {
                log.warn("No admin password set in environment variables. Skipping admin user creation.");
                return;
            }

            log.info("Setting up initial admin user from environment variables...");

            // Create admin role if not exists
            Role adminRole = roleRepository.findByRole("ROLE_ADMIN");
            if (adminRole == null) {
                adminRole = new Role();
                adminRole.setRole("ROLE_ADMIN");
                adminRole = roleRepository.save(adminRole);
                log.info("Created ROLE_ADMIN role");
            }

            // Check if admin user already exists
            User existingAdmin = userRepository.findByUsername(adminUsername);
            if (existingAdmin == null) {
                User adminUser = new User();
                adminUser.setEmail(adminEmail);
                adminUser.setUsername(adminUsername);
                adminUser.setPassword(passwordEncoder.encode(adminPassword));
                adminUser.setFirstName(adminFirstName);
                adminUser.setLastName(adminLastName);
                adminUser.setEnabled(true);
                adminUser.setRole(adminRole);

                userRepository.save(adminUser);
                log.info("Created admin user: {}", adminUsername);
            } else {
                log.info("Admin user already exists: {}", adminUsername);
            }
        };
    }
}