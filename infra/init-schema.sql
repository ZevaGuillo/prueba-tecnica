CREATE SCHEMA IF NOT EXISTS msclients_schema;
CREATE SCHEMA IF NOT EXISTS msaccounts_schema;

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

GRANT ALL ON SCHEMA msclients_schema TO banking_user;
GRANT ALL ON SCHEMA msaccounts_schema TO banking_user;
GRANT ALL ON ALL TABLES IN SCHEMA msclients_schema TO banking_user;
GRANT ALL ON ALL TABLES IN SCHEMA msaccounts_schema TO banking_user;