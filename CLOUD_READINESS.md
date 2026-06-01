# CRM Application - Cloud Readiness Fixes

## Overview
This application has been updated to be cloud-ready for AWS deployment. All cloud compatibility blockers have been resolved.

## Cloud Readiness Fixes Applied

### 1. File System Dependencies Eliminated
**Blockers Fixed:** cr-java-0061, cr-java-0062, cr-java-0063

- **ReadDataUtils.java**: Replaced local file system operations with Amazon S3 SDK
- **PdfController.java**: Migrated PDF generation from local file writes to S3 uploads
- **CSVTest.java**: Replaced java.io.File operations with S3 streaming

All file operations now use Amazon S3 for durable, scalable cloud storage.

### 2. Time/Clock Dependencies Standardized
**Blockers Fixed:** cr-java-0111 (2 instances)

- **DateTimeTestController.java**: 
  - Replaced `java.util.Date` with `java.time.Instant`
  - Standardized all time operations to UTC using `Clock.systemUTC()`
  - Ensures consistency across distributed cloud environments

### 3. AWS SDK Integration
**New Dependencies Added:**
- AWS SDK for Java v2 (S3 client)
- Version: 2.17.100

### 4. Cloud-Native Configuration
**New Configuration Files:**
- `AwsS3Config.java`: S3 client configuration with IAM role support
- `S3StorageService.java`: Centralized S3 operations service
- Updated `application.properties` with cloud-native settings

## Environment Variables Required

### AWS Configuration
```bash
AWS_REGION=us-east-1                    # AWS region
AWS_S3_BUCKET_NAME=your-bucket-name     # S3 bucket for file storage
AWS_S3_PDF_PREFIX=pdfs/                 # S3 prefix for PDF files
AWS_S3_CSV_PREFIX=csv/                  # S3 prefix for CSV files
```

### Database Configuration (for cloud deployment)
```bash
SPRING_DATASOURCE_URL=jdbc:mysql://your-rds-endpoint:3306/crm?useSSL=true
SPRING_DATASOURCE_USERNAME=your-db-user
SPRING_DATASOURCE_PASSWORD=your-db-password
```

## AWS IAM Permissions Required

The application requires the following S3 permissions:
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:GetObject",
        "s3:DeleteObject",
        "s3:HeadObject",
        "s3:ListBucket"
      ],
      "Resource": [
        "arn:aws:s3:::your-bucket-name/*",
        "arn:aws:s3:::your-bucket-name"
      ]
    }
  ]
}
```

## Deployment Checklist

### Pre-Deployment
- [ ] Create S3 bucket in target AWS region
- [ ] Configure IAM role with S3 permissions
- [ ] Set up RDS MySQL instance (or use existing database)
- [ ] Configure security groups for application and database

### Environment Setup
- [ ] Set all required environment variables
- [ ] Verify AWS credentials are configured (IAM role or credentials file)
- [ ] Test S3 bucket access from deployment environment

### Application Configuration
- [ ] Update `application.properties` with production values
- [ ] Configure database connection for RDS
- [ ] Set appropriate logging levels
- [ ] Enable HTTPS/TLS for production

## Cloud Deployment Options

### AWS Elastic Beanstalk
```bash
eb init -p java-8 crm-application
eb create crm-production
eb deploy
```

### AWS ECS/Fargate
- Build Docker image (separate workflow)
- Push to ECR
- Deploy to ECS cluster with task definition

### AWS Lambda (with Spring Cloud Function)
- Package as Spring Cloud Function
- Deploy to Lambda with API Gateway

## Testing Cloud Readiness

### Local Testing with LocalStack
```bash
# Start LocalStack for S3 emulation
docker run -d -p 4566:4566 localstack/localstack

# Set environment variables
export AWS_REGION=us-east-1
export AWS_S3_BUCKET_NAME=test-bucket
export AWS_ENDPOINT_URL=http://localhost:4566

# Run application
mvn spring-boot:run
```

### Integration Testing
```bash
# Run tests with cloud services
mvn test -Dspring.profiles.active=cloud
```

## Migration Notes

### Data Migration
If migrating from existing deployment with local files:
1. Upload existing PDF files to S3 bucket
2. Update database records with S3 keys
3. Run migration script to update file references

### Backward Compatibility
The updated code is NOT backward compatible with local file system operations.
All file operations now require S3 connectivity.

## Monitoring and Logging

### CloudWatch Integration
- Application logs are written to stdout/stderr
- Configure CloudWatch Logs agent for log aggregation
- Set up CloudWatch alarms for S3 operation failures

### Metrics to Monitor
- S3 operation latency
- S3 operation errors
- Database connection pool metrics
- Application response times

## Troubleshooting

### S3 Access Issues
```
Error: Access Denied (403)
Solution: Verify IAM role has correct S3 permissions
```

### Region Mismatch
```
Error: The bucket is in this region: us-west-2
Solution: Set AWS_REGION environment variable to match bucket region
```

### Credentials Not Found
```
Error: Unable to load credentials from any provider
Solution: Configure IAM role or AWS credentials file
```

## Support and Documentation

- AWS SDK for Java v2: https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/
- Spring Boot on AWS: https://spring.io/guides/gs/spring-boot-aws/
- AWS Best Practices: https://aws.amazon.com/architecture/well-architected/

## Version History

- **v1.0.0**: Initial cloud readiness fixes
  - Migrated file operations to S3
  - Standardized time handling to UTC
  - Added AWS SDK dependencies
  - Created cloud-native configuration
