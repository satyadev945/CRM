@echo off
setlocal enabledelayedexpansion

echo ========================================
echo AWS EKS Deployment Script
echo ========================================
echo.

REM Project configuration
set PROJECT_NAME=crm-app
set NAMESPACE=crm-app

REM Prompt for AWS configuration
set /p AWS_REGION="Enter AWS Region (e.g., us-east-1): "
if "!AWS_REGION!"=="" (
    echo ERROR: AWS Region is required
    exit /b 1
)

set /p CLUSTER_NAME="Enter EKS Cluster Name: "
if "!CLUSTER_NAME!"=="" (
    echo ERROR: EKS Cluster Name is required
    exit /b 1
)

REM Prompt for Docker image URI
set /p IMAGE_URI="Enter Docker Image URI (e.g., account.dkr.ecr.region.amazonaws.com/repo:tag): "
if "!IMAGE_URI!"=="" (
    echo ERROR: Docker Image URI is required
    exit /b 1
)

echo.
echo --- Application Configuration ---
echo The following environment variables can be configured:
echo.

REM Database configuration
set /p DDL_AUTO="Enter DDL_AUTO mode (validate/update/create/create-drop) [default: validate]: "
if "!DDL_AUTO!"=="" set DDL_AUTO=validate

set /p DB_URL="Enter database URL (e.g., jdbc:mysql://host:3306/crm?useSSL=false): "
if "!DB_URL!"=="" (
    echo WARNING: No database URL provided. Using placeholder.
    set DB_URL=jdbc:mysql://your-mysql-host:3306/crm?useSSL=false
)

set /p DB_USER="Enter database username [default: root]: "
if "!DB_USER!"=="" set DB_USER=root

set /p DB_PASSWORD="Enter database password: "
if "!DB_PASSWORD!"=="" (
    echo WARNING: No database password provided. Using placeholder.
    set DB_PASSWORD=changeme
)

echo.
echo ========================================
echo Configuring kubectl for EKS
echo ========================================

aws eks update-kubeconfig --region !AWS_REGION! --name !CLUSTER_NAME!

if !ERRORLEVEL! neq 0 (
    echo ERROR: Failed to configure kubectl for EKS cluster
    exit /b 1
)

echo Verifying cluster connectivity...
kubectl cluster-info
if !ERRORLEVEL! neq 0 (
    echo ERROR: Cannot connect to Kubernetes cluster
    exit /b 1
)

echo.
echo ========================================
echo Updating Kubernetes Manifests
echo ========================================

cd /d "%~dp0.."

if not exist "kubernetes" (
    echo ERROR: kubernetes directory not found
    exit /b 1
)

REM Create backup of manifests
if exist kubernetes.backup rmdir /s /q kubernetes.backup
xcopy kubernetes kubernetes.backup\ /E /I /Q

echo Updating deployment.yaml with image URI and configuration...
powershell -Command "(Get-Content kubernetes\deployment.yaml) -replace '{{IMAGE_URI}}', '%IMAGE_URI%' | Set-Content kubernetes\deployment.yaml"
powershell -Command "(Get-Content kubernetes\deployment.yaml) -replace '{{DDL_AUTO}}', '%DDL_AUTO%' | Set-Content kubernetes\deployment.yaml"
powershell -Command "(Get-Content kubernetes\deployment.yaml) -replace '{{DB_URL}}', '%DB_URL%' | Set-Content kubernetes\deployment.yaml"
powershell -Command "(Get-Content kubernetes\deployment.yaml) -replace '{{DB_USER}}', '%DB_USER%' | Set-Content kubernetes\deployment.yaml"
powershell -Command "(Get-Content kubernetes\deployment.yaml) -replace '{{DB_PASSWORD}}', '%DB_PASSWORD%' | Set-Content kubernetes\deployment.yaml"

echo Updating ingress.yaml...
powershell -Command "(Get-Content kubernetes\ingress.yaml) -replace 'crm-app.example.com', '%PROJECT_NAME%.%AWS_REGION%.elb.amazonaws.com' | Set-Content kubernetes\ingress.yaml"

echo.
echo ========================================
echo Deploying to AWS EKS
echo ========================================

echo Creating namespace...
kubectl apply -f kubernetes\namespace.yaml

echo Deploying application...
kubectl apply -f kubernetes\deployment.yaml

echo Creating service...
kubectl apply -f kubernetes\service.yaml

echo Creating ingress...
kubectl apply -f kubernetes\ingress.yaml

echo.
echo ========================================
echo Waiting for Deployment Rollout
echo ========================================

kubectl rollout status deployment/!PROJECT_NAME! -n !NAMESPACE! --timeout=5m

if !ERRORLEVEL! neq 0 (
    echo ERROR: Deployment rollout failed
    echo Rolling back to previous version...
    kubectl rollout undo deployment/!PROJECT_NAME! -n !NAMESPACE!
    exit /b 1
)

echo.
echo ========================================
echo Verifying Deployment
echo ========================================

kubectl get pods,svc,ingress -n !NAMESPACE!

echo.
echo ========================================
echo Deployment Completed Successfully!
echo ========================================
echo.
echo Application Details:
echo   Namespace: !NAMESPACE!
echo   Image: !IMAGE_URI!
echo   Replicas: 2
echo.

REM Get ingress URL
for /f "delims=" %%i in ('kubectl get ingress !PROJECT_NAME!-ingress -n !NAMESPACE! -o jsonpath="{.status.loadBalancer.ingress[0].hostname}" 2^>nul') do set INGRESS_URL=%%i
if "!INGRESS_URL!"=="" set INGRESS_URL=Pending...

echo Access your application at:
if not "!INGRESS_URL!"=="Pending..." (
    echo   http://!INGRESS_URL!
) else (
    echo   Ingress URL is being provisioned. Check with:
    echo   kubectl get ingress -n !NAMESPACE!
)

echo.
echo Useful Commands:
echo   View pods: kubectl get pods -n !NAMESPACE!
echo   View logs: kubectl logs -f deployment/!PROJECT_NAME! -n !NAMESPACE!
echo   Scale deployment: kubectl scale deployment/!PROJECT_NAME! --replicas=3 -n !NAMESPACE!
echo   Delete deployment: kubectl delete namespace !NAMESPACE!
echo.

REM Restore original manifests
rmdir /s /q kubernetes
move kubernetes.backup kubernetes

echo Manifest templates restored.
echo.

endlocal