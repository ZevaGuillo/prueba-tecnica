# Esquema de Base de Datos — banking_db

Base de datos única `banking_db` en PostgreSQL 15, con tres esquemas separados por microservicio.

```mermaid
erDiagram
  direction LR

  %% ── msclients_schema ──────────────────────────────────────
  msclients_persona {
    VARCHAR identificacion PK
    VARCHAR nombre
    VARCHAR genero
    INT     edad
    VARCHAR direccion
    VARCHAR telefono
  }

  msclients_cliente {
    VARCHAR cliente_id    PK
    VARCHAR identificacion FK
    VARCHAR contrasena
    VARCHAR estado
  }

  msclients_outbox_events {
    VARCHAR id            PK
    VARCHAR aggregate_id
    VARCHAR aggregate_type
    VARCHAR event_type
    VARCHAR topic
    TEXT    payload
    VARCHAR status
    TS      created_at
    TS      published_at
  }

  msclients_persona ||--o{ msclients_cliente : "fk_cliente_persona"

  %% ── msaccounts_schema ─────────────────────────────────────
  msaccounts_cuentas {
    VARCHAR cuenta_id   PK
    VARCHAR numero_cuenta
    VARCHAR tipo_cuenta
    DOUBLE  saldo
    VARCHAR estado
    VARCHAR cliente_id
    INT     version
  }

  msaccounts_movimientos {
    VARCHAR movimiento_id PK
    VARCHAR cuenta_id    FK
    VARCHAR tipo_movimiento
    DOUBLE  valor
    DOUBLE  saldo_resultante
    TS      fecha
    VARCHAR transaction_id  "UNIQUE"
  }

  msaccounts_clientes_cache {
    VARCHAR cliente_id  PK
    VARCHAR nombre
    VARCHAR estado
    TS      synced_at
  }

  msaccounts_outbox_events {
    VARCHAR id            PK
    VARCHAR aggregate_id
    VARCHAR aggregate_type
    VARCHAR event_type
    VARCHAR topic
    TEXT    payload
    VARCHAR status
    TS      created_at
    TS      published_at
  }

  msaccounts_cuentas ||--o{ msaccounts_movimientos : "fk_movimientos_cuentas"

  %% ── reportes_schema (read model CQRS) ─────────────────────
  reportes_reporte_cliente {
    UUID    id PK
    VARCHAR cliente_id
    VARCHAR nombre
    VARCHAR identificacion
    VARCHAR email
    VARCHAR telefono
    TS      fecha_creacion
    TS      fecha_actualizacion
  }

  reportes_reporte_cuenta {
    UUID    id PK
    VARCHAR cuenta_id
    VARCHAR cliente_id
    VARCHAR numero_cuenta
    VARCHAR tipo
    DECIMAL saldo_actual
    VARCHAR moneda
    VARCHAR estado
    TS      fecha_creacion
  }

  reportes_reporte_movimiento {
    UUID    id PK
    VARCHAR movimiento_id
    VARCHAR cuenta_id
    VARCHAR cliente_id
    VARCHAR tipo
    DECIMAL monto
    DECIMAL saldo_posterior
    VARCHAR descripcion
    TS      fecha
    TS      fecha_procesamiento
  }

  reportes_processed_event {
    VARCHAR event_id PK
    VARCHAR event_type
    TS      fecha_procesamiento
  }

  reportes_reporte_cliente ||--o{ reportes_reporte_cuenta     : "clienteId (denormalizado)"
  reportes_reporte_cuenta  ||--o{ reportes_reporte_movimiento : "cuentaId (denormalizado)"
```

## Notas

### Modelos de escritura
- `msclients_schema` y `msaccounts_schema` son los modelos de escritura (negocio).
- Cada uno tiene su tabla `outbox_events` para el Transactional Outbox Pattern.

### Outbox Pattern (`outbox_events`)
- `status`: `PENDING` (recien creado, aun no publicado a Kafka) o `PUBLISHED` (ya enviado y confirmado).
- `topic`: nombre del topic Kafka destino (ej: `cliente-events`, `cuenta-creada`).
- `payload`: JSON serializado de la entidad de dominio.
- `published_at`: timestamp cuando el `OutboxRelayService` lo publicó exitosamente.
- Indice sobre `status` para que el relay consulte eficientemente solo los `PENDING`.

### Read Model local (`clientes_cache` en `msaccounts_schema`)
- Proyeccion local en `ms-accounts` de los clientes existentes en `ms-clients`.
- Actualizada por el `ClienteEventConsumer` al recibir eventos del topic `cliente-events`.
- Usada por `CrearCuentaUseCaseImpl` para validar existencia del cliente sin llamadas HTTP.
- `synced_at`: ultima vez que el evento fue procesado (permite detectar eventos desactualizados).

### Read Model CQRS (`reportes_schema`)
- Modelo de lectura exclusivo de `ms-reportes`, actualizado solo por eventos Kafka.
- `processed_event` garantiza idempotencia: un `event_id` no se procesa dos veces.
- Todos los `cliente_id`, `cuenta_id`, `movimiento_id` son `VARCHAR(50)` para admitir IDs arbitrarios (ej: `cli-001`, `acc-001`).
