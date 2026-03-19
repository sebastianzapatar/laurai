###############################################################
# modules/ecs/main.tf - Cluster, Service, Task Definition, ALB
###############################################################

# ══════════════════════════════════════════════════════════════
# Security Groups
# ══════════════════════════════════════════════════════════════

resource "aws_security_group" "alb" {
  name        = "${var.project_name}-alb-sg"
  description = "Public traffic to ALB"
  vpc_id      = var.vpc_id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "HTTP"
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "HTTPS"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project_name}-alb-sg" }
}

resource "aws_security_group" "ecs" {
  name        = "${var.project_name}-ecs-sg"
  description = "Traffic to ECS tasks (only from ALB)"
  vpc_id      = var.vpc_id

  ingress {
    from_port       = var.container_port
    to_port         = var.container_port
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
    description     = "App desde ALB"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project_name}-ecs-sg" }
}

# ══════════════════════════════════════════════════════════════
# IAM Roles
# ══════════════════════════════════════════════════════════════

# Rol de ejecución: permite a ECS descargar la imagen y leer SSM
resource "aws_iam_role" "ecs_execution" {
  name = "${var.project_name}-ecs-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = { Service = "ecs-tasks.amazonaws.com" }
      Action    = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_execution_basic" {
  role       = aws_iam_role.ecs_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# Permiso adicional para leer parámetros de SSM Parameter Store
resource "aws_iam_role_policy" "ecs_ssm_read" {
  name = "${var.project_name}-ecs-ssm-read"
  role = aws_iam_role.ecs_execution.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect   = "Allow"
      Action   = ["ssm:GetParameters", "ssm:GetParameter", "kms:Decrypt"]
      Resource = var.ssm_parameter_arns
    }]
  })
}

# Rol de tarea: permisos que tiene la app en runtime (ej: S3, SQS, etc.)
resource "aws_iam_role" "ecs_task" {
  name = "${var.project_name}-ecs-task-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = { Service = "ecs-tasks.amazonaws.com" }
      Action    = "sts:AssumeRole"
    }]
  })
}

# ══════════════════════════════════════════════════════════════
# CloudWatch Log Group
# ══════════════════════════════════════════════════════════════

resource "aws_cloudwatch_log_group" "app" {
  name              = "/ecs/${var.project_name}"
  retention_in_days = 30
}

# ══════════════════════════════════════════════════════════════
# ECS Cluster
# ══════════════════════════════════════════════════════════════

resource "aws_ecs_cluster" "main" {
  name = "${var.project_name}-cluster"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }
}

resource "aws_ecs_cluster_capacity_providers" "main" {
  cluster_name       = aws_ecs_cluster.main.name
  capacity_providers = ["FARGATE", "FARGATE_SPOT"]

  default_capacity_provider_strategy {
    capacity_provider = "FARGATE"
    weight            = 1
    base              = 1
  }
}

# ══════════════════════════════════════════════════════════════
# Task Definition
# ══════════════════════════════════════════════════════════════

resource "aws_ecs_task_definition" "app" {
  family                   = "${var.project_name}-api-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = var.task_cpu
  memory                   = var.task_memory
  execution_role_arn       = aws_iam_role.ecs_execution.arn
  task_role_arn            = aws_iam_role.ecs_task.arn

  container_definitions = jsonencode([
    {
      name      = "api-container"
      image     = "${var.ecr_repository_url}:latest"
      essential = true

      portMappings = [{
        containerPort = var.container_port
        protocol      = "tcp"
      }]

      # Las variables de entorno se inyectan desde SSM en runtime
      secrets = [
        {
          name      = "SPRING_DATASOURCE_URL"
          valueFrom = "/${var.project_name}/${var.environment}/SPRING_DATASOURCE_URL"
        },
        {
          name      = "SPRING_DATASOURCE_USERNAME"
          valueFrom = "/${var.project_name}/${var.environment}/SPRING_DATASOURCE_USERNAME"
        },
        {
          name      = "SPRING_DATASOURCE_PASSWORD"
          valueFrom = "/${var.project_name}/${var.environment}/SPRING_DATASOURCE_PASSWORD"
        },
        {
          name      = "KEYCLOAK_ISSUER_URI"
          valueFrom = "/${var.project_name}/${var.environment}/KEYCLOAK_ISSUER_URI"
        }
      ]

      environment = [
        { name = "SPRING_PROFILES_ACTIVE", value = var.environment }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.app.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "ecs"
        }
      }

      healthCheck = {
        command     = ["CMD-SHELL", "curl -f http://localhost:${var.container_port}/actuator/health || exit 1"]
        interval    = 30
        timeout     = 5
        retries     = 3
        startPeriod = 60
      }
    }
  ])

  lifecycle {
    # El CI/CD actualiza la imagen; Terraform no debe revertir esos cambios
    ignore_changes = [container_definitions]
  }
}

# ══════════════════════════════════════════════════════════════
# Application Load Balancer
# ══════════════════════════════════════════════════════════════

resource "aws_lb" "main" {
  name               = "${var.project_name}-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = var.public_subnet_ids

  enable_deletion_protection = false

  tags = { Name = "${var.project_name}-alb" }
}

resource "aws_lb_target_group" "app" {
  name        = "${var.project_name}-tg"
  port        = var.container_port
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"  # Obligatorio para Fargate

  health_check {
    path                = "/actuator/health"
    healthy_threshold   = 2
    unhealthy_threshold = 5
    timeout             = 5
    interval            = 30
    matcher             = "200"
  }

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.main.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.app.arn
  }
}

# ══════════════════════════════════════════════════════════════
# ECS Service
# ══════════════════════════════════════════════════════════════

resource "aws_ecs_service" "app" {
  name            = "${var.project_name}-api-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.app.arn
  desired_count   = var.desired_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [aws_security_group.ecs.id]
    assign_public_ip = false  # Usa NAT Gateway para salir a internet
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.app.arn
    container_name   = "api-container"
    container_port   = var.container_port
  }

  deployment_minimum_healthy_percent = 100
  deployment_maximum_percent         = 200

  deployment_circuit_breaker {
    enable   = true
    rollback = true   # Rollback automático si el deploy falla
  }

  depends_on = [aws_lb_listener.http]

  lifecycle {
    # Permite que el CI/CD actualice la task definition sin que Terraform lo revierta
    ignore_changes = [task_definition, desired_count]
  }
}
