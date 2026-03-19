###############################################################
# modules/ssm/main.tf - Parameter Store para secrets seguros
###############################################################

locals {
  prefix = "/${var.project_name}/${var.environment}"
}

resource "aws_ssm_parameter" "db_url" {
  name      = "${local.prefix}/SPRING_DATASOURCE_URL"
  type      = "SecureString"
  value     = var.db_url
  overwrite = true
  tags      = { Name = "SPRING_DATASOURCE_URL" }
}

resource "aws_ssm_parameter" "db_username" {
  name      = "${local.prefix}/SPRING_DATASOURCE_USERNAME"
  type      = "SecureString"
  value     = var.db_username
  overwrite = true
  tags      = { Name = "SPRING_DATASOURCE_USERNAME" }
}

resource "aws_ssm_parameter" "db_password" {
  name      = "${local.prefix}/SPRING_DATASOURCE_PASSWORD"
  type      = "SecureString"
  value     = var.db_password
  overwrite = true
  tags      = { Name = "SPRING_DATASOURCE_PASSWORD" }
}

resource "aws_ssm_parameter" "keycloak_uri" {
  name      = "${local.prefix}/KEYCLOAK_ISSUER_URI"
  type      = "SecureString"
  value     = var.keycloak_issuer_uri
  overwrite = true
  tags      = { Name = "KEYCLOAK_ISSUER_URI" }
}
