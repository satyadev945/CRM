@echo off
setlocal enabledelayedexpansion

echo =====================================
echo AWS ECS Fargate Deployment Script
echo =====================================
echo.

set PROJECT_NAME=container-testrah
set TASK_FAMILY=!PROJECT_NAME!-task
set SERVICE_NAME=!PROJECT_NAME!-service

set /p AWS_REGION="Enter AWS Region (e.g., us-east-1): "
set AWS_DEFAULT_REGION=!AWS_REGION!

set /p CLUSTER_NAME="Enter ECS Cluster Name: "

echo.
echo Checking ECS cluster...
aws ecs describe-clusters --clusters !CLUSTER_NAME! --region !AWS_REGION! >nul 2>&1
if !ERRORLEVEL! neq 0 (
    echo Cluster does not exist. Creating ECS cluster: !CLUSTER_NAME!
    aws ecs create-cluster --cluster-name !CLUSTER_NAME! --region !AWS_REGION!
    if !ERRORLEVEL! neq 0 (
        echo ERROR: Failed to create cluster
        exit /b 1
    )
    echo Cluster created successfully
)

echo Retrieving AWS Account ID...
for /f "delims=" %%i in ('aws sts get-caller-identity --query Account --output text') do set ACCOUNT_ID=%%i
echo Account ID: !ACCOUNT_ID!

echo.
echo === Network Configuration ===
set /p VPC_ID="Enter VPC ID: "
set /p SUBNETS_INPUT="Enter Subnet IDs (comma-separated, at least 2): "
set /p SECURITY_GROUP="Enter Security Group ID: "

for /f "tokens=1,2 delims=," %%a in ("!SUBNETS_INPUT!") do (
    set SUBNET_1=%%a
    set SUBNET_2=%%b
)

set SUBNET_1=!SUBNET_1: =!
if "!SUBNET_2!"=="" set SUBNET_2=!SUBNET_1!
set SUBNET_2=!SUBNET_2: =!

echo Subnet 1: !SUBNET_1!
echo Subnet 2: !SUBNET_2!

echo.
set /p IMAGE_URI="Enter ECR Image URI (e.g., 123456789.dkr.ecr.us-east-1.amazonaws.com/app:latest): "

echo.
set /p NEED_LB="Do you need a load balancer for this service? (y/n): "

if /i "!NEED_LB!"=="y" (
    echo.
    echo Creating Application Load Balancer and Target Group...
    
    set ALB_NAME=!PROJECT_NAME!-alb
    echo Creating ALB: !ALB_NAME!
    
    for /f "delims=" %%i in ('aws elbv2 create-load-balancer --name !ALB_NAME! --subnets !SUBNET_1! !SUBNET_2! --security-groups !SECURITY_GROUP! --scheme internet-facing --type application --ip-address-type ipv4 --region !AWS_REGION! --query "LoadBalancers[0].LoadBalancerArn" --output text 2^>nul') do set ALB_ARN=%%i
    
    if "!ALB_ARN!"=="" (
        echo ALB may already exist. Attempting to describe existing ALB...
        for /f "delims=" %%i in ('aws elbv2 describe-load-balancers --names !ALB_NAME! --region !AWS_REGION! --query "LoadBalancers[0].LoadBalancerArn" --output text 2^>nul') do set ALB_ARN=%%i
    )
    
    if "!ALB_ARN!"=="" (
        echo ERROR: Failed to create or find ALB
        exit /b 1
    )
    
    echo ALB ARN: !ALB_ARN!
    
    for /f "delims=" %%i in ('aws elbv2 describe-load-balancers --load-balancer-arns !ALB_ARN! --region !AWS_REGION! --query "LoadBalancers[0].DNSName" --output text') do set ALB_DNS=%%i
    
    set TG_NAME=!PROJECT_NAME!-tg
    echo Creating Target Group: !TG_NAME!
    
    for /f "delims=" %%i in ('aws elbv2 create-target-group --name !TG_NAME! --protocol HTTP --port 8080 --vpc-id !VPC_ID! --target-type ip --health-check-enabled --health-check-protocol HTTP --health-check-path "/actuator/health" --health-check-interval-seconds 30 --health-check-timeout-seconds 5 --healthy-threshold-count 2 --unhealthy-threshold-count 3 --region !AWS_REGION! --query "TargetGroups[0].TargetGroupArn" --output text 2^>nul') do set TARGET_GROUP_ARN=%%i
    
    if "!TARGET_GROUP_ARN!"=="" (
        echo Target Group may already exist. Attempting to describe existing TG...
        for /f "delims=" %%i in ('aws elbv2 describe-target-groups --names !TG_NAME! --region !AWS_REGION! --query "TargetGroups[0].TargetGroupArn" --output text 2^>nul') do set TARGET_GROUP_ARN=%%i
    )
    
    if "!TARGET_GROUP_ARN!"=="" (
        echo ERROR: Failed to create or find Target Group
        exit /b 1
    )
    
    echo Target Group ARN: !TARGET_GROUP_ARN!
    
    echo Creating ALB Listener...
    aws elbv2 create-listener --load-balancer-arn !ALB_ARN! --protocol HTTP --port 80 --default-actions Type=forward,TargetGroupArn=!TARGET_GROUP_ARN! --region !AWS_REGION! >nul 2>&1
    
    set USE_LOAD_BALANCER=true
) else (
    set USE_LOAD_BALANCER=false
    set TARGET_GROUP_ARN=
)

