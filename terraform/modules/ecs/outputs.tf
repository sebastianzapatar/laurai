output "cluster_name" {
  value = aws_ecs_cluster.main.name
}

output "service_name" {
  value = aws_ecs_service.app.name
}

output "alb_dns_name" {
  description = "DNS del Application Load Balancer"
  value       = aws_lb.main.dns_name
}

output "alb_arn" {
  value = aws_lb.main.arn
}

output "ecs_sg_id" {
  description = "Security Group ID de las tareas ECS (necesario para RDS)"
  value       = aws_security_group.ecs.id
}
