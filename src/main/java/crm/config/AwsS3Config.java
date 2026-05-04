package crm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * AWS S3 configuration for cloud-native object storage.
 * Provides a Spring-managed S3Client bean using the default AWS credential provider chain,
 * which supports IAM roles, environment variables (AWS_ACCESS_KEY_ID / AWS_SECRET_ACCESS_KEY),
 * and instance profile credentials — following 12-factor app principles.
 */
@Configuration
public class AwsS3Config {

    @Value("${aws.region:${AWS_REGION:us-east-1}}")
    private String awsRegion;

    /**
     * Creates an AWS S3Client bean using the default credential provider chain.
     * Credentials are resolved in order: environment variables → system properties →
     * AWS credentials file → IAM instance profile / ECS task role.
     *
     * @return configured S3Client instance
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(awsRegion))
                .build();
    }

}
