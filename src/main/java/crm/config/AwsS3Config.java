package crm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * AWS S3 Configuration for cloud-native storage.
 * Configures S3 client with proper credentials and region settings for cloud deployment.
 */
@Configuration
public class AwsS3Config {

    @Value("${aws.s3.region:us-east-1}")
    private String awsRegion;

    /**
     * Creates and configures S3 client bean for dependency injection.
     * Uses DefaultCredentialsProvider which supports:
     * - Environment variables (AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)
     * - System properties
     * - AWS credentials file
     * - IAM instance profile credentials (for EC2/ECS)
     * - IAM role for service accounts (for EKS)
     * 
     * @return Configured S3Client instance
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

}
