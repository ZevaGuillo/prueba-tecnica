-- Schema for ms-reportes (CQRS Read Model)

-- Cliente snapshot table
CREATE TABLE IF NOT EXISTS reporte_cliente (
    id UUID PRIMARY KEY,
    cliente_id UUID NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    identificacion VARCHAR(50) NOT NULL,
    email VARCHAR(255),
    telefono VARCHAR(50),
    fecha_creacion TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_reporte_cliente_cliente_id ON reporte_cliente(cliente_id);

-- Cuenta table
CREATE TABLE IF NOT EXISTS reporte_cuenta (
    id UUID PRIMARY KEY,
    cuenta_id UUID NOT NULL,
    cliente_id UUID NOT NULL,
    numero_cuenta VARCHAR(50) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    saldo_actual DECIMAL(19, 4) NOT NULL,
    moneda VARCHAR(3) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_reporte_cuenta_cliente_id ON reporte_cuenta(cliente_id);

-- Movimiento table
CREATE TABLE IF NOT EXISTS reporte_movimiento (
    id UUID PRIMARY KEY,
    movimiento_id UUID NOT NULL,
    cuenta_id UUID NOT NULL,
    cliente_id UUID NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    monto DECIMAL(19, 4) NOT NULL,
    saldo_posterior DECIMAL(19, 4) NOT NULL,
    descripcion VARCHAR(500),
    fecha TIMESTAMP NOT NULL,
    fecha_procesamiento TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_reporte_movimiento_cliente_fecha ON reporte_movimiento(cliente_id, fecha DESC);

-- Processed events for idempotency
CREATE TABLE IF NOT EXISTS processed_event (
    event_id VARCHAR(100) PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    fecha_procesamiento TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_processed_event_fecha ON processed_event(fecha_procesamiento);