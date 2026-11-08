# [010]: [Estratégia de Internacionalização (i18n) Multi-idioma]

* **Status**: Aceito
* **Data**: 27/04/2026

## 1. Contexto e Problema

Para atender a diferentes mercados, o sistema precisa suportar múltiplos idiomas (PT-BR, EN, ES). As mensagens de erro, validações e retornos da API não devem estar hardcoded em um único idioma, permitindo que o cliente da API receba a resposta no idioma preferencial.

## 2. Decisão

Adotamos a seguinte estratégia de i18n:

- **Resource Bundles**: Utilizamos arquivos `.properties` localizados em `src/main/resources/i18n/`.
- **Arquivos**:
    - `messages.properties`: Idioma padrão (Português Brasil).
    - `messages_en.properties`: Inglês.
    - `messages_es.properties`: Espanhol.
- **Resolução de Locale**: O Spring Boot deve identificar o idioma através do header `Accept-Language` da requisição HTTP.
- **Exceções de Domínio**: Integradas conforme a [ADR 006], utilizando a `messageKey` para buscar a tradução correspondente no bundle antes de retornar ao cliente.
- **Validações Bean Validation**: As mensagens de erro do Jakarta Bean Validation também devem ser mapeadas para chaves nos bundles.

## 3. Consequências

- Facilidade em adicionar novos idiomas sem alterar o código-fonte.
- Melhor experiência do usuário final ao receber erros em sua língua nativa.
- Necessidade de garantir que toda nova chave de tradução seja adicionada em todos os arquivos de tradução.
