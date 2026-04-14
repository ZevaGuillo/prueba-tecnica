package com.zevaguillo.application.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        private String movimientoId;
        private String cuentaId;
        private String clienteId;
        private String tipo;
        private BigDecimal monto;
        private BigDecimal saldoPosterior;
        private String descripcion;
        private LocalDateTime fecha;

        public String getMovimientoId() { return movimientoId; }
        public void setMovimientoId(String movimientoId) { this.movimientoId = movimientoId; }
        public String getCuentaId() { return cuentaId; }
        public void setCuentaId(String cuentaId) { this.cuentaId = cuentaId; }
        public String getClienteId() { return clienteId; }
        public void setClienteId(String clienteId) { this.clienteId = clienteId; }
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