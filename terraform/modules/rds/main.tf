###############################################################
# modules/rds/main.tf - PostgreSQL RDS con acceso solo desde ECS
###############################################################

# Security Group para RDS (solo acepta conexiones desde ECS)
resource "aws_security_group" "rds" {
  name        = "${var.project_name}-rds-sg"
  description = "Acceso a RDS solo desde ECS"
  vpc_id      = var.vpc_id

  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [var.ecs_sg_id]
    description     = "PostgreSQL desde ECS"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project_name}-rds-sg" }
}

# Subnet group: las subnets donde puede vivir RDS
resource "aws_db_subnet_group" "main" {
  name       = "${var.project_name}-db-subnet-group"
  subnet_ids = var.subnet_ids

  tags = { Name = "${var.project_name}-db-subnet-group" }
}

# Instancia RDS PostgreSQL
resource "aws_db_instance" "postgres" {
  identifier        = "${var.project_name}-postgres"
  engine            = "postgres"
  engine_version    = "16.6"
  instance_class    = "db.t3.micro"
  allocated_storage = 20
  storage_encrypted = true

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]

  publicly_accessible = false   # Solo accesible desde dentro de la VPC
  multi_az            = false   # Cambiar a true en producción real
  skip_final_snapshot = true    # Cambiar a false en producción real

  backup_retention_period = 0   # Free tier no soporta backups automáticos (máximo = 0)
  backup_window           = "03:00-04:00"
  maintenance_window      = "Mon:04:00-Mon:05:00"

  tags = { Name = "${var.project_name}-postgres" }

  lifecycle {
    # Evita recrear la DB si solo cambia la contraseña (se gestiona por SSM)
    ignore_changes = [password]
  }
}
