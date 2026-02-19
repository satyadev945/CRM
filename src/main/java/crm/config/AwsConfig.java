package crm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

/**
 * AWS Configuration for cloud deployment
 * Manages AWS-specific settings and credentials
 */
@Configuration
@Slf4j
public class AwsConfig {

    @Value("${cloud.aws.region.static:us-east-1}")
    private String awsRegion;

    @Value("${cloud.aws.s3.bucket:crm-documents}")
    private String s3BucketName;

    @Value("${cloud.aws.region.auto:false}")
    private boolean autoDetectRegion;

    @PostConstruct
    public void init() {
        log.info("AWS Configuration initialized");
        log.info("AWS Region: {}", awsRegion);
        log.info("S3 Bucket: {}", s3BucketName);
        log.info("Auto-detect Region: {}", autoDetectRegion);
        
        // Validate AWS configuration
        if (s3BucketName == null || s3BucketName.isEmpty()) {
            log.warn("S3 bucket name not configured. Set cloud.aws.s3.bucket property.");
        }
    }

    public String getAwsRegion() {
        return awsRegion;
    }

    public String getS3BucketName() {
        return s3BucketName;
    }

    public boolean isAutoDetectRegion() {
        return autoDetectRegion;
    }
}
