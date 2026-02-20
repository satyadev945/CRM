#!/bin/bash
set -e
set -o pipefail

# Deploy to AWS EKS Script for test-CRM-008
# This script deploys the containerized application to AWS EKS

PROJECT_NAME="test-crm-008"
NAMESPACE="test-crm-008"

echo "=========================================="
echo "AWS EKS Deployment Script"
echo "Project: $PROJECT_NAME"
echo "=========================================="
echo ""

# Prompt for AWS configuration
read -p "Enter AWS Region (e.g., us-east-1): " AWS_REGION
if [ -z "$AWS_REGION" ]; then
    echo "ERROR: AWS Region is required"
    exit 1
fi

read -p "Enter EKS Cluster Name: " CLUSTER_NAME
if [ -z "$CLUSTER_NAME" ]; then
    echo "ERROR: EKS Cluster Name is required"
    exit 1
fi

# Prompt for Docker image URI
echo ""
read -p "Enter Docker Image URI (e.g., 123456789.dkr.ecr.us-east-1.amazonaws.com/test-crm-008:latest): " IMAGE_URI
if [ -z "$IMAGE_URI" ]; then
    echo "ERROR: Docker Image URI is required"
    exit 1
fi

# Prompt for database configuration
echo ""
echo "=== Database Configuration ==="
read -p "Enter Database Host (default: mysql-host): " DB_HOST
DB_HOST=${DB_HOST:-mysql-host}

read -p "Enter Database Port (default: 3306): " DB_PORT
DB_PORT=${DB_PORT:-3306}

read -p "Enter Database Name (default: crm): " DB_NAME
DB_NAME=${DB_NAME:-crm}

read -p "Enter Database Username (default: root): " DB_USERNAME
DB_USERNAME=${DB_USERNAME:-root}

read -sp "Enter Database Password: " DB_PASSWORD
echo ""
if [ -z "$DB_PASSWORD" ]; then
    DB_PASSWORD="password"
fi

# Configure kubectl for EKS
echo ""
echo "=========================================="
echo "Configuring kubectl for EKS cluster..."
echo "=========================================="
aws eks update-kubeconfig --region "$AWS_REGION" --name "$CLUSTER_NAME"

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to configure kubectl for EKS cluster"
    exit 1
fi

# Verify cluster connectivity
echo ""
echo "Verifying cluster connectivity..."
kubectl cluster-info || {
    echo "ERROR: Cannot connect to Kubernetes cluster"
    exit 1
}

# Update Kubernetes manifests with actual values
echo ""
echo "=========================================="
echo "Updating Kubernetes manifests..."
echo "=========================================="

MANIFEST_DIR="kubernetes"

# Create temporary manifest files
cp "$MANIFEST_DIR/deployment.yaml" "$MANIFEST_DIR/deployment.yaml.tmp"
cp "$MANIFEST_DIR/ingress.yaml" "$MANIFEST_DIR/ingress.yaml.tmp"

# Replace placeholders in deployment.yaml
sed -i "s|{{IMAGE_URI}}|$IMAGE_URI|g" "$MANIFEST_DIR/deployment.yaml.tmp"
sed -i "s|{{DB_HOST}}|$DB_HOST|g" "$MANIFEST_DIR/deployment.yaml.tmp"
sed -i "s|{{DB_PORT}}|$DB_PORT|g" "$MANIFEST_DIR/deployment.yaml.tmp"
sed -i "s|{{DB_NAME}}|$DB_NAME|g" "$MANIFEST_DIR/deployment.yaml.tmp"
sed -i "s|{{DB_USERNAME}}|$DB_USERNAME|g" "$MANIFEST_DIR/deployment.yaml.tmp"
sed -i "s|{{DB_PASSWORD}}|$DB_PASSWORD|g" "$MANIFEST_DIR/deployment.yaml.tmp"

echo "Manifests updated successfully"

# Apply Kubernetes manifests
echo ""
echo "=========================================="
echo "Deploying to EKS cluster..."
echo "=========================================="

echo ""
echo "Creating namespace..."
kubectl apply -f "$MANIFEST_DIR/namespace.yaml"

echo ""
echo "Deploying application..."
kubectl apply -f "$MANIFEST_DIR/deployment.yaml.tmp"

echo ""
echo "Creating service..."
kubectl apply -f "$MANIFEST_DIR/service.yaml"

echo ""
echo "Creating ingress..."
kubectl apply -f "$MANIFEST_DIR/ingress.yaml.tmp"

# Wait for deployment rollout
echo ""
echo "=========================================="
echo "Waiting for deployment to complete..."
echo "=========================================="
kubectl rollout status deployment/$PROJECT_NAME -n $NAMESPACE --timeout=5m

if [ $? -ne 0 ]; then
    echo "ERROR: Deployment rollout failed"
    echo ""
    echo "Checking pod status..."
    kubectl get pods -n $NAMESPACE
    echo ""
    echo "Checking pod logs..."
    kubectl logs -n $NAMESPACE -l app=$PROJECT_NAME --tail=50
    
    # Cleanup temporary files
    rm -f "$MANIFEST_DIR/deployment.yaml.tmp"
    rm -f "$MANIFEST_DIR/ingress.yaml.tmp"
    
    exit 1
fi

# Verify deployment
echo ""
echo "=========================================="
echo "Verifying deployment..."
echo "=========================================="
kubectl get pods,svc,ingress -n $NAMESPACE

# Get ingress URL
echo ""
echo "=========================================="
echo "Deployment Information"
echo "=========================================="
INGRESS_ADDRESS=$(kubectl get ingress $PROJECT_NAME-ingress -n $NAMESPACE -o jsonpath='{.status.loadBalancer.ingress[0].hostname}' 2>/dev/null || echo "Pending...")

echo "Namespace: $NAMESPACE"
echo "Deployment: $PROJECT_NAME"
echo "Image: $IMAGE_URI"
echo ""
echo "Application URL: http://$INGRESS_ADDRESS"
echo ""
echo "Note: It may take a few minutes for the Load Balancer to become available."
echo ""
echo "To check the status:"
echo "  kubectl get pods -n $NAMESPACE"
echo "  kubectl get svc -n $NAMESPACE"
echo "  kubectl get ingress -n $NAMESPACE"
echo ""
echo "To view logs:"
echo "  kubectl logs -n $NAMESPACE -l app=$PROJECT_NAME"
echo ""
echo "To rollback if needed:"
echo "  kubectl rollout undo deployment/$PROJECT_NAME -n $NAMESPACE"
echo "=========================================="

# Cleanup temporary files
rm -f "$MANIFEST_DIR/deployment.yaml.tmp"
rm -f "$MANIFEST_DIR/ingress.yaml.tmp"

echo ""
echo "Deployment completed successfully!"
