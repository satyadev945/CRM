# Cloud Readiness Fixes - CRM Application

## Overview
This document describes the cloud readiness fixes applied to make the CRM application fully compatible with AWS cloud deployment.

## Fixed Issues

### 1. Hard-coded File Paths (cr-java-0061)
**File:** `crm/utils/ReadDataUtils.java`
**Issue:** Application used absolute file paths with JFileChooser (Swing GUI component)
**Fix:** 
- Replaced Swing-based file selection with Amazon S3 integration
- Implemented S3-based file reading using AWS SDK for Java v2
- Added support for environment variable configuration (S3_BUCKET_NAME)

### 2. Local File System Write Operations (cr-java-0062)
**File:** `crm/controller/PdfController.java`
**Issue:** PDF files were written directly to local file system using FileOutputStream
**Fix:**
- Replaced FileOutputStream with in-memory ByteArrayOutputStream
- Implemented S3 upload functionality for PDF files
- Added environment variable support for S3 bucket and prefix configuration
- Ensured data durability and availability in cloud environments

### 3. Java.io.File Usage for Data Storage (cr-java-0063)
**File:** `crm/csv/CSVTest.java`
**Issue:** Used java.io.File with JFileChooser for CSV file selection
**Fix:**
- Replaced File-based operations with S3 GetObject API
- Implemented streaming CSV processing from S3
- Added proper error handling and resource cleanup
- Configured via environment variables (S3_BUCKET_NAME, S3_CSV_KEY, AWS_REGION)

### 4 & 5. Clock/Time Dependencies (cr-java-0111)
**File:** `crm/controller/DateTimeTestController.java`
**Issue:** Used java.util.Date which relies on server-local timezone
**Fix:**
- Replaced java.util.Date with java.time API (Instant, ZonedDateTime, Clock)
- Standardized all time operations on UTC timezone
- Used Clock.systemUTC() for consistent time across distributed systems
- Added UTC configuration to application properties

## New Components Added

### 1. AWS S3 Configuration
**File:** `crm/config/AwsS3Config.java`
- Spring Boot configuration for S3Client bean
- Uses DefaultCredentialsProvider for flexible authentication
- Supports IAM roles, environment variables, and credentials file

### 2. S3 Storage Service
**File:** `crm/service/S3StorageService.java`
- Abstraction layer for S3 operations
- Provides upload, download, exists, and delete operations
- Centralized error handling and logging
- Environment-based configuration

### 3. Updated Dependencies
**File:** `pom.xml`
Added AWS SDK for Java v2 dependencies:
- `software.amazon.awssdk:s3` (version 2.17.100)
- `software.amazon.awssdk:core` (version 2.17.100)
- `software.amazon.awssdk:auth` (version 2.17.100)

### 4. Cloud-Ready Configuration
**File:** `src/main/resources/application.properties`
Added environment variable support for:
- Database connection (DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)
- AWS region (AWS_REGION)
- S3 bucket configuration (S3_BUCKET_NAME, S3_PDF_PREFIX, S3_CSV_PREFIX)
- UTC timezone standardization

## Environment Variables

### Required for AWS Deployment
```bash
# AWS Configuration
AWS_REGION=us-east-1
S3_BUCKET_NAME=your-crm-bucket-name

# Database Configuration
DATABASE_URL=jdbc:mysql://your-rds-endpoint:3306/crm?useSSL=true
DATABASE_USERNAME=your-db-username
DATABASE_PASSWORD=your-db-password
```

### Optional Configuration
```bash
# S3 Prefixes
S3_PDF_PREFIX=pdfs/
S3_CSV_PREFIX=data/

# AWS Credentials (if not using IAM roles)
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
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
        "s3:GetObject",
        "s3:PutObject",
        "s3:DeleteObject",
        "s3:HeadObject"
      ],
      "Resource": "arn:aws:s3:::your-bucket-name/*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "s3:ListBucket"
      ],
      "Resource": "arn:aws:s3:::your-bucket-name"
    }
  ]
}
```

## Deployment Recommendations

### 1. AWS Elastic Beanstalk
- Set environment variables in Beanstalk configuration
- Attach IAM role with S3 permissions to EC2 instances
- Use RDS for MySQL database

### 2. AWS ECS/Fargate
- Configure environment variables in task definition
- Attach IAM role to task execution role
- Use RDS for MySQL database
- Store secrets in AWS Secrets Manager

### 3. AWS Lambda (if applicable)
- Configure environment variables in Lambda function
- Attach IAM role with S3 and RDS permissions
- Consider using RDS Proxy for connection pooling

## Testing

### Local Testing with LocalStack
```bash
# Start LocalStack for S3 emulation
docker run -d -p 4566:4566 localstack/localstack

# Set environment variables
export AWS_REGION=us-east-1
export S3_BUCKET_NAME=test-bucket
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_ENDPOINT_URL=http://localhost:4566

# Create test bucket
aws --endpoint-url=http://localhost:4566 s3 mb s3://test-bucket
```

### Integration Testing
1. Create S3 bucket in AWS
2. Configure environment variables
3. Run application and test file upload/download operations
4. Verify files are stored in S3, not local file system

## Migration Notes

### Data Migration
If you have existing local files:
1. Upload existing PDF files to S3 under the configured prefix
2. Upload existing CSV files to S3
3. Update any database references to use S3 keys instead of file paths

### Backward Compatibility
The application no longer supports local file system operations. All file operations now use S3. Ensure S3 is properly configured before deployment.

## Compliance

### 12-Factor App Principles
✅ **III. Config** - Configuration stored in environment variables
✅ **VI. Processes** - Application is stateless, files stored externally
✅ **IX. Disposability** - No local state, can be stopped/started quickly
✅ **XI. Logs** - Structured logging with SLF4J

### Cloud-Native Patterns
✅ **Externalized Storage** - All files stored in S3
✅ **Environment-based Configuration** - No hardcoded values
✅ **UTC Standardization** - Consistent time handling across regions
✅ **Stateless Design** - No local file system dependencies

## Support

For issues or questions regarding cloud deployment, refer to:
- AWS SDK for Java v2 Documentation: https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/
- Spring Boot on AWS: https://spring.io/guides/gs/spring-boot-aws/
- AWS S3 Best Practices: https://docs.aws.amazon.com/AmazonS3/latest/userguide/best-practices.html
