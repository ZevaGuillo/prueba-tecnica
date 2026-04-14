# Prueba Tecnica - Microservicios Bancarios

Sistema basado en microservicios para gestion de clientes, cuentas, movimientos y reportes, usando PostgreSQL y Kafka.

## Tabla De Contenidos
- Arquitectura
- Estructura Del Repositorio
- Prerrequisitos
- Levantar Todo Con Docker
- Levantar Localmente (sin Docker para apps)
- Endpoints Por Servicio
- Script End-To-End
- Eventos Kafka
- Troubleshooting

## Arquitectura
- **`ms-clients`**: alta y gestion de `personas` y `clientes`.
- **`ms-accounts`**: gestion de `cuentas` y `movimientos`.
- **`ms-reportes`**: read model para consulta consolidada de estado de cuenta.
- **`postgres`**: base `banking_db` con esquemas `msclients_schema`, `msaccounts_schema`, `reportes_schema`.
- **`kafka`**: bus de eventos entre servicios.

### Estilo De Diseno
- Arquitectura por capas `domain` / `application` / `infrastructure` dentro de cada servicio.
  - `ms-reportes` sigue enfoque tipo CQRS read model:
    - consume eventos asincronos desde Kafka,
    - persiste proyecciones en `reportes_schema`,
    - expone consultas en `/api/reportes`.

## Estructura Del Repositorio

```text
.
├── infra/
│   ├── compose.yml
│   ├── init-schema.sql
│   └── scripts/
│       ├── create-kafka-topics.sh
│       └── e2e-full-flow.sh
├── services/
│   ├── ms-clients/
│   ├── ms-accounts/
│   └── ms-reportes/
└── README.md
```

## Prerrequisitos
- Java 21
- Gradle 9.x (o usar `./gradlew` dentro de cada servicio)
- Docker + Docker Compose plugin
- Bash + curl

## Levantar Todo Con Docker

Desde la raiz del repo:

```bash
docker compose -f infra/compose.yml up --build
```

Servicios expuestos:
- `ms-reportes`: `http://localhost:8080`
- `ms-accounts`: `http://localhost:8081`
- `ms-clients`: `http://localhost:8082`
- PostgreSQL: `localhost:5432`
- Kafka: `localhost:9092`

Detener:

```bash
docker compose -f infra/compose.yml down
```

Limpiar volumenes (opcional):

```bash
docker compose -f infra/compose.yml down -v
```

## Levantar Localmente (sin Docker para apps)

### 1) Levantar solo infraestructura
```bash
docker compose -f infra/compose.yml up -d postgres kafka kafka-init-topics
```

### 2) Levantar microservicios en terminales separadas

`ms-clients`:
```bash
cd services/ms-clients
gradle bootRun
```

`ms-accounts`:
```bash
cd services/ms-accounts
gradle bootRun
```

`ms-reportes`:
```bash
cd services/ms-reportes
gradle bootRun
```

### 3) Health checks rapidos
```bash
curl -s http://localhost:8082/api/personas
curl -s http://localhost:8081/api/cuentas
curl -s "http://localhost:8080/api/reportes?clienteId=cli-001&fechaInicio=2026-01-01&fechaFin=2026-12-31&page=0&size=20"
```

## Endpoints Por Servicio

> Todos los endpoints siguen el formato: `http://{servidor}:{puerto}/api/{método}/{parámetros}`

## `ms-clients` (`http://localhost:8082`)

### Personas
- `POST /api/personas`
- `GET /api/personas`
- `GET /api/personas/{id}`

Crear persona:
```bash
curl -X POST 'http://localhost:8082/api/personas' \
  -H 'Content-Type: application/json' \
  -d '{"identificacion":"1233","nombre":"Juan","genero":"M","edad":30,"direccion":"Calle 1","telefono":"099123456"}'
```

### Clientes
- `POST /api/clientes`
- `GET /api/clientes`
- `GET /api/clientes/{id}`
- `PUT /api/clientes/{id}`
- `DELETE /api/clientes/{id}`

Crear cliente:
```bash
curl -X POST 'http://localhost:8082/api/clientes' \
  -H 'Content-Type: application/json' \
  -d '{"clienteId":"CLI002","identificacion":"1233","nombre":"Juan","genero":"M","edad":30,"direccion":"Calle 1","telefono":"099123456","contrasena":"password123"}'
```

Notas de negocio:
- `POST /api/clientes` rechaza duplicados por `clienteId` con `409`.
- `contrasena` se guarda con hash BCrypt, no en texto plano.

## `ms-accounts` (`http://localhost:8081`)

- `POST /api/cuentas`
- `GET /api/cuentas`
- `GET /api/cuentas/{cuentaId}`
- `GET /api/cuentas/cliente/{clienteId}`
- `PATCH /api/cuentas/{cuentaId}`
- `DELETE /api/cuentas/{cuentaId}`
- `POST /api/movimientos`

Crear cuenta:
```bash
curl -X POST 'http://localhost:8081/api/cuentas' \
  -H 'Content-Type: application/json' \
  -d '{"cuentaId":"acc-001","numeroCuenta":"478758","tipoCuenta":"AHORROS","saldo":1000.0,"clienteId":"CLI002","estado":"ACTIVE"}'
```

Registrar movimiento:
```bash
curl -X POST 'http://localhost:8081/api/movimientos' \
  -H 'Content-Type: application/json' \
  -H 'X-Transaction-Id: tx-001' \
  -d '{"movimientoId":"mov-001","cuentaId":"acc-001","tipoMovimiento":"DEPOSITO","valor":200.0}'
```

Notas de negocio:
- La cuenta debe estar en estado `ACTIVE` para aceptar movimientos.
- `X-Transaction-Id` evita reprocesamiento de movimientos duplicados.

## `ms-reportes` (`http://localhost:8080`)

- `GET /api/reportes`
  - query params:
    - `clienteId` (requerido)
    - `fechaInicio` (requerido, `YYYY-MM-DD`)
    - `fechaFin` (requerido, `YYYY-MM-DD`)
    - `page` (opcional, default `0`)
    - `size` (opcional, default `20`, max `100`)
  - header opcional: `X-Correlation-ID`

Ejemplo:
```bash
curl -X GET "http://localhost:8080/api/reportes?clienteId=CLI002&fechaInicio=2026-01-01&fechaFin=2026-12-31&page=0&size=20" \
  -H "X-Correlation-ID: corr-001"
```

## Script End-To-End

Existe un script integrado de flujo completo:

- `infra/scripts/e2e-full-flow.sh`

Ejecucion:
```bash
chmod +x infra/scripts/e2e-full-flow.sh
./infra/scripts/e2e-full-flow.sh
```

Que valida:
- flujo personas/clientes,
- flujo cuentas/movimientos,
- consulta de reportes con espera activa por consistencia eventual (Kafka),
- casos de negocio (duplicado de cliente).

Opcionalmente puedes cambiar URLs:
```bash
CLIENTS_BASE_URL=http://localhost:8082 \
ACCOUNTS_BASE_URL=http://localhost:8081 \
REPORTES_BASE_URL=http://localhost:8080 \
./infra/scripts/e2e-full-flow.sh
```

## Eventos Kafka

Topics usados en el entorno:
- `cliente-events`
- `cuenta-creada`
- `cuenta-actualizada`
- `movimiento-registrado`
- (compatibilidad historica) `cuenta-events`, `movimiento-events`

Inicializacion de topics:
- `infra/scripts/create-kafka-topics.sh`
- servicio `kafka-init-topics` en `infra/compose.yml`

## Troubleshooting

- **`Cuenta no esta activa` en movimientos**
  - Verifica estado de cuenta: debe ser exactamente `ACTIVE`.

- **`ms-reportes` no refleja movimientos al instante**
  - Es asincrono por Kafka. Reintenta `GET /reportes` unos segundos despues.

- **Error FK `fk_cliente_persona` al crear cliente**
  - Debe existir primero la persona con la misma `identificacion`.

- **`POST /clientes` repetido**
  - Debe responder `409 Cliente Duplicado` cuando `clienteId` ya existe.

- **Puertos ocupados**
  - Cambia `SERVER_PORT` al levantar localmente:
    - `SERVER_PORT=8087 gradle bootRun`

---

## Diagramas

Los diagramas Mermaid C4 y ER viven en [`docs/`](docs/README.md):

| Diagrama | Descripcion |
|---|---|
| [C4 Context](docs/c4-context.md) | Vista de actores y sistema |
| [C4 Container](docs/c4-container.md) | Microservicios, PostgreSQL y Kafka |
| [C4 Component ms-clients](docs/c4-component-ms-clients.md) | Capas internas de ms-clients |
| [C4 Component ms-accounts](docs/c4-component-ms-accounts.md) | Capas internas de ms-accounts |
| [C4 Component ms-reportes](docs/c4-component-ms-reportes.md) | Capas internas de ms-reportes |
| [Sequence Diagrams](docs/sequence-kafka-flow.md) | Flujos E2E y flujos de error |
| [ER Schema](docs/schema-database.md) | Esquema banking_db completo |
