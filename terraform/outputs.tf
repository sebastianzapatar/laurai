###############################################################
# outputs.tf - Salidas útiles tras el apply
###############################################################

output "ecr_repository_url" {
  description = "URL del repositorio ECR para hacer docker push"
  value       = module.ecr.repository_url
}

output "alb_dns_name" {
  description = "DNS del Load Balancer (apunta tu dominio aquí)"
  value       = module.ecs.alb_dns_name
}

output "rds_endpoint" {
  description = "Endpoint de la base de datos RDS PostgreSQL"
  value       = module.rds.db_endpoint
  sensitive   = true
}

output "ecs_cluster_name" {
  description = "Nombre del cluster ECS"
  value       = module.ecs.cluster_name
}

output "ecs_service_name" {
  description = "Nombre del servicio ECS"
  value       = module.ecs.service_name
}
