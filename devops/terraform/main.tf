# ---------------------------------------------------------------------------
# Security Group do RDS
# Só os worker nodes do EKS (SG publicado no SSM) e SGs/CIDRs extras acessam
# a porta 5432. O banco nunca é público.
# ---------------------------------------------------------------------------
resource "aws_security_group" "rds" {
  name        = "${local.rds_identifier}-rds"
  description = "Acesso ao PostgreSQL do servico ${var.service_name} (${var.environment})."
  vpc_id      = data.aws_ssm_parameter.vpc_id.value

  dynamic "ingress" {
    for_each = local.allowed_security_group_ids
    content {
      description     = "PostgreSQL a partir do SG ${ingress.value}"
      from_port       = 5432
      to_port         = 5432
      protocol        = "tcp"
      security_groups = [ingress.value]
    }
  }

  dynamic "ingress" {
    for_each = var.allowed_cidr_blocks
    content {
      description = "PostgreSQL a partir de ${ingress.value}"
      from_port   = 5432
      to_port     = 5432
      protocol    = "tcp"
      cidr_blocks = [ingress.value]
    }
  }

  egress {
    description = "Saida liberada (o RDS inicia conexoes apenas para servicos gerenciados)."
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${local.rds_identifier}-rds"
  }
}

# ---------------------------------------------------------------------------
# Subnet group do RDS
# Exige pelo menos 2 subnets privadas em AZs diferentes.
# ---------------------------------------------------------------------------
resource "aws_db_subnet_group" "main" {
  name        = "${local.rds_identifier}-subnet-group"
  description = "Subnets privadas do RDS ${local.rds_identifier}."
  subnet_ids  = local.private_subnet_ids

  tags = {
    Name = "${local.rds_identifier}-subnet-group"
  }

  lifecycle {
    precondition {
      condition     = length(local.private_subnet_ids) >= 2
      error_message = "O RDS exige pelo menos 2 subnets privadas em AZs diferentes. Verifique a descoberta pela tag Name ou informe private_subnet_ids."
    }
  }
}

# ---------------------------------------------------------------------------
# Enhanced Monitoring do RDS
# Métricas granulares (CPU, memória, disco, rede) no CloudWatch.
# ---------------------------------------------------------------------------
data "aws_iam_policy_document" "rds_monitoring_assume_role" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["monitoring.rds.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "rds_monitoring" {
  name               = "${local.rds_identifier}-rds-monitoring"
  assume_role_policy = data.aws_iam_policy_document.rds_monitoring_assume_role.json
}

resource "aws_iam_role_policy_attachment" "rds_monitoring" {
  role       = aws_iam_role.rds_monitoring.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonRDSEnhancedMonitoringRole"
}

# ---------------------------------------------------------------------------
# RDS PostgreSQL
# ---------------------------------------------------------------------------
resource "aws_db_instance" "main" {
  identifier = local.rds_identifier

  engine         = "postgres"
  engine_version = var.postgres_version
  instance_class = var.db_instance_class

  allocated_storage     = var.db_allocated_storage
  max_allocated_storage = var.db_max_allocated_storage
  storage_type          = "gp3"
  storage_encrypted     = true

  db_name  = var.db_name
  username = var.db_username
  password = random_password.db_password.result
  port     = 5432

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]
  publicly_accessible    = false

  multi_az                = var.multi_az
  backup_retention_period = var.backup_retention_period
  backup_window           = "03:00-04:00"
  maintenance_window      = "sun:05:00-sun:06:00"

  copy_tags_to_snapshot = true

  deletion_protection       = var.deletion_protection
  skip_final_snapshot       = var.skip_final_snapshot
  final_snapshot_identifier = var.skip_final_snapshot ? null : "${local.rds_identifier}-final"

  performance_insights_enabled          = true
  performance_insights_retention_period = 7
  monitoring_interval                   = 60
  monitoring_role_arn                   = aws_iam_role.rds_monitoring.arn

  auto_minor_version_upgrade = true
  apply_immediately          = false

  tags = {
    Name = local.rds_identifier
  }
}
