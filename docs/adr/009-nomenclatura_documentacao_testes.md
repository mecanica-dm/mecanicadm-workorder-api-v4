# [009]: [Nomenclatura e Documentação de Testes]

* **Status**: Aceito
* **Data**: 24/05/2024

## 1. Contexto e Problema

Testes com nomes genéricos dificultam a identificação de falhas e não servem como documentação viva do sistema.

## 2. Decisão

Adotaremos um padrão de nomenclatura descritivo e o uso de anotações de exibição.

- **Nome do Método**: Deve seguir o padrão `should[ExpectedBehavior]When[Scenario]` (ex: `shouldReturn401WhenNotAuthenticated`).
- **@DisplayName**: Deve ser utilizada em todos os métodos de teste, escrita em português, descrevendo de forma clara o que o teste está validando. Isso facilita a leitura de relatórios de CI/CD.

### Exemplo

```java
@Test
@DisplayName("Deve retornar 401 ao buscar usuário sem autenticação")
void shouldReturn401WhenNotAuthenticated() {
    // Given (Contexto implícito na rota)
    
    // When
    RestAssuredMockMvc.given()
            .when()
            .get("/user/{id}", UUID.randomUUID())
            
    // Then
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
}
```

