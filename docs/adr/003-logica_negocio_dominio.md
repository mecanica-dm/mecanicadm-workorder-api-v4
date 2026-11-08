# [003]: [Lógica de Negócio no Domínio (Rich Domain Model)]

* **Status**: Aceito
* **Data**: 21/04/2026

## 1. Contexto e Problema

Para garantir que as regras de negócio e validações essenciais estejam encapsuladas e próximas aos dados que manipulam, evitando a anêmica camada de domínio e a duplicação de lógica.

## 2. Decisão

A lógica de negócio e as validações devem residir dentro das entidades de domínio (Rich Domain Model) ou em Domain Services quando a lógica envolve múltiplas entidades ou coordenação complexa.

- Entidades de domínio devem conter métodos que representam comportamentos e regras de negócio relacionadas aos seus atributos.
- UseCases devem orquestrar as operações, chamando métodos das entidades de domínio ou Domain Services, mas não devem conter a lógica de negócio em si.
- Validações de estado da entidade devem ser realizadas dentro da própria entidade.

### Exemplo

```java
public class User extends AuditEntity {
    // ... atributos ...

    public static User create(String email, String rawPassword, String name, PasswordEncoder passwordEncoder) {
        validatePassword(rawPassword); // Validação de regra de negócio
        return new User(email, passwordEncoder.encode(rawPassword), name);
    }

    public void changePassword(String rawPassword, PasswordEncoder passwordEncoder) {
        validatePassword(rawPassword); // Validação de regra de negócio
        this.password = passwordEncoder.encode(rawPassword);
    }

    private static void validatePassword(String rawPassword) {
        if (!StringUtils.hasText(rawPassword) || rawPassword.length() < 6) {
            throw new UserExceptions.PasswordMinLength();
        }
    }

    private void validate() {
        if (!StringUtils.hasText(this.email)) {
            throw new UserExceptions.EmailNotEmpty();
        }
    }
}
```