echo.
echo Creating CloudWatch Log Group...
set LOG_GROUP=/ecs/!PROJECT_NAME!
aws logs create-log-group --log-group-name !LOG_GROUP! --region !AWS_REGION! >nul 2>&1

echo.
echo Preparing task definition...
copy ecs\task-definition.json %TEMP%\task-definition.json >nul

powershell -Command "(Get-Content %TEMP%\task-definition.json) -replace '{{ACCOUNT_ID}}', '!ACCOUNT_ID!' | Set-Content %TEMP%\task-definition.json"
powershell -Command "(Get-Content %TEMP%\task-definition.json) -replace '{{AWS_REGION}}', '!AWS_REGION!' | Set-Content %TEMP%\task-definition.json"
powershell -Command "(Get-Content %TEMP%\task-definition.json) -replace '{{IMAGE_URI}}', '!IMAGE_URI!' | Set-Content %TEMP%\task-definition.json"

echo Registering task definition...
for /f "delims=" %%i in ('aws ecs register-task-definition --cli-input-json file://%TEMP%/task-definition.json --region !AWS_REGION! --query "taskDefinition.taskDefinitionArn" --output text') do set TASK_DEF_ARN=%%i

if "!TASK_DEF_ARN!"=="" (
    echo ERROR: Failed to register task definition
    exit /b 1
)

echo Task Definition ARN: !TASK_DEF_ARN!

echo.
echo Preparing service definition...
copy ecs\service-definition.json %TEMP%\service-definition.json >nul

powershell -Command "(Get-Content %TEMP%\service-definition.json) -replace '{{CLUSTER_NAME}}', '!CLUSTER_NAME!' | Set-Content %TEMP%\service-definition.json"
powershell -Command "(Get-Content %TEMP%\service-definition.json) -replace '{{SUBNET_1}}', '!SUBNET_1!' | Set-Content %TEMP%\service-definition.json"
powershell -Command "(Get-Content %TEMP%\service-definition.json) -replace '{{SUBNET_2}}', '!SUBNET_2!' | Set-Content %TEMP%\service-definition.json"
powershell -Command "(Get-Content %TEMP%\service-definition.json) -replace '{{SECURITY_GROUP}}', '!SECURITY_GROUP!' | Set-Content %TEMP%\service-definition.json"

if "!USE_LOAD_BALANCER!"=="false" (
    powershell -Command "$json = Get-Content %TEMP%\service-definition.json | ConvertFrom-Json; $json.PSObject.Properties.Remove('loadBalancers'); $json.PSObject.Properties.Remove('healthCheckGracePeriodSeconds'); $json | ConvertTo-Json -Depth 10 | Set-Content %TEMP%\service-definition.json"
) else (
    powershell -Command "(Get-Content %TEMP%\service-definition.json) -replace '{{TARGET_GROUP_ARN}}', '!TARGET_GROUP_ARN!' | Set-Content %TEMP%\service-definition.json"
)

echo Checking if service exists...
for /f "delims=" %%i in ('aws ecs describe-services --cluster !CLUSTER_NAME! --services !SERVICE_NAME! --region !AWS_REGION! --query "services[0].serviceName" --output text 2^>nul') do set EXISTING_SERVICE=%%i

if "!EXISTING_SERVICE!"=="None" (
    echo Service does not exist. Creating new service...
    aws ecs create-service --cli-input-json file://%TEMP%/service-definition.json --region !AWS_REGION!
) else (
    echo Service exists. Updating service with new task definition...
    aws ecs update-service --cluster !CLUSTER_NAME! --service !SERVICE_NAME! --task-definition !TASK_DEF_ARN! --force-new-deployment --region !AWS_REGION!
)

if !ERRORLEVEL! neq 0 (
    echo ERROR: Failed to create or update service
    exit /b 1
)

echo.
echo Waiting for service to become stable (this may take several minutes)...
aws ecs wait services-stable --cluster !CLUSTER_NAME! --services !SERVICE_NAME! --region !AWS_REGION!

echo.
echo =====================================
echo Deployment Completed Successfully
echo =====================================
echo.

aws ecs describe-services --cluster !CLUSTER_NAME! --services !SERVICE_NAME! --region !AWS_REGION! --query "services[0].[serviceName,status,runningCount,desiredCount]" --output table

echo.
echo Service Name: !SERVICE_NAME!
echo Cluster: !CLUSTER_NAME!
echo Task Definition: !TASK_DEF_ARN!
echo CloudWatch Logs: !LOG_GROUP!

if "!USE_LOAD_BALANCER!"=="true" (
    echo Load Balancer DNS: !ALB_DNS!
    echo Access your application at http://!ALB_DNS!
)

echo.
echo To view logs: aws logs tail !LOG_GROUP! --follow --region !AWS_REGION!
echo.

endlocal