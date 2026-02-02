@echo off
setlocal enabledelayedexpansion

:: Function to display messages
call :print_message "AWS ECS Fargate Deployment Script for CRM Application"
call :print_message "------------------------------------------------------"

:: Check if AWS CLI is installed
where aws >nul 2>&1 || (
    call :print_error "AWS CLI is not installed. Please install it first."
    exit /b 1
)

:: Check for required files
if not exist ".\ecs\task-definition.json" (
    call :print_error "Task definition file not found at .\ecs\task-definition.json"
    exit /b 1
)

if not exist ".\ecs\service-definition.json" (
    call :print_error "Service definition file not found at .\ecs\service-definition.json"
    exit /b 1
)

:: Get AWS region
set /p AWS_REGION="Enter AWS region (e.g., us-east-1): "
if "!AWS_REGION!"=="" (
    call :print_error "AWS region cannot be empty"
    exit /b 1
)

:: Get ECS cluster name
set /p CLUSTER_NAME="Enter ECS cluster name (default: default): "
if "!CLUSTER_NAME!"=="" (
    set CLUSTER_NAME=default
    call :print_message "Using default cluster name: default"
)

:: Get AWS account ID
call :print_message "Retrieving AWS account ID..."
for /f "tokens=*" %%i in ('aws sts get-caller-identity --query Account --output text 2^>nul') do set ACCOUNT_ID=%%i

if "!ACCOUNT_ID!"=="" (
    set /p ACCOUNT_ID="Could not retrieve AWS account ID automatically. Please enter your AWS account ID: "
    if "!ACCOUNT_ID!"=="" (
        call :print_error "AWS account ID cannot be empty"
        exit /b 1
    )
) else (
    call :print_message "AWS account ID: !ACCOUNT_ID!"
)

:: Get environment name
set /p ENVIRONMENT="Enter environment name (default: production): "
if "!ENVIRONMENT!"=="" (
    set ENVIRONMENT=production
    call :print_message "Using default environment: production"
)

:: Get subnet information
set /p SUBNET_1="Enter subnet ID for first AZ: "
if "!SUBNET_1!"=="" (
    call :print_error "First subnet ID cannot be empty"
    exit /b 1
)

set /p SUBNET_2="Enter subnet ID for second AZ: "
if "!SUBNET_2!"=="" (
    call :print_error "Second subnet ID cannot be empty"
    exit /b 1
)

:: Get security group
set /p SECURITY_GROUP="Enter security group ID: "
if "!SECURITY_GROUP!"=="" (
    call :print_error "Security group ID cannot be empty"
    exit /b 1
)

:: Database configuration
set /p DATABASE_HOST="Enter database host (default: localhost): "
if "!DATABASE_HOST!"=="" (
    set DATABASE_HOST=localhost
    call :print_message "Using default database host: localhost"
)

set /p DATABASE_USER="Enter database username (default: root): "
if "!DATABASE_USER!"=="" (
    set DATABASE_USER=root
    call :print_message "Using default database username: root"
)

set /p DATABASE_PASSWORD="Enter database password (default: password): "
if "!DATABASE_PASSWORD!"=="" (
    set DATABASE_PASSWORD=password
    call :print_message "Using default database password: password"
    call :print_warning "Using default database password in production is not recommended!"
)

:: Get Docker image URI
if exist ".\image-uri.txt" (
    for /f "tokens=*" %%i in (.\image-uri.txt) do set DEFAULT_IMAGE_URI=%%i
    set /p IMAGE_URI="Enter Docker image URI (default: !DEFAULT_IMAGE_URI!): "
    if "!IMAGE_URI!"=="" (
        set IMAGE_URI=!DEFAULT_IMAGE_URI!
        call :print_message "Using image URI from image-uri.txt: !IMAGE_URI!"
    )
) else (
    set /p IMAGE_URI="Enter Docker image URI (e.g., 123456789012.dkr.ecr.region.amazonaws.com/crm:latest): "
    if "!IMAGE_URI!"=="" (
        call :print_error "Docker image URI cannot be empty"
        exit /b 1
    )
)

:: Ask about load balancer
set /p USE_LOAD_BALANCER="Do you want to use a load balancer? (y/n): "

