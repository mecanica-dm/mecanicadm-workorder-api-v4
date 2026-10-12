# [008]: [Padrões de Testes de Integração]

* **Status**: Aceito
* **Data**: 24/05/2024

## 1. Contexto e Problema

Testes de integração podem se tornar lentos, difíceis de manter e instáveis se não houver um padrão claro de como lidar com o contexto do Spring, banco de dados e autenticação.

## 2. Decisão

Para testes de integração de API, seguiremos o seguinte padrão:

- **Framework**: Utilizar `@SpringBootTest` com `@AutoConfigureMockMvc`.
- **DSL de Asserção**: Utilizar `RestAssuredMockMvc` para uma escrita de testes fluida e legível.
- **Isolamento de Dados**: Utilizar `@Transactional` para garantir que as alterações no banco de dados sejam revertidas após cada teste.
- **Perfil de Teste**: Utilizar `@ActiveProfiles("test")` para usar configurações específicas de teste, com banco PostgreSQL provisionado via Testcontainers (`AbstractIntegrationTest`).
- **Massa de Dados**: Utilizar a anotação `@Sql` para carregar scripts SQL de preparação para cenários complexos.
- **Autenticação**: Utilizar classes utilitárias (ex: `AuthUtils`) para geração de tokens JWT durante os testes.

### Exemplo

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FeatureIT {
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @Sql(scripts = "/sql/data.sql")
    @DisplayName("Deve realizar operação com sucesso")
    void shouldPerformOperation() {
        RestAssuredMockMvc.given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/resource")
                .then()
                .statusCode(200);
    }
}
```

