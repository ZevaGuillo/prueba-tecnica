# Data Model: Account Report CQRS

## Phase 1: Design

### Entity: ReporteCliente (Snapshot)

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| clienteId | UUID | NOT NULL, INDEX | Reference to MS-Clientes |
| nombre | VARCHAR(255) | NOT NULL | Customer name |
| identificacion | VARCHAR(50) | NOT NULL | ID number (CI/RUT) |
| email | VARCHAR(255) | NULL | Contact email |
| telefono | VARCHAR(50) | NULL | Contact phone |
| fechaCreacion | TIMESTAMP | NOT NULL | When snapshot was created |
| fechaActualizacion | TIMESTAMP | NOT NULL | Last update timestamp |

**Rationale**: Snapshot denormalizado para evitar joins. Se actualiza vía eventos.

---

### Entity: ReporteCuenta

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| cuentaId | UUID | NOT NULL, INDEX | Reference to account |
| clienteId | UUID | NOT NULL, INDEX | FK to ReporteCliente |
| numeroCuenta | VARCHAR(50) | NOT NULL | Account number |
| tipo | VARCHAR(20) | NOT NULL | SAVINGS/CHECKING |
| saldoActual | DECIMAL(19,4) | NOT NULL | Current balance |
| moneda | VARCHAR(3) | NOT NULL | USD/EUR/etc |
| estado | VARCHAR(20) | NOT NULL | ACTIVE/CLOSED |
| fechaCreacion | TIMESTAMP | NOT NULL | Account creation date |

**Rationale**: Denormalizado para queries rápidas por clienteId.

---

### Entity: ReporteMovimiento

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| movimientoId | UUID | NOT NULL, INDEX | Reference to movement |
| cuentaId | UUID | NOT NULL, INDEX | FK to ReporteCuenta |
| clienteId | UUID | NOT NULL, INDEX | FK to ReporteCliente |
| tipo | VARCHAR(20) | NOT NULL | DEBIT/CREDIT |
| monto | DECIMAL(19,4) | NOT NULL | Transaction amount |
| saldoPosterior | DECIMAL(19,4) | NOT NULL | Balance after transaction |
| descripcion | VARCHAR(500) | NULL | Transaction description |
| fecha | TIMESTAMP | NOT NULL, INDEX | Movement date |
| fechaProcesamiento | TIMESTAMP | NOT NULL | When event was processed |

**Rationale**: Saldo posterior necesario para estado de cuenta. Index por fecha para range queries.

---

### Entity: ProcessedEvent (Idempotency Control)

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| eventId | VARCHAR(100) | PK | Unique event identifier |
| eventType | VARCHAR(50) | NOT NULL | Event type (CLIENTE/CUENTA/MOVIMIENTO) |
| fechaProcesamiento | TIMESTAMP | NOT NULL | When event was processed |

---

### Entity: OutboxEvent (Outbox Pattern)

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| aggregateType | VARCHAR(50) | NOT NULL | Type of aggregate |
| aggregateId | VARCHAR(100) | NOT NULL | Aggregate ID |
| eventType | VARCHAR(50) | NOT NULL | Type of event |
| payload | JSONB | NOT NULL | Event payload |
| createdAt | TIMESTAMP | NOT NULL | Creation timestamp |
| processedAt | TIMESTAMP | NULL | When event was published |

---

### Relationships

```
ReporteCliente (1) ──< (N) ReporteCuenta
ReporteCliente (1) ──< (N) ReporteMovimiento
ReporteCuenta (1) ──< (N) ReporteMovimiento
```

**Rationale**: Relaciones denormalizadas en el read model para evitar joins en query time.

---

### Indexes

```sql
-- Primary lookup
CREATE INDEX idx_cliente_fecha ON reporte_movimientos(clienteId, fecha DESC);

-- Account lookup
CREATE INDEX idx_cuenta_cliente ON reporte_cuenta(clienteId);

-- Idempotency check
CREATE INDEX idx_processed_eventid ON processed_event(eventId);
```

---

### Validation Rules

- clienteId: UUID válido, obligatorio
- fechaInicio <= fechaFin: Rango de fechas válido
- page >= 0, size > 0: Paginación válida
- saldoPosterior >= 0: Saldo no puede ser negativo

---

### State Transitions

**ReporteCliente**: CREATED → UPDATED (on ClienteActualizadoEvent)  
**ReporteCuenta**: CREATED → UPDATED → CLOSED (on cuenta closure)  
**ReporteMovimiento**: CREATED (append-only, no updates)