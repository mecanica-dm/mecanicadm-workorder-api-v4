# ---------------------------------------------------------------------------
# Senhas geradas
# ---------------------------------------------------------------------------
resource "random_password" "db_password" {
  length           = 32
  special          = true
  override_special = "!#$%&*()-_=+[]{}?"
  min_upper        = 1
  min_lower        = 1
  min_numeric      = 1

  keepers = {
    environment = var.environment
    service     = var.service_name
  }
}

resource "random_password" "jwt_secret" {
  length  = 64
  special = false

  keepers = {
    environment = var.environment
  }
}

# ---------------------------------------------------------------------------
# Secrets Manager — credenciais do banco
# ---------------------------------------------------------------------------
resource "aws_secretsmanager_secret" "db" {
  name                    = "mecanicadm/${var.environment}/${var.service_name}/db-credentials"
  description             = "Credenciais do RDS PostgreSQL do servico ${var.service_name} (${var.environment})."
  recovery_window_in_days = var.secret_recovery_window_in_days

  tags = {
    Name = "${local.rds_identifier}-db-credentials"
  }
}

resource "aws_secretsmanager_secret_version" "db" {
  secret_id = aws_secretsmanager_secret.db.id

  secret_string = jsonencode({
    engine               = "postgres"
    host                 = aws_db_instance.main.address
    port                 = aws_db_instance.main.port
    dbname               = var.db_name
    username             = var.db_username
    password             = random_password.db_password.result
    dbInstanceIdentifier = aws_db_instance.main.identifier
    sslmode              = var.db_sslmode
  })
}

# ---------------------------------------------------------------------------
# Secrets Manager — JWT compartilhado entre os serviços da saga
#
# Sem isso o deploy usa o default "development" do application.yaml. O caminho
# é compartilhado (/mecanicadm/<env>/jwt-secret) para que finance-api e saga-api
# validem os mesmos tokens.
# ---------------------------------------------------------------------------
resource "aws_secretsmanager_secret" "jwt" {
  name                    = "mecanicadm/${var.environment}/jwt-secret"
  description             = "Segredo HMAC dos tokens JWT dos servicos mecanicadm (${var.environment})."
  recovery_window_in_days = var.secret_recovery_window_in_days

  tags = {
    Name = "mecanicadm-${var.environment}-jwt-secret"
  }
}

resource "aws_secretsmanager_secret_version" "jwt" {
  secret_id = aws_secretsmanager_secret.jwt.id

  secret_string = jsonencode({
    secret = random_password.jwt_secret.result
  })
}

# ---------------------------------------------------------------------------
# SSM — consumido pela pipeline para montar o Secret do Kubernetes.
#
# A aplicação espera exatamente: DB_HOST, DB_PORT, DB_NAME, DB_USER,
# DB_PASSWORD e JWT_SECRET.
# ---------------------------------------------------------------------------
resource "aws_ssm_parameter" "db_host" {
  name  = "${local.ssm_prefix}/db_host"
  type  = "SecureString"
  value = aws_db_instance.main.address

  tags = {
    Name = "${local.rds_identifier}-db-host"
  }
}

resource "aws_ssm_parameter" "db_port" {
  name  = "${local.ssm_prefix}/db_port"
  type  = "String"
  value = tostring(aws_db_instance.main.port)

  tags = {
    Name = "${local.rds_identifier}-db-port"
  }
}

resource "aws_ssm_parameter" "db_name" {
  name  = "${local.ssm_prefix}/db_name"
  type  = "String"
  value = var.db_name

  tags = {
    Name = "${local.rds_identifier}-db-name"
  }
}

resource "aws_ssm_parameter" "db_user" {
  name  = "${local.ssm_prefix}/db_user"
  type  = "SecureString"
  value = var.db_username

  tags = {
    Name = "${local.rds_identifier}-db-user"
  }
}

resource "aws_ssm_parameter" "db_password" {
  name  = "${local.ssm_prefix}/db_password"
  type  = "SecureString"
  value = random_password.db_password.result

  tags = {
    Name = "${local.rds_identifier}-db-password"
  }
}

resource "aws_ssm_parameter" "db_sslmode" {
  name  = "${local.ssm_prefix}/db_sslmode"
  type  = "String"
  value = var.db_sslmode

  tags = {
    Name = "${local.rds_identifier}-db-sslmode"
  }
}

resource "aws_ssm_parameter" "jwt_secret" {
  name  = "/mecanicadm/${var.environment}/jwt_secret"
  type  = "SecureString"
  value = random_password.jwt_secret.result

  tags = {
    Name = "mecanicadm-${var.environment}-jwt-secret"
  }
}
