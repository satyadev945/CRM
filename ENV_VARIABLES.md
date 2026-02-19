# Environment Variables Configuration

This document lists all environment variables required for cloud deployment of the minicompcrm application.

## Database Configuration

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DB_URL` | Database JDBC URL | `jdbc:mysql://localhost:3306/crm?useSSL=false` | Yes |
| `DB_USERNAME` | Database username | `root` | Yes |
| `DB_PASSWORD` | Database password | `password` | Yes |
| `DB_DDL_AUTO` | Hibernate DDL auto mode | `validate` | No |
| `DB_POOL_SIZE` | Maximum connection pool size | `10` | No |
| `DB_POOL_MIN_IDLE` | Minimum idle connections | `5` | No |
| `DB_CONNECTION_TIMEOUT` | Connection timeout (ms) | `30000` | No |
| `DB_IDLE_TIMEOUT` | Idle timeout (ms) | `600000` | No |
| `DB_MAX_LIFETIME` | Max connection lifetime (ms) | `1800000` | No |

## AWS Configuration

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `AWS_REGION` | AWS region | `us-east-1` | Yes |
| `AWS_ACCESS_KEY_ID` | AWS access key (use IAM role instead) | - | No* |
| `AWS_SECRET_ACCESS_KEY` | AWS secret key (use IAM role instead) | - | No* |
| `cloud.aws.s3.bucket` | S3 bucket name for document storage | `crm-documents` | Yes |

*Note: Use IAM roles for EC2/ECS instead of access keys for better security.

## Server Configuration

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `SERVER_PORT` | Application server port | `8080` | No |
| `MANAGEMENT_SECURITY_ENABLED` | Enable management endpoint security | `true` | No |
| `MANAGEMENT_CONTEXT_PATH` | Management endpoint path | `/appinfo` | No |

## Logging Configuration

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `LOG_LEVEL` | Root log level | `INFO` | No |
| `APP_LOG_LEVEL` | Application log level | `INFO` | No |
| `ENVIRONMENT` | Environment name (dev/staging/prod) | `dev` | No |

## Thymeleaf Configuration

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `THYMELEAF_CACHE` | Enable Thymeleaf template caching | `true` | No |

## Example AWS ECS Task Definition

```json
{
  "containerDefinitions": [
    {
      "name": "minicompcrm",
      "image": "your-ecr-repo/minicompcrm:latest",
      "environment": [
        {"name": "DB_URL", "value": "jdbc:mysql://your-rds-endpoint:3306/crm?useSSL=false"},
        {"name": "DB_USERNAME", "value": "admin"},
        {"name": "AWS_REGION", "value": "us-east-1"},
        {"name": "cloud.aws.s3.bucket", "value": "your-s3-bucket"},
        {"name": "LOG_LEVEL", "value": "INFO"},
        {"name": "ENVIRONMENT", "value": "prod"}
      ],
      "secrets": [
        {"name": "DB_PASSWORD", "valueFrom": "arn:aws:secretsmanager:region:account:secret:db-password"}
      ]
    }
  ]
}
```

## Example Kubernetes Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: minicompcrm
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: minicompcrm
        image: your-registry/minicompcrm:latest
        env:
        - name: DB_URL
          value: "jdbc:mysql://mysql-service:3306/crm?useSSL=false"
        - name: DB_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: username
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: password
        - name: AWS_REGION
          value: "us-east-1"
        - name: cloud.aws.s3.bucket
          value: "your-s3-bucket"
        - name: ENVIRONMENT
          value: "prod"
```

## Security Best Practices

1. **Never hardcode credentials** - Use AWS Secrets Manager or Parameter Store
2. **Use IAM roles** - Assign IAM roles to EC2/ECS tasks instead of access keys
3. **Enable encryption** - Use SSL/TLS for database connections
4. **Rotate credentials** - Regularly rotate database passwords and API keys
5. **Least privilege** - Grant minimum required permissions to IAM roles
6. **Enable audit logging** - Use CloudWatch Logs for audit trails

## Cloud Deployment Checklist

- [ ] Set all required environment variables
- [ ] Configure IAM role with S3 and RDS permissions
- [ ] Create S3 bucket for document storage
- [ ] Set up RDS MySQL instance
- [ ] Configure security groups for network access
- [ ] Enable CloudWatch logging
- [ ] Set up health check endpoints
- [ ] Configure auto-scaling policies
- [ ] Enable SSL/TLS certificates
- [ ] Set up monitoring and alerts
