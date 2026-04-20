# Cloud Deployment Guide - CRM Application

## Overview
This application has been modernized for cloud deployment on AWS with the following cloud-native improvements:

## Cloud Readiness Fixes Applied

### 1. Amazon S3 Integration (Blockers 1, 2, 3)
**Issue**: Application used local file system for data storage and file operations, which is ephemeral in cloud environments.

**Solution**: Migrated to Amazon S3 for durable, scalable storage.

**Changes Made**:
- `ReadDataUtils.java`: Replaced local file reading with S3 object retrieval
- `PdfController.java`: Changed PDF generation to write directly to S3 instead of local filesystem
- `CSVTest.java`: Updated CSV processing to read from S3 streams
- `Pdf.java` entity: Added `s3Key` and `s3Bucket` fields to track S3 locations

### 2. UTC Timezone Standardization (Blockers 4, 5)
**Issue**: Application used `java.util.Date` which relies on server-local timezone, causing inconsistencies in distributed cloud environments.

**Solution**: Migrated to `java.time` API with UTC standardization.

**Changes Made**:
- `DateTimeTestController.java`: Replaced `java.util.Date` with `Instant`, `ZonedDateTime` using UTC
- `application.properties`: Added timezone configuration for Hibernate and Jackson to use UTC

### 3. Environment-Based Configuration
**Issue**: Hard-coded configuration values prevent flexible cloud deployment.

**Solution**: Externalized all configuration to environment variables.

**Changes Made**:
- `application.properties`: All sensitive and environment-specific values now use environment variables with sensible defaults
- Database credentials, S3 bucket names, AWS region all configurable via environment variables

## Required Environment Variables

### Database Configuration
```bash
DATABASE_URL=jdbc:mysql://your-rds-endpoint:3306/crm?useSSL=true
DATABASE_USERNAME=your-db-username
DATABASE_PASSWORD=your-db-password
DB_DDL_AUTO=update  # Use 'update' or 'validate' in production, not 'create-drop'
```

### AWS S3 Configuration
```bash
S3_BUCKET_NAME=your-crm-data-bucket
S3_PDF_BUCKET=your-crm-pdf-bucket
AWS_REGION=us-east-1
S3_PDF_PREFIX=pdfs/
```

### AWS Credentials (if not using IAM roles)
```bash
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
```

## AWS Services Required

### 1. Amazon S3
Create two S3 buckets (or use one for both):
- Data bucket for CSV and general file storage
- PDF bucket for generated PDF documents

**Bucket Configuration**:
```bash
aws s3 mb s3://your-crm-data-bucket --region us-east-1
aws s3 mb s3://your-crm-pdf-bucket --region us-east-1
```

### 2. Amazon RDS (MySQL)
- Create an RDS MySQL instance
- Configure security groups to allow access from your application
- Update DATABASE_URL with the RDS endpoint

### 3. IAM Roles and Policies
For EC2/ECS/EKS deployments, attach an IAM role with the following policy:

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
        "s3:ListBucket"
      ],
      "Resource": [
        "arn:aws:s3:::your-crm-data-bucket/*",
        "arn:aws:s3:::your-crm-pdf-bucket/*",
        "arn:aws:s3:::your-crm-data-bucket",
        "arn:aws:s3:::your-crm-pdf-bucket"
      ]
    }
  ]
}
```

## Deployment Options

### Option 1: AWS Elastic Beanstalk
```bash
# Package the application
mvn clean package

# Deploy to Elastic Beanstalk
eb init -p java-8 crm-application
eb create crm-production
eb setenv DATABASE_URL=... DATABASE_USERNAME=... DATABASE_PASSWORD=... S3_BUCKET_NAME=...
```

### Option 2: AWS ECS (Elastic Container Service)
1. Build Docker image (handled separately)
2. Push to Amazon ECR
3. Create ECS task definition with environment variables
4. Deploy to ECS cluster with IAM task role

### Option 3: AWS EKS (Elastic Kubernetes Service)
1. Build Docker image (handled separately)
2. Create Kubernetes deployment with ConfigMap/Secrets for environment variables
3. Use IAM Roles for Service Accounts (IRSA) for S3 access

## Testing Cloud Readiness

### 1. Test S3 Connectivity
```java
// Ensure S3 buckets are accessible
S3Client s3Client = S3Client.builder().build();
s3Client.listBuckets();
```

### 2. Test Database Connectivity
```bash
# Test RDS connection
mysql -h your-rds-endpoint -u username -p
```

### 3. Test Application
```bash
# Run locally with cloud configuration
export DATABASE_URL=jdbc:mysql://localhost:3306/crm
export S3_BUCKET_NAME=your-test-bucket
export AWS_REGION=us-east-1
mvn spring-boot:run
```

## Migration Checklist

- [ ] Create S3 buckets for data and PDF storage
- [ ] Set up RDS MySQL database
- [ ] Configure IAM roles/policies for S3 access
- [ ] Set all required environment variables
- [ ] Test S3 read/write operations
- [ ] Test database connectivity
- [ ] Verify timezone handling (all times in UTC)
- [ ] Test PDF generation and S3 upload
- [ ] Test CSV processing from S3
- [ ] Monitor application logs for any file system errors

## Monitoring and Logging

The application now uses structured logging compatible with CloudWatch Logs:
- All timestamps are in UTC
- S3 operations are logged with bucket and key information
- Database operations use UTC timezone

Configure CloudWatch Logs agent to capture application logs for monitoring.

## Security Best Practices

1. **Never hard-code credentials**: Use IAM roles when possible
2. **Enable S3 encryption**: Use server-side encryption (SSE-S3 or SSE-KMS)
3. **Use VPC endpoints**: For S3 access from private subnets
4. **Enable RDS encryption**: Encrypt data at rest
5. **Use SSL/TLS**: Enable SSL for database connections in production

## Troubleshooting

### S3 Access Denied
- Verify IAM role/policy has correct S3 permissions
- Check bucket names are correct
- Ensure AWS region matches bucket region

### Database Connection Issues
- Verify security group allows inbound traffic on port 3306
- Check DATABASE_URL format and credentials
- Ensure RDS instance is in correct VPC/subnet

### Timezone Issues
- All times should be stored in UTC
- Verify `spring.jpa.properties.hibernate.jdbc.time_zone=UTC` is set
- Check application logs for timezone-related warnings

## Support

For issues related to cloud deployment, check:
1. Application logs in CloudWatch
2. S3 bucket policies and permissions
3. RDS security groups and network configuration
4. Environment variable configuration
