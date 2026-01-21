# CRM Application - AWS EKS Deployment Guide

This guide provides comprehensive instructions for deploying the Spring Boot CRM application to AWS EKS (Elastic Kubernetes Service).

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Local Development Setup](#local-development-setup)
3. [Building and Pushing Docker Images](#building-and-pushing-docker-images)
4. [AWS EKS Cluster Setup](#aws-eks-cluster-setup)
5. [Deploying to AWS EKS](#deploying-to-aws-eks)
6. [Configuration Management](#configuration-management)
7. [Monitoring and Troubleshooting](#monitoring-and-troubleshooting)
8. [Scaling and Management](#scaling-and-management)
9. [Security Considerations](#security-considerations)
10. [Technology-Specific Notes](#technology-specific-notes)

---

## Prerequisites

### Required Tools

- **Docker** (v20.10 or higher)
  - Download: https://www.docker.com/get-started
  - Verify: `docker --version`

- **Docker Compose** (v2.0 or higher)
  - Included with Docker Desktop
  - Verify: `docker-compose --version`

- **AWS CLI** (v2.x)
  - Installation: https://aws.amazon.com/cli/
  - Verify: `aws --version`
  - Configure: `aws configure`

- **kubectl** (v1.28 or higher)
  - Installation: https://kubernetes.io/docs/tasks/tools/
  - Verify: `kubectl version --client`

- **eksctl** (optional but recommended)
  - Installation: https://eksctl.io/installation/
  - Verify: `eksctl version`

### AWS Requirements

- **AWS Account** with appropriate permissions
- **IAM Permissions** required:
  - EKS cluster management (eks:*)
  - ECR repository access (ecr:*)
  - VPC and networking (ec2:*)
  - IAM role creation (iam:CreateRole, iam:AttachRolePolicy)
  - CloudFormation (for eksctl)

- **AWS Resources**:
  - EKS cluster (v1.28 or higher)
  - ECR repository for Docker images
  - MySQL database (RDS or external)
  - VPC with appropriate subnets

### Java Development Environment (for building from source)

- **Java JDK 8** (Oracle JDK or OpenJDK)
- **Maven 3.6+** (for building)
- **Git** (for version control)

---

## Local Development Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd crm-application
```

### 2. Configure Application Properties

Create or update `src/main/resources/application.properties`:

```properties
# Server configuration
server.port=8080

# Database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/crm?useSSL=false
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=validate

# Management endpoints
management.context-path=/appinfo
management.security.enabled=false
```

### 3. Setup Local Database

**Using MySQL:**

```bash
# Install MySQL (if not already installed)
# macOS: brew install mysql
# Ubuntu: sudo apt-get install mysql-server
# Windows: Download from https://dev.mysql.com/downloads/

# Start MySQL service
mysql.server start  # macOS
# OR
sudo systemctl start mysql  # Linux

# Create database
mysql -u root -p
CREATE DATABASE crm;
EXIT;
```

**Using Docker Compose (Development):**

```yaml
# Add to docker-compose.yml for local development
version: '3.8'

services:
  mysql:
    image: mysql:5.7
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: crm
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql

volumes:
  mysql-data:
```

### 4. Build and Run Locally

**Option A: Using Maven**

```bash
# Build the application
mvn clean package -DskipTests

# Run the application
java -jar target/crm-0.0.1-SNAPSHOT.jar
```

**Option B: Using Docker Compose**

```bash
# Build and start containers
docker-compose up --build

# Access application at http://localhost:8080
```

### 5. Verify Local Deployment

- **Application URL**: http://localhost:8080
- **Health Check**: http://localhost:8080/appinfo/health
- **Application Info**: http://localhost:8080/appinfo/info

**Default Users** (from data.sql):
- Admin: username=`admin`, password=`admin`
- User: username=`user`, password=`user`
- Manager: username=`manager`, password=`manager`
- Owner: username=`owner`, password=`owner`

---

## Building and Pushing Docker Images

### 1. Review Dockerfile

The multi-stage Dockerfile optimizes the build process:

```dockerfile
# Builder stage - compiles Java application
FROM maven:3.8.6-openjdk-8-slim AS builder
WORKDIR /workspace
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime stage - runs the application
FROM amazoncorretto:8
WORKDIR /app
COPY --from=builder /workspace/target/*.jar app.jar
# ... security and configuration settings
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### 2. Build Docker Image

**Using build-push.sh (Linux/macOS):**

```bash
chmod +x scripts/build-push.sh
./scripts/build-push.sh
```

**Using build-push.bat (Windows):**

```cmd
scripts\build-push.bat
```

**Interactive Prompts:**

1. Enter image tag (default: latest)
2. Select registry:
   - Option 1: AWS ECR
     - AWS Region (e.g., us-east-1)
     - AWS Account ID
     - ECR Repository Name
   - Option 2: Docker Hub
     - Docker Hub username
     - Docker Hub password/token

### 3. Manual Docker Build (if needed)

```bash
# Build image
docker build -t crm-app:latest .

# Tag for ECR
docker tag crm-app:latest <account-id>.dkr.ecr.<region>.amazonaws.com/crm-app:latest

# Login to ECR
aws ecr get-login-password --region <region> | docker login --username AWS --password-stdin <account-id>.dkr.ecr.<region>.amazonaws.com

# Push to ECR
docker push <account-id>.dkr.ecr.<region>.amazonaws.com/crm-app:latest
```

### 4. Verify Image

```bash
# List local images
docker images | grep crm-app

# Test image locally
docker run -p 8080:8080 -e DB_URL="jdbc:mysql://host.docker.internal:3306/crm?useSSL=false" -e DB_PASSWORD="password" crm-app:latest
```

---

## AWS EKS Cluster Setup

### Option 1: Create EKS Cluster with eksctl (Recommended)

```bash
# Create EKS cluster with eksctl
eksctl create cluster \
  --name crm-cluster \
  --region us-east-1 \
  --nodegroup-name standard-workers \
  --node-type t3.medium \
  --nodes 2 \
  --nodes-min 2 \
  --nodes-max 4 \
  --managed

# This command will:
# - Create VPC and networking
# - Create EKS control plane
# - Create managed node group
# - Configure kubectl context
```

### Option 2: Create EKS Cluster via AWS Console

1. Navigate to **EKS Console** → **Clusters** → **Create cluster**
2. **Configure cluster**:
   - Name: crm-cluster
   - Kubernetes version: 1.28
   - Cluster service role: Create or select existing
3. **Configure networking**:
   - VPC: Select or create
   - Subnets: Select at least 2 in different AZs
   - Security groups: Configure appropriately
4. **Create managed node group**:
   - Name: standard-workers
   - Instance type: t3.medium
   - Desired size: 2
   - Min size: 2
   - Max size: 4

### Configure kubectl

```bash
# Update kubeconfig
aws eks update-kubeconfig --region us-east-1 --name crm-cluster

# Verify connection
kubectl cluster-info
kubectl get nodes
```

### Install AWS Load Balancer Controller

Required for Ingress with ALB:

```bash
# Download IAM policy
curl -o iam_policy.json https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/v2.6.0/docs/install/iam_policy.json

# Create IAM policy
aws iam create-policy \
  --policy-name AWSLoadBalancerControllerIAMPolicy \
  --policy-document file://iam_policy.json

# Install using Helm
helm repo add eks https://aws.github.io/eks-charts
helm repo update

helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=crm-cluster \
  --set serviceAccount.create=true \
  --set serviceAccount.name=aws-load-balancer-controller
```

---

## Deploying to AWS EKS

### 1. Review Kubernetes Manifests

The deployment includes:

- **namespace.yaml**: Creates `crm-app` namespace
- **deployment.yaml**: Defines application deployment with 2 replicas
- **service.yaml**: Creates ClusterIP service
- **ingress.yaml**: Configures ALB ingress

### 2. Configure Deployment

Update placeholders in `kubernetes/deployment.yaml`:

```yaml
# Image URI
image: {{IMAGE_URI}}  # Replace with actual ECR URI

# Environment variables
- name: DB_URL
  value: "{{DB_URL}}"  # Replace with RDS endpoint
- name: DB_PASSWORD
  value: "{{DB_PASSWORD}}"  # Replace with actual password
```

### 3. Deploy Using Scripts

**Using deploy-image.sh (Linux/macOS):**

```bash
chmod +x scripts/deploy-image.sh
./scripts/deploy-image.sh
```

**Using deploy-image.bat (Windows):**

```cmd
scripts\deploy-image.bat
```

**Interactive Prompts:**

1. AWS Region
2. EKS Cluster Name
3. Docker Image URI (from build-push script)
4. DDL_AUTO mode (validate/update/create/create-drop)
5. Database URL (RDS endpoint)
6. Database username
7. Database password

### 4. Manual Deployment (if needed)

```bash
# Update manifests with actual values
sed -i 's|{{IMAGE_URI}}|<your-ecr-uri>|g' kubernetes/deployment.yaml
sed -i 's|{{DB_URL}}|jdbc:mysql://<rds-endpoint>:3306/crm?useSSL=false|g' kubernetes/deployment.yaml
sed -i 's|{{DB_PASSWORD}}|<password>|g' kubernetes/deployment.yaml

# Apply manifests
kubectl apply -f kubernetes/namespace.yaml
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
kubectl apply -f kubernetes/ingress.yaml

# Wait for rollout
kubectl rollout status deployment/crm-app -n crm-app
```

### 5. Verify Deployment

```bash
# Check all resources
kubectl get all -n crm-app

# Check pods
kubectl get pods -n crm-app

# Check service
kubectl get svc -n crm-app

# Check ingress
kubectl get ingress -n crm-app

# View logs
kubectl logs -f deployment/crm-app -n crm-app
```

### 6. Access Application

```bash
# Get ALB URL
kubectl get ingress crm-app-ingress -n crm-app -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'

# Access application
curl http://<alb-url>/appinfo/health

# Or open in browser
open http://<alb-url>
```

---

## Configuration Management

### Environment Variables

The application uses the following environment variables:

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `SPRING_PROFILES_ACTIVE` | Spring profile | docker | No |
| `SERVER_PORT` | Application port | 8080 | No |
| `DDL_AUTO` | Hibernate DDL mode | validate | No |
| `DB_URL` | Database JDBC URL | - | Yes |
| `DB_USER` | Database username | root | Yes |
| `DB_PASSWORD` | Database password | - | Yes |
| `JAVA_OPTS` | JVM options | -Xmx768m -Xms384m | No |
| `TZ` | Timezone | UTC | No |

### Using Kubernetes Secrets

For sensitive data (database passwords):

```bash
# Create secret
kubectl create secret generic crm-db-secret \
  --from-literal=password='<your-password>' \
  -n crm-app

# Verify secret
kubectl get secret crm-db-secret -n crm-app
```

Secret is referenced in deployment.yaml:

```yaml
- name: DB_PASSWORD
  valueFrom:
    secretKeyRef:
      name: crm-db-secret
      key: password
```

### Using ConfigMaps

For non-sensitive configuration:

```bash
# Create ConfigMap
kubectl create configmap crm-app-config \
  --from-file=application.properties \
  -n crm-app

# Mount as volume
# (Already configured in deployment.yaml)
```

### Database Setup (RDS)

**Create RDS MySQL Instance:**

1. Navigate to **RDS Console** → **Create database**
2. **Engine**: MySQL 5.7 or 8.0
3. **Templates**: Production or Dev/Test
4. **Settings**:
   - DB instance identifier: crm-database
   - Master username: admin
   - Master password: (set securely)
5. **Instance configuration**: db.t3.micro or larger
6. **Connectivity**:
   - VPC: Same as EKS cluster
   - Public access: No
   - Security group: Allow 3306 from EKS nodes
7. **Database options**:
   - Initial database name: crm

**Initialize Database:**

```bash
# Connect to RDS
mysql -h <rds-endpoint> -u admin -p

# Create database if not created
CREATE DATABASE IF NOT EXISTS crm;

# Run initialization script
USE crm;
source data.sql;
```

---

## Monitoring and Troubleshooting

### Viewing Logs

```bash
# View logs from all pods
kubectl logs -f deployment/crm-app -n crm-app

# View logs from specific pod
kubectl logs -f <pod-name> -n crm-app

# View previous logs (for crashed pods)
kubectl logs --previous <pod-name> -n crm-app

# Stream logs with timestamps
kubectl logs -f --timestamps deployment/crm-app -n crm-app
```

### Debugging Pods

```bash
# Describe pod (shows events)
kubectl describe pod <pod-name> -n crm-app

# Execute command in pod
kubectl exec -it <pod-name> -n crm-app -- /bin/sh

# Port forward for local testing
kubectl port-forward deployment/crm-app 8080:8080 -n crm-app
```

### Common Issues

#### 1. ImagePullBackOff

**Symptoms**: Pods stuck in ImagePullBackOff state

**Solutions**:
- Verify ECR image exists: `aws ecr describe-images --repository-name crm-app`
- Check ECR permissions for EKS nodes
- Ensure image URI is correct in deployment.yaml

```bash
# Grant ECR access to node IAM role
aws iam attach-role-policy \
  --role-name <node-instance-role> \
  --policy-arn arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly
```

#### 2. CrashLoopBackOff

**Symptoms**: Pods continuously restarting

**Solutions**:
- Check application logs: `kubectl logs <pod-name> -n crm-app`
- Common causes:
  - Database connection failure
  - Missing environment variables
  - JVM memory issues
  - Application startup errors

```bash
# Increase JVM memory
kubectl set env deployment/crm-app JAVA_OPTS="-Xmx1024m -Xms512m" -n crm-app
```

#### 3. Database Connection Issues

**Symptoms**: Application cannot connect to database

**Solutions**:
- Verify RDS security group allows traffic from EKS nodes
- Test database connectivity from pod:

```bash
kubectl exec -it <pod-name> -n crm-app -- sh
# Inside pod (if telnet available)
telnet <rds-endpoint> 3306
```

- Check environment variables:

```bash
kubectl exec <pod-name> -n crm-app -- env | grep DB_
```

#### 4. Ingress Not Working

**Symptoms**: Cannot access application via ALB

**Solutions**:
- Verify AWS Load Balancer Controller is installed
- Check ingress status: `kubectl describe ingress crm-app-ingress -n crm-app`
- Verify ALB exists in AWS Console
- Check ALB security group allows HTTP traffic
- Check target group health in AWS Console

```bash
# View ALB details
kubectl get ingress crm-app-ingress -n crm-app -o yaml
```

#### 5. Health Check Failures

**Symptoms**: Pods marked as not ready

**Solutions**:
- Verify health endpoint: `kubectl exec <pod-name> -n crm-app -- wget -O- http://localhost:8080/appinfo/health`
- Increase initialDelaySeconds in deployment.yaml (Spring Boot startup can be slow)
- Check application logs for errors

### Health Checks

```bash
# Check application health
curl http://<alb-url>/appinfo/health

# Expected response:
{
  "status": "UP"
}

# Check application info
curl http://<alb-url>/appinfo/info
```

---

## Scaling and Management

### Manual Scaling

```bash
# Scale deployment
kubectl scale deployment/crm-app --replicas=3 -n crm-app

# Verify scaling
kubectl get pods -n crm-app
```

### Horizontal Pod Autoscaler (HPA)

```bash
# Create HPA
kubectl autoscale deployment crm-app \
  --cpu-percent=70 \
  --min=2 \
  --max=10 \
  -n crm-app

# View HPA status
kubectl get hpa -n crm-app
```

**HPA Manifest** (kubernetes/hpa.yaml):

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: crm-app-hpa
  namespace: crm-app
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: crm-app
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

### Rolling Updates

```bash
# Update image
kubectl set image deployment/crm-app crm-app=<new-image-uri> -n crm-app

# Watch rollout
kubectl rollout status deployment/crm-app -n crm-app

# View rollout history
kubectl rollout history deployment/crm-app -n crm-app
```

### Rollback

```bash
# Rollback to previous version
kubectl rollout undo deployment/crm-app -n crm-app

# Rollback to specific revision
kubectl rollout undo deployment/crm-app --to-revision=2 -n crm-app
```

### Resource Management

Current resource configuration:

```yaml
resources:
  requests:
    cpu: "250m"      # 0.25 CPU cores
    memory: "512Mi"  # 512 MB RAM
  limits:
    cpu: "1000m"     # 1 CPU core
    memory: "1Gi"    # 1 GB RAM
```

Adjust based on monitoring:

```bash
# Update resource limits
kubectl set resources deployment/crm-app \
  --limits=cpu=2,memory=2Gi \
  --requests=cpu=500m,memory=1Gi \
  -n crm-app
```

---

## Security Considerations

### 1. Container Security

- **Non-root user**: Application runs as `appuser` (UID 1000)
- **Read-only filesystem**: Consider adding `readOnlyRootFilesystem: true`
- **Security context**:

```yaml
securityContext:
  runAsNonRoot: true
  runAsUser: 1000
  allowPrivilegeEscalation: false
  capabilities:
    drop:
      - ALL
```

### 2. Network Policies

Restrict pod-to-pod communication:

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: crm-app-netpol
  namespace: crm-app
spec:
  podSelector:
    matchLabels:
      app: crm-app
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - podSelector: {}
    ports:
    - protocol: TCP
      port: 8080
  egress:
  - to:
    - podSelector: {}
  - to:  # Allow database access
    - namespaceSelector: {}
    ports:
    - protocol: TCP
      port: 3306
```

### 3. Secrets Management

**Best Practices**:
- Use AWS Secrets Manager or Parameter Store
- Integrate with External Secrets Operator
- Rotate credentials regularly
- Never commit secrets to Git

**Using AWS Secrets Manager**:

```bash
# Store secret in AWS Secrets Manager
aws secretsmanager create-secret \
  --name crm-app/db-password \
  --secret-string "my-secure-password" \
  --region us-east-1

# Install External Secrets Operator
helm repo add external-secrets https://charts.external-secrets.io
helm install external-secrets external-secrets/external-secrets -n external-secrets-system --create-namespace

# Create SecretStore and ExternalSecret
# (manifests provided separately)
```

### 4. RBAC (Role-Based Access Control)

Limit access to Kubernetes resources:

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: crm-app-sa
  namespace: crm-app
---
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: crm-app-role
  namespace: crm-app
rules:
- apiGroups: [""]
  resources: ["configmaps", "secrets"]
  verbs: ["get", "list"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: crm-app-rolebinding
  namespace: crm-app
subjects:
- kind: ServiceAccount
  name: crm-app-sa
  namespace: crm-app
roleRef:
  kind: Role
  name: crm-app-role
  apiGroup: rbac.authorization.k8s.io
```

### 5. Database Security

- Enable SSL/TLS for database connections
- Use RDS encryption at rest
- Implement least privilege database users
- Enable RDS audit logging
- Keep database in private subnets

### 6. Application Security

- Keep Spring Boot and dependencies updated
- Enable HTTPS (configure TLS certificate on ALB)
- Implement rate limiting
- Use Spring Security for authentication/authorization
- Sanitize user inputs
- Enable CSRF protection

---

## Technology-Specific Notes

### Spring Boot 1.5.10 Considerations

**Legacy Version**: Spring Boot 1.5.x reached end-of-life. Consider upgrading to Spring Boot 3.x for:
- Security updates
- Performance improvements
- Native image support (GraalVM)
- Improved observability

**Current Configuration**:
- Java 8 runtime (Amazon Corretto 8)
- Spring Boot Actuator on `/appinfo` path
- Management security disabled

**Actuator Endpoints**:
- Health: `/appinfo/health`
- Info: `/appinfo/info`

### JVM Tuning

**Current JVM Options**:
```
-Xmx768m                    # Maximum heap size: 768 MB
-Xms384m                    # Initial heap size: 384 MB
-XX:+UseContainerSupport    # Use container memory limits
-XX:MaxRAMPercentage=75.0   # Use 75% of container memory
-Djava.security.egd=file:/dev/./urandom  # Faster startup
```

**Monitoring JVM**:

```bash
# View JVM metrics
kubectl exec <pod-name> -n crm-app -- java -XX:+PrintFlagsFinal -version | grep -i heap

# Enable JMX for monitoring
# Add to JAVA_OPTS:
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.port=9010 \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.ssl=false
```

### Maven Build Optimization

**Dependency Caching**: The Dockerfile uses a two-step approach:

1. Download dependencies (cached layer)
2. Build application

This speeds up subsequent builds.

**Build Without Wrapper**: The Dockerfile uses system Maven (`mvn`) instead of Maven wrapper (`mvnw`) to avoid wrapper dependencies.

### Database Initialization

**data.sql**: The application includes a `data.sql` file with initial data (users, roles). This runs on startup when using `ddl-auto=create` or `create-drop`.

**Production Recommendation**: Use `ddl-auto=validate` and manage schema migrations with tools like Flyway or Liquibase.

### PDF Generation

The application uses iTextPDF and PDFBox for PDF generation. These libraries require:
- Sufficient memory (already configured)
- Font files (included in JAR)
- No additional dependencies

### CSV Import/Export

Supports CSV operations via OpenCSV and Super CSV. Endpoints:
- Export: `/customers` (CSV format)
- Import: `/api/csv/upload`

---

## Additional Resources

### AWS Documentation
- [EKS User Guide](https://docs.aws.amazon.com/eks/latest/userguide/)
- [ECR User Guide](https://docs.aws.amazon.com/ecr/)
- [AWS Load Balancer Controller](https://kubernetes-sigs.github.io/aws-load-balancer-controller/)

### Kubernetes Documentation
- [Kubernetes Concepts](https://kubernetes.io/docs/concepts/)
- [kubectl Cheat Sheet](https://kubernetes.io/docs/reference/kubectl/cheatsheet/)

### Spring Boot Documentation
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/1.5.x/reference/html/production-ready.html)
- [Spring Boot Docker](https://spring.io/guides/gs/spring-boot-docker/)

---

## Support and Troubleshooting

For issues or questions:

1. Check application logs: `kubectl logs -f deployment/crm-app -n crm-app`
2. Review Kubernetes events: `kubectl get events -n crm-app --sort-by='.lastTimestamp'`
3. Verify resource status: `kubectl get all -n crm-app`
4. Check AWS resources (EKS, ECR, RDS, ALB) in AWS Console

---

**Document Version**: 1.0  
**Last Updated**: 2026-01-21  
**Application**: CRM Application (Spring Boot 1.5.10)  
**Platform**: AWS EKS (Kubernetes)