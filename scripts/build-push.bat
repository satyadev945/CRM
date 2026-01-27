@echo off
setlocal enabledelayedexpansion

echo ========================================
echo Docker Build and Push Script
echo ========================================
echo.

set PROJECT_NAME=crm

set /p IMAGE_TAG="Enter image tag (default: latest): "
if "!IMAGE_TAG!"==" " set IMAGE_TAG=latest

for %%i in ("!IMAGE_TAG!") do set IMAGE_TAG=%%~i
for /f "delims=" %%a in ('powershell -command "'!IMAGE_TAG!' -replace '[^a-zA-Z0-9]+', '-' -replace '^-+', '' -replace '-+$', '' | ForEach-Object { $_.ToLower() }"') do set IMAGE_TAG=%%a

if "!IMAGE_TAG!"=="" set IMAGE_TAG=latest

echo Using sanitized tag: !IMAGE_TAG!
echo.

for /f "delims=" %%a in ('powershell -command "'!PROJECT_NAME!' -replace '[^a-zA-Z0-9]+', '-' -replace '^-+', '' -replace '-+$', '' | ForEach-Object { $_.ToLower() }"') do set IMAGE_NAME=%%a

echo Select container registry:
echo 1. AWS ECR (Elastic Container Registry)
echo 2. Docker Hub
set /p REGISTRY_CHOICE="Enter choice (1 or 2): "

if "!REGISTRY_CHOICE!"=="1" (
  echo.
  echo ========================================
  echo AWS ECR Configuration
  echo ========================================
  
  set /p AWS_REGION="Enter AWS Region (e.g., us-east-1): "
  set /p AWS_ACCOUNT_ID="Enter AWS Account ID: "
  set /p ECR_REPO="Enter ECR Repository Name (default: !IMAGE_NAME!): "
  if "!ECR_REPO!"=="" set ECR_REPO=!IMAGE_NAME!
  
  for /f "delims=" %%a in ('powershell -command "'!ECR_REPO!' -replace '[^a-zA-Z0-9-_/]+', '-' -replace '^-+', '' -replace '-+$', '' | ForEach-Object { $_.ToLower() }"') do set ECR_REPO=%%a
  
  set REGISTRY_URL=!AWS_ACCOUNT_ID!.dkr.ecr.!AWS_REGION!.amazonaws.com
  set FULL_IMAGE_NAME=!REGISTRY_URL!/!ECR_REPO!:!IMAGE_TAG!
  
  echo.
  echo Authenticating with AWS ECR...
  aws ecr get-login-password --region !AWS_REGION! | docker login --username AWS --password-stdin !REGISTRY_URL!
  
  if !ERRORLEVEL! neq 0 (
    echo ECR login failed. Exiting.
    exit /b 1
  )
  
  echo.
  echo Checking if ECR repository exists...
  aws ecr describe-repositories --repository-names !ECR_REPO! --region !AWS_REGION! >nul 2>&1
  
  if !ERRORLEVEL! neq 0 (
    echo Repository does not exist. Creating ECR repository: !ECR_REPO!
    aws ecr create-repository --repository-name !ECR_REPO! --region !AWS_REGION!
    echo Repository created successfully.
  ) else (
    echo Repository already exists.
  )
  
) else if "!REGISTRY_CHOICE!"=="2" (
  echo.
  echo ========================================
  echo Docker Hub Configuration
  echo ========================================
  
  set /p DOCKER_USERNAME="Enter Docker Hub username: "
  set /p DOCKER_PASSWORD="Enter Docker Hub password or access token: "
  
  set FULL_IMAGE_NAME=!DOCKER_USERNAME!/!IMAGE_NAME!:!IMAGE_TAG!
  
  echo.
  echo Authenticating with Docker Hub...
  echo !DOCKER_PASSWORD! | docker login --username !DOCKER_USERNAME! --password-stdin
  
  if !ERRORLEVEL! neq 0 (
    echo Docker Hub login failed. Exiting.
    exit /b 1
  )
  
) else (
  echo Invalid choice. Exiting.
  exit /b 1
)

echo.
echo ========================================
echo Building Docker Image
echo ========================================
echo Image: !FULL_IMAGE_NAME!
echo.

docker build -t "!FULL_IMAGE_NAME!" .

if !ERRORLEVEL! neq 0 (
  echo Docker build failed. Exiting.
  exit /b 1
)

echo.
echo ========================================
echo Pushing Docker Image
echo ========================================
echo.

docker push "!FULL_IMAGE_NAME!"

if !ERRORLEVEL! neq 0 (
  echo Docker push failed. Exiting.
  exit /b 1
)

echo.
echo ========================================
echo Build and Push Completed Successfully
echo ========================================
echo Image: !FULL_IMAGE_NAME!
echo.
if "!REGISTRY_CHOICE!"=="1" (
  echo To deploy to ECS, run:
  echo   .\scripts\deploy-image.bat
) else (
  echo To run locally:
  echo   docker run -d -p 8080:8080 !FULL_IMAGE_NAME!
)
echo.

endlocal