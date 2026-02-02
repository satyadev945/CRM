@echo off
setlocal enabledelayedexpansion

:: Function to display messages
call :print_message "CRM Application - Docker Build and Push Script"

:: Project information
set PROJECT_NAME=crm
call :print_message "Building project: %PROJECT_NAME%"

:: Select registry type
echo Select registry type:
echo 1. AWS ECR (Amazon Elastic Container Registry)
echo 2. Docker Hub
set /p REGISTRY_SELECTION="Enter selection (1-2): "

:: Process selection and collect required information
if "%REGISTRY_SELECTION%"=="1" (
    call :print_message "Selected AWS ECR registry"
    set /p AWS_REGION="Enter AWS region (e.g., us-east-1): "

    :: Get AWS account ID
    call :print_message "Retrieving AWS account ID..."
    for /f "tokens=*" %%i in ('aws sts get-caller-identity --query Account --output text 2^>nul') do set ACCOUNT_ID=%%i

    if "!ACCOUNT_ID!"=="" (
        set /p ACCOUNT_ID="Could not retrieve AWS account ID automatically. Please enter your AWS account ID: "
    ) else (
        call :print_message "AWS account ID: !ACCOUNT_ID!"
    )

    :: Create ECR repository name from project name (PowerShell for better string handling)
    for /f "tokens=*" %%i in ('powershell -command "$name = '%PROJECT_NAME%'.ToLower() -replace '[^a-z0-9]', '-' -replace '^-+', '' -replace '-+$', ''; echo $name"') do set ECR_REPO=%%i

    :: Format full registry URL for ECR
    set REGISTRY_URL=!ACCOUNT_ID!.dkr.ecr.!AWS_REGION!.amazonaws.com

) else if "%REGISTRY_SELECTION%"=="2" (
    call :print_message "Selected Docker Hub registry"
    set /p DOCKER_USERNAME="Enter Docker Hub username: "
    set /p DOCKER_PASSWORD="Enter Docker Hub password: "
    set REGISTRY_URL=docker.io
) else (
    call :print_error "Invalid selection. Please choose 1 for AWS ECR or 2 for Docker Hub."
    exit /b 1
)

:: Sanitize image name (PowerShell for better string handling)
for /f "tokens=*" %%i in ('powershell -command "$name = '%PROJECT_NAME%'.ToLower() -replace '[^a-z0-9]', '-' -replace '^-+', '' -replace '-+$', ''; echo $name"') do set IMAGE_NAME=%%i

:: Ask for image tag
set /p IMAGE_TAG="Enter image tag (default: latest): "
if "!IMAGE_TAG!"=="" (
    set IMAGE_TAG=latest
)

:: Sanitize tag (PowerShell for better string handling)
for /f "tokens=*" %%i in ('powershell -command "$tag = '!IMAGE_TAG!'.ToLower() -replace '[^a-z0-9.-]', '-' -replace '^-+', '' -replace '-+$', ''; if ([string]::IsNullOrEmpty($tag)) { echo 'latest' } else { echo $tag }"') do set IMAGE_TAG=%%i

:: Build the Docker image
call :print_message "Building Docker image: !IMAGE_NAME!:!IMAGE_TAG!"
docker build -t "!IMAGE_NAME!:!IMAGE_TAG!" -f Dockerfile .
if !ERRORLEVEL! neq 0 (
    call :print_error "Docker build failed"
    exit /b 1
)

:: Handle registry login and push based on selection
if "%REGISTRY_SELECTION%"=="1" (
    :: ECR login
    call :print_message "Logging in to Amazon ECR in region !AWS_REGION!"
    aws ecr get-login-password --region !AWS_REGION! | docker login --username AWS --password-stdin !REGISTRY_URL!
    if !ERRORLEVEL! neq 0 (
        call :print_error "ECR login failed"
        exit /b 1
    )

    :: Check if repository exists, create if not
    call :print_message "Checking if ECR repository exists: !ECR_REPO!"
    aws ecr describe-repositories --repository-names !ECR_REPO! --region !AWS_REGION! >nul 2>&1
    if !ERRORLEVEL! neq 0 (
        call :print_message "Creating ECR repository: !ECR_REPO!"
        aws ecr create-repository --repository-name !ECR_REPO! --region !AWS_REGION!
        if !ERRORLEVEL! neq 0 (
            call :print_error "Failed to create ECR repository"
            exit /b 1
        )
    )

    :: Tag and push to ECR
    set FULL_IMAGE_NAME=!REGISTRY_URL!/!ECR_REPO!:!IMAGE_TAG!
    call :print_message "Tagging image for ECR: !FULL_IMAGE_NAME!"
    docker tag "!IMAGE_NAME!:!IMAGE_TAG!" "!FULL_IMAGE_NAME!"

    call :print_message "Pushing image to ECR: !FULL_IMAGE_NAME!"
    docker push "!FULL_IMAGE_NAME!"
    if !ERRORLEVEL! neq 0 (
        call :print_error "Image push to ECR failed"
        exit /b 1
    )

) else if "%REGISTRY_SELECTION%"=="2" (
    :: Docker Hub login
    call :print_message "Logging in to Docker Hub as !DOCKER_USERNAME!"
    echo !DOCKER_PASSWORD! | docker login --username !DOCKER_USERNAME! --password-stdin
    if !ERRORLEVEL! neq 0 (
        call :print_error "Docker Hub login failed"
        exit /b 1
    )

    :: Tag and push to Docker Hub
    set FULL_IMAGE_NAME=!DOCKER_USERNAME!/!IMAGE_NAME!:!IMAGE_TAG!
    call :print_message "Tagging image for Docker Hub: !FULL_IMAGE_NAME!"
    docker tag "!IMAGE_NAME!:!IMAGE_TAG!" "!FULL_IMAGE_NAME!"

    call :print_message "Pushing image to Docker Hub: !FULL_IMAGE_NAME!"
    docker push "!FULL_IMAGE_NAME!"
    if !ERRORLEVEL! neq 0 (
        call :print_error "Image push to Docker Hub failed"
        exit /b 1
    )
)

:: Print success message with image details
call :print_success "Successfully built and pushed image: !FULL_IMAGE_NAME!"
echo Image URI: !FULL_IMAGE_NAME!
echo Use this Image URI when deploying to AWS ECS

:: Save image URI to a file for later use
echo !FULL_IMAGE_NAME! > image-uri.txt
call :print_message "Image URI saved to .\image-uri.txt"

exit /b 0

:: Functions
:print_message
echo [34m^>^> %~1[0m
exit /b 0

:print_error
echo [31m^>^> ERROR: %~1[0m
exit /b 0

:print_success
echo [32m^>^> %~1[0m
exit /b 0