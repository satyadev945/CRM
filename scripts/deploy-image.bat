@echo off
setlocal enabledelayedexpansion

echo === CRM Container - AWS ECS Fargate Deployment ===
echo.

set /p AWS_REGION="Enter AWS Region (e.g., us-east-1): "
set /p CLUSTER_NAME="Enter ECS Cluster Name: "
set /p VPC_ID="Enter VPC ID: "
set /p SUBNETS_INPUT="Enter Subnet IDs (comma-separated, at least 2): "
set /p SECURITY_GROUP="Enter Security Group ID: "
set /p IMAGE_URI="Enter ECR Image URI: "

for /f "tokens=1,2 delims=," %%a in ("!SUBNETS_INPUT!") do (
    set SUBNET_1=%%a
    set SUBNET_2=%%b
)

set SUBNET_1=!SUBNET_1: =!
set SUBNET_2=!SUBNET_2: =!

echo.
echo Getting AWS Account ID...
for /f "delims=" %%i in ('aws sts get-caller-identity --query Account --output text') do set ACCOUNT_ID=%%i
echo Account ID: !ACCOUNT_ID!

echo Checking if ECS cluster exists...
aws ecs describe-clusters --clusters !CLUSTER_NAME! --region !AWS_REGION! >nul 2>&1
if !ERRORLEVEL! neq 0 (
    echo Creating ECS cluster: !CLUSTER_NAME!
    aws ecs create-cluster --cluster-name !CLUSTER_NAME! --region !AWS_REGION!
)

echo.
set /p USE_LB="Do you need a load balancer for this service? (y/n): "

if /i "!USE_LB!"=="y" (
    echo Creating Application Load Balancer and Target Group...
    
    set ALB_NAME=crm-container-alb
    echo Creating Application Load Balancer: !ALB_NAME!
    
    for /f "delims=" %%i in ('aws elbv2 create-load-balancer --name !ALB_NAME! --subnets !SUBNET_1! !SUBNET_2! --security-groups !SECURITY_GROUP! --region !AWS_REGION! --query "LoadBalancers[0].LoadBalancerArn" --output text') do set ALB_ARN=%%i
    
    echo ALB ARN: !ALB_ARN!
    
    for /f "delims=" %%i in ('aws elbv2 describe-load-balancers --load-balancer-arns !ALB_ARN! --region !AWS_REGION! --query "LoadBalancers[0].DNSName" --output text') do set ALB_DNS=%%i
    
    set TG_NAME=crm-container-tg
    echo Creating Target Group: !TG_NAME!
    
    for /f "delims=" %%i in ('aws elbv2 create-target-group --name !TG_NAME! --protocol HTTP --port 8080 --vpc-id !VPC_ID! --target-type ip --health-check-enabled --health-check-path /actuator/health --health-check-interval-seconds 30 --health-check-timeout-seconds 5 --healthy-threshold-count 2 --unhealthy-threshold-count 3 --region !AWS_REGION! --query "TargetGroups[0].TargetGroupArn" --output text') do set TARGET_GROUP_ARN=%%i
    
    echo Target Group ARN: !TARGET_GROUP_ARN!
    
    echo Creating ALB Listener on port 80...
    aws elbv2 create-listener --load-balancer-arn !ALB_ARN! --protocol HTTP --port 80 --default-actions Type=forward,TargetGroupArn=!TARGET_GROUP_ARN! --region !AWS_REGION!
    
    set LOAD_BALANCER_CONFIG=true
) else (
    echo Skipping load balancer configuration
    set LOAD_BALANCER_CONFIG=false
)

echo Creating CloudWatch Log Group...
aws logs create-log-group --log-group-name /ecs/crm-container --region !AWS_REGION! 2>nul || echo Log group already exists

echo Preparing ECS Task Definition...
copy ecs\task-definition.json ecs\task-definition-tmp.json >nul

powershell -Command "(Get-Content ecs\task-definition-tmp.json) -replace '{{IMAGE_URI}}', '!IMAGE_URI!' | Set-Content ecs\task-definition-tmp.json"
powershell -Command "(Get-Content ecs\task-definition-tmp.json) -replace '{{AWS_REGION}}', '!AWS_REGION!' | Set-Content ecs\task-definition-tmp.json"
powershell -Command "(Get-Content ecs\task-definition-tmp.json) -replace '{{ACCOUNT_ID}}', '!ACCOUNT_ID!' | Set-Content ecs\task-definition-tmp.json"

echo Registering ECS Task Definition...
for /f "delims=" %%i in ('aws ecs register-task-definition --cli-input-json file://ecs/task-definition-tmp.json --region !AWS_REGION! --query "taskDefinition.taskDefinitionArn" --output text') do set TASK_DEF_ARN=%%i

echo Task Definition ARN: !TASK_DEF_ARN!

copy ecs\service-definition.json ecs\service-definition-tmp.json >nul

powershell -Command "(Get-Content ecs\service-definition-tmp.json) -replace '{{CLUSTER_NAME}}', '!CLUSTER_NAME!' | Set-Content ecs\service-definition-tmp.json"
powershell -Command "(Get-Content ecs\service-definition-tmp.json) -replace '{{SUBNET_1}}', '!SUBNET_1!' | Set-Content ecs\service-definition-tmp.json"
powershell -Command "(Get-Content ecs\service-definition-tmp.json) -replace '{{SUBNET_2}}', '!SUBNET_2!' | Set-Content ecs\service-definition-tmp.json"
powershell -Command "(Get-Content ecs\service-definition-tmp.json) -replace '{{SECURITY_GROUP}}', '!SECURITY_GROUP!' | Set-Content ecs\service-definition-tmp.json"

if "!LOAD_BALANCER_CONFIG!"=="true" (
    powershell -Command "(Get-Content ecs\service-definition-tmp.json) -replace '{{TARGET_GROUP_ARN}}', '!TARGET_GROUP_ARN!' | Set-Content ecs\service-definition-tmp.json"
) else (
    powershell -Command "$json = Get-Content ecs\service-definition-tmp.json | ConvertFrom-Json; $json.PSObject.Properties.Remove('loadBalancers'); $json | ConvertTo-Json -Depth 10 | Set-Content ecs\service-definition-tmp.json"
)

set SERVICE_NAME=crm-container-service
echo Checking if ECS service exists...

for /f "delims=" %%i in ('aws ecs describe-services --cluster !CLUSTER_NAME! --services !SERVICE_NAME! --region !AWS_REGION! --query "services[?status==`ACTIVE`].serviceName" --output text') do set EXISTING_SERVICE=%%i

if "!EXISTING_SERVICE!"=="" (
    echo Creating ECS Service...
    aws ecs create-service --cli-input-json file://ecs/service-definition-tmp.json --region !AWS_REGION!
) else (
    echo Updating existing ECS Service...
    aws ecs update-service --cluster !CLUSTER_NAME! --service !SERVICE_NAME! --task-definition !TASK_DEF_ARN! --force-new-deployment --region !AWS_REGION!
)

echo Waiting for service to become stable...
aws ecs wait services-stable --cluster !CLUSTER_NAME! --services !SERVICE_NAME! --region !AWS_REGION!

echo.
echo Deployment completed successfully!
echo.
echo Service Details:
aws ecs describe-services --cluster !CLUSTER_NAME! --services !SERVICE_NAME! --region !AWS_REGION! --query "services[0].{ServiceName:serviceName,Status:status,DesiredCount:desiredCount,RunningCount:runningCount}"

if "!LOAD_BALANCER_CONFIG!"=="true" (
    echo.
    echo Load Balancer DNS:
    echo http://!ALB_DNS!
)

echo.
echo CloudWatch Logs:
echo Log Group: /ecs/crm-container
echo Region: !AWS_REGION!

del ecs\task-definition-tmp.json ecs\service-definition-tmp.json 2>nul

echo.
echo Deployment complete!

endlocal