if /i "!USE_LOAD_BALANCER!"=="y" (
    call :print_message "Setting up with load balancer..."

    :: Check if we need to create new target group
    set /p CREATE_TARGET_GROUP="Do you want to create a new target group? (y/n, default: y): "

    if /i "!CREATE_TARGET_GROUP!"=="n" (
        set /p TARGET_GROUP_ARN="Enter existing target group ARN: "
        if "!TARGET_GROUP_ARN!"=="" (
            call :print_error "Target group ARN cannot be empty"
            exit /b 1
        )
    ) else (
        :: Create target group
        call :print_message "Creating target group..."

        :: Get VPC ID for target group
        set /p VPC_ID="Enter VPC ID for target group: "
        if "!VPC_ID!"=="" (
            call :print_error "VPC ID cannot be empty"
            exit /b 1
        )

        :: Use PowerShell to get the date/time format for unique name
        for /f "tokens=*" %%i in ('powershell -command "Get-Date -Format 'yyyyMMddHHmmss'"') do set DATETIME=%%i
        set TARGET_GROUP_NAME=crm-tg-!DATETIME!

        call :print_message "Creating target group: !TARGET_GROUP_NAME!"

        :: Create target group and extract ARN using PowerShell for better JSON handling
        powershell -command "$tgResult = aws elbv2 create-target-group --name '!TARGET_GROUP_NAME!' --protocol HTTP --port 8080 --vpc-id '!VPC_ID!' --target-type ip --health-check-path '/appinfo/health' --health-check-interval-seconds 30 --health-check-timeout-seconds 5 --healthy-threshold-count 2 --unhealthy-threshold-count 3 --region '!AWS_REGION!' | ConvertFrom-Json; $tgResult.TargetGroups[0].TargetGroupArn" >target-group-arn.tmp
        set /p TARGET_GROUP_ARN=<target-group-arn.tmp
        del target-group-arn.tmp

        call :print_message "Created target group: !TARGET_GROUP_ARN!"

        :: Create or use existing load balancer
        set /p CREATE_LOAD_BALANCER="Do you want to create a new load balancer? (y/n, default: n): "

        if /i "!CREATE_LOAD_BALANCER!"=="y" (
            set LB_NAME=crm-alb-!DATETIME!
            call :print_message "Creating load balancer: !LB_NAME!"

            :: Create load balancer and extract ARN using PowerShell
            powershell -command "$lbResult = aws elbv2 create-load-balancer --name '!LB_NAME!' --subnets '!SUBNET_1!' '!SUBNET_2!' --security-groups '!SECURITY_GROUP!' --scheme internet-facing --type application --region '!AWS_REGION!' | ConvertFrom-Json; $lbResult.LoadBalancers[0].LoadBalancerArn" >lb-arn.tmp
            set /p LOAD_BALANCER_ARN=<lb-arn.tmp
            del lb-arn.tmp

            call :print_message "Created load balancer: !LOAD_BALANCER_ARN!"

            :: Create listener
            call :print_message "Creating listener for load balancer..."
            aws elbv2 create-listener --load-balancer-arn "!LOAD_BALANCER_ARN!" --protocol HTTP --port 80 --default-actions Type=forward,TargetGroupArn=!TARGET_GROUP_ARN! --region "!AWS_REGION!"
            if !ERRORLEVEL! neq 0 (
                call :print_error "Failed to create listener"
                exit /b 1
            )
            call :print_message "Created listener for load balancer"

            :: Get load balancer DNS name
            powershell -command "$lbDns = aws elbv2 describe-load-balancers --load-balancer-arns '!LOAD_BALANCER_ARN!' --region '!AWS_REGION!' --query 'LoadBalancers[0].DNSName' --output text; Write-Output $lbDns" >lb-dns.tmp
            set /p LB_DNS=<lb-dns.tmp
            del lb-dns.tmp

            call :print_message "Load balancer DNS: !LB_DNS!"
            echo Load balancer DNS: !LB_DNS! > lb-dns.txt
        ) else (
            set /p LOAD_BALANCER_ARN="Enter existing load balancer ARN: "
            if "!LOAD_BALANCER_ARN!"=="" (
                call :print_error "Load balancer ARN cannot be empty"
                exit /b 1
            )

            :: Create listener
            call :print_message "Creating listener for existing load balancer..."
            aws elbv2 create-listener --load-balancer-arn "!LOAD_BALANCER_ARN!" --protocol HTTP --port 80 --default-actions Type=forward,TargetGroupArn=!TARGET_GROUP_ARN! --region "!AWS_REGION!"
            if !ERRORLEVEL! neq 0 (
                call :print_error "Failed to create listener"
                exit /b 1
            )
            call :print_message "Created listener for existing load balancer"
        )
    )
) else (
    call :print_message "Not using load balancer, updating service definition..."
    :: Remove the loadBalancers section from the service definition using PowerShell
    powershell -command "$serviceJson = Get-Content -Raw .\ecs\service-definition.json | ConvertFrom-Json; $serviceJson.PSObject.Properties.Remove('loadBalancers'); $serviceJson.PSObject.Properties.Remove('healthCheckGracePeriodSeconds'); $serviceJson | ConvertTo-Json -Depth 10 | Set-Content -Path .\ecs\service-definition.json"
    set TARGET_GROUP_ARN=""
)

