package com.zevaguillo.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ReporteCuenta {
    private UUID id;
    private String cuentaId;
    private String clienteId;
    private String numeroCuenta;
    private String tipo;
    private BigDecimal saldoActual;
    private String moneda;
    private String estado;
    private LocalDateTime fechaCreacion;

    public ReporteCuenta() {
    }

    public ReporteCuenta(String cuentaId, String clienteId, String numeroCuenta, String tipo, BigDecimal saldoInicial,
                         String moneda) {
        this.id = UUID.randomUUID();
        this.cuentaId = cuentaId;
        this.clienteId = clienteId;
        this.numeroCuenta = numeroCuenta;
        this.tipo = tipo;
        this.saldoActual = saldoInicial;
        this.moneda = moneda;
        this.estado = "ACTIVE";
        this.fechaCreacion = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public BigDecimal getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(BigDecimal saldoActual) {
        this.saldoActual = saldoActual;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
