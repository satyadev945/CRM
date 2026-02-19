package crm.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AWS Configuration for cloud deployment
 * Uses DefaultAWSCredentialsProviderChain for secure credential management
 * Supports: IAM roles, environment variables, AWS credentials file
 */
@Configuration
@Slf4j
public class AwsConfig {

    @Value("${aws.s3.region:us-east-1}")
    private String region;

    /**
     * AWS Credentials Provider using default chain
     * Priority order:
     * 1. Environment variables (AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)
     * 2. System properties
     * 3. AWS credentials file (~/.aws/credentials)
     * 4. IAM role for EC2/ECS/Lambda (recommended for production)
     */
    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        log.info("Initializing AWS credentials provider with default chain");
        return new DefaultAWSCredentialsProviderChain();
    }

    /**
     * Amazon S3 Client for cloud storage
     */
    @Bean
    public AmazonS3 amazonS3(AWSCredentialsProvider credentialsProvider) {
        log.info("Initializing Amazon S3 client for region: {}", region);
        
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(credentialsProvider)
                .build();
    }
}
