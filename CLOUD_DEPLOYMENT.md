# Cloud Deployment Guide

## Overview
This application has been modernized for cloud deployment on AWS. All cloud readiness issues have been addressed.

## Cloud Readiness Fixes Applied

### 1. Configuration Management
- ✅ Replaced hardcoded database credentials with environment variables
- ✅ Replaced hardcoded database URL with environment variable
- ✅ Added support for external configuration via environment variables
- ✅ Created cloud-specific configuration profile

### 2. File System Dependencies
- ✅ Replaced local file system PDF generation with AWS S3 storage
- ✅ Removed desktop GUI file chooser (JFileChooser)
- ✅ Added S3StorageService for cloud-native file operations
- ✅ Updated PdfController to use in-memory PDF generation and S3 upload

### 3. Database Persistence
- ✅ Configured HikariCP connection pooling
- ✅ Added proper connection timeout configurations
- ✅ Optimized connection pool settings for cloud deployment
- ✅ Changed DDL auto setting from 'create-drop' to 'update' (configurable)

### 4. Legacy Frameworks
- ✅ Upgraded Spring Boot from 1.5.10 to 2.7.18
- ✅ Replaced deprecated WebMvcConfigurerAdapter with WebMvcConfigurer
- ✅ Updated Thymeleaf Spring Security dialect from springsecurity4 to springsecurity5
- ✅ Updated all dependencies to latest stable versions

### 5. Logging & Monitoring
- ✅ Replaced System.out.println with proper SLF4J logging
- ✅ Added structured JSON logging for cloud environments
- ✅ Configured Logback with cloud-specific profiles
- ✅ Added Prometheus metrics support
- ✅ Configured Spring Boot Actuator for health checks

### 6. Security
- ✅ Removed password exposure in PDF export functionality
- ✅ Configured secure session cookies for cloud deployment
- ✅ Added AWS credentials management using DefaultAWSCredentialsProviderChain
- ✅ Disabled management security (replaced with proper actuator security)

### 7. Resource Management
- ✅ Added try-with-resources for file operations
- ✅ Configured connection leak detection
- ✅ Added proper resource cleanup in PDF generation

## Environment Variables Required

### Database Configuration
```bash
DATABASE_URL=jdbc:mysql://your-rds-endpoint:3306/crm?useSSL=true
DATABASE_USERNAME=your-db-username
DATABASE_PASSWORD=your-db-password
DB_DDL_AUTO=validate  # Use 'validate' in production
```

### AWS Configuration
```bash
AWS_REGION=us-east-1
AWS_S3_BUCKET_NAME=your-crm-pdfs-bucket
# AWS credentials via IAM role (recommended) or:
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
```

### Application Configuration
```bash
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=cloud
LOG_LEVEL_ROOT=INFO
LOG_LEVEL_APP=INFO
```

### Optional Configuration
```bash
DB_POOL_SIZE=20
DB_MIN_IDLE=5
DB_CONNECTION_TIMEOUT=30000
THYMELEAF_CACHE=true
```

## AWS Resources Required

### 1. RDS MySQL Database
- Create an RDS MySQL instance
- Configure security groups to allow access from your application
- Note the endpoint URL for DATABASE_URL

### 2. S3 Bucket
- Create an S3 bucket for PDF storage
- Configure bucket policy for application access
- Enable versioning (recommended)

### 3. IAM Role (Recommended)
Create an IAM role with the following policies:
- AmazonS3FullAccess (or custom policy with s3:PutObject, s3:GetObject)
- AmazonRDSFullAccess (or custom policy for RDS access)

### 4. EC2/ECS/EKS Instance
- Attach the IAM role to your compute instance
- Configure security groups for HTTP/HTTPS access
- Ensure outbound access to RDS and S3

## Deployment Steps

### 1. Build the Application
```bash
mvn clean package -DskipTests
```

### 2. Create Docker Image (Optional)
```dockerfile
FROM openjdk:8-jre-alpine
COPY target/crm-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### 3. Deploy to AWS

#### Option A: EC2 Deployment
```bash
# Copy JAR to EC2
scp target/crm-0.0.1-SNAPSHOT.jar ec2-user@your-instance:/home/ec2-user/

# SSH to EC2 and run
ssh ec2-user@your-instance
export DATABASE_URL=jdbc:mysql://your-rds:3306/crm
export DATABASE_USERNAME=admin
export DATABASE_PASSWORD=your-password
export AWS_S3_BUCKET_NAME=your-bucket
export SPRING_PROFILES_ACTIVE=cloud

java -jar crm-0.0.1-SNAPSHOT.jar
```

#### Option B: ECS Deployment
1. Push Docker image to ECR
2. Create ECS task definition with environment variables
3. Create ECS service
4. Configure Application Load Balancer

#### Option C: Elastic Beanstalk
1. Create Elastic Beanstalk application
2. Configure environment variables in EB console
3. Deploy JAR file via EB CLI or console

## Health Checks

The application exposes the following health check endpoints:

- **Liveness**: `GET /actuator/health`
- **Readiness**: `GET /actuator/health/readiness`
- **Metrics**: `GET /actuator/metrics`
- **Prometheus**: `GET /actuator/prometheus`

Configure your load balancer to use `/actuator/health` for health checks.

## Monitoring

### CloudWatch Integration
- Application logs are written to stdout (JSON format in cloud profile)
- Configure CloudWatch Logs agent to collect logs
- Set up CloudWatch alarms for critical metrics

### Prometheus Integration
- Metrics are exposed at `/actuator/prometheus`
- Configure Prometheus to scrape metrics
- Use Grafana for visualization

## Security Considerations

1. **Never hardcode credentials** - Use environment variables or AWS Secrets Manager
2. **Use IAM roles** - Preferred over access keys for AWS services
3. **Enable SSL/TLS** - Use HTTPS for all external communication
4. **Secure actuator endpoints** - Add Spring Security configuration if needed
5. **Regular updates** - Keep dependencies up to date

## Troubleshooting

### Database Connection Issues
- Check security group rules
- Verify RDS endpoint and credentials
- Check connection pool settings

### S3 Access Issues
- Verify IAM role permissions
- Check bucket name and region
- Verify AWS credentials configuration

### Application Startup Issues
- Check application logs in CloudWatch
- Verify all required environment variables are set
- Check health endpoint: `/actuator/health`

## Performance Tuning

### Database Connection Pool
Adjust based on your workload:
```bash
DB_POOL_SIZE=20  # Maximum connections
DB_MIN_IDLE=5    # Minimum idle connections
```

### JVM Settings
For production deployment:
```bash
java -Xms512m -Xmx2048m -XX:+UseG1GC -jar app.jar
```

## Rollback Plan

1. Keep previous version deployed
2. Use blue-green deployment strategy
3. Monitor health checks after deployment
4. Have database backup ready
5. Document rollback procedure

## Support

For issues or questions:
- Check application logs in CloudWatch
- Review health check endpoints
- Verify environment variables
- Check AWS service status

## Next Steps

1. Set up CI/CD pipeline
2. Configure auto-scaling
3. Implement distributed caching (Redis/ElastiCache)
4. Add distributed tracing (X-Ray)
5. Implement circuit breakers
6. Add rate limiting
