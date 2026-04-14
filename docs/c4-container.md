# C4 Level 2 — Container

Infraestructura y microservicios con sus tecnologías, puertos y patrones de comunicacion.

```mermaid
C4Container
  title Container Diagram - Microservicios Bancarios

  Person(usuario, "Usuario / API Client", "Consume los servicios REST via HTTP")

  Container_Boundary(infra, "Infraestructura") {
    ContainerDb(postgres, "PostgreSQL 15", "Relational DB :5432", "Base banking_db con esquemas msclients, msaccounts, reportes")
    Container(kafka, "Apache Kafka", "cp-kafka:7.5.0 :9092", "Bus de eventos asincronos entre microservicios")
  }

  Container_Boundary(apps, "Microservicios") {
    Container(msClients, "ms-clients", "Spring Boot - Java 21 - :8082", "Gestiona personas y clientes. Publica eventos via Outbox Pattern al topic cliente-events")
    Container(msAccounts, "ms-accounts", "Spring Boot - Java 21 - :8081", "Gestiona cuentas y movimientos. Valida clientes via Read Model local. Publica eventos via Outbox Pattern")
    Container(msReportes, "ms-reportes", "Spring Boot - Java 21 - :8080", "Read model CQRS. Consume eventos Kafka y expone GET /api/reportes")
  }

  Rel(usuario, msClients, "REST", "HTTP :8082")
  Rel(usuario, msAccounts, "REST", "HTTP :8081")
  Rel(usuario, msReportes, "REST", "HTTP :8080")

  Rel(msClients, postgres, "Lee y escribe personas/clientes/outbox", "JDBC - msclients_schema")
  Rel(msAccounts, postgres, "Lee y escribe cuentas/movimientos/outbox/clientes_cache", "JDBC - msaccounts_schema")
  Rel(msReportes, postgres, "Lee y escribe proyecciones", "JDBC - reportes_schema")

  Rel(msClients, kafka, "Publica via Outbox Relay", "cliente-events")
  Rel(msAccounts, kafka, "Publica via Outbox Relay", "cuenta-creada / cuenta-actualizada / movimiento-registrado")
  Rel(kafka, msReportes, "Consume eventos", "cliente-events / cuenta-creada / movimiento-registrado")
  Rel(kafka, msAccounts, "Consume para Read Model", "cliente-events (group: ms-accounts-group)")
```
