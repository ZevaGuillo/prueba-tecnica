# Quickstart: Account Report CQRS

## Prerequisites

- Java 21
- Docker & Docker Compose
- PostgreSQL (optional local)
- Kafka (optional local)

## Quick Start

### 1. Start Infrastructure

```bash
docker-compose -f infra/compose.yml up -d
```

### 2. Build

```bash
./gradlew :ms-reportes:build
```

### 3. Run

```bash
./gradlew :ms-reportes:bootRun
```

### 4. Test

```bash
# Health check
curl http://localhost:8080/actuator/health

# Get report
curl "http://localhost:8080/reportes?clienteId=550e8400-e29b-41d4-a716-446655440000&fechaInicio=2026-01-01&fechaFin=2026-12-31"
```

---

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| server.port | 8080 | HTTP port |
| spring.kafka.bootstrap-servers | localhost:9092 | Kafka brokers |
| spring.datasource.url | jdbc:postgresql://localhost:5432/reportes | PostgreSQL URL |

---

## Development

### Topics Kafka

- `cliente-events`: Customer events
- `cuenta-events`: Account events  
- `movimiento-events`: Movement events

### Database

```sql
-- Schema initialization
CREATE TABLE reporte_cliente (...);
CREATE TABLE reporte_cuenta (...);
CREATE TABLE reporte_movimiento (...);
CREATE TABLE processed_event (...);
CREATE TABLE outbox_event (...);
```

---

## Testing

### Integration Tests

```bash
./gradlew :ms-reportes:test:int
```

### Unit Tests

```bash
./gradlew :ms-reportes:test
```

### Manual Testing

```bash
# Test endpoint
curl "http://localhost:8080/reportes?clienteId=550e8400-e29b-41d4-a716-446655440000&fechaInicio=2026-01-01&fechaFin=2026-12-31"

# Test with date validation (should return 400)
curl "http://localhost:8080/reportes?clienteId=550e8400-e29b-41d4-a716-446655440000&fechaInicio=2026-12-31&fechaFin=2026-01-01"

# Test with invalid pagination (should return 400)
curl "http://localhost:8080/reportes?clienteId=550e8400-e29b-41d4-a716-446655440000&fechaInicio=2026-01-01&fechaFin=2026-12-31&size=0"
```

---

## Operations

### Health

```bash
curl http://localhost:8080/actuator/health
```

### Logs

```bash
tail -f logs/ms-reportes.log