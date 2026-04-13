# Research: Account Report CQRS

## Phase 0: Research Findings

### 1. Kafka Consumer Patterns for Java

**Decision**: Usar Spring Kafka con @KafkaListener

**Rationale**: 
- Integración nativa con Spring Boot (ya usado en el proyecto)
- Manejo automático de offsets y rebalancing
- Soporte para DLQ via DeadLetterPublishingErrorHandler

**Alternatives considered**:
- KafkaClientAPI vanilla: Más complejo, mismo resultado
- Reactor Kafka: Overkill para este caso de uso

---

### 2. Outbox Pattern Implementation

**Decision**: Polling table outbox + Kafka producer

**Rationale**:
- Garantiza exactly-once delivery
- Simple de implementar con PostgreSQL
- Sin dependencias externas adicionales

**Implementation**:
1. Productor escribe evento en outbox table (transacción con dominio)
2. Worker polling lee eventos no publicados
3. Publica a Kafka y marca como publicado
4. DLQ para eventos que fallan después de N reintentos

**Alternatives considered**:
- Transactional Outbox (Debezium CDC): Requires additional infrastructure
- Dual write: No garantizado, puede perder eventos

---

### 3. Paginación en PostgreSQL

**Decision**: OFFSET/LIMIT con cursor básico

**Rationale**:
- Simple de implementar
- Suficiente para el caso de uso
- page + size parametrizable

**Query pattern**:
```sql
SELECT * FROM reporte_movimientos 
WHERE cliente_id = :clienteId 
  AND fecha BETWEEN :fechaInicio AND :fechaFin
ORDER BY fecha DESC
LIMIT :size OFFSET :page * :size
```

**Alternatives considered**:
- Keyset pagination (cursor): Mejor performance para grandes datasets pero más complejo
- No pagination: Puede causar memory issues con muchos movimientos

---

### 4. Idempotency in Event Processing

**Decision**: eventId único en cada evento + tabla de processed_events

**Rationale**:
- Garantiza exactly-once processing
- Simple de verificar con SELECT antes de INSERT
- Tabla de control pequeña (solo IDs)

**Implementation**:
```java
@KafkaListener(topics = "...", groupId = "...")
public void handle(Event event) {
    if (eventProcessor.exists(event.getEventId())) {
        return; // Already processed - idempotency
    }
    process(event);
    eventProcessor.markProcessed(event.getEventId());
}
```

**Alternatives considered**:
- Kafka exactly-once: Requiere transacciones, overkill
- Redis distributed lock: Agrega dependencia

---

## Summary

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Kafka Consumer | Spring Kafka @KafkaListener | Integración nativa con proyecto existente |
| Outbox Pattern | PostgreSQL polling | Simple, garantiza delivery |
| Paginación | OFFSET/LIMIT | Suficiente para caso de uso |
| Idempotencia | eventId + processed table | Exactly-once guarantee |

**All NEEDS CLARIFICATION resolved**: None remaining