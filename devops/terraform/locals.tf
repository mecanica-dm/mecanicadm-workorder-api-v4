# Parâmetros publicados pelo repositório mecanicadm-infra-v4.
data "aws_ssm_parameter" "vpc_id" {
  name = "/mecanicadm/${var.environment}/vpc_id"
}

data "aws_ssm_parameter" "eks_node_sg_id" {
  name = "/mecanicadm/${var.environment}/eks_node_sg_id"
}

data "aws_availability_zones" "available" {
  state = "available"
}

locals {
  name_prefix    = "mecanicadm-${var.environment}"
  rds_identifier = "mecanicadm-${var.environment}-${var.service_name}"
  ssm_prefix     = "/mecanicadm/${var.environment}/${var.service_name}"

  # O infra-v4 cria 3 subnets privadas, uma por AZ, com Name:
  #   mecanicadm-<environment>-private-<az>
  # Como ele ainda não publica os IDs no SSM, descobrimos as duas primeiras AZs.
  # Para sobrescrever, informe private_subnet_ids em terraform.tfvars.
  selected_azs = slice(
    data.aws_availability_zones.available.names,
    0,
    min(2, length(data.aws_availability_zones.available.names))
  )

  common_tags = {
    Project     = "mecanicadm"
    Environment = var.environment
    Service     = var.service_name
    ManagedBy   = "terraform"
  }
}

data "aws_subnet" "private" {
  for_each = length(var.private_subnet_ids) == 0 ? toset(local.selected_azs) : toset([])

  vpc_id = data.aws_ssm_parameter.vpc_id.value

  filter {
    name   = "tag:Name"
    values = ["${local.name_prefix}-private-${each.key}"]
  }
}

locals {
  discovered_private_subnet_ids = [for subnet in values(data.aws_subnet.private) : subnet.id]
  private_subnet_ids            = length(var.private_subnet_ids) > 0 ? var.private_subnet_ids : local.discovered_private_subnet_ids

  allowed_security_group_ids = distinct(concat(
    [data.aws_ssm_parameter.eks_node_sg_id.value],
    var.additional_allowed_security_group_ids,
  ))
}
