# [006]: [Contrato de Exceções de Domínio e i18n]

* **Status**: Aceito
* **Data**: 21/04/2026

## 1. Contexto e Problema

O projeto evoluiu para um tratamento padronizado de erros de negócio usando `DomainExceptionCore`, com `messageKey` para internacionalização e `HttpStatus` por exceção. Sem documentar esse contrato, surgem inconsistências como uso de exceções genéricas (`IllegalArgumentException`/`RuntimeException`) para regras de negócio e ausência de chaves de mensagem nos bundles.

## 2. Decisão

Padronizamos o contrato de erros de domínio com as seguintes regras:

- Exceções de negócio devem estender `DomainExceptionCore`.
- Exceções devem ser centralizadas por módulo na classe `[Dominio]Exceptions` (classes internas estáticas).
- Cada exceção deve definir `messageKey` e `HttpStatus` no construtor.
- Toda `messageKey` deve existir em `messages.properties`, `messages_en.properties` e `messages_es.properties`.
- Regras de negócio no domínio/aplicação não devem lançar exceções genéricas quando existir equivalente de domínio.

## 3. Consequências

- Ganha-se consistência na API de erros e no `GlobalExceptionHandler`.
- Testes ficam mais robustos ao validar tipo da exceção, `messageKey` e `status`.
- A manutenção exige sincronizar novas exceções com os arquivos de i18n.

