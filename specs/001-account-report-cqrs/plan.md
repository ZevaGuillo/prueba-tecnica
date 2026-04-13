# Implementation Plan: Account Report CQRS

**Branch**: `001-account-report-cqrs` | **Date**: 2026-04-13 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-account-report-cqrs/spec.md`

## Summary

Microservicio de generación de reportes de estado de cuenta utilizando arquitectura Event-Driven + CQRS. El servicio consume eventos de MS-Clientes y MS-Cuentas via Kafka, construye un modelo de lectura denormalizado, y expone endpoint GET /reportes con filtros por clienteId y rango de fechas. Prioriza disponibilidad y rendimiento sobre consistencia inmediata.

## Technical Context

**Language/Version**: Java 21  
**Primary Dependencies**: Spring Boot 4.x, Kafka, JUnit 5, PostgreSQL  
**Storage**: PostgreSQL (read model), Kafka (event bus)  
**Testing**: JUnit 5 + AssertJ  
**Target Platform**: Linux server  
**Project Type**: microservicio (Spring Boot)  
**Performance Goals**: <500ms p95, 1000 req/s concurrentes  
**Constraints**: Consistencia eventual <5s, modo degradado cuando otros servicios no disponibles  
**Scale/Scope**: Soporte para múltiples clientes simultáneos, alta carga de lectura

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Notes |
|-----------|--------|-------|
| I. Arquitectura Hexagonal | ✅ PASS | Use cases en capa application, adaptadores en infraestructura |
| II. Puertos y Adaptadores | ✅ PASS | Puertos de entrada (consultar reporte), puertos de salida (Kafka consumer, PostgreSQL) |
| III. Test-First | ✅ PASS | Tests unitarios para use cases, tests de integración para adaptadores |
| IV. Contract-First | ✅ PASS | DTOs definidos para API de consulta |
| V. Observabilidad | ✅ PASS | Logging JSON, correlation ID, metrics via actuator |

## Project Structure

### Documentation (this feature)

```text
specs/001-account-report-cqrs/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
└── tasks.md             # Phase 2 output (/speckit.tasks)
```

### Source Code (repository root)

```text
services/ms-reportes/
├── application/          # Use cases (puertos de entrada)
│   ├── src/main/java/.../usecase/
│   └── src/test/java/.../usecase/
├── infrastructure/       # Adaptadores (Kafka consumer, REST, PostgreSQL)
│   └── src/main/java/.../adapter/
├── domain/              # Entidades del dominio (opcional para este caso)
│   └── src/main/java/.../model/
└── test/integration/    # Tests de integración
```

**Structure Decision**: Nuevo módulo Gradle `ms-reportes` siguiendo la estructura existente de ms-accounts y ms-clients. Arquitectura hexagonal con clear separation: application (use cases) → infrastructure (adapters).

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Duplicación de datos en read model | CQRS requiere modelo de lectura separado para rendimiento | Consultar MS-Clientes y MS-Cuentas síncronamente violaría restricción arquitectónica |
| Consistencia eventual | Availability over consistency en sistemas distribuidos | Sincronía crea acoplamiento y punto único de fallo |

---

## Phase 0: Research ✅ COMPLETE

Research completado en `research.md`:
- Kafka consumer: Spring Kafka @KafkaListener
- Outbox Pattern: PostgreSQL polling
- Paginación: OFFSET/LIMIT
- Idempotencia: eventId + processed table

---

## Phase 1: Design ✅ COMPLETE

- `data-model.md`: Entidades definidas
- `contracts/api.md`: Contratos API y eventos
- `quickstart.md`: Guía de inicio rápido

---

## Next Step

Ejecutar `/speckit.tasks` para descomposición en tareas de implementación.