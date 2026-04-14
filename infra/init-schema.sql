CREATE SCHEMA IF NOT EXISTS msclients_schema;
CREATE SCHEMA IF NOT EXISTS msaccounts_schema;
CREATE SCHEMA IF NOT EXISTS reportes_schema;

-- Tabla persona
CREATE TABLE IF NOT EXISTS msclients_schema.persona (
    identificacion VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    genero VARCHAR(1) NOT NULL,
    edad INTEGER NOT NULL,
    direccion VARCHAR(200),
    telefono VARCHAR(20)
);

-- Tabla cliente
CREATE TABLE IF NOT EXISTS msclients_schema.cliente (
    cliente_id VARCHAR(20) PRIMARY KEY,
    identificacion VARCHAR(20) NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    estado VARCHAR(10) NOT NULL,
    CONSTRAINT fk_cliente_persona FOREIGN KEY (identificacion) REFERENCES msclients_schema.persona(identificacion)
);

-- Tabla cuentas
CREATE TABLE IF NOT EXISTS msaccounts_schema.cuentas (
    cuenta_id VARCHAR(50) PRIMARY KEY,
    numero_cuenta VARCHAR(50) UNIQUE NOT NULL,
    tipo_cuenta VARCHAR(20),
    saldo DOUBLE PRECISION,
    estado VARCHAR(20),
    cliente_id VARCHAR(50),
    version INTEGER DEFAULT 0
);

-- Tabla movimientos
CREATE TABLE IF NOT EXISTS msaccounts_schema.movimientos (
    movimiento_id VARCHAR(50) PRIMARY KEY,
    cuenta_id VARCHAR(50) NOT NULL,
    tipo_movimiento VARCHAR(20),
    valor DOUBLE PRECISION,
    saldo_resultante DOUBLE PRECISION,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transaction_id VARCHAR(50) UNIQUE,
    CONSTRAINT fk_movimientos_cuentas FOREIGN KEY (cuenta_id) REFERENCES msaccounts_schema.cuentas(cuenta_id)
);

CREATE INDEX idx_movimientos_cuenta_id ON msaccounts_schema.movimientos(cuenta_id);
CREATE INDEX idx_movimientos_fecha ON msaccounts_schema.movimientos(fecha);

-- ---------------------------------------------------------------------------
-- Read model ms-reportes (CQRS) — esquema dedicado reportes_schema
-- ---------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS reportes_schema.reporte_cliente (
    id UUID PRIMARY KEY,
    cliente_id VARCHAR(50) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    identificacion VARCHAR(50) NOT NULL,
    email VARCHAR(255),
    telefono VARCHAR(50),
    fecha_creacion TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_reporte_cliente_cliente_id ON reportes_schema.reporte_cliente(cliente_id);

CREATE TABLE IF NOT EXISTS reportes_schema.reporte_cuenta (
    id UUID PRIMARY KEY,
    cuenta_id VARCHAR(50) NOT NULL,
    cliente_id VARCHAR(50) NOT NULL,
    numero_cuenta VARCHAR(50) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    saldo_actual DECIMAL(19, 4) NOT NULL,
    moneda VARCHAR(3) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_reporte_cuenta_cliente_id ON reportes_schema.reporte_cuenta(cliente_id);

CREATE TABLE IF NOT EXISTS reportes_schema.reporte_movimiento (
    id UUID PRIMARY KEY,
    movimiento_id VARCHAR(50) NOT NULL,
    cuenta_id VARCHAR(50) NOT NULL,
    cliente_id VARCHAR(50) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    monto DECIMAL(19, 4) NOT NULL,
    saldo_posterior DECIMAL(19, 4) NOT NULL,
    descripcion VARCHAR(500),
    fecha TIMESTAMP NOT NULL,
    fecha_procesamiento TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_reporte_movimiento_cliente_fecha ON reportes_schema.reporte_movimiento(cliente_id, fecha DESC);

CREATE TABLE IF NOT EXISTS reportes_schema.processed_event (
    event_id VARCHAR(100) PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    fecha_procesamiento TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_processed_event_fecha ON reportes_schema.processed_event(fecha_procesamiento);

-- Compatibilidad con entornos ya creados: permite IDs no UUID (ej: cli-001)
ALTER TABLE IF EXISTS reportes_schema.reporte_cliente
    ALTER COLUMN cliente_id TYPE VARCHAR(50) USING cliente_id::varchar;
ALTER TABLE IF EXISTS reportes_schema.reporte_cuenta
    ALTER COLUMN cuenta_id TYPE VARCHAR(50) USING cuenta_id::varchar;
ALTER TABLE IF EXISTS reportes_schema.reporte_cuenta
    ALTER COLUMN cliente_id TYPE VARCHAR(50) USING cliente_id::varchar;
ALTER TABLE IF EXISTS reportes_schema.reporte_movimiento
    ALTER COLUMN movimiento_id TYPE VARCHAR(50) USING movimiento_id::varchar;
ALTER TABLE IF EXISTS reportes_schema.reporte_movimiento
    ALTER COLUMN cuenta_id TYPE VARCHAR(50) USING cuenta_id::varchar;
ALTER TABLE IF EXISTS reportes_schema.reporte_movimiento
    ALTER COLUMN cliente_id TYPE VARCHAR(50) USING cliente_id::varchar;

-- ---------------------------------------------------------------------------
-- Read model cliente en ms-accounts (cache local para validación)
-- ---------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS msaccounts_schema.clientes_cache (
    cliente_id  VARCHAR(50)  PRIMARY KEY,
    nombre      VARCHAR(100),
    estado      VARCHAR(20)  NOT NULL,
    synced_at   TIMESTAMP    NOT NULL
);

-- ---------------------------------------------------------------------------
-- Outbox pattern — eventos pendientes de publicación a Kafka
-- ---------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS msaccounts_schema.outbox_events (
    id             VARCHAR(36)   PRIMARY KEY,
    aggregate_id   VARCHAR(100)  NOT NULL,
    aggregate_type VARCHAR(100)  NOT NULL,
    event_type     VARCHAR(100)  NOT NULL,
    topic          VARCHAR(200)  NOT NULL,
    payload        TEXT          NOT NULL,
    status         VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    created_at     TIMESTAMP     NOT NULL,
    published_at   TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_msaccounts_outbox_status
    ON msaccounts_schema.outbox_events(status);

CREATE TABLE IF NOT EXISTS msclients_schema.outbox_events (
    id             VARCHAR(36)   PRIMARY KEY,
    aggregate_id   VARCHAR(100)  NOT NULL,
    aggregate_type VARCHAR(100)  NOT NULL,
    event_type     VARCHAR(100)  NOT NULL,
    topic          VARCHAR(200)  NOT NULL,
    payload        TEXT          NOT NULL,
    status         VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    created_at     TIMESTAMP     NOT NULL,
    published_at   TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_msclients_outbox_status
    ON msclients_schema.outbox_events(status);

GRANT ALL ON SCHEMA msclients_schema TO banking_user;
GRANT ALL ON SCHEMA msaccounts_schema TO banking_user;
GRANT ALL ON SCHEMA reportes_schema TO banking_user;
GRANT ALL ON ALL TABLES IN SCHEMA msclients_schema TO banking_user;
GRANT ALL ON ALL TABLES IN SCHEMA msaccounts_schema TO banking_user;
GRANT ALL ON ALL TABLES IN SCHEMA reportes_schema TO banking_user;