@echo off
setlocal enabledelayedexpansion

REM Deploy to AWS ECS Fargate Script for CRM Application
REM This script deploys the Docker image to AWS ECS Fargate

echo ==========================================
echo CRM Application - ECS Fargate Deployment
echo ==========================================
echo.

REM Project configuration
set PROJECT_NAME=crm-045
set TASK_FAMILY=crm-045-task
set SERVICE_NAME=crm-045-service

echo Project: %PROJECT_NAME%
echo Task Family: %TASK_FAMILY%
echo Service Name: %SERVICE_NAME%
echo.

REM Prompt for AWS configuration
echo === AWS Configuration ===
set /p AWS_REGION="Enter AWS region (e.g., us-east-1): "
set /p CLUSTER_NAME="Enter ECS cluster name: "

echo.
echo Checking if ECS cluster exists...
aws ecs describe-clusters --clusters !CLUSTER_NAME! --region !AWS_REGION! >nul 2>&1

if !ERRORLEVEL! neq 0 (
    echo Cluster does not exist. Creating ECS cluster: !CLUSTER_NAME!
    aws ecs create-cluster --cluster-name !CLUSTER_NAME! --region !AWS_REGION!
    if !ERRORLEVEL! neq 0 (
        echo ERROR: Failed to create ECS cluster
        exit /b 1
    )
    echo ECS cluster created successfully
)
echo.

REM Get AWS Account ID
echo Retrieving AWS Account ID...
for /f "tokens=*" %%i in ('aws sts get-caller-identity --query Account --output text') do set ACCOUNT_ID=%%i
echo AWS Account ID: !ACCOUNT_ID!
echo.

REM Prompt for network configuration
echo === Network Configuration ===
set /p VPC_ID="Enter VPC ID: "
set /p SUBNETS_INPUT="Enter Subnet IDs (comma-separated, at least 2): "
set /p SECURITY_GROUP="Enter Security Group ID: "

REM Parse subnets
for /f "tokens=1,2 delims=," %%a in ("!SUBNETS_INPUT!") do (
    set SUBNET_1=%%a
    set SUBNET_2=%%b
)
set SUBNET_1=!SUBNET_1: =!
set SUBNET_2=!SUBNET_2: =!

echo.
echo VPC: !VPC_ID!
echo Subnet 1: !SUBNET_1!
echo Subnet 2: !SUBNET_2!
echo Security Group: !SECURITY_GROUP!
echo.

REM Prompt for Docker image URI
echo === Docker Image Configuration ===
set /p IMAGE_URI="Enter Docker image URI (e.g., 123456789.dkr.ecr.us-east-1.amazonaws.com/crm-045:latest): "
echo Image URI: !IMAGE_URI!
echo.

REM Prompt for database configuration
echo === Database Configuration ===
set /p DB_HOST="Enter database host (e.g., mydb.123456.us-east-1.rds.amazonaws.com): "
set /p DB_USERNAME="Enter database username: "
set /p DB_PASSWORD="Enter database password: "
echo.

REM Load balancer configuration
echo === Load Balancer Configuration ===
set /p NEED_LB="Do you need a load balancer for this service? (y/n): "

