package com.zevaguillo.ms_accounts.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "cuentas", schema = "msaccounts_schema")
public class CuentaEntity {

    @Id
    @Column(name = "cuenta_id")
    private String cuentaId;

    @Column(name = "numero_cuenta", unique = true, nullable = false)
    private String numeroCuenta;

    @Column(name = "tipo_cuenta")
    private String tipoCuenta;

    @Column(name = "saldo")
    private Double saldo;

    @Column(name = "estado")
    private String estado;

    @Column(name = "cliente_id")
    private String clienteId;

    @Version
    private Integer version;

    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovimientoEntity> movimientos;

    public String getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(String cuentaId) {
        this.cuentaId = cuentaId;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<MovimientoEntity> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoEntity> movimientos) {
        this.movimientos = movimientos;
    }
}