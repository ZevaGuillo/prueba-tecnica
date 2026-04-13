package com.zevaguillo.msreportes.application.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MovimientoRegistradoEvent {
    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private Payload payload;

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Payload getPayload() { return payload; }
    public void setPayload(Payload payload) { this.payload = payload; }

    public static class Payload {
        private UUID movimientoId;
        private UUID cuentaId;
        private UUID clienteId;
        private String tipo;
        private BigDecimal monto;
        private BigDecimal saldoPosterior;
        private String descripcion;
        private LocalDateTime fecha;

        public UUID getMovimientoId() { return movimientoId; }
        public void setMovimientoId(UUID movimientoId) { this.movimientoId = movimientoId; }
        public UUID getCuentaId() { return cuentaId; }
        public void setCuentaId(UUID cuentaId) { this.cuentaId = cuentaId; }
        public UUID getClienteId() { return clienteId; }
        public void setClienteId(UUID clienteId) { this.clienteId = clienteId; }
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        public BigDecimal getMonto() { return monto; }
        public void setMonto(BigDecimal monto) { this.monto = monto; }
        public BigDecimal getSaldoPosterior() { return saldoPosterior; }
        public void setSaldoPosterior(BigDecimal saldoPosterior) { this.saldoPosterior = saldoPosterior; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public LocalDateTime getFecha() { return fecha; }
        public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    }
}