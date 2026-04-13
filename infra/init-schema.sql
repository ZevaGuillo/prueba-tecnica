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

GRANT ALL ON SCHEMA msclients_schema TO banking_user;
GRANT ALL ON SCHEMA msaccounts_schema TO banking_user;
GRANT ALL ON ALL TABLES IN SCHEMA msclients_schema TO banking_user;
GRANT ALL ON ALL TABLES IN SCHEMA msaccounts_schema TO banking_user;