if /i "!NEED_LB!"=="y" (
    echo.
    echo Creating Application Load Balancer and Target Group...
    
    REM Create target group with ip target type
    for /f "tokens=*" %%i in ('powershell -Command "[int](Get-Date -UFormat %%s)"') do set TIMESTAMP=%%i
    set TG_NAME=crm-045-tg-!TIMESTAMP!
    echo Creating target group: !TG_NAME!
    
    for /f "tokens=*" %%i in ('aws elbv2 create-target-group --name !TG_NAME! --protocol HTTP --port 8080 --vpc-id !VPC_ID! --target-type ip --health-check-enabled --health-check-protocol HTTP --health-check-path "/appinfo/health" --health-check-interval-seconds 30 --health-check-timeout-seconds 5 --healthy-threshold-count 2 --unhealthy-threshold-count 3 --region !AWS_REGION! --query "TargetGroups[0].TargetGroupArn" --output text') do set TARGET_GROUP_ARN=%%i
    
    echo Target Group created: !TARGET_GROUP_ARN!
    
    REM Check if ALB exists
    set ALB_NAME=crm-045-alb
    for /f "tokens=*" %%i in ('aws elbv2 describe-load-balancers --names !ALB_NAME! --region !AWS_REGION! --query "LoadBalancers[0].LoadBalancerArn" --output text 2^>nul') do set ALB_ARN=%%i
    
    if "!ALB_ARN!"=="" (
        echo Creating Application Load Balancer: !ALB_NAME!
        
        for /f "tokens=*" %%i in ('aws elbv2 create-load-balancer --name !ALB_NAME! --subnets !SUBNET_1! !SUBNET_2! --security-groups !SECURITY_GROUP! --scheme internet-facing --type application --ip-address-type ipv4 --region !AWS_REGION! --query "LoadBalancers[0].LoadBalancerArn" --output text') do set ALB_ARN=%%i
        
        echo Application Load Balancer created: !ALB_ARN!
        
        REM Get ALB DNS name
        for /f "tokens=*" %%i in ('aws elbv2 describe-load-balancers --load-balancer-arns !ALB_ARN! --region !AWS_REGION! --query "LoadBalancers[0].DNSName" --output text') do set ALB_DNS=%%i
        
        REM Create listener
        echo Creating ALB listener...
        aws elbv2 create-listener --load-balancer-arn !ALB_ARN! --protocol HTTP --port 80 --default-actions Type=forward,TargetGroupArn=!TARGET_GROUP_ARN! --region !AWS_REGION! >nul
        
        echo ALB listener created
        echo ALB DNS Name: !ALB_DNS!
    ) else (
        echo Using existing Application Load Balancer: !ALB_ARN!
        
        REM Get ALB DNS name
        for /f "tokens=*" %%i in ('aws elbv2 describe-load-balancers --load-balancer-arns !ALB_ARN! --region !AWS_REGION! --query "LoadBalancers[0].DNSName" --output text') do set ALB_DNS=%%i
        
        echo ALB DNS Name: !ALB_DNS!
    )
    
    echo.
) else (
    echo Skipping load balancer configuration
    set TARGET_GROUP_ARN=
)

REM Create CloudWatch log group
echo === CloudWatch Logs Configuration ===
set LOG_GROUP=/ecs/crm-045
echo Creating CloudWatch log group: !LOG_GROUP!
aws logs create-log-group --log-group-name !LOG_GROUP! --region !AWS_REGION! 2>nul
echo.

REM Prepare task definition
echo === Preparing Task Definition ===
set TASK_DEF_FILE=..\ecs\task-definition.json
set TASK_DEF_TEMP=%TEMP%\task-definition-%RANDOM%.json

copy !TASK_DEF_FILE! !TASK_DEF_TEMP! >nul

REM Replace placeholders using PowerShell
powershell -Command "(Get-Content '!TASK_DEF_TEMP!') -replace '{{IMAGE_URI}}', '!IMAGE_URI!' | Set-Content '!TASK_DEF_TEMP!'"
powershell -Command "(Get-Content '!TASK_DEF_TEMP!') -replace '{{AWS_REGION}}', '!AWS_REGION!' | Set-Content '!TASK_DEF_TEMP!'"
powershell -Command "(Get-Content '!TASK_DEF_TEMP!') -replace '{{ACCOUNT_ID}}', '!ACCOUNT_ID!' | Set-Content '!TASK_DEF_TEMP!'"
powershell -Command "(Get-Content '!TASK_DEF_TEMP!') -replace '{{DB_HOST}}', '!DB_HOST!' | Set-Content '!TASK_DEF_TEMP!'"
powershell -Command "(Get-Content '!TASK_DEF_TEMP!') -replace '{{DB_USERNAME}}', '!DB_USERNAME!' | Set-Content '!TASK_DEF_TEMP!'"
powershell -Command "(Get-Content '!TASK_DEF_TEMP!') -replace '{{DB_PASSWORD}}', '!DB_PASSWORD!' | Set-Content '!TASK_DEF_TEMP!'"

echo Task definition prepared
echo.

REM Register task definition
echo === Registering Task Definition ===
for /f "tokens=*" %%i in ('aws ecs register-task-definition --cli-input-json file://!TASK_DEF_TEMP! --region !AWS_REGION! --query "taskDefinition.taskDefinitionArn" --output text') do set TASK_DEF_ARN=%%i

if !ERRORLEVEL! neq 0 (
    echo ERROR: Failed to register task definition
    del !TASK_DEF_TEMP!
    exit /b 1
)

echo Task definition registered: !TASK_DEF_ARN!
echo.

REM Clean up temp file
del !TASK_DEF_TEMP!

REM Prepare service definition
echo === Preparing Service Definition ===
set SERVICE_DEF_FILE=..\ecs\service-definition.json
set SERVICE_DEF_TEMP=%TEMP%\service-definition-%RANDOM%.json

copy !SERVICE_DEF_FILE! !SERVICE_DEF_TEMP! >nul

REM Replace placeholders
powershell -Command "(Get-Content '!SERVICE_DEF_TEMP!') -replace '{{CLUSTER_NAME}}', '!CLUSTER_NAME!' | Set-Content '!SERVICE_DEF_TEMP!'"
powershell -Command "(Get-Content '!SERVICE_DEF_TEMP!') -replace '{{SUBNET_1}}', '!SUBNET_1!' | Set-Content '!SERVICE_DEF_TEMP!'"
powershell -Command "(Get-Content '!SERVICE_DEF_TEMP!') -replace '{{SUBNET_2}}', '!SUBNET_2!' | Set-Content '!SERVICE_DEF_TEMP!'"
powershell -Command "(Get-Content '!SERVICE_DEF_TEMP!') -replace '{{SECURITY_GROUP}}', '!SECURITY_GROUP!' | Set-Content '!SERVICE_DEF_TEMP!'"

REM Handle load balancer configuration
if "!TARGET_GROUP_ARN!"=="" (
    REM Remove loadBalancers section if no load balancer
    powershell -Command "$json = Get-Content '!SERVICE_DEF_TEMP!' | ConvertFrom-Json; $json.PSObject.Properties.Remove('loadBalancers'); $json.PSObject.Properties.Remove('healthCheckGracePeriodSeconds'); $json | ConvertTo-Json -Depth 10 | Set-Content '!SERVICE_DEF_TEMP!'"
) else (
    REM Replace target group ARN
    powershell -Command "(Get-Content '!SERVICE_DEF_TEMP!') -replace '{{TARGET_GROUP_ARN}}', '!TARGET_GROUP_ARN!' | Set-Content '!SERVICE_DEF_TEMP!'"
)

echo Service definition prepared
echo.

REM Check if service exists
echo === Checking Service Status ===
for /f "tokens=*" %%i in ('aws ecs describe-services --cluster !CLUSTER_NAME! --services !SERVICE_NAME! --region !AWS_REGION! --query "services[?status==`ACTIVE`].serviceName" --output text 2^>nul') do set EXISTING_SERVICE=%%i

if "!EXISTING_SERVICE!"=="" (
    echo Service does not exist. Creating new service...
    
    aws ecs create-service --cli-input-json file://!SERVICE_DEF_TEMP! --region !AWS_REGION! >nul
    
    if !ERRORLEVEL! neq 0 (
        echo ERROR: Failed to create service
        del !SERVICE_DEF_TEMP!
        exit /b 1
    )
    
    echo Service created: !SERVICE_NAME!
) else (
    echo Service exists. Updating service...
    
    aws ecs update-service --cluster !CLUSTER_NAME! --service !SERVICE_NAME! --task-definition !TASK_DEF_ARN! --desired-count 2 --region !AWS_REGION! >nul
    
    if !ERRORLEVEL! neq 0 (
        echo ERROR: Failed to update service
        del !SERVICE_DEF_TEMP!
        exit /b 1
    )
    
    echo Service updated: !SERVICE_NAME!
)

echo.

REM Clean up temp file
del !SERVICE_DEF_TEMP!

REM Wait for service stability
echo === Waiting for Service Stability ===
echo This may take several minutes...
aws ecs wait services-stable --cluster !CLUSTER_NAME! --services !SERVICE_NAME! --region !AWS_REGION!

if !ERRORLEVEL! neq 0 (
    echo WARNING: Service stability check timed out or failed
) else (
    echo Service is stable
)
echo.

REM Verify deployment
echo === Deployment Verification ===
aws ecs describe-services --cluster !CLUSTER_NAME! --services !SERVICE_NAME! --region !AWS_REGION! --query "services[0].{ServiceName:serviceName,Status:status,DesiredCount:desiredCount,RunningCount:runningCount,TaskDefinition:taskDefinition}" --output table

echo.
echo ==========================================
echo DEPLOYMENT SUCCESSFUL!
echo ==========================================
echo.
echo Service Details:
echo   Cluster: !CLUSTER_NAME!
echo   Service: !SERVICE_NAME!
echo   Region: !AWS_REGION!
echo.

if not "!ALB_DNS!"=="" (
    echo Application URL:
    echo   http://!ALB_DNS!
    echo.
)

echo CloudWatch Logs:
echo   Log Group: !LOG_GROUP!
echo   Region: !AWS_REGION!
echo.
echo To view logs:
echo   aws logs tail !LOG_GROUP! --follow --region !AWS_REGION!
echo.
echo To check service status:
echo   aws ecs describe-services --cluster !CLUSTER_NAME! --services !SERVICE_NAME! --region !AWS_REGION!
echo.
echo ==========================================

endlocal
