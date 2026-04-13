# Prueba Técnica Banking Constitution

## Core Principles

### I. Arquitectura Hexagonal
Todo el código de dominio debe ser independiente de frameworks y基础设施. La lógica de negocio reside en la capa application (use cases), separada de la infraestructura (adaptadores). Las dependencias fluyen hacia el interior: infrastructure → application → domain. El dominio puro no debe contener anotaciones de Spring ni dependencias de frameworks.

### II. Puertos y Adaptadores
Cada servicio expone funcionalidad a través de puertos (interfaces Java) y la implementa mediante adaptadores. Los puertos de entrada (port/in) definen los use cases; los puertos de salida (port/out) definen las operaciones de infraestructura. Esta separación permite cambiar implementaciones (DB, messaging, REST) sin modificar la lógica de negocio.

### III. Test-First (NON-NEGOTIABLE)
TDD obligatorio: Tests escritos antes de la implementación. Ciclo Red-Green-Refactor estrictamente aplicado. Tests unitarios para cada use case, tests de integración para adaptadores. Cobertura mínima del 80% en capa application. Zero tolerancia para código sin tests.

### IV. Contract-First
Los contratos API (DTOs, esquemas) se definen antes de las implementaciones. breaking changes requieren versionado explícito (MAJOR). Documentación OpenAPI obligatoria para todos los endpoints REST. Contratos versionados en headers, nunca en paths.

### V. Observabilidad
Logging estructurado obligatorio en formato JSON. Niveles: ERROR para excepciones, INFO para operaciones de negocio, DEBUG para desarrollo. Tracing correlation ID en todas las comunicaciones entre servicios. Métricas expuestas via /actuator/prometheus. Health checks en /actuator/health.

## Technology Stack

Java 21 con Gradle multi-project. Spring Boot 4.x para infraestructura. JUnit 5 + AssertJ para tests. Arquitectura de módulos: application (use cases), infrastructure (adaptadores), test (integración). PostgreSQL para persistencia, Kafka para eventos. Versionado semántico: MAJOR.MINOR.PATCH.

## Development Workflow

Branching: feature/{ticket-id}-{description}. Pull requests obligatorios con code review. CI valida compilación, tests, y análisis estático (SpotBugs). Deployment a staging tras merge a main. Rollback automático si tests de integración fallan.

## Governance

La constitución prevalece sobre otras prácticas. Enmiendas requieren aprobación y plan de migración. Todas las PRs deben verificar cumplimiento con estos principios. Complejidad debe justificarse documentalmente. Verificar adherencia en code review.

**Version**: 1.0.0 | **Ratified**: 2026-04-13 | **Last Amended**: 2026-04-13