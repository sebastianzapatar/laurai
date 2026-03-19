###############################################################
# variables.tf - Variables globales del proyecto
###############################################################

variable "aws_region" {
  description = "Región de AWS donde se desplegará la infraestructura"
  type        = string
  default     = "us-east-2"
}

variable "project_name" {
  description = "Nombre del proyecto (se usa como prefijo en todos los recursos)"
  type        = string
  default     = "laura"
}

variable "environment" {
  description = "Entorno de despliegue (dev, staging, prod)"
  type        = string
  default     = "prod"
}

# ── Base de datos ─────────────────────────────────────────────

variable "db_name" {
  description = "Nombre de la base de datos PostgreSQL"
  type        = string
  default     = "chefs_db"
}

variable "db_username" {
  description = "Usuario de la base de datos"
  type        = string
  default     = "postgres"
  sensitive   = true
}

variable "db_password" {
  description = "Contraseña de la base de datos (usar terraform.tfvars o variable de entorno TF_VAR_db_password)"
  type        = string
  sensitive   = true
}

# ── Keycloak ──────────────────────────────────────────────────

variable "keycloak_issuer_uri" {
  description = "URI del issuer de Keycloak"
  type        = string
  default     = "http://localhost:8080/realms/chefs-realm"
}

# ── ECS / Contenedor ──────────────────────────────────────────

variable "container_port" {
  description = "Puerto expuesto por la aplicación Spring Boot"
  type        = number
  default     = 6969
}

variable "task_cpu" {
  description = "CPU asignada a la tarea ECS (unidades: 256=0.25vCPU, 512=0.5vCPU, 1024=1vCPU)"
  type        = number
  default     = 512
}

variable "task_memory" {
  description = "Memoria RAM asignada a la tarea ECS en MB"
  type        = number
  default     = 1024
}

variable "desired_count" {
  description = "Número de instancias del servicio ECS a mantener corriendo"
  type        = number
  default     = 1
}
