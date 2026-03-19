###############################################################
# modules/ecr/main.tf - Repositorio ECR para imágenes Docker
###############################################################

resource "aws_ecr_repository" "app" {
  name                 = "${var.project_name}-api"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  lifecycle {
    prevent_destroy = false
  }
}

# Política para limpiar imágenes antiguas automáticamente (mantiene las últimas 10)
resource "aws_ecr_lifecycle_policy" "cleanup" {
  repository = aws_ecr_repository.app.name

  policy = jsonencode({
    rules = [{
      rulePriority = 1
      description  = "Mantener solo las últimas 10 imágenes"
      selection = {
        tagStatus   = "any"
        countType   = "imageCountMoreThan"
        countNumber = 10
      }
      action = { type = "expire" }
    }]
  })
}

###############################################################
# Build y Push automático de la imagen Docker
# Se ejecuta en la máquina local al hacer terraform apply
# Requisito: Docker Desktop debe estar corriendo
###############################################################

locals {
  ecr_registry = "${var.aws_account_id}.dkr.ecr.${var.aws_region}.amazonaws.com"
  image_url    = "${local.ecr_registry}/${aws_ecr_repository.app.name}"
  # Ruta a la raíz del proyecto (dos niveles arriba de terraform/)
  project_root = "${path.module}/../../.."
}

resource "null_resource" "docker_build_push" {
  # Se re-ejecuta solo si cambian el Dockerfile o el pom.xml
  triggers = {
    dockerfile_hash = filemd5("${local.project_root}/Dockerfile")
    pom_hash        = filemd5("${local.project_root}/pom.xml")
  }

  depends_on = [aws_ecr_repository.app]

  # 1. Login a ECR
  provisioner "local-exec" {
    command = "aws ecr get-login-password --region ${var.aws_region} | docker login --username AWS --password-stdin ${local.ecr_registry}"
  }

  # 2. Build de la imagen desde la raíz del proyecto
  provisioner "local-exec" {
    command     = "docker build -t ${local.image_url}:latest ."
    working_dir = local.project_root
  }

  # 3. Push a ECR
  provisioner "local-exec" {
    command = "docker push ${local.image_url}:latest"
  }
}
