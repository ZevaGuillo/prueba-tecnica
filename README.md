# Prueba Tecnica - Microservicios Bancarios

Sistema basado en microservicios para gestion de clientes, cuentas, movimientos y reportes, usando PostgreSQL y Kafka.

## Tabla De Contenidos
- Arquitectura
- Patrones Implementados
- Estructura Del Repositorio
- Prerrequisitos
- Levantar Todo Con Docker
- Levantar Localmente (sin Docker para apps)
- Endpoints Por Servicio
- Script End-To-End
- Eventos Kafka
- Troubleshooting
- Diagramas

## Arquitectura
- **`ms-clients`**: alta y gestion de `personas` y `clientes`. Publica eventos via Outbox Pattern.
- **`ms-accounts`**: gestion de `cuentas` y `movimientos`. Valida clientes via Read Model local. Publica eventos via Outbox Pattern.
- **`ms-reportes`**: read model CQRS para consulta consolidada de estado de cuenta.
- **`postgres`**: base `banking_db` con esquemas `msclients_schema`, `msaccounts_schema`, `reportes_schema`.
- **`kafka`**: bus de eventos entre servicios.

### Estilo De Diseno

Arquitectura hexagonal (ports & adapters) con capas `domain` / `application` / `infrastructure` por servicio.

#### Transactional Outbox Pattern (`ms-clients` y `ms-accounts`)

Los use cases NO publican directo a Kafka. En su lugar, dentro de la misma transaccion de negocio, escriben un registro en la tabla `outbox_events` con `status=PENDING`. Un servicio scheduler (`OutboxEventRelayService`, `@Scheduled` cada 1 segundo) lee los eventos pendientes, los publica a Kafka y los marca como `PUBLISHED`.

Esto garantiza que si Kafka esta caido, el estado de negocio y el evento esten siempre en sincronía (o ambos se persisten, o ninguno).

```
UseCase (@Transactional)
  ├── save(entidad)         → msX_schema.tabla
  └── save(OutboxEvent)     → msX_schema.outbox_events (PENDING)

OutboxRelayService (@Scheduled 1s)
  ├── findByStatus(PENDING) → msX_schema.outbox_events
  ├── kafkaTemplate.send()  → Kafka topic
  └── markAsPublished()     → msX_schema.outbox_events (PUBLISHED)
```

#### Read Model Local (`ms-accounts`)

`ms-accounts` consume el topic `cliente-events` y mantiene una tabla local `msaccounts_schema.clientes_cache`. Al crear una cuenta, `CrearCuentaUseCaseImpl` valida que el `clienteId` exista en la cache local antes de persistir. Si no existe, retorna `404 Not Found`.

Esto evita cualquier llamada HTTP sincrónica entre microservicios, manteniendo bajo acoplamiento con consistencia eventual (~1s de ventana).

```
ms-clients → outbox_events → OutboxRelay → Kafka (cliente-events)
                                                ↓
ms-accounts: @KafkaListener → clientes_cache.upsert(clienteId)
                                                ↓
POST /api/cuentas → clienteCachePort.existsById() → 404 si no existe
```

#### Read Model CQRS (`ms-reportes`)

Consume eventos Kafka de los tres topics, persiste proyecciones en `reportes_schema`, y expone consultas en `/api/reportes`. No tiene escrituras de negocio directas.

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
├── docs/
│   ├── c4-context.md
│   ├── c4-container.md
│   ├── c4-component-ms-clients.md
│   ├── c4-component-ms-accounts.md
│   ├── c4-component-ms-reportes.md
│   ├── sequence-kafka-flow.md
│   └── schema-database.md
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
- Al crear un cliente, se publica un evento via Outbox Pattern al topic `cliente-events`. `ms-accounts` lo consume y actualiza su `clientes_cache` local.

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
- `POST /api/cuentas` valida que el `clienteId` exista en la cache local (`clientes_cache`). Si no existe, retorna `404 Not Found`. El cliente debe haberse creado en `ms-clients` y el evento debe haberse propagado (~1s) antes de crear la cuenta.
- La cuenta debe estar en estado `ACTIVE` para aceptar movimientos.
- `X-Transaction-Id` evita reprocesamiento de movimientos duplicados (`409`).
- Los eventos de cuenta y movimiento se publican via Outbox Pattern (atomico con la escritura de negocio).

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
- Smoke checks de los tres servicios.
- Flujo personas/clientes (crear, obtener, actualizar, duplicado 409).
- **Read Model**: intento de crear cuenta con `clienteId` inexistente retorna `404`.
- Espera de propagacion Kafka (~3s) para que el cliente llegue al cache de `ms-accounts`.
- Flujo cuentas/movimientos (deposito, retiro, idempotencia de transacciones).
- Consulta de reportes con espera activa por consistencia eventual Kafka.
- Limpieza: elimina cuenta y cliente al final.

Opcionalmente puedes cambiar URLs:
```bash
CLIENTS_BASE_URL=http://localhost:8082 \
ACCOUNTS_BASE_URL=http://localhost:8081 \
REPORTES_BASE_URL=http://localhost:8080 \
./infra/scripts/e2e-full-flow.sh
```

## Eventos Kafka

Topics usados:

| Topic | Productor | Consumidor(es) |
|---|---|---|
| `cliente-events` | `ms-clients` (via Outbox) | `ms-reportes`, `ms-accounts` |
| `cuenta-creada` | `ms-accounts` (via Outbox) | `ms-reportes` |
| `cuenta-actualizada` | `ms-accounts` (via Outbox) | `ms-reportes` |
| `movimiento-registrado` | `ms-accounts` (via Outbox) | `ms-reportes` |

Inicializacion de topics:
- `infra/scripts/create-kafka-topics.sh`
- servicio `kafka-init-topics` en `infra/compose.yml`

### Tablas Outbox

Cada servicio tiene su propia tabla de outbox en su esquema:

| Tabla | Servicio |
|---|---|
| `msaccounts_schema.outbox_events` | `ms-accounts` |
| `msclients_schema.outbox_events` | `ms-clients` |

Campos: `id (UUID PK)`, `aggregate_id`, `aggregate_type`, `event_type`, `topic`, `payload (TEXT/JSON)`, `status (PENDING/PUBLISHED)`, `created_at`, `published_at`.

## Troubleshooting

- **`404 Cliente no existe` al crear cuenta**
  - El cliente aun no fue propagado al cache de `ms-accounts`. Espera ~2-3 segundos despues de crear el cliente en `ms-clients` y reintenta. Es consistencia eventual del Read Model.

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

Los diagramas Mermaid C4 y ER viven en [`docs/`](docs/):

| Diagrama | Descripcion |
|---|---|
| [C4 Context](docs/c4-context.md) | Vista de actores y sistema |
| [C4 Container](docs/c4-container.md) | Microservicios, PostgreSQL y Kafka |
| [C4 Component ms-clients](docs/c4-component-ms-clients.md) | Capas internas de ms-clients |
| [C4 Component ms-accounts](docs/c4-component-ms-accounts.md) | Capas internas de ms-accounts con Outbox y Read Model |
| [C4 Component ms-reportes](docs/c4-component-ms-reportes.md) | Capas internas de ms-reportes |
| [Sequence Diagrams](docs/sequence-kafka-flow.md) | Flujos E2E, Outbox Pattern, Read Model y errores |
| [ER Schema](docs/schema-database.md) | Esquema banking_db completo con tablas outbox y cache |
