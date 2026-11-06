# [005]: [Estratégia de Soft Delete e Auditoria]

* **Status**: Substituído por [ADR 011](011-arquitetura-limpa-purista-novas-features.md)
* **Data**: 21/04/2026

## 1. Contexto e Problema

Para garantir a integridade dos dados históricos e facilitar a auditoria, é necessário um padrão para lidar com a exclusão de registros e o rastreamento de informações de criação e atualização. Sem um filtro global, registros "excluídos" continuariam aparecendo em consultas padrão.

## 2. Decisão

Adotaremos a estratégia de "Soft Delete" para todas as entidades principais, utilizando a classe `AuditEntity` como base e anotações do Hibernate para automação.

- Todas as entidades que necessitam de soft delete e campos de auditoria (data de criação e atualização) devem estender `AuditEntity`.
- A exclusão lógica será implementada através da anotação `@SQLDelete` do Hibernate, que atualizará o campo `deletedAt` em vez de remover fisicamente o registro.
- Para garantir que registros excluídos não sejam retornados em consultas (`select`), deve-se utilizar a anotação `@SQLRestriction("deleted_at IS NULL")` na classe da entidade.
- O campo `deletedAt` em `AuditEntity` será utilizado para indicar que um registro foi logicamente excluído.

### Exemplo

```java
@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class User extends AuditEntity {
    // ...
}

@MappedSuperclass
public abstract class AuditEntity {
    @Column(name = "date_created", updatable = false)
    @CreationTimestamp
    protected LocalDateTime dateCreated;

    @Column(name = "date_updated")
    @UpdateTimestamp
    protected LocalDateTime dateUpdated;

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;
}
```
