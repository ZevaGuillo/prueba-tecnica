package com.zevaguillo.domain.model;

import java.time.LocalDateTime;

/**
 * Movimiento - Domain Entity (PURE POJO - No framework annotations)
 * Represents a transaction (deposit/withdrawal).
 */
public class Movimiento {

    private String movimientoId;
    private String cuentaId;
    private String tipoMovimiento;  // DEPOSITO, RETIRO
    private Double valor;
    private Double saldoResultante;
    private LocalDateTime fecha;
    private String transactionId;  // For idempotency

    public Movimiento() {
        this.fecha = LocalDateTime.now();
    }

    public Movimiento(String movimientoId, String cuentaId, String tipoMovimiento,
                      Double valor, Double saldoResultante) {
        this.movimientoId = movimientoId;
        this.cuentaId = cuentaId;
        this.tipoMovimiento = tipoMovimiento;
        this.valor = valor;
        this.saldoResultante = saldoResultante;
        this.fecha = LocalDateTime.now();
    }

    public static Movimiento crear(String movimientoId, String cuentaId, 
                                    String tipoMovimiento, Double valor, Double saldoResultante) {
        Movimiento mov = new Movimiento();
        mov.setMovimientoId(movimientoId);
        mov.setCuentaId(cuentaId);
        mov.setTipoMovimiento(tipoMovimiento);
        mov.setValor(valor);
        mov.setSaldoResultante(saldoResultante);
        mov.setFecha(LocalDateTime.now());
        return mov;
    }

    public boolean isDeposito() {
        return "DEPOSITO".equals(this.tipoMovimiento);
    }

    public boolean isRetiro() {
        return "RETIRO".equals(this.tipoMovimiento);
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

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Double getSaldoResultante() {
        return saldoResultante;
    }

    public void setSaldoResultante(Double saldoResultante) {
        this.saldoResultante = saldoResultante;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}