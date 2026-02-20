# Deployment Guide for test-CRM-008

## Table of Contents
1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Local Development Setup](#local-development-setup)
4. [Building and Pushing Docker Images](#building-and-pushing-docker-images)
5. [AWS EKS Deployment](#aws-eks-deployment)
6. [Configuration Management](#configuration-management)
7. [Troubleshooting](#troubleshooting)
8. [Security Considerations](#security-considerations)
9. [Technology-Specific Notes](#technology-specific-notes)

---

## Overview

This guide provides comprehensive instructions for deploying the **test-CRM-008** Spring Boot application to AWS EKS (Elastic Kubernetes Service). The application is a CRM system built with Spring Boot 1.5.10, Java 8, and includes features for customer management, contract handling, and PDF generation.

**Application Details:**
- **Technology Stack**: Spring Boot 1.5.10, Java 8
- **Build Tool**: Maven
- **Package Type**: JAR
- **Application Port**: 8080
- **Management Port**: 8080 (via /appinfo context path)
- **Health Endpoint**: /appinfo/health
- **Database**: MySQL (external service)

---

## Prerequisites

### Required Software

#### For Local Development:
- **Docker**: Version 20.10 or higher
- **Docker Compose**: Version 1.29 or higher
- **Java Development Kit (JDK)**: Version 8
- **Maven**: Version 3.6 or higher (optional, for local builds)

#### For AWS EKS Deployment:
- **AWS CLI**: Version 2.x
  ```bash
  # Install AWS CLI
  curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
  unzip awscliv2.zip
  sudo ./aws/install
  ```

- **kubectl**: Version 1.24 or higher
  ```bash
  # Install kubectl
  curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
  chmod +x kubectl
  sudo mv kubectl /usr/local/bin/
  ```

- **eksctl** (optional, for cluster creation):
  ```bash
  # Install eksctl
  curl --silent --location "https://github.com/weksctl/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
  sudo mv /tmp/eksctl /usr/local/bin
  ```

### AWS Requirements

1. **AWS Account** with appropriate permissions
2. **IAM Permissions** for:
   - EKS cluster access
   - ECR repository management
   - EC2 and VPC resources
   - IAM role creation

3. **EKS Cluster** (if not already created):
   ```bash
   eksctl create cluster \
     --name test-crm-008-cluster \
     --region us-east-1 \
     --nodegroup-name standard-workers \
     --node-type t3.medium \
     --nodes 2 \
     --nodes-min 1 \
     --nodes-max 3 \
     --managed
   ```

4. **AWS Load Balancer Controller** installed on EKS cluster:
   ```bash
   # Install AWS Load Balancer Controller
   kubectl apply -k "github.com/aws/eks-charts/stable/aws-load-balancer-controller//crds?ref=master"
   
   helm repo add eks https://aws.github.io/eks-charts
   helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
     -n kube-system \
     --set clusterName=test-crm-008-cluster \
     --set serviceAccount.create=false \
     --set serviceAccount.name=aws-load-balancer-controller
   ```

### External Services

The application requires the following external services:

1. **MySQL Database**:
   - Version: 5.7 or higher
   - Accessible from EKS cluster
   - Database name: `crm` (configurable)
   - Default port: 3306

   **Option 1: AWS RDS MySQL**:
   ```bash
   aws rds create-db-instance \
     --db-instance-identifier test-crm-008-db \
     --db-instance-class db.t3.micro \
     --engine mysql \
     --master-username admin \
     --master-user-password YourPassword123 \
     --allocated-storage 20 \
     --vpc-security-group-ids sg-xxxxxxxx \
     --db-subnet-group-name your-subnet-group
   ```

   **Option 2: Self-hosted MySQL**:
   - Ensure network connectivity from EKS cluster
   - Configure security groups to allow traffic on port 3306

---

## Local Development Setup

### Step 1: Clone the Repository

```bash
cd /path/to/test-CRM-008
```

### Step 2: Configure Environment Variables

Create a `.env` file in the project root:

```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=crm
DB_USERNAME=root
DB_PASSWORD=password

# Spring Boot Configuration
SPRING_PROFILES_ACTIVE=docker
JAVA_OPTS=-Xmx512m -Xms256m
```

### Step 3: Start MySQL Database (Local Testing)

If you need a local MySQL instance for testing:

```bash
docker run -d \
  --name mysql-crm \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=crm \
  -p 3306:3306 \
  mysql:5.7
```

### Step 4: Build and Run with Docker Compose

```bash
# Build the Docker image
docker-compose build

# Start the application
docker-compose up -d

# View logs
docker-compose logs -f test-crm-008

# Stop the application
docker-compose down
```

### Step 5: Access the Application

- **Application URL**: http://localhost:8080
- **Health Check**: http://localhost:8080/appinfo/health
- **Actuator Info**: http://localhost:8080/appinfo/info

---

## Building and Pushing Docker Images

### Using build-push.sh (Linux/macOS)

```bash
cd /path/to/test-CRM-008
chmod +x scripts/build-push.sh
./scripts/build-push.sh
```

**Script Workflow:**
1. Prompts for image tag (default: latest)
2. Asks to select registry type:
   - **Option 1**: AWS ECR
   - **Option 2**: Docker Hub
3. Collects registry-specific credentials
4. Builds the Docker image
5. Authenticates with the selected registry
6. Pushes the image to the registry

**Example for AWS ECR:**
```
Enter image tag (default: latest): v1.0.0
Select container registry:
1. AWS ECR (Elastic Container Registry)
2. Docker Hub
Enter choice (1 or 2): 1

Enter AWS Region (e.g., us-east-1): us-east-1
Enter AWS Account ID: 123456789012
Enter ECR Repository Name (default: test-crm-008): test-crm-008

Authenticating with AWS ECR...
Building Docker image...
Pushing image to registry...

SUCCESS!
Image: 123456789012.dkr.ecr.us-east-1.amazonaws.com/test-crm-008:v1.0.0
```

### Using build-push.bat (Windows)

```cmd
cd C:\path\to\test-CRM-008
scripts\build-push.bat
```

Follow the same prompts as the Linux/macOS version.

### Manual Build and Push

#### AWS ECR:

```bash
# Authenticate with ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 123456789012.dkr.ecr.us-east-1.amazonaws.com

# Create ECR repository (if not exists)
aws ecr create-repository --repository-name test-crm-008 --region us-east-1

# Build image
docker build -t test-crm-008:v1.0.0 .

# Tag image
docker tag test-crm-008:v1.0.0 123456789012.dkr.ecr.us-east-1.amazonaws.com/test-crm-008:v1.0.0

# Push image
docker push 123456789012.dkr.ecr.us-east-1.amazonaws.com/test-crm-008:v1.0.0
```

#### Docker Hub:

```bash
# Login to Docker Hub
docker login -u your-username

# Build image
docker build -t test-crm-008:v1.0.0 .

# Tag image
docker tag test-crm-008:v1.0.0 your-username/test-crm-008:v1.0.0

# Push image
docker push your-username/test-crm-008:v1.0.0
```

---

## AWS EKS Deployment

### Step 1: Configure AWS CLI

```bash
# Configure AWS credentials
aws configure

# Verify configuration
aws sts get-caller-identity
```

### Step 2: Configure kubectl for EKS

```bash
# Update kubeconfig
aws eks update-kubeconfig --region us-east-1 --name test-crm-008-cluster

# Verify connectivity
kubectl cluster-info
kubectl get nodes
```

### Step 3: Deploy Using deploy-image.sh (Linux/macOS)

```bash
cd /path/to/test-CRM-008
chmod +x scripts/deploy-image.sh
./scripts/deploy-image.sh
```

**Script Workflow:**
1. Prompts for AWS region and EKS cluster name
2. Asks for Docker image URI
3. Collects database configuration:
   - Database host
   - Database port
   - Database name
   - Database username
   - Database password
4. Configures kubectl for the EKS cluster
5. Updates Kubernetes manifests with provided values
6. Applies manifests in order:
   - Namespace
   - Deployment
   - Service
   - Ingress
7. Waits for deployment rollout
8. Displays deployment information and access URL

**Example:**
```
Enter AWS Region (e.g., us-east-1): us-east-1
Enter EKS Cluster Name: test-crm-008-cluster
Enter Docker Image URI: 123456789012.dkr.ecr.us-east-1.amazonaws.com/test-crm-008:v1.0.0

=== Database Configuration ===
Enter Database Host (default: mysql-host): test-crm-008-db.xxxxxx.us-east-1.rds.amazonaws.com
Enter Database Port (default: 3306): 3306
Enter Database Name (default: crm): crm
Enter Database Username (default: root): admin
Enter Database Password: ********

Configuring kubectl for EKS cluster...
Updating Kubernetes manifests...
Deploying to EKS cluster...
Waiting for deployment to complete...

Deployment completed successfully!
Application URL: http://xxxxx-xxxxxxxx.us-east-1.elb.amazonaws.com
```

### Step 4: Deploy Using deploy-image.bat (Windows)

```cmd
cd C:\path\to\test-CRM-008
scripts\deploy-image.bat
```

Follow the same prompts as the Linux/macOS version.

### Step 5: Verify Deployment

```bash
# Check namespace
kubectl get namespace test-crm-008

# Check pods
kubectl get pods -n test-crm-008

# Check services
kubectl get svc -n test-crm-008

# Check ingress
kubectl get ingress -n test-crm-008

# View pod logs
kubectl logs -n test-crm-008 -l app=test-crm-008 --tail=100

# Describe pod for detailed information
kubectl describe pod -n test-crm-008 -l app=test-crm-008
```

### Step 6: Access the Application

Once the ingress is ready, access the application using the Load Balancer URL:

```bash
# Get ingress URL
kubectl get ingress test-crm-008-ingress -n test-crm-008 -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'

# Access application
curl http://<load-balancer-url>/appinfo/health
```

---

## Configuration Management

### Environment Variables

The application uses the following environment variables:

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `SPRING_PROFILES_ACTIVE` | Spring Boot profile | docker | No |
| `JAVA_OPTS` | JVM options | -Xmx512m -Xms256m | No |
| `DB_HOST` | Database host | localhost | Yes |
| `DB_PORT` | Database port | 3306 | Yes |
| `DB_NAME` | Database name | crm | Yes |
| `DB_USERNAME` | Database username | root | Yes |
| `DB_PASSWORD` | Database password | password | Yes |
| `TZ` | Timezone | UTC | No |

### Updating Configuration

#### Update Deployment Configuration:

```bash
# Edit deployment
kubectl edit deployment test-crm-008 -n test-crm-008

# Or update via manifest
kubectl apply -f kubernetes/deployment.yaml
```

#### Update Environment Variables:

```bash
# Set new environment variable
kubectl set env deployment/test-crm-008 -n test-crm-008 DB_HOST=new-db-host

# Restart pods to apply changes
kubectl rollout restart deployment/test-crm-008 -n test-crm-008
```

### Secrets Management

For sensitive data like database passwords, use Kubernetes Secrets:

```bash
# Create secret
kubectl create secret generic db-credentials \
  --from-literal=username=admin \
  --from-literal=password=SecurePassword123 \
  -n test-crm-008

# Update deployment to use secret
kubectl patch deployment test-crm-008 -n test-crm-008 --type='json' -p='[
  {
    "op": "replace",
    "path": "/spec/template/spec/containers/0/env/4",
    "value": {
      "name": "DB_USERNAME",
      "valueFrom": {
        "secretKeyRef": {
          "name": "db-credentials",
          "key": "username"
        }
      }
    }
  },
  {
    "op": "replace",
    "path": "/spec/template/spec/containers/0/env/5",
    "value": {
      "name": "DB_PASSWORD",
      "valueFrom": {
        "secretKeyRef": {
          "name": "db-credentials",
          "key": "password"
        }
      }
    }
  }
]'
```

---

## Troubleshooting

### Common Issues

#### 1. Pods Not Starting

**Symptoms:**
- Pods stuck in `Pending`, `CrashLoopBackOff`, or `Error` state

**Diagnosis:**
```bash
# Check pod status
kubectl get pods -n test-crm-008

# Describe pod for events
kubectl describe pod <pod-name> -n test-crm-008

# View pod logs
kubectl logs <pod-name> -n test-crm-008

# View previous pod logs (if crashed)
kubectl logs <pod-name> -n test-crm-008 --previous
```

**Common Causes:**
- **Image pull errors**: Verify image URI and ECR permissions
- **Database connection issues**: Check DB_HOST, DB_PORT, and network connectivity
- **Resource constraints**: Increase memory/CPU limits in deployment.yaml
- **Configuration errors**: Verify environment variables

#### 2. Service Not Accessible

**Symptoms:**
- Cannot access application via service or ingress

**Diagnosis:**
```bash
# Check service
kubectl get svc -n test-crm-008
kubectl describe svc test-crm-008-service -n test-crm-008

# Check endpoints
kubectl get endpoints -n test-crm-008

# Check ingress
kubectl get ingress -n test-crm-008
kubectl describe ingress test-crm-008-ingress -n test-crm-008

# Test service internally
kubectl run -it --rm debug --image=busybox --restart=Never -n test-crm-008 -- wget -O- http://test-crm-008-service/appinfo/health
```

**Common Causes:**
- **Ingress not ready**: Wait for Load Balancer provisioning (5-10 minutes)
- **Security groups**: Verify EKS node security groups allow traffic
- **Health check failures**: Check /appinfo/health endpoint

#### 3. Database Connection Errors

**Symptoms:**
- Application logs show database connection errors
- Health check fails

**Diagnosis:**
```bash
# Check logs for database errors
kubectl logs -n test-crm-008 -l app=test-crm-008 | grep -i "database\|mysql\|connection"

# Test database connectivity from pod
kubectl exec -it <pod-name> -n test-crm-008 -- sh
# Inside pod:
# apt-get update && apt-get install -y mysql-client
# mysql -h $DB_HOST -P $DB_PORT -u $DB_USERNAME -p$DB_PASSWORD
```

**Common Causes:**
- **Incorrect credentials**: Verify DB_USERNAME and DB_PASSWORD
- **Network issues**: Check security groups and VPC configuration
- **Database not accessible**: Verify RDS endpoint and port

#### 4. High Memory Usage / OOM Kills

**Symptoms:**
- Pods restarting frequently
- OOMKilled status in pod events

**Diagnosis:**
```bash
# Check resource usage
kubectl top pods -n test-crm-008

# Check pod events
kubectl describe pod <pod-name> -n test-crm-008 | grep -A 10 Events
```

**Solution:**
```bash
# Increase memory limits in deployment.yaml
resources:
  requests:
    memory: "768Mi"
  limits:
    memory: "1.5Gi"

# Update JAVA_OPTS
env:
- name: JAVA_OPTS
  value: "-Xmx1024m -Xms512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Apply changes
kubectl apply -f kubernetes/deployment.yaml
```

### Rollback Deployment

If a deployment fails or causes issues:

```bash
# View rollout history
kubectl rollout history deployment/test-crm-008 -n test-crm-008

# Rollback to previous version
kubectl rollout undo deployment/test-crm-008 -n test-crm-008

# Rollback to specific revision
kubectl rollout undo deployment/test-crm-008 -n test-crm-008 --to-revision=2

# Check rollout status
kubectl rollout status deployment/test-crm-008 -n test-crm-008
```

### Scaling

```bash
# Scale up
kubectl scale deployment test-crm-008 -n test-crm-008 --replicas=3

# Scale down
kubectl scale deployment test-crm-008 -n test-crm-008 --replicas=1

# Auto-scaling (HPA)
kubectl autoscale deployment test-crm-008 -n test-crm-008 --cpu-percent=70 --min=2 --max=5
```

---

## Security Considerations

### 1. Image Security

- **Use specific image tags**: Avoid `latest` tag in production
- **Scan images for vulnerabilities**:
  ```bash
  # Using AWS ECR image scanning
  aws ecr start-image-scan --repository-name test-crm-008 --image-id imageTag=v1.0.0
  
  # View scan results
  aws ecr describe-image-scan-findings --repository-name test-crm-008 --image-id imageTag=v1.0.0
  ```

### 2. Network Security

- **Use Network Policies** to restrict pod-to-pod communication
- **Configure Security Groups** for EKS nodes and RDS
- **Enable VPC Flow Logs** for network monitoring

### 3. Secrets Management

- **Never commit secrets** to version control
- **Use Kubernetes Secrets** or AWS Secrets Manager
- **Rotate credentials regularly**

Example using AWS Secrets Manager:

```bash
# Create secret in AWS Secrets Manager
aws secretsmanager create-secret \
  --name test-crm-008/db-credentials \
  --secret-string '{"username":"admin","password":"SecurePassword123"}'

# Install Secrets Store CSI Driver
kubectl apply -f https://raw.githubusercontent.com/kubernetes-sigs/secrets-store-csi-driver/main/deploy/rbac-secretproviderclass.yaml
kubectl apply -f https://raw.githubusercontent.com/kubernetes-sigs/secrets-store-csi-driver/main/deploy/csidriver.yaml
kubectl apply -f https://raw.githubusercontent.com/kubernetes-sigs/secrets-store-csi-driver/main/deploy/secrets-store.csi.x-k8s.io_secretproviderclasses.yaml
kubectl apply -f https://raw.githubusercontent.com/kubernetes-sigs/secrets-store-csi-driver/main/deploy/secrets-store.csi.x-k8s.io_secretproviderclasspodstatuses.yaml
kubectl apply -f https://raw.githubusercontent.com/kubernetes-sigs/secrets-store-csi-driver/main/deploy/secrets-store-csi-driver.yaml

# Install AWS Provider
kubectl apply -f https://raw.githubusercontent.com/aws/secrets-store-csi-driver-provider-aws/main/deployment/aws-provider-installer.yaml
```

### 4. RBAC

Implement Role-Based Access Control:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: test-crm-008-role
  namespace: test-crm-008
rules:
- apiGroups: [""]
  resources: ["pods", "services"]
  verbs: ["get", "list", "watch"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: test-crm-008-rolebinding
  namespace: test-crm-008
subjects:
- kind: User
  name: developer
  apiGroup: rbac.authorization.k8s.io
roleRef:
  kind: Role
  name: test-crm-008-role
  apiGroup: rbac.authorization.k8s.io
```

### 5. Pod Security

- **Run as non-root user** (already configured in Dockerfile)
- **Use read-only root filesystem** where possible
- **Drop unnecessary capabilities**

---

## Technology-Specific Notes

### Spring Boot 1.5.10 Considerations

1. **Actuator Endpoints**:
   - Management context path: `/appinfo`
   - Health endpoint: `/appinfo/health`
   - Info endpoint: `/appinfo/info`
   - Security is disabled for management endpoints

2. **JVM Configuration**:
   - Recommended heap size: 512MB max, 256MB min
   - Use container-aware JVM flags: `-XX:+UseContainerSupport`
   - Set MaxRAMPercentage: `-XX:MaxRAMPercentage=75.0`

3. **Startup Time**:
   - Spring Boot 1.5.x has slower startup compared to newer versions
   - Increase `initialDelaySeconds` for liveness probe (90 seconds)
   - Increase `initialDelaySeconds` for readiness probe (60 seconds)

4. **Thymeleaf Configuration**:
   - Legacy HTML5 mode enabled
   - Template caching disabled for development

5. **Database Initialization**:
   - Hibernate DDL auto set to `create-drop`
   - **WARNING**: This will drop and recreate tables on startup
   - For production, change to `validate` or `none`:
     ```properties
     spring.jpa.hibernate.ddl-auto=validate
     ```

### Java 8 Considerations

1. **Base Image**:
   - Using `eclipse-temurin:8-jdk` for runtime
   - Includes security updates and patches

2. **Memory Management**:
   - Java 8 requires explicit heap size configuration
   - Monitor memory usage and adjust JAVA_OPTS accordingly

3. **Timezone**:
   - Set TZ environment variable to ensure consistent date/time handling

### Maven Build

1. **Build Command**: `mvn clean package -DskipTests`
2. **Artifact Location**: `target/crm-0.0.1-SNAPSHOT.jar`
3. **Dependencies**: Downloaded during Docker build for layer caching

---

## Monitoring and Observability

### Application Metrics

Access Spring Boot Actuator endpoints:

```bash
# Health check
curl http://<load-balancer-url>/appinfo/health

# Application info
curl http://<load-balancer-url>/appinfo/info

# Metrics (if enabled)
curl http://<load-balancer-url>/appinfo/metrics
```

### Kubernetes Monitoring

```bash
# View pod metrics
kubectl top pods -n test-crm-008

# View node metrics
kubectl top nodes

# Stream logs
kubectl logs -f -n test-crm-008 -l app=test-crm-008

# View events
kubectl get events -n test-crm-008 --sort-by='.lastTimestamp'
```

### AWS CloudWatch Integration

Enable Container Insights for EKS:

```bash
# Install CloudWatch agent
kubectl apply -f https://raw.githubusercontent.com/aws-samples/amazon-cloudwatch-container-insights/latest/k8s-deployment-manifest-templates/deployment-mode/daemonset/container-insights-monitoring/quickstart/cwagent-fluentd-quickstart.yaml
```

---

## Maintenance

### Updating the Application

1. Build and push new image version
2. Update deployment with new image:
   ```bash
   kubectl set image deployment/test-crm-008 test-crm-008=<new-image-uri> -n test-crm-008
   ```
3. Monitor rollout:
   ```bash
   kubectl rollout status deployment/test-crm-008 -n test-crm-008
   ```

### Backup and Restore

#### Database Backup:

```bash
# Backup MySQL database
kubectl run mysql-backup --image=mysql:5.7 --rm -it --restart=Never -- \
  mysqldump -h $DB_HOST -u $DB_USERNAME -p$DB_PASSWORD $DB_NAME > backup.sql

# Restore database
kubectl run mysql-restore --image=mysql:5.7 --rm -it --restart=Never -- \
  mysql -h $DB_HOST -u $DB_USERNAME -p$DB_PASSWORD $DB_NAME < backup.sql
```

### Cleanup

```bash
# Delete all resources
kubectl delete namespace test-crm-008

# Delete ECR repository
aws ecr delete-repository --repository-name test-crm-008 --region us-east-1 --force

# Delete EKS cluster (if needed)
eksctl delete cluster --name test-crm-008-cluster --region us-east-1
```

---

## Support and Resources

### Documentation Links

- [Spring Boot 1.5.x Documentation](https://docs.spring.io/spring-boot/docs/1.5.x/reference/html/)
- [AWS EKS Documentation](https://docs.aws.amazon.com/eks/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Docker Documentation](https://docs.docker.com/)

### Useful Commands Reference

```bash
# Kubernetes
kubectl get all -n test-crm-008
kubectl describe pod <pod-name> -n test-crm-008
kubectl logs <pod-name> -n test-crm-008
kubectl exec -it <pod-name> -n test-crm-008 -- /bin/sh
kubectl port-forward <pod-name> 8080:8080 -n test-crm-008

# Docker
docker ps
docker logs <container-id>
docker exec -it <container-id> /bin/sh
docker images
docker system prune -a

# AWS
aws eks list-clusters
aws eks describe-cluster --name test-crm-008-cluster
aws ecr describe-repositories
aws ecr list-images --repository-name test-crm-008
```

---

## Conclusion

This deployment guide provides comprehensive instructions for deploying the test-CRM-008 Spring Boot application to AWS EKS. Follow the steps carefully, and refer to the troubleshooting section if you encounter any issues.

For additional support or questions, please contact the development team or refer to the official documentation links provided above.
