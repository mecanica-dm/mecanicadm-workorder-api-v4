# [004]: [Padrão de Exceções Modulares]

* **Status**: Aceito
* **Data**: 21/04/2026

## 1. Contexto e Problema

Para manter as exceções organizadas e fáceis de localizar, evitando uma proliferação de arquivos de exceção individuais e garantindo que as mensagens de erro sejam centralizadas por módulo.

## 2. Decisão

Cada módulo deve ter uma classe centralizadora de exceções no pacote `exception`, utilizando classes estáticas internas para representar exceções específicas.

- A classe principal deve ser nomeada como `[Dominio]Exceptions`.
- As exceções específicas devem herdar de `RuntimeException` ou de uma exceção base do projeto.
- Cada exceção interna deve representar um erro específico de negócio ou infraestrutura.

### Exemplo

```java
public class UserExceptions {

    public static class UserNotFound extends RuntimeException {
        public UserNotFound() {
            super("Usuário não encontrado.");
        }
    }

    public static class PasswordMinLength extends RuntimeException {
        public PasswordMinLength() {
            super("A senha deve ter no mínimo 6 caracteres.");
        }
    }

    public static class EmailNotEmpty extends RuntimeException {
        public EmailNotEmpty() {
            super("O e-mail não pode ser vazio.");
        }
    }
}
```
