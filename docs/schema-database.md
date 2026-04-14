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

  msclients_persona ||--o{ msclients_cliente : "fk_cliente_persona"

  %% ── msaccounts_schema ─────────────────────────────────────
  msaccounts_cuentas {
    VARCHAR cuentas_cuenta_id   PK
    VARCHAR numero_cuenta
    VARCHAR tipo_cuenta
    DOUBLE  saldo
    VARCHAR estado
    VARCHAR cliente_id
    INT     version
  }

  msaccounts_movimientos {
    VARCHAR movimientos_movimiento_id PK
    VARCHAR cuenta_id    FK
    VARCHAR tipo_movimiento
    DOUBLE  valor
    DOUBLE  saldo_resultante
    TS      fecha
    VARCHAR transaction_id  "UNIQUE"
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
    UUID    r_cuenta_id PK
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
    UUID    r_mov_id PK
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

- `msclients_schema` y `msaccounts_schema` son los modelos de escritura.
- `reportes_schema` es el modelo de lectura (CQRS), actualizado exclusivamente por eventos Kafka.
- `processed_event` garantiza idempotencia: un evento con el mismo `event_id` no se procesa dos veces.
- Todos los `cliente_id`, `cuenta_id`, `movimiento_id` en `reportes_schema` son `VARCHAR(50)` para admitir IDs arbitrarios (ej: `cli-001`, `acc-001`).
