output "parameter_arns" {
  description = "ARNs de los parámetros SSM (necesarios para permisos IAM de ECS)"
  value = [
    aws_ssm_parameter.db_url.arn,
    aws_ssm_parameter.db_username.arn,
    aws_ssm_parameter.db_password.arn,
    aws_ssm_parameter.keycloak_uri.arn,
  ]
  sensitive = true
}

output "parameter_names" {
  description = "Nombres de los parámetros SSM"
  value = [
    aws_ssm_parameter.db_url.name,
    aws_ssm_parameter.db_username.name,
    aws_ssm_parameter.db_password.name,
    aws_ssm_parameter.keycloak_uri.name,
  ]
}
