# [001]: [Nomenclatura das consultas]

* **Status**: Aceito
* **Data**: 08/04/2026

## 1. Contexto e Problema

Para evitar diferenças nas nomenclaturas de consultas e seguirmos um padrão único de nomenclatura. Definimos um padrão
na nomeação das consultas que serão criadas neste projeto.

## 2. Decisão

O acordo ficou da seguinte maneira:

- getDOMINIObyId para consultas de getById
- getAllDOMINIO para consultas de listagem

### Exemplo

Um exemplo pode ser encontrado no package `vehicle`:

- A consulta de listagem de vehicles: GetAllVehicleService
- A consulta de vehicle por id: GetVehicleByIdService