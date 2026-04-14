# C4 Level 3 — Component: ms-accounts

Capas internas de `ms-accounts` siguiendo arquitectura hexagonal, con Outbox Pattern y Read Model local.

```mermaid
C4Component
  title Component Diagram - ms-accounts (:8081)

  Person(usuario, "API Client", "Consume REST endpoints")
  ContainerDb(postgres, "PostgreSQL", "JDBC", "banking_db / msaccounts_schema")
  Container(kafka, "Kafka", "PLAINTEXT :9092", "Bus de eventos")

  Container_Boundary(infraLayer, "Infrastructure Layer") {
    Component(cuentaCtrl, "CuentaController", "Spring REST", "POST/GET/PATCH/DELETE /api/cuentas")
    Component(movCtrl, "MovimientoController", "Spring REST", "POST /api/movimientos con X-Transaction-Id")
    Component(exHandler, "GlobalExceptionHandler", "RestControllerAdvice", "404 cliente - 409 saldo/concurrencia/tx duplicada")
    Component(cuentaJpa, "CuentaJpaAdapter", "Spring Data JPA", "Persiste cuentas. Captura OptimisticLockingFailure")
    Component(movJpa, "MovimientoJpaAdapter", "Spring Data JPA", "Persiste movimientos. existsByTransactionId()")
    Component(outboxJpa, "OutboxJpaAdapter", "Spring Data JPA", "save() / findPending() / markAsPublished()")
    Component(cacheJpa, "ClienteCacheJpaAdapter", "Spring Data JPA", "upsert() / existsById() sobre clientes_cache")
    Component(relay, "OutboxEventRelayService", "Spring @Scheduled 1s", "Lee PENDING, publica a Kafka via KafkaTemplate String-String")
    Component(consumer, "ClienteEventConsumer", "Spring @KafkaListener", "Consume cliente-events. UPSERT en clientes_cache")
  }

  Container_Boundary(appLayer, "Application Layer") {
    Component(crearCuenta, "CrearCuentaUseCaseImpl", "@Transactional", "Valida cliente en cache - Crea cuenta - Escribe outbox event")
    Component(actualizarCuenta, "ActualizarCuentaUseCaseImpl", "@Transactional", "Actualiza cuenta - Escribe outbox event")
    Component(eliminarCuenta, "EliminarCuentaUseCaseImpl", "Use Case", "Lanza CuentaConSaldoActivoException si saldo > 0")
    Component(registrarMov, "RegistrarMovimientoUseCaseImpl", "@Transactional", "Valida tx duplicada - verifica ACTIVE - actualiza saldo - escribe outbox event")
  }

  Container_Boundary(domainLayer, "Domain Layer") {
    Component(cuentaModel, "Cuenta", "POJO", "cuentaId, saldo, estado, version - isActiva() - tieneSaldo()")
    Component(movModel, "Movimiento", "POJO", "movimientoId, cuentaId, tipoMovimiento, valor, saldoResultante")
    Component(outboxModel, "OutboxEvent", "POJO", "id, topic, payload, status PENDING-PUBLISHED, createdAt")
    Component(cacheModel, "ClienteCache", "POJO", "clienteId, nombre, estado, syncedAt")
  }

  Rel(usuario, cuentaCtrl, "HTTP")
  Rel(usuario, movCtrl, "HTTP")
  Rel(kafka, consumer, "@KafkaListener cliente-events")
  Rel(consumer, cacheJpa, "upsert(ClienteCache)")
  Rel(cacheJpa, postgres, "UPSERT clientes_cache")
  Rel(cuentaCtrl, crearCuenta, "execute()")
  Rel(cuentaCtrl, actualizarCuenta, "execute()")
  Rel(cuentaCtrl, eliminarCuenta, "execute()")
  Rel(movCtrl, registrarMov, "execute()")
  Rel(crearCuenta, cacheJpa, "existsById(clienteId)")
  Rel(crearCuenta, cuentaJpa, "save(Cuenta)")
  Rel(crearCuenta, outboxJpa, "save(OutboxEvent CuentaCreada)")
  Rel(actualizarCuenta, cuentaJpa, "save(Cuenta)")
  Rel(actualizarCuenta, outboxJpa, "save(OutboxEvent CuentaActualizada)")
  Rel(registrarMov, cuentaJpa, "findById() / save()")
  Rel(registrarMov, movJpa, "save(Movimiento)")
  Rel(registrarMov, outboxJpa, "save(OutboxEvent MovimientoRegistrado)")
  Rel(eliminarCuenta, cuentaJpa, "delete()")
  Rel(cuentaJpa, postgres, "JDBC cuentas")
  Rel(movJpa, postgres, "JDBC movimientos")
  Rel(outboxJpa, postgres, "JDBC outbox_events")
  Rel(relay, outboxJpa, "findPendingEvents() / markAsPublished()")
  Rel(relay, kafka, "KafkaTemplate send(topic, payload)")
```
