variable "project_name" { type = string }
variable "environment"  { type = string }

variable "db_url" {
  type      = string
  sensitive = true
}
variable "db_username" {
  type      = string
  sensitive = true
}
variable "db_password" {
  type      = string
  sensitive = true
}
variable "keycloak_issuer_uri" {
  type    = string
  default = "http://localhost:8080/realms/chefs-realm"
}
