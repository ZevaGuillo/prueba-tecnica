package com.zevaguillo.application.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CuentaCreadaEvent {
    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private Payload payload;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public static class Payload {
        private String cuentaId;
        private String clienteId;
        private String numeroCuenta;
        private String tipo;
        private BigDecimal saldoInicial;
        private String moneda;

        public String getCuentaId() {
            return cuentaId;
        }

        public void setCuentaId(String cuentaId) {
            this.cuentaId = cuentaId;
        }

        public String getClienteId() {
            return clienteId;
        }

        public void setClienteId(String clienteId) {
            this.clienteId = clienteId;
        }

        public String getNumeroCuenta() {
            return numeroCuenta;
        }

        public void setNumeroCuenta(String numeroCuenta) {
            this.numeroCuenta = numeroCuenta;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public BigDecimal getSaldoInicial() {
            return saldoInicial;
        }

        public void setSaldoInicial(BigDecimal saldoInicial) {
            this.saldoInicial = saldoInicial;
        }

        public String getMoneda() {
            return moneda;
        }

        public void setMoneda(String moneda) {
            this.moneda = moneda;
        }
    }
}
