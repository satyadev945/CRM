package crm.health;

import crm.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Custom health indicator for storage service.
 * This indicator will be automatically included in Spring Boot Actuator health endpoint.
 */
@Component
@Slf4j
public class StorageHealthIndicator implements HealthIndicator {

    private final StorageService storageService;

    @Autowired
    public StorageHealthIndicator(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public Health health() {
        try {
            // Perform a simple health check by checking if storage service is accessible
            String testFileName = "health-check-test.tmp";
            byte[] testData = "Health check".getBytes();

            // Store a test file
            storageService.storeFile(testFileName, testData);

            // Retrieve the file to confirm it works
            byte[] retrievedData = storageService.getFile(testFileName);

            // Delete the test file
            storageService.deleteFile(testFileName);

            // Compare the data to make sure it was stored and retrieved correctly
            if (new String(retrievedData).equals(new String(testData))) {
                return Health.up()
                        .withDetail("message", "Storage service is functioning correctly")
                        .build();
            } else {
                return Health.down()
                        .withDetail("message", "Storage service data integrity check failed")
                        .build();
            }
        } catch (Exception e) {
            log.warn("Storage health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("message", "Storage service health check failed")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}