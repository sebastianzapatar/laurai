variable "project_name"       { type = string }
variable "environment"         { type = string }
variable "aws_region"          { type = string }
variable "vpc_id"              { type = string }
variable "public_subnet_ids"   { type = list(string) }
variable "private_subnet_ids"  { type = list(string) }
variable "ecr_repository_url"  { type = string }
variable "ssm_parameter_arns" {
  type      = list(string)
  sensitive = true
}
variable "container_port" {
  type    = number
  default = 6969
}
variable "task_cpu" {
  type    = number
  default = 512
}
variable "task_memory" {
  type    = number
  default = 1024
}
variable "desired_count" {
  type    = number
  default = 1
}
