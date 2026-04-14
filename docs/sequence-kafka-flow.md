# Sequence Diagrams — Flujos principales

## Flujo completo con Outbox Pattern y Read Model

```mermaid
sequenceDiagram
  autonumber
  actor User as Usuario

  participant msC  as ms-clients :8082
  participant msA  as ms-accounts :8081
  participant msR  as ms-reportes :8080
  participant PG   as PostgreSQL
  participant K    as Kafka

  Note over User, K: Paso 1 — Alta de persona y cliente

  User ->>+ msC: POST /api/personas {identificacion, nombre, ...}
  msC  ->>  PG:  INSERT msclients_schema.persona
  msC  -->>- User: 201 PersonaResponse

  User ->>+ msC: POST /api/clientes {clienteId, identificacion, contrasena, ...}
  msC  ->>  PG:  existsById(clienteId) → false
  msC  ->>  msC: BCrypt.encode(contrasena)
  msC  ->>  PG:  INSERT msclients_schema.cliente
  msC  ->>  PG:  INSERT msclients_schema.outbox_events (status=PENDING)
  Note over msC, PG: Transaccion atomica
  msC  -->>- User: 201 ClienteResponse

  Note over PG, K: Outbox Relay (ms-clients, cada 1s)
  msC  ->>  PG:  findByStatus(PENDING)
  msC  ->>  K:   send(cliente-events, payload)
  msC  ->>  PG:  UPDATE outbox_events SET status=PUBLISHED

  Note over K, msA: Read Model sync en ms-accounts
  K    -->>  msA: @KafkaListener cliente-events
  msA  ->>   PG:  UPSERT msaccounts_schema.clientes_cache

  Note over K, msR: Proyeccion en ms-reportes
  K    -->>  msR: @KafkaListener cliente-events
  msR  ->>   PG:  INSERT reportes_schema.reporte_cliente

  Note over User, K: Paso 2 — Crear cuenta (con validacion Read Model)

  User ->>+ msA: POST /api/cuentas {cuentaId, clienteId, tipoCuenta, estado=ACTIVE}
  msA  ->>  PG:  existsById(clienteId) en clientes_cache → true
  msA  ->>  PG:  existsByNumeroCuenta() → false
  msA  ->>  PG:  INSERT msaccounts_schema.cuentas
  msA  ->>  PG:  INSERT msaccounts_schema.outbox_events (status=PENDING)
  Note over msA, PG: Transaccion atomica
  msA  -->>- User: 201 CuentaResponse

  Note over PG, K: Outbox Relay (ms-accounts, cada 1s)
  msA  ->>  PG:  findByStatus(PENDING)
  msA  ->>  K:   send(cuenta-creada, payload)
  msA  ->>  PG:  UPDATE outbox_events SET status=PUBLISHED

  K    -->>  msR: @KafkaListener cuenta-creada
  msR  ->>   PG:  INSERT reportes_schema.reporte_cuenta

  Note over User, K: Paso 3 — Registrar movimiento

  User ->>+ msA: POST /api/movimientos {movimientoId, cuentaId, DEPOSITO, valor:200}\n       X-Transaction-Id: tx-001
  msA  ->>  PG:  existsByTransactionId(tx-001) → false
  msA  ->>  PG:  SELECT cuenta → estado=ACTIVE
  msA  ->>  msA: nuevoSaldo = saldo + 200
  msA  ->>  PG:  UPDATE cuentas SET saldo=nuevoSaldo
  msA  ->>  PG:  INSERT msaccounts_schema.movimientos
  msA  ->>  PG:  INSERT msaccounts_schema.outbox_events (status=PENDING)
  Note over msA, PG: Transaccion atomica (saldo + movimiento + outbox)
  msA  -->>- User: 201 MovimientoResponse

  Note over PG, K: Outbox Relay (ms-accounts, cada 1s)
  msA  ->>  K:   send(movimiento-registrado, payload)
  msA  ->>  PG:  UPDATE outbox_events SET status=PUBLISHED

  K    -->>  msR: @KafkaListener movimiento-registrado
  msR  ->>   PG:  SELECT reporte_cuenta WHERE cuentaId
  msR  ->>   PG:  INSERT reportes_schema.reporte_movimiento

  Note over User, K: Paso 4 — Consultar reporte

  User ->>+ msR: GET /reportes?clienteId=cli-X&fechaInicio=...&fechaFin=...
  msR  ->>  PG:  SELECT reporte_cliente WHERE clienteId
  msR  ->>  PG:  SELECT reporte_cuenta WHERE clienteId
  msR  ->>  PG:  SELECT reporte_movimiento WHERE clienteId AND fecha BETWEEN
  msR  -->>- User: 200 ReporteResponse {cliente, cuentas:[{movimientos:[...]}]}
```

---

## Flujo Outbox Pattern — detalle

```mermaid
sequenceDiagram
  participant UC as UseCase (@Transactional)
  participant PG as PostgreSQL
  participant Relay as OutboxRelayService (@Scheduled 1s)
  participant K as Kafka

  UC ->> PG: save(entidad de negocio)
  UC ->> PG: save(OutboxEvent status=PENDING)
  Note over UC, PG: Ambas escrituras en la misma transaccion

  loop cada 1 segundo
    Relay ->> PG: findByStatus(PENDING)
    PG -->> Relay: lista de eventos pendientes
    loop por cada evento
      Relay ->> K: kafkaTemplate.send(topic, aggregateId, payload JSON)
      Relay ->> PG: UPDATE status=PUBLISHED publishedAt=now()
    end
  end
```

---

## Flujo Read Model — sincronizacion clientes_cache

```mermaid
sequenceDiagram
  participant msC as ms-clients
  participant OutboxC as outbox_events (msclients)
  participant Relay as OutboxRelayService (ms-clients)
  participant K as Kafka
  participant Consumer as ClienteEventConsumer (ms-accounts)
  participant Cache as clientes_cache (msaccounts)
  participant UC as CrearCuentaUseCase

  msC ->> OutboxC: save(ClienteCreado PENDING)
  Relay ->> K: send(cliente-events, payload)
  K -->> Consumer: @KafkaListener(cliente-events, group=ms-accounts-group)
  Consumer ->> Cache: UPSERT clientes_cache(clienteId, nombre, estado)

  Note over UC, Cache: Posterior: usuario crea cuenta
  UC ->> Cache: existsById(clienteId)
  Cache -->> UC: true
  UC ->> UC: continua con la creacion
```

---

## Flujo de error: cuenta con cliente inexistente (Read Model)

```mermaid
sequenceDiagram
  autonumber
  actor User as Usuario
  participant msA as ms-accounts :8081
  participant Cache as clientes_cache

  User ->>+ msA: POST /api/cuentas {clienteId: "cli-fantasma", ...}
  msA  ->>  Cache: existsById("cli-fantasma") → false
  msA  -->>- User: 404 {error: "Not Found", message: "Cliente no existe: cli-fantasma"}
```

---

## Flujo de error: transaccion duplicada

```mermaid
sequenceDiagram
  autonumber
  actor User as Usuario
  participant msA as ms-accounts :8081
  participant PG  as PostgreSQL

  User ->>+ msA: POST /api/movimientos {movimientoId, ...}\n       X-Transaction-Id: tx-001
  msA  ->>  PG:  existsByTransactionId(tx-001) → true
  msA  -->>- User: 409 {error: "Transaccion Duplicada",\n message: "X-Transaction-Id 'tx-001' ya fue procesado"}
```

---

## Flujo de error: cuenta no activa

```mermaid
sequenceDiagram
  autonumber
  actor User as Usuario
  participant msA as ms-accounts :8081
  participant PG  as PostgreSQL

  User ->>+ msA: POST /api/movimientos {cuentaId: "acc-X", ...}
  msA  ->>  PG:  SELECT cuenta WHERE cuentaId → estado=INACTIVE
  msA  -->>- User: 400 IllegalStateException: "Cuenta no esta activa"
```

---

## Flujo de error: cliente duplicado

```mermaid
sequenceDiagram
  autonumber
  actor User as Usuario
  participant msC as ms-clients :8082
  participant PG  as PostgreSQL

  User ->>+ msC: POST /clientes {clienteId: "CLI002", ...}
  msC  ->>  PG:  existsById("CLI002") → true
  msC  -->>- User: 409 {error: "Cliente Duplicado",\n message: "Ya existe un cliente con ID: CLI002"}
```

---

## Flujo de error: FK persona inexistente

```mermaid
sequenceDiagram
  autonumber
  actor User as Usuario
  participant msC as ms-clients :8082
  participant PG  as PostgreSQL

  User ->>+ msC: POST /clientes {identificacion: "999", ...}
  msC  ->>  PG:  INSERT cliente WHERE identificacion=999
  PG   -->>  msC: PSQLException: fk_cliente_persona — Key (identificacion)=(999) not found in persona
  msC  -->>- User: 409 {error: "Foreign Key Violation",\n message: "La persona con identificacion '999' no existe"}
```
