package crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
// Removed Jsr310JpaConverters import as it's no longer needed in Spring Boot 3.x

@EntityScan(
        basePackageClasses = {CrmApplication.class}
)
@SpringBootApplication
public class CrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrmApplication.class, args);
    }

}
