# Cloud Deployment Guide

## Cloud Readiness Fixes Applied

This document outlines all cloud readiness fixes applied to make the application deployable on AWS.

### Fixed Issues

#### 1. Configuration Management (Critical)
- **Changed**: Database schema auto-creation from `create-drop` to `validate`
- **Changed**: All hardcoded credentials replaced with environment variables
- **Added**: HikariCP connection pooling configuration
- **Changed**: SSL enabled for database connections
- **Changed**: Management endpoints secured

#### 2. File System Dependencies (Critical)
- **Fixed**: PdfController now generates PDFs in-memory using ByteArrayOutputStream
- **Fixed**: CSVTest refactored to use InputStream instead of File API
- **Fixed**: Removed Swing/AWT GUI components from ReadDataUtils
- **Added**: Cloud-compatible file upload patterns documented

#### 3. Security & Authentication (High)
- **Changed**: Database credentials now use environment variables
- **Changed**: SSL enabled for MySQL connections
- **Changed**: Management endpoint security enabled by default
- **Added**: AWS SDK dependencies for Secrets Manager integration

#### 4. Dependency Updates (High/Medium)
- **Upgraded**: Spring Boot 1.5.10 → 2.7.18
- **Updated**: itextpdf 5.5.13 → 5.5.13.3
- **Updated**: pdfbox 2.0.8 → 2.0.30
- **Updated**: bouncycastle 1.59 → 1.70
- **Updated**: opencsv 4.0 → 5.9
- **Updated**: poi 3.15 → 5.2.5
- **Removed**: nekohtml (no longer needed)
- **Added**: AWS SDK for S3 and Secrets Manager

#### 5. Resource Management (Medium)
- **Fixed**: CsvView now uses try-with-resources for proper cleanup
- **Added**: Connection timeout configurations for database
- **Added**: HikariCP connection pool limits

#### 6. Logging & Monitoring (Medium/Low)
- **Fixed**: CSVTest uses SLF4J logger instead of printStackTrace()
- **Fixed**: ReadDataUtils uses SLF4J logger instead of System.out.println
- **Fixed**: PdfController improved error logging
- **Changed**: Thymeleaf mode from LEGACYHTML5 to HTML
- **Changed**: Thymeleaf cache enabled by default in production

## Environment Variables Required

Set these environment variables before deploying to AWS:

### Database Configuration
```bash
DB_DDL_AUTO=validate  # Use 'update' for development, 'validate' for production
DB_URL=jdbc:mysql://your-rds-endpoint:3306/crm?useSSL=true&requireSSL=true
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
```

### Database Connection Pool (Optional - defaults provided)
```bash
DB_CONNECTION_TIMEOUT=30000
DB_MAX_POOL_SIZE=20
DB_MIN_IDLE=5
DB_IDLE_TIMEOUT=600000
DB_MAX_LIFETIME=1800000
```

### Application Configuration
```bash
SPRING_PROFILES_ACTIVE=cloud
MANAGEMENT_SECURITY_ENABLED=true
TEMPLATE_CACHE=true
```

### AWS Configuration
```bash
AWS_REGION=us-east-1
```

## AWS Deployment Steps

### 1. Setup AWS RDS MySQL Database
```bash
# Create RDS MySQL instance with:
# - Multi-AZ deployment for high availability
# - Automated backups enabled
# - SSL/TLS encryption enabled
# - Security group allowing access from ECS/EKS
```

### 2. Store Credentials in AWS Secrets Manager
```bash
aws secretsmanager create-secret \
  --name crm-db-credentials \
  --secret-string '{"username":"your_user","password":"your_password","host":"your-rds-endpoint","port":"3306","database":"crm"}'
```

### 3. Build Docker Image
```dockerfile
FROM amazoncorretto:8-alpine
VOLUME /tmp
COPY target/crm-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
```

### 4. Deploy to AWS ECS/Fargate
- Use task definition with environment variables
- Configure ALB health checks to `/actuator/health`
- Set up CloudWatch Logs for log aggregation
- Configure Auto Scaling based on CPU/memory

### 5. Setup S3 Bucket for File Storage
```bash
# Create S3 bucket for PDF and CSV file storage
aws s3 mb s3://your-crm-app-files --region us-east-1

# Configure bucket policy for application access
# Grant IAM role permissions for PutObject and GetObject
```

## Next Steps for Full Cloud Native Implementation

### 1. Implement S3 Integration for File Storage
Replace in-memory PDF generation with S3 uploads:
```java
@Service
public class S3FileService {
    @Autowired
    private AmazonS3 s3Client;

    public String uploadPdf(byte[] pdfBytes, String fileName) {
        String bucketName = System.getenv("S3_BUCKET_NAME");
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/pdf");
        metadata.setContentLength(pdfBytes.length);

        s3Client.putObject(bucketName, fileName,
            new ByteArrayInputStream(pdfBytes), metadata);

        return s3Client.getUrl(bucketName, fileName).toString();
    }
}
```

### 2. Add File Upload REST Endpoint
Replace Swing file chooser with REST API:
```java
@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        // Process uploaded file
        // Upload to S3 if needed for persistence
        return ResponseEntity.ok("File uploaded successfully");
    }
}
```

### 3. Implement AWS Secrets Manager Integration
Load database credentials from Secrets Manager:
```java
@Configuration
public class AwsSecretsConfig {

    @Bean
    public DataSource dataSource() {
        // Fetch credentials from AWS Secrets Manager
        // Configure HikariDataSource with credentials
    }
}
```

### 4. Add Distributed Tracing
Implement Spring Cloud Sleuth + AWS X-Ray for request tracing across services.

### 5. Add CloudWatch Metrics
Configure Micrometer to publish custom metrics to CloudWatch.

## Testing Cloud Deployment

1. **Local Testing**: Use Docker Compose with MySQL
2. **Integration Testing**: Deploy to AWS dev environment
3. **Load Testing**: Use AWS load testing tools
4. **Security Testing**: Run AWS Inspector and GuardDuty scans

## Monitoring & Observability

- **Logs**: CloudWatch Logs with JSON format
- **Metrics**: CloudWatch Metrics via Micrometer
- **Tracing**: AWS X-Ray for distributed tracing
- **Alarms**: CloudWatch Alarms for critical metrics
- **Dashboard**: CloudWatch Dashboard for application health

## Cost Optimization

- Use AWS Fargate Spot for non-production environments
- Configure RDS instance right-sizing
- Implement S3 lifecycle policies for old files
- Use CloudWatch Logs retention policies

## Security Best Practices

- Never commit credentials to source control
- Use AWS IAM roles for service-to-service authentication
- Enable encryption at rest for RDS and S3
- Use AWS WAF to protect against common web exploits
- Regularly update dependencies to patch security vulnerabilities
- Enable VPC flow logs for network monitoring
