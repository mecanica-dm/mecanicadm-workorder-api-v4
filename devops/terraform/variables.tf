variable "region" {
  description = "Região AWS onde o RDS será criado (deve ser a mesma do cluster EKS)."
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Ambiente. O projeto v4 suporta apenas produção."
  type        = string
  default     = "prod"

  validation {
    condition     = var.environment == "prod"
    error_message = "Apenas o ambiente de produção (prod) é suportado."
  }
}

variable "service_name" {
  description = "Nome do serviço usado nos nomes dos recursos e no prefixo dos parâmetros SSM."
  type        = string
  default     = "workorder"

  validation {
    condition     = can(regex("^[a-z0-9-]+$", var.service_name))
    error_message = "Use apenas letras minúsculas, números e hífens."
  }
}

variable "db_name" {
  description = "Nome do banco criado no RDS (precisa bater com o DB_NAME da aplicação)."
  type        = string
  default     = "workorder_db"

  validation {
    condition     = can(regex("^[a-zA-Z][a-zA-Z0-9_]+$", var.db_name))
    error_message = "O nome do banco deve começar com letra e conter apenas letras, números e underscore."
  }
}

variable "db_username" {
  description = "Usuário administrador do banco."
  type        = string
  default     = "workorder_admin"

  validation {
    condition     = can(regex("^[a-zA-Z][a-zA-Z0-9_]+$", var.db_username))
    error_message = "O usuário deve começar com letra e conter apenas letras, números e underscore."
  }
}

variable "postgres_version" {
  description = "Versão do PostgreSQL no RDS. Use apenas a major (ex.: 16) para o RDS aplicar o minor mais recente."
  type        = string
  default     = "16"
}

variable "db_instance_class" {
  description = "Classe da instância RDS."
  type        = string
  default     = "db.t4g.micro"
}

variable "db_allocated_storage" {
  description = "Armazenamento inicial em GB."
  type        = number
  default     = 20

  validation {
    condition     = var.db_allocated_storage >= 20
    error_message = "O RDS PostgreSQL exige pelo menos 20 GB de armazenamento."
  }
}

variable "db_max_allocated_storage" {
  description = "Limite do storage autoscaling em GB (0 desabilita o autoscaling)."
  type        = number
  default     = 50

  validation {
    condition     = var.db_max_allocated_storage == 0 || var.db_max_allocated_storage >= var.db_allocated_storage
    error_message = "db_max_allocated_storage deve ser 0 (desabilitado) ou maior/igual a db_allocated_storage."
  }
}

variable "multi_az" {
  description = "Habilita réplica síncrona em outra AZ (aumenta custo e disponibilidade)."
  type        = bool
  default     = false
}

variable "backup_retention_period" {
  description = "Dias de retenção dos backups automáticos (0 desabilita)."
  type        = number
  default     = 1

  validation {
    condition     = var.backup_retention_period >= 0 && var.backup_retention_period <= 35
    error_message = "backup_retention_period deve estar entre 0 e 35 dias."
  }
}

variable "deletion_protection" {
  description = "Protege o banco contra exclusão acidental. Ative em produção real."
  type        = bool
  default     = false
}

variable "skip_final_snapshot" {
  description = "Pula o snapshot final no destroy. Para produção real, use false."
  type        = bool
  default     = true
}

variable "secret_recovery_window_in_days" {
  description = "Janela de recuperação dos secrets no Secrets Manager (0 = exclusão imediata, útil para destroy/recreate)."
  type        = number
  default     = 0

  validation {
    condition     = var.secret_recovery_window_in_days == 0 || (var.secret_recovery_window_in_days >= 7 && var.secret_recovery_window_in_days <= 30)
    error_message = "Use 0 para exclusão imediata ou um valor entre 7 e 30 dias."
  }
}

variable "private_subnet_ids" {
  description = "Subnets privadas do RDS. Se vazio, são descobertas pela tag Name mecanicadm-<env>-private-<az>."
  type        = list(string)
  default     = []
}

variable "additional_allowed_security_group_ids" {
  description = "Security groups extras autorizados a acessar o PostgreSQL (além do SG dos nodes do EKS)."
  type        = list(string)
  default     = []
}

variable "allowed_cidr_blocks" {
  description = "CIDRs extras autorizados a acessar o PostgreSQL (ex.: IP de um bastion). Evite 0.0.0.0/0."
  type        = list(string)
  default     = []

  validation {
    condition     = alltrue([for cidr in var.allowed_cidr_blocks : can(cidrhost(cidr, 0))])
    error_message = "allowed_cidr_blocks deve conter apenas blocos CIDR válidos."
  }
}

variable "db_sslmode" {
  description = "sslmode recomendado para os clientes PostgreSQL (publicado no SSM)."
  type        = string
  default     = "prefer"

  validation {
    condition     = contains(["disable", "allow", "prefer", "require", "verify-ca", "verify-full"], var.db_sslmode)
    error_message = "db_sslmode inválido. Use disable, allow, prefer, require, verify-ca ou verify-full."
  }
}
