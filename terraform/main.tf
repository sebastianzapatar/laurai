###############################################################
# main.tf - Orquestador principal de módulos
###############################################################

terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  # Backend remoto en S3 (recomendado para equipos)
  # Descomenta esto cuando hayas creado el bucket manualmente UNA SOLA VEZ:
  # backend "s3" {
  #   bucket         = "laura-terraform-state"
  #   key            = "lauracanceleatiempo/terraform.tfstate"
  #   region         = "us-east-2"
  #   dynamodb_table = "laura-terraform-locks"
  #   encrypt        = true
  # }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = var.project_name
      Environment = var.environment
      ManagedBy   = "Terraform"
    }
  }
}

###############################################################
# Módulos
###############################################################

# Obtiene el Account ID de AWS automáticamente
data "aws_caller_identity" "current" {}

module "ecr" {
  source          = "./modules/ecr"
  project_name    = var.project_name
  environment     = var.environment
  aws_region      = var.aws_region
  aws_account_id  = data.aws_caller_identity.current.account_id
}

module "network" {
  source       = "./modules/network"
  project_name = var.project_name
  environment  = var.environment
  aws_region   = var.aws_region
}

module "rds" {
  source      = "./modules/rds"
  project_name  = var.project_name
  environment   = var.environment
  vpc_id        = module.network.vpc_id
  subnet_ids    = module.network.private_subnet_ids
  ecs_sg_id     = module.ecs.ecs_sg_id
  db_name       = var.db_name
  db_username   = var.db_username
  db_password   = var.db_password
}

module "ssm" {
  source       = "./modules/ssm"
  project_name = var.project_name
  environment  = var.environment
  db_url              = "jdbc:postgresql://${module.rds.db_endpoint}/${var.db_name}"
  db_username         = var.db_username
  db_password         = var.db_password
  keycloak_issuer_uri = var.keycloak_issuer_uri
}

module "ecs" {
  source               = "./modules/ecs"
  project_name         = var.project_name
  environment          = var.environment
  aws_region           = var.aws_region
  vpc_id               = module.network.vpc_id
  public_subnet_ids    = module.network.public_subnet_ids
  private_subnet_ids   = module.network.private_subnet_ids
  ecr_repository_url   = module.ecr.repository_url
  ssm_parameter_arns   = module.ssm.parameter_arns
  container_port       = var.container_port
  task_cpu             = var.task_cpu
  task_memory          = var.task_memory
  desired_count        = var.desired_count
}
