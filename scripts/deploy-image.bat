@echo off
setlocal enabledelayedexpansion

echo ========================================
echo AWS ECS Fargate Deployment Script
echo ========================================
echo.

set PROJECT_NAME=crm
set TASK_FAMILY=crm-task
set SERVICE_NAME=crm-service

set /p AWS_REGION="Enter AWS Region (e.g., us-east-1): "
set /p CLUSTER_NAME="Enter ECS Cluster Name: "

echo.
echo Checking if ECS cluster exists...
for /f "delims=" %%i in ('aws ecs describe-clusters --clusters !CLUSTER_NAME! --region !AWS_REGION! --query "clusters[0].clusterName" --output text 2^>nul') do set EXISTING_CLUSTER=%%i

if "!EXISTING_CLUSTER!"=="" (
  echo Cluster does not exist. Creating ECS cluster: !CLUSTER_NAME!
  aws ecs create-cluster --cluster-name !CLUSTER_NAME! --region !AWS_REGION!
  echo Cluster created successfully.
) else (
  echo Cluster already exists.
)

echo.
echo ========================================
echo Network Configuration
echo ========================================
set /p VPC_ID="Enter VPC ID: "
set /p SUBNETS_INPUT="Enter Subnet IDs (comma-separated, at least 2): "
set /p SECURITY_GROUP="Enter Security Group ID: "

for /f "tokens=1,2 delims=," %%a in ("!SUBNETS_INPUT!") do (
  set SUBNET_1=%%a
  set SUBNET_2=%%b
)
set SUBNET_1=!SUBNET_1: =!
set SUBNET_2=!SUBNET_2: =!

echo.
echo ========================================
echo Database Configuration
echo ========================================
set /p DB_HOST="Enter Database Host: "
set /p DB_PORT="Enter Database Port (default: 3306): "
if "!DB_PORT!"=="" set DB_PORT=3306
set /p DB_NAME="Enter Database Name (default: crm): "
if "!DB_NAME!"=="" set DB_NAME=crm
set /p DB_USER="Enter Database User: "
set /p DB_PASSWORD="Enter Database Password: "

echo.
echo ========================================
echo Container Image Configuration
echo ========================================
set /p IMAGE_URI="Enter ECR Image URI (e.g., 123456789.dkr.ecr.us-east-1.amazonaws.com/crm:latest): "

echo.
echo ========================================
echo Load Balancer Configuration
echo ========================================
set /p NEED_LB="Do you need a load balancer for this service? (y/n): "

if /i "!NEED_LB!"=="y" (
  echo Creating Application Load Balancer and Target Group...
  
  set ALB_NAME=!PROJECT_NAME!-alb
  echo Creating ALB: !ALB_NAME!
  
  for /f "delims=" %%i in ('aws elbv2 create-load-balancer --name !ALB_NAME! --subnets !SUBNET_1! !SUBNET_2! --security-groups !SECURITY_GROUP! --scheme internet-facing --type application --region !AWS_REGION! --query "LoadBalancers[0].LoadBalancerArn" --output text') do set ALB_ARN=%%i
  
  echo ALB created: !ALB_ARN!
  
  set TG_NAME=!PROJECT_NAME!-tg
  echo Creating Target Group: !TG_NAME!
  
  for /f "delims=" %%i in ('aws elbv2 create-target-group --name !TG_NAME! --protocol HTTP --port 8080 --vpc-id !VPC_ID! --target-type ip --health-check-enabled --health-check-path "/appinfo/health" --health-check-interval-seconds 30 --health-check-timeout-seconds 5 --healthy-threshold-count 2 --unhealthy-threshold-count 3 --region !AWS_REGION! --query "TargetGroups[0].TargetGroupArn" --output text') do set TARGET_GROUP_ARN=%%i
  
  echo Target Group created: !TARGET_GROUP_ARN!
  
  echo Creating ALB Listener...
  aws elbv2 create-listener --load-balancer-arn !ALB_ARN! --protocol HTTP --port 80 --default-actions Type=forward,TargetGroupArn=!TARGET_GROUP_ARN! --region !AWS_REGION!
  
  echo Listener created successfully.
  
  for /f "delims=" %%i in ('aws elbv2 describe-load-balancers --load-balancer-arns !ALB_ARN! --region !AWS_REGION! --query "LoadBalancers[0].DNSName" --output text') do set ALB_DNS=%%i
  
  echo Load Balancer DNS: !ALB_DNS!
) else (
  echo Skipping load balancer creation.
  set TARGET_GROUP_ARN=
)

echo.
echo Getting AWS Account ID...
for /f "delims=" %%i in ('aws sts get-caller-identity --query Account --output text') do set ACCOUNT_ID=%%i
echo Account ID: !ACCOUNT_ID!

echo.
echo ========================================
echo Creating CloudWatch Log Group
echo ========================================
set LOG_GROUP=/ecs/!PROJECT_NAME!
aws logs describe-log-groups --log-group-name-prefix !LOG_GROUP! --region !AWS_REGION! --query "logGroups[?logGroupName=='!LOG_GROUP!']" --output text | findstr !LOG_GROUP! >nul

if !ERRORLEVEL! neq 0 (
  echo Creating log group: !LOG_GROUP!
  aws logs create-log-group --log-group-name !LOG_GROUP! --region !AWS_REGION!
  echo Log group created.
) else (
  echo Log group already exists.
)

echo.
echo ========================================
echo Preparing ECS Task Definition
echo ========================================

copy ecs\task-definition.json ecs\task-definition.tmp.json >nul

powershell -Command "(Get-Content ecs\task-definition.tmp.json) -replace '{{IMAGE_URI}}', '!IMAGE_URI!' | Set-Content ecs\task-definition.tmp.json"
powershell -Command "(Get-Content ecs\task-definition.tmp.json) -replace '{{AWS_REGION}}', '!AWS_REGION!' | Set-Content ecs\task-definition.tmp.json"
powershell -Command "(Get-Content ecs\task-definition.tmp.json) -replace '{{ACCOUNT_ID}}', '!ACCOUNT_ID!' | Set-Content ecs\task-definition.tmp.json"
powershell -Command "(Get-Content ecs\task-definition.tmp.json) -replace '{{DB_HOST}}', '!DB_HOST!' | Set-Content ecs\task-definition.tmp.json"
powershell -Command "(Get-Content ecs\task-definition.tmp.json) -replace '{{DB_PORT}}', '!DB_PORT!' | Set-Content ecs\task-definition.tmp.json"
powershell -Command "(Get-Content ecs\task-definition.tmp.json) -replace '{{DB_NAME}}', '!DB_NAME!' | Set-Content ecs\task-definition.tmp.json"
powershell -Command "(Get-Content ecs\task-definition.tmp.json) -replace '{{DB_USER}}', '!DB_USER!' | Set-Content ecs\task-definition.tmp.json"
powershell -Command "(Get-Content ecs\task-definition.tmp.json) -replace '{{DB_PASSWORD}}', '!DB_PASSWORD!' | Set-Content ecs\task-definition.tmp.json"

echo Registering ECS task definition...
for /f "delims=" %%i in ('aws ecs register-task-definition --cli-input-json file://ecs/task-definition.tmp.json --region !AWS_REGION! --query "taskDefinition.taskDefinitionArn" --output text') do set TASK_DEF_ARN=%%i

echo Task definition registered: !TASK_DEF_ARN!

del /f ecs\task-definition.tmp.json >nul 2>&1

echo.
echo ========================================
echo Preparing ECS Service Definition
echo ========================================

copy ecs\service-definition.json ecs\service-definition.tmp.json >nul

powershell -Command "(Get-Content ecs\service-definition.tmp.json) -replace '{{CLUSTER_NAME}}', '!CLUSTER_NAME!' | Set-Content ecs\service-definition.tmp.json"
powershell -Command "(Get-Content ecs\service-definition.tmp.json) -replace '{{SUBNET_1}}', '!SUBNET_1!' | Set-Content ecs\service-definition.tmp.json"
powershell -Command "(Get-Content ecs\service-definition.tmp.json) -replace '{{SUBNET_2}}', '!SUBNET_2!' | Set-Content ecs\service-definition.tmp.json"
powershell -Command "(Get-Content ecs\service-definition.tmp.json) -replace '{{SECURITY_GROUP}}', '!SECURITY_GROUP!' | Set-Content ecs\service-definition.tmp.json"

if /i "!NEED_LB!"=="y" (
  powershell -Command "(Get-Content ecs\service-definition.tmp.json) -replace '{{TARGET_GROUP_ARN}}', '!TARGET_GROUP_ARN!' | Set-Content ecs\service-definition.tmp.json"
) else (
  powershell -Command "$content = Get-Content ecs\service-definition.tmp.json | ConvertFrom-Json; $content.PSObject.Properties.Remove('loadBalancers'); $content.PSObject.Properties.Remove('healthCheckGracePeriodSeconds'); $content | ConvertTo-Json -Depth 10 | Set-Content ecs\service-definition.tmp.json"
)

echo Checking if service exists...
for /f "delims=" %%i in ('aws ecs describe-services --cluster !CLUSTER_NAME! --services !SERVICE_NAME! --region !AWS_REGION! --query "services[?serviceName=='!SERVICE_NAME!' ^&^& status=='ACTIVE'].serviceName" --output text 2^>nul') do set EXISTING_SERVICE=%%i

if "!EXISTING_SERVICE!"=="" (
  echo Service does not exist. Creating new service...
  aws ecs create-service --cli-input-json file://ecs/service-definition.tmp.json --region !AWS_REGION!
  echo Service created successfully.
) else (
  echo Service exists. Updating service with new task definition...
  aws ecs update-service --cluster !CLUSTER_NAME! --service !SERVICE_NAME! --task-definition !TASK_DEF_ARN! --region !AWS_REGION!
  echo Service updated successfully.
)

del /f ecs\service-definition.tmp.json >nul 2>&1

echo.
echo ========================================
echo Waiting for Service Stability
echo ========================================
echo This may take several minutes...

aws ecs wait services-stable --cluster !CLUSTER_NAME! --services !SERVICE_NAME! --region !AWS_REGION!

echo.
echo ========================================
echo Deployment Completed Successfully
echo ========================================
echo.
echo Service Details:
aws ecs describe-services --cluster !CLUSTER_NAME! --services !SERVICE_NAME! --region !AWS_REGION! --query "services[0].[serviceName,status,runningCount,desiredCount]" --output table

echo.
echo CloudWatch Logs:
echo   Log Group: !LOG_GROUP!

if /i "!NEED_LB!"=="y" (
  echo.
  echo Application URL:
  echo   http://!ALB_DNS!
  echo.
  echo Note: It may take a few minutes for the target group health checks to pass.
)

echo.
echo Deployment complete!
echo.

endlocal