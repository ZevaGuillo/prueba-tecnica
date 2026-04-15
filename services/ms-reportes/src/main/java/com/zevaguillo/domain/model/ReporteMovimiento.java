package com.zevaguillo.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ReporteMovimiento {
    private UUID id;
    private String movimientoId;
    private String cuentaId;
    private String clienteId;
    private String tipo;
    private BigDecimal monto;
    private BigDecimal saldoPosterior;
    private String descripcion;
    private LocalDateTime fecha;
    private LocalDateTime fechaProcesamiento;

    public ReporteMovimiento() {
    }

    public ReporteMovimiento(String movimientoId, String cuentaId, String clienteId, String tipo,
                             BigDecimal monto, BigDecimal saldoPosterior, String descripcion, LocalDateTime fecha) {
        this.id = UUID.randomUUID();
        this.movimientoId = movimientoId;
        this.cuentaId = cuentaId;
        this.clienteId = clienteId;
        this.tipo = tipo;
        this.monto = monto;
        this.saldoPosterior = saldoPosterior;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.fechaProcesamiento = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMovimientoId() {
        return movimientoId;
    }

    public void setMovimientoId(String movimientoId) {
        this.movimientoId = movimientoId;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public BigDecimal getSaldoPosterior() {
        return saldoPosterior;
    }

    public void setSaldoPosterior(BigDecimal saldoPosterior) {
        this.saldoPosterior = saldoPosterior;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getFechaProcesamiento() {
        return fechaProcesamiento;
    }

    public void setFechaProcesamiento(LocalDateTime fechaProcesamiento) {
        this.fechaProcesamiento = fechaProcesamiento;
    }
}
