#!/bin/bash
set -e
set -o pipefail

echo "========================================"
echo "AWS EKS Deployment Script"
echo "========================================"
echo ""

# Project configuration
PROJECT_NAME="crm-app"
NAMESPACE="crm-app"

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
read -p "Enter Docker Image URI (e.g., account.dkr.ecr.region.amazonaws.com/repo:tag): " IMAGE_URI
if [ -z "$IMAGE_URI" ]; then
    echo "ERROR: Docker Image URI is required"
    exit 1
fi

echo ""
echo "--- Application Configuration ---"
echo "The following environment variables can be configured:"
echo ""

# Database configuration
read -p "Enter DDL_AUTO mode (validate/update/create/create-drop) [default: validate]: " DDL_AUTO
DDL_AUTO=${DDL_AUTO:-validate}

read -p "Enter database URL (e.g., jdbc:mysql://host:3306/crm?useSSL=false): " DB_URL
if [ -z "$DB_URL" ]; then
    echo "WARNING: No database URL provided. Using placeholder."
    DB_URL="jdbc:mysql://your-mysql-host:3306/crm?useSSL=false"
fi

read -p "Enter database username [default: root]: " DB_USER
DB_USER=${DB_USER:-root}

read -sp "Enter database password: " DB_PASSWORD
echo ""
if [ -z "$DB_PASSWORD" ]; then
    echo "WARNING: No database password provided. Using placeholder."
    DB_PASSWORD="changeme"
fi

echo ""
echo "========================================"
echo "Configuring kubectl for EKS"
echo "========================================"

aws eks update-kubeconfig --region "$AWS_REGION" --name "$CLUSTER_NAME"

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to configure kubectl for EKS cluster"
    exit 1
fi

echo "Verifying cluster connectivity..."
kubectl cluster-info || {
    echo "ERROR: Cannot connect to Kubernetes cluster"
    exit 1
}

echo ""
echo "========================================"
echo "Updating Kubernetes Manifests"
echo "========================================"

# Update manifests with configuration values
cd "$(dirname "$0")/.."

if [ ! -d "kubernetes" ]; then
    echo "ERROR: kubernetes directory not found"
    exit 1
fi

# Create backup of manifests
cp -r kubernetes kubernetes.backup

echo "Updating deployment.yaml with image URI and configuration..."
sed -i "s|{{IMAGE_URI}}|$IMAGE_URI|g" kubernetes/deployment.yaml
sed -i "s|{{DDL_AUTO}}|$DDL_AUTO|g" kubernetes/deployment.yaml
sed -i "s|{{DB_URL}}|$DB_URL|g" kubernetes/deployment.yaml
sed -i "s|{{DB_USER}}|$DB_USER|g" kubernetes/deployment.yaml
sed -i "s|{{DB_PASSWORD}}|$DB_PASSWORD|g" kubernetes/deployment.yaml

echo "Updating ingress.yaml..."
sed -i "s|crm-app.example.com|$PROJECT_NAME.$AWS_REGION.elb.amazonaws.com|g" kubernetes/ingress.yaml

echo ""
echo "========================================"
echo "Deploying to AWS EKS"
echo "========================================"

echo "Creating namespace..."
kubectl apply -f kubernetes/namespace.yaml

echo "Deploying application..."
kubectl apply -f kubernetes/deployment.yaml

echo "Creating service..."
kubectl apply -f kubernetes/service.yaml

echo "Creating ingress..."
kubectl apply -f kubernetes/ingress.yaml

echo ""
echo "========================================"
echo "Waiting for Deployment Rollout"
echo "========================================"

kubectl rollout status deployment/$PROJECT_NAME -n $NAMESPACE --timeout=5m

if [ $? -ne 0 ]; then
    echo "ERROR: Deployment rollout failed"
    echo "Rolling back to previous version..."
    kubectl rollout undo deployment/$PROJECT_NAME -n $NAMESPACE
    exit 1
fi

echo ""
echo "========================================"
echo "Verifying Deployment"
echo "========================================"

kubectl get pods,svc,ingress -n $NAMESPACE

echo ""
echo "========================================"
echo "Deployment Completed Successfully!"
echo "========================================"
echo ""
echo "Application Details:"
echo "  Namespace: $NAMESPACE"
echo "  Image: $IMAGE_URI"
echo "  Replicas: 2"
echo ""

# Get ingress URL
INGRESS_URL=$(kubectl get ingress $PROJECT_NAME-ingress -n $NAMESPACE -o jsonpath='{.status.loadBalancer.ingress[0].hostname}' 2>/dev/null || echo "Pending...")

echo "Access your application at:"
if [ "$INGRESS_URL" != "Pending..." ]; then
    echo "  http://$INGRESS_URL"
else
    echo "  Ingress URL is being provisioned. Check with:"
    echo "  kubectl get ingress -n $NAMESPACE"
fi

echo ""
echo "Useful Commands:"
echo "  View pods: kubectl get pods -n $NAMESPACE"
echo "  View logs: kubectl logs -f deployment/$PROJECT_NAME -n $NAMESPACE"
echo "  Scale deployment: kubectl scale deployment/$PROJECT_NAME --replicas=3 -n $NAMESPACE"
echo "  Delete deployment: kubectl delete namespace $NAMESPACE"
echo ""

# Restore original manifests
rm -rf kubernetes
mv kubernetes.backup kubernetes

echo "Manifest templates restored."
echo ""