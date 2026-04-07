###############################################################
# modules/ecr/main.tf - Repositorio ECR para imágenes Docker
###############################################################

resource "aws_ecr_repository" "app" {
  name                 = "${var.project_name}-api"
  image_tag_mutability = "MUTABLE"
  force_delete         = true

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

locals {
  ecr_registry = "${var.aws_account_id}.dkr.ecr.${var.aws_region}.amazonaws.com"
  image_url    = "${local.ecr_registry}/${aws_ecr_repository.app.name}"
}

# Nota: el build y push de la imagen Docker se realizan en GitHub Actions,
# no en Terraform. Ver .github/workflows/deploy.yml
