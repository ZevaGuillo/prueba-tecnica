# Feature Specification: Account Report CQRS

**Feature Branch**: `feature/001-account-report-cqrs`  
**Created**: 2026-04-13  
**Status**: Draft  
**Input**: User description: "CREAR NUEVO MICROSERVICIO manteniendo el mismo estandar ya manejado en los demas servicios: Diseña una solución backend basada en arquitectura de microservicios para la generación de reportes de estado de cuenta utilizando un enfoque Event-Driven + CQRS, asegurando desacoplamiento, escalabilidad y resiliencia."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Generate Account Report (Priority: P1)

Como usuario del sistema bancario, necesito consultar un reporte de estado de cuenta filtrado por cliente y rango de fechas para visualizar el saldo disponible, movimientos y información de mis cuentas asociadas.

**Why this priority**: Este es el caso de uso principal que justifica la existencia del nuevo microservicio de reportes. Sin la capacidad de generar reportes, los clientes no pueden consultar su historial financiero.

**Independent Test**: Se puede probar completamente consultando el endpoint GET /reportes?clienteId={id}&fechaInicio={f1}&fechaFin={f2} y verificando que retorne información del cliente, cuentas asociadas, movimientos en el rango de fechas y saldo disponible por cada movimiento.

**Acceptance Scenarios**:

1. **Given** un cliente con cuentas activas, **When** consulta el reporte con rango de fechas válido, **Then** el sistema retorna información del cliente, lista de cuentas y movimientos en el período
2. **Given** un cliente sin cuentas, **When** consulta el reporte, **Then** el sistema retorna información del cliente con lista vacía de cuentas y movimientos
3. **Given** un rango de fechas sin movimientos, **When** consulta el reporte, **Then** el sistema retorna información del cliente y cuentas pero sin movimientos en el período

---

### User Story 2 - Real-Time Report Updates (Priority: P2)

Como usuario, quiero que mi reporte se actualice automáticamente cuando se registran nuevos movimientos en mis cuentas para tener información actualizada sin necesidad de volver a solicitar el reporte.

**Why this priority**: La consistencia eventual permite que el reporte se construya incrementalmente, reduciendo la carga en tiempo de consulta y mejorando el rendimiento general del sistema.

**Independent Test**: Se puede probar registrando un nuevo movimiento y verificando que aparezca en consultas subsecuentes del reporte.

**Acceptance Scenarios**:

1. **Given** un cliente con reporte existente, **When** se registra un nuevo movimiento en una de sus cuentas, **Then** el movimiento aparece en consultas posteriores del reporte
2. **Given** un cliente que consulta inmediatamente después de un movimiento, **Then** el sistema puede servir la versión más reciente del dato

---

### User Story 3 - High Volume Report Queries (Priority: P3)

Como sistema de atención al cliente, necesito poder generar múltiples reportes concurrentemente sin degradar el rendimiento para dar soporte a múltiples usuarios simultáneamente.

**Why this priority**: La arquitectura debe soportar alta carga de lecturas sin bloqueos, priorizando disponibilidad y rendimiento sobre consistencia inmediata.

**Independent Test**: Se puede probar generando múltiples solicitudes de reporte simultáneas y midiendo tiempos de respuesta.

**Acceptance Scenarios**:

1. **Given** múltiples solicitudes de reporte simultáneas, **When** se procesan concurrentemente, **Then** el sistema responde a todas sin tiempo de espera excesivo
2. **Given** una consulta durante mantenimiento de otros microservicios, **Then** el servicio de reportes permanece disponible con datos consistentes hasta el último evento procesado

---

### Edge Cases

- ¿Qué sucede cuando el clienteId no existe en el sistema?
- ¿Cómo maneja el sistema rangos de fechas inválidos (fechaInicio > fechaFin)?
- ¿Qué pasa cuando el cliente tiene cuentas en diferentes monedas?
- ¿Cómo maneja el sistema la eliminación de un cliente después de generado el reporte?
- ¿Qué sucede cuando hay un gap temporal entre eventos por fallos de red?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema debe exponer un endpoint GET /reportes que acepte parámetros clienteId, fechaInicio y fechaFin
- **FR-002**: El endpoint debe retornar información del cliente (nombre, identificación, datos de contacto)
- **FR-003**: El endpoint debe retornar todas las cuentas asociadas al cliente con su saldo actual
- **FR-004**: El endpoint debe retornar historial de movimientos en el rango de fechas especificado
- **FR-005**: Cada movimiento debe incluir el saldo disponible después de la transacción
- **FR-006**: El sistema debe construir el reporte a partir de eventos asynchronously procesados
- **FR-007**: El sistema debe garantizar idempotencia en el procesamiento de eventos (eventId único)
- **FR-008**: El sistema debe mantener un modelo de lectura denormalizado optimizado para consultas
- **FR-009**: El sistema debe soportar paginación en el listado de movimientos
- **FR-010**: El sistema debe ordenar movimientos por fecha descendente

### Key Entities *(include if feature involves data)*

- **Reporte**: Agregado principal que contiene información del cliente, cuentas y movimientos para un cliente y rango de fechas específico
- **ReporteCliente**: Snapshot de datos del cliente al momento de generar el reporte
- **ReporteCuenta**: Datos de cada cuenta asociada al cliente (número, tipo, saldo)
- **ReporteMovimiento**: Historial de transacciones con saldo posterior

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El tiempo de respuesta del endpoint de reportes debe ser inferior a 500ms para el percentil 95 bajo carga normal
- **SC-002**: El sistema debe soportar al menos 1000 consultas concurrentes de reportes sin degradación
- **SC-003**: La consistencia eventual no debe exceder 5 segundos entre el registro de un movimiento y su aparición en reportes
- **SC-004**: El sistema debe estar disponible incluso cuando MS-Clientes o MS-Cuentas no estén accesibles (modo degradado)

## Assumptions

- Los microservicios MS-Clientes y MS-Cuentas existent ya publican los eventos necesarios (ClienteCreadoEvent, CuentaCreadaEvent, MovimientoRegistradoEvent)

## Clarifications

### Session 2026-04-13

- Q: ¿Cómo debe manejar el sistema la autorización para que un cliente solo pueda ver sus propios reportes? → A: Cualquiera puede consultar cualquier clienteId (sin restricciones de autenticación por el momento)
- Q: ¿Qué datos del cliente debe incluir el reporte? → A: Identificación + datos de contacto + cuentas con saldos + detalle de movimientos
- Q: ¿Qué formato de paginación prefiere? → A: page + size con rango de fechas en parámetros del endpoint
- Q: Estrategia de consistencia eventual → A: Outbox pattern para garantizar publicación de eventos
- Kafka está disponible como broker de eventos para la comunicación asíncrona
- Existe una base de datos separada para el modelo de lectura del microservicio de reportes
- El formato de fechas utilizado es ISO 8601 (YYYY-MM-DD)
- El clienteId corresponde al identificador único del cliente en el sistema