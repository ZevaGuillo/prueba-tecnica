package com.zevaguillo.ms_accounts.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos", schema = "msaccounts_schema")
public class MovimientoEntity {

    @Id
    @Column(name = "movimiento_id")
    private String movimientoId;

    @Column(name = "cuenta_id")
    private String cuentaId;

    @Column(name = "tipo_movimiento")
    private String tipoMovimiento;

    @Column(name = "valor")
    private Double valor;

    @Column(name = "saldo_resultante")
    private Double saldoResultante;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", insertable = false, updatable = false)
    private CuentaEntity cuenta;

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

    public CuentaEntity getCuenta() {
        return cuenta;
    }

    public void setCuenta(CuentaEntity cuenta) {
        this.cuenta = cuenta;
    }
}