:: Create or use existing ECS cluster
call :print_message "Checking if ECS cluster exists: !CLUSTER_NAME!"
powershell -command "$clusterCheck = aws ecs describe-clusters --clusters '!CLUSTER_NAME!' --region '!AWS_REGION!' | ConvertFrom-Json; $clusterCheck.clusters.Count" >cluster-check.tmp
set /p CLUSTER_CHECK=<cluster-check.tmp
del cluster-check.tmp

if "!CLUSTER_CHECK!"=="0" (
    call :print_message "Creating ECS cluster: !CLUSTER_NAME!"
    aws ecs create-cluster --cluster-name "!CLUSTER_NAME!" --region "!AWS_REGION!"
    if !ERRORLEVEL! neq 0 (
        call :print_error "Failed to create ECS cluster"
        exit /b 1
    )
) else (
    call :print_message "Using existing ECS cluster: !CLUSTER_NAME!"
)

:: Create CloudWatch log group if it doesn't exist
call :print_message "Checking if CloudWatch log group exists: /ecs/crm"
powershell -command "$logGroupCheck = aws logs describe-log-groups --log-group-name-prefix '/ecs/crm' --region '!AWS_REGION!' | ConvertFrom-Json; $logGroupCheck.logGroups.Count" >log-group-check.tmp
set /p LOG_GROUP_CHECK=<log-group-check.tmp
del log-group-check.tmp

if "!LOG_GROUP_CHECK!"=="0" (
    call :print_message "Creating CloudWatch log group: /ecs/crm"
    aws logs create-log-group --log-group-name "/ecs/crm" --region "!AWS_REGION!"
    if !ERRORLEVEL! neq 0 (
        call :print_error "Failed to create CloudWatch log group"
        exit /b 1
    )
) else (
    call :print_message "Using existing CloudWatch log group: /ecs/crm"
)

:: Replace placeholders in task definition using PowerShell
call :print_message "Updating task definition file with configuration..."
powershell -command "(Get-Content -Path .\ecs\task-definition.json) -replace '{{ACCOUNT_ID}}', '!ACCOUNT_ID!' -replace '{{AWS_REGION}}', '!AWS_REGION!' -replace '{{IMAGE_URI}}', '!IMAGE_URI!' -replace '{{DATABASE_HOST}}', '!DATABASE_HOST!' -replace '{{DATABASE_USER}}', '!DATABASE_USER!' -replace '{{DATABASE_PASSWORD}}', '!DATABASE_PASSWORD!' -replace '{{ENVIRONMENT}}', '!ENVIRONMENT!' | Set-Content -Path .\ecs\task-definition.json"

:: Replace placeholders in service definition using PowerShell
call :print_message "Updating service definition file with configuration..."
powershell -command "(Get-Content -Path .\ecs\service-definition.json) -replace '{{CLUSTER_NAME}}', '!CLUSTER_NAME!' -replace '{{SUBNET_1}}', '!SUBNET_1!' -replace '{{SUBNET_2}}', '!SUBNET_2!' -replace '{{SECURITY_GROUP}}', '!SECURITY_GROUP!' -replace '{{ENVIRONMENT}}', '!ENVIRONMENT!' | Set-Content -Path .\ecs\service-definition.json"

:: Replace target group ARN if using load balancer
if /i "!USE_LOAD_BALANCER!"=="y" (
    call :print_message "Updating target group ARN in service definition..."
    powershell -command "(Get-Content -Path .\ecs\service-definition.json) -replace '{{TARGET_GROUP_ARN}}', '!TARGET_GROUP_ARN!' | Set-Content -Path .\ecs\service-definition.json"
)

:: Register task definition
call :print_message "Registering ECS task definition..."
powershell -command "$taskDefResult = aws ecs register-task-definition --cli-input-json file://ecs/task-definition.json --region '!AWS_REGION!' | ConvertFrom-Json; $taskDefResult.taskDefinition.taskDefinitionArn" >task-def-arn.tmp
set /p TASK_DEFINITION_ARN=<task-def-arn.tmp
del task-def-arn.tmp

call :print_message "Registered task definition: !TASK_DEFINITION_ARN!"

:: Check if service exists
call :print_message "Checking if service exists: crm-service"
powershell -command "try { $serviceCheck = aws ecs describe-services --cluster '!CLUSTER_NAME!' --services 'crm-service' --region '!AWS_REGION!' | ConvertFrom-Json; if($serviceCheck.services.Count -gt 0) { $serviceCheck.services[0].serviceName } else { 'not-found' } } catch { 'not-found' }" >service-check.tmp
set /p SERVICE_CHECK=<service-check.tmp
del service-check.tmp

if "!SERVICE_CHECK!"=="crm-service" (
    call :print_message "Updating existing ECS service: crm-service"

    :: Update existing service
    aws ecs update-service --cluster "!CLUSTER_NAME!" --service "crm-service" --task-definition "!TASK_DEFINITION_ARN!" --region "!AWS_REGION!"
    if !ERRORLEVEL! neq 0 (
        call :print_error "Failed to update ECS service"
        exit /b 1
    )
) else (
    call :print_message "Creating new ECS service: crm-service"

    :: Create new service
    aws ecs create-service --cli-input-json file://ecs/service-definition.json --region "!AWS_REGION!"
    if !ERRORLEVEL! neq 0 (
        call :print_error "Failed to create ECS service"
        exit /b 1
    )
)

:: Wait for service to stabilize
call :print_message "Waiting for service to stabilize..."
aws ecs wait services-stable --cluster "!CLUSTER_NAME!" --services "crm-service" --region "!AWS_REGION!"
if !ERRORLEVEL! neq 0 (
    call :print_warning "Service stabilization wait timed out - check ECS console for details"
)

:: Print service details
call :print_message "Deployment completed successfully!"
powershell -command "$serviceDetails = aws ecs describe-services --cluster '!CLUSTER_NAME!' --services 'crm-service' --region '!AWS_REGION!' | ConvertFrom-Json; Write-Output $serviceDetails.services[0].runningCount,$serviceDetails.services[0].desiredCount" >service-details.tmp
for /f "tokens=1,2" %%a in (service-details.tmp) do (
    set RUNNING_COUNT=%%a
    set DESIRED_COUNT=%%b
)
del service-details.tmp

call :print_success "CRM Application deployed successfully to ECS Fargate!"
echo   Cluster:       !CLUSTER_NAME!
echo   Service:       crm-service
echo   Running tasks: !RUNNING_COUNT! / !DESIRED_COUNT!
echo   Task definition: !TASK_DEFINITION_ARN!
echo   CloudWatch logs: /ecs/crm

:: Print load balancer info if applicable
if /i "!USE_LOAD_BALANCER!"=="y" if exist "lb-dns.txt" (
    for /f "tokens=3" %%i in (lb-dns.txt) do set LB_DNS=%%i
    echo   Application URL: http://!LB_DNS!
    call :print_message "The application may take a few minutes to become available at the URL."
)

call :print_message "For detailed logs, check CloudWatch logs at: /ecs/crm"
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

:print_warning
echo [33m^>^> WARNING: %~1[0m
exit /b 0