# Sequence Diagrams — Flujos principales

## Flujo completo: crear cliente → crear cuenta → registrar movimiento → consultar reporte

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

  User ->>+ msC: POST /personas {identificacion, nombre, ...}
  msC  ->>  PG:  INSERT msclients_schema.persona
  msC  -->>- User: 201 PersonaResponse

  User ->>+ msC: POST /clientes {clienteId, identificacion, contrasena, ...}
  msC  ->>  PG:  SELECT existsById(clienteId) → false
  msC  ->>  msC: BCrypt.encode(contrasena)
  msC  ->>  PG:  INSERT msclients_schema.cliente
  msC  ->>+ K:   Publish → cliente-events {clienteId, nombre, ...}
  K    -->>- msR: @KafkaListener cliente-events
  msR  ->>  PG:  INSERT reportes_schema.reporte_cliente
  msC  -->>- User: 201 ClienteResponse

  Note over User, K: Paso 2 — Crear cuenta

  User ->>+ msA: POST /api/cuentas {cuentaId, clienteId, tipoCuenta, estado=ACTIVE}
  msA  ->>  PG:  SELECT existsById(cuentaId) → false
  msA  ->>  PG:  INSERT msaccounts_schema.cuentas
  msA  ->>+ K:   Publish → cuenta-creada {cuentaId, clienteId, saldo, ...}
  K    -->>- msR: @KafkaListener cuenta-creada
  msR  ->>  PG:  INSERT reportes_schema.reporte_cuenta
  msA  -->>- User: 201 CuentaResponse

  Note over User, K: Paso 3 — Registrar movimiento

  User ->>+ msA: POST /api/movimientos {movimientoId, cuentaId, DEPOSITO, valor:200}\n       X-Transaction-Id: tx-001
  msA  ->>  PG:  existsByTransactionId(tx-001) → false
  msA  ->>  PG:  SELECT cuenta → estado=ACTIVE
  msA  ->>  msA: nuevoSaldo = saldo + 200
  msA  ->>  PG:  UPDATE cuentas SET saldo = nuevoSaldo
  msA  ->>  PG:  INSERT msaccounts_schema.movimientos
  msA  ->>+ K:   Publish → movimiento-registrado {movimientoId, cuentaId, valor, saldoResultante}
  K    -->>- msR: @KafkaListener movimiento-registrado
  msR  ->>  PG:  SELECT reporte_cuenta WHERE cuentaId → clienteId
  msR  ->>  PG:  INSERT reportes_schema.reporte_movimiento
  msA  -->>- User: 201 MovimientoResponse

  Note over User, K: Paso 4 — Consultar reporte

  User ->>+ msR: GET /reportes?clienteId=cli-X&fechaInicio=...&fechaFin=...
  msR  ->>  PG:  SELECT reporte_cliente WHERE clienteId
  msR  ->>  PG:  SELECT reporte_cuenta WHERE clienteId
  msR  ->>  PG:  SELECT reporte_movimiento WHERE clienteId AND fecha BETWEEN
  msR  -->>- User: 200 ReporteResponse {cliente, cuentas:[{movimientos:[...]}]}
```

---

## Flujo de error: transacción duplicada

```mermaid
sequenceDiagram
  autonumber
  actor User as Usuario
  participant msA as ms-accounts :8081
  participant PG  as PostgreSQL

  User ->>+ msA: POST /api/movimientos {movimientoId, ...}\n       X-Transaction-Id: tx-001
  msA  ->>  PG:  existsByTransactionId(tx-001) → true
  msA  -->>- User: 409 {error: "Transacción Duplicada",\n message: "X-Transaction-Id 'tx-001' ya fue procesado"}
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
  msA  -->>- User: 500 IllegalStateException: "Cuenta no está activa"
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
