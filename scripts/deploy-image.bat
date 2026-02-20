@echo off
setlocal enabledelayedexpansion

REM Deploy to AWS EKS Script for test-CRM-008
REM This script deploys the containerized application to AWS EKS

set PROJECT_NAME=test-crm-008
set NAMESPACE=test-crm-008

echo ==========================================
echo AWS EKS Deployment Script
echo Project: %PROJECT_NAME%
echo ==========================================
echo.

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
echo.
set /p IMAGE_URI="Enter Docker Image URI (e.g., 123456789.dkr.ecr.us-east-1.amazonaws.com/test-crm-008:latest): "
if "!IMAGE_URI!"=="" (
    echo ERROR: Docker Image URI is required
    exit /b 1
)

REM Prompt for database configuration
echo.
echo === Database Configuration ===
set /p DB_HOST="Enter Database Host (default: mysql-host): "
if "!DB_HOST!"=="" set DB_HOST=mysql-host

set /p DB_PORT="Enter Database Port (default: 3306): "
if "!DB_PORT!"=="" set DB_PORT=3306

set /p DB_NAME="Enter Database Name (default: crm): "
if "!DB_NAME!"=="" set DB_NAME=crm

set /p DB_USERNAME="Enter Database Username (default: root): "
if "!DB_USERNAME!"=="" set DB_USERNAME=root

set /p DB_PASSWORD="Enter Database Password: "
if "!DB_PASSWORD!"=="" set DB_PASSWORD=password

REM Configure kubectl for EKS
echo.
echo ==========================================
echo Configuring kubectl for EKS cluster...
echo ==========================================
aws eks update-kubeconfig --region !AWS_REGION! --name !CLUSTER_NAME!

if !ERRORLEVEL! neq 0 (
    echo ERROR: Failed to configure kubectl for EKS cluster
    exit /b 1
)

REM Verify cluster connectivity
echo.
echo Verifying cluster connectivity...
kubectl cluster-info
if !ERRORLEVEL! neq 0 (
    echo ERROR: Cannot connect to Kubernetes cluster
    exit /b 1
)

REM Update Kubernetes manifests with actual values
echo.
echo ==========================================
echo Updating Kubernetes manifests...
echo ==========================================

set MANIFEST_DIR=kubernetes

REM Create temporary manifest files
copy "%MANIFEST_DIR%\deployment.yaml" "%MANIFEST_DIR%\deployment.yaml.tmp" >nul
copy "%MANIFEST_DIR%\ingress.yaml" "%MANIFEST_DIR%\ingress.yaml.tmp" >nul

REM Replace placeholders using PowerShell
powershell -Command "(Get-Content '%MANIFEST_DIR%\deployment.yaml.tmp') -replace '{{IMAGE_URI}}', '%IMAGE_URI%' | Set-Content '%MANIFEST_DIR%\deployment.yaml.tmp'"
powershell -Command "(Get-Content '%MANIFEST_DIR%\deployment.yaml.tmp') -replace '{{DB_HOST}}', '%DB_HOST%' | Set-Content '%MANIFEST_DIR%\deployment.yaml.tmp'"
powershell -Command "(Get-Content '%MANIFEST_DIR%\deployment.yaml.tmp') -replace '{{DB_PORT}}', '%DB_PORT%' | Set-Content '%MANIFEST_DIR%\deployment.yaml.tmp'"
powershell -Command "(Get-Content '%MANIFEST_DIR%\deployment.yaml.tmp') -replace '{{DB_NAME}}', '%DB_NAME%' | Set-Content '%MANIFEST_DIR%\deployment.yaml.tmp'"
powershell -Command "(Get-Content '%MANIFEST_DIR%\deployment.yaml.tmp') -replace '{{DB_USERNAME}}', '%DB_USERNAME%' | Set-Content '%MANIFEST_DIR%\deployment.yaml.tmp'"
powershell -Command "(Get-Content '%MANIFEST_DIR%\deployment.yaml.tmp') -replace '{{DB_PASSWORD}}', '%DB_PASSWORD%' | Set-Content '%MANIFEST_DIR%\deployment.yaml.tmp'"

echo Manifests updated successfully

REM Apply Kubernetes manifests
echo.
echo ==========================================
echo Deploying to EKS cluster...
echo ==========================================

echo.
echo Creating namespace...
kubectl apply -f %MANIFEST_DIR%\namespace.yaml

echo.
echo Deploying application...
kubectl apply -f %MANIFEST_DIR%\deployment.yaml.tmp

echo.
echo Creating service...
kubectl apply -f %MANIFEST_DIR%\service.yaml

echo.
echo Creating ingress...
kubectl apply -f %MANIFEST_DIR%\ingress.yaml.tmp

REM Wait for deployment rollout
echo.
echo ==========================================
echo Waiting for deployment to complete...
echo ==========================================
kubectl rollout status deployment/%PROJECT_NAME% -n %NAMESPACE% --timeout=5m

if !ERRORLEVEL! neq 0 (
    echo ERROR: Deployment rollout failed
    echo.
    echo Checking pod status...
    kubectl get pods -n %NAMESPACE%
    echo.
    echo Checking pod logs...
    kubectl logs -n %NAMESPACE% -l app=%PROJECT_NAME% --tail=50
    
    REM Cleanup temporary files
    del /f /q "%MANIFEST_DIR%\deployment.yaml.tmp" 2>nul
    del /f /q "%MANIFEST_DIR%\ingress.yaml.tmp" 2>nul
    
    exit /b 1
)

REM Verify deployment
echo.
echo ==========================================
echo Verifying deployment...
echo ==========================================
kubectl get pods,svc,ingress -n %NAMESPACE%

REM Get ingress URL
echo.
echo ==========================================
echo Deployment Information
echo ==========================================

for /f "delims=" %%i in ('kubectl get ingress %PROJECT_NAME%-ingress -n %NAMESPACE% -o jsonpath^="{.status.loadBalancer.ingress[0].hostname}" 2^>nul') do set INGRESS_ADDRESS=%%i
if "!INGRESS_ADDRESS!"=="" set INGRESS_ADDRESS=Pending...

echo Namespace: %NAMESPACE%
echo Deployment: %PROJECT_NAME%
echo Image: !IMAGE_URI!
echo.
echo Application URL: http://!INGRESS_ADDRESS!
echo.
echo Note: It may take a few minutes for the Load Balancer to become available.
echo.
echo To check the status:
echo   kubectl get pods -n %NAMESPACE%
echo   kubectl get svc -n %NAMESPACE%
echo   kubectl get ingress -n %NAMESPACE%
echo.
echo To view logs:
echo   kubectl logs -n %NAMESPACE% -l app=%PROJECT_NAME%
echo.
echo To rollback if needed:
echo   kubectl rollout undo deployment/%PROJECT_NAME% -n %NAMESPACE%
echo ==========================================

REM Cleanup temporary files
del /f /q "%MANIFEST_DIR%\deployment.yaml.tmp" 2>nul
del /f /q "%MANIFEST_DIR%\ingress.yaml.tmp" 2>nul

echo.
echo Deployment completed successfully!

endlocal
