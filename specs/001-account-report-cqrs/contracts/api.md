# API Contract: Account Report Service

## Endpoint: GET /reportes

### Request

```
GET /reportes?clienteId={uuid}&fechaInicio={YYYY-MM-DD}&fechaFin={YYYY-MM-DD}&page={int}&size={int}
```

| Parameter | Type | Required | Default | Constraints |
|-----------|------|----------|---------|-------------|
| clienteId | UUID | Yes | - | Valid UUID |
| fechaInicio | Date | Yes | - | ISO 8601 (YYYY-MM-DD) |
| fechaFin | Date | Yes | - | ISO 8601, >= fechaInicio |
| page | Integer | No | 0 | >= 0 |
| size | Integer | No | 20 | 1-100 |

### Response 200 OK

```json
{
  "cliente": {
    "clienteId": "uuid",
    "nombre": "Juan Perez",
    "identificacion": "12345678",
    "email": "juan@example.com",
    "telefono": "+59891234567"
  },
  "cuentas": [
    {
      "cuentaId": "uuid",
      "numeroCuenta": "CCA-12345678",
      "tipo": "CHECKINGS",
      "saldoActual": 10000.00,
      "moneda": "USD",
      "estado": "ACTIVE"
    }
  ],
  "movimientos": [
    {
      "movimientoId": "uuid",
      "cuentaId": "uuid",
      "tipo": "CREDIT",
      "monto": 500.00,
      "saldoPosterior": 10500.00,
      "descripcion": "Depósito",
      "fecha": "2026-04-10T10:30:00Z"
    }
  ],
  "pagination": {
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8
  },
  "metadata": {
    "ultimaActualizacion": "2026-04-13T14:30:00Z",
    "lag": 2
  }
}
```

### Response 400 Bad Request

```json
{
  "error": "VALIDATION_ERROR",
  "message": "fechaInicio must be before fechaFin",
  "details": [
    {
      "field": "fechaFin",
      "message": "Must be >= fechaInicio"
    }
  ]
}
```

### Response 404 Not Found

```json
{
  "error": "NOT_FOUND",
  "message": "Cliente not found",
  "details": [
    {
      "field": "clienteId",
      "message": "No cliente with id {clienteId}"
    }
  ]
}
```

---

## Event Contracts (Input)

### ClienteCreadoEvent

```json
{
  "eventId": "uuid",
  "eventType": "ClienteCreadoEvent",
  "timestamp": "2026-04-13T10:00:00Z",
  "payload": {
    "clienteId": "uuid",
    "nombre": "Juan Perez",
    "identificacion": "12345678",
    "email": "juan@example.com",
    "telefono": "+59891234567"
  }
}
```

### CuentaCreadaEvent

```json
{
  "eventId": "uuid",
  "eventType": "CuentaCreadaEvent",
  "timestamp": "2026-04-13T10:05:00Z",
  "payload": {
    "cuentaId": "uuid",
    "clienteId": "uuid",
    "numeroCuenta": "CCA-12345678",
    "tipo": "CHECKINGS",
    "saldoInicial": 0.00,
    "moneda": "USD"
  }
}
```

### MovimientoRegistradoEvent

```json
{
  "eventId": "uuid",
  "eventType": "MovimientoRegistradoEvent",
  "timestamp": "2026-04-13T10:30:00Z",
  "payload": {
    "movimientoId": "uuid",
    "cuentaId": "uuid",
    "clienteId": "uuid",
    "tipo": "CREDIT",
    "monto": 500.00,
    "saldoPosterior": 500.00,
    "descripcion": "Depósito inicial",
    "fecha": "2026-04-13T10:30:00Z"
  }
}
```