# [002]: [Padrão de UseCases e Commands]

* **Status**: Substituído por [ADR 011](011-arquitetura-limpa-purista-novas-features.md)
* **Data**: 21/04/2026

## 1. Contexto e Problema

Para garantir a consistência na camada de aplicação e facilitar a manutenção, precisamos de um padrão para a execução de operações de negócio.

## 2. Decisão

Adotamos o padrão de UseCases como interfaces e Commands/Queries como objetos de transferência de dados (DTOs) de entrada.

- Cada UseCase deve ser uma interface com um único método chamado `handle`.
- O método `handle` deve receber um Command (para operações de escrita) ou Query (para operações de leitura).
- A implementação do UseCase deve residir no pacote `service`.

### Exemplo

```java
public interface CreateUserUseCase {
    UUID handle(CreateUserCommand cmd);
}

@Service
public class CreateUserService implements CreateUserUseCase {
    @Override
    public UUID handle(CreateUserCommand cmd) {
        // implementação
    }
}
```
