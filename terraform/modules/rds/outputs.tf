output "db_endpoint" {
  description = "Endpoint de conexión a RDS (host:puerto)"
  value       = aws_db_instance.postgres.endpoint
  sensitive   = true
}

output "db_address" {
  description = "Hostname de RDS (sin puerto)"
  value       = aws_db_instance.postgres.address
  sensitive   = true
}
