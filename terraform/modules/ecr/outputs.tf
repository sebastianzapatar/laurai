output "repository_url" {
  description = "URL del repositorio ECR"
  value       = aws_ecr_repository.app.repository_url
}

output "repository_name" {
  value = aws_ecr_repository.app.name
}
