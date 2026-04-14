# Documentación — Diagramas

Diagramas Mermaid C4 + ER + Sequence del sistema bancario de microservicios.

## Archivos

| Archivo | Nivel | Contenido |
|---|---|---|
| `c4-context.md` | C4 L1 — Context | Actores externos y sistema |
| `c4-container.md` | C4 L2 — Container | Microservicios, PostgreSQL, Kafka y sus relaciones |
| `c4-component-ms-clients.md` | C4 L3 — Component | Capas internas de `ms-clients` |
| `c4-component-ms-accounts.md` | C4 L3 — Component | Capas internas de `ms-accounts` |
| `c4-component-ms-reportes.md` | C4 L3 — Component | Capas internas de `ms-reportes` (CQRS read model) |
| `sequence-kafka-flow.md` | Sequence | Flujo E2E + flujos de error |
| `schema-database.md` | ER | Esquema completo de `banking_db` |

## Cómo visualizarlos

Los diagramas usan Mermaid y se renderizan directamente en GitHub, GitLab, o en cualquier editor compatible (ej: VS Code con la extensión Mermaid).

Para renderizar localmente también puedes usar:

```bash
npx @mermaid-js/mermaid-cli -i docs/c4-container.md -o docs/c4-container.png
```
