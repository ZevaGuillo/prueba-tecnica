package com.zevaguillo.domain.model;

/**
 * Cuenta - Domain Entity (PURE POJO - No framework annotations)
 * Represents a bank account.
 */
public class Cuenta {

    private String cuentaId;
    private String numeroCuenta;
    private String tipoCuenta;  // AHORRO, CORRIENTE
    private Double saldo;
    private String estado;      // ACTIVE, INACTIVE
    private String clienteId;
    private Integer version;    // Optimistic locking

    public Cuenta() {
    }

    public Cuenta(String cuentaId, String numeroCuenta, String tipoCuenta,
                  Double saldo, String estado, String clienteId) {
        this.cuentaId = cuentaId;
        this.numeroCuenta = numeroCuenta;
        this.tipoCuenta = tipoCuenta;
        this.saldo = saldo;
        this.estado = estado;
        this.clienteId = clienteId;
    }

    public static Cuenta crear(String cuentaId, String numeroCuenta, String tipoCuenta,
                               Double saldoInicial, String clienteId) {
        Cuenta cuenta = new Cuenta();
        cuenta.setCuentaId(cuentaId);
        cuenta.setNumeroCuenta(numeroCuenta);
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setSaldo(saldoInicial);
        cuenta.setEstado("ACTIVE");
        cuenta.setClienteId(clienteId);
        cuenta.setVersion(0);
        return cuenta;
    }

    public boolean isActiva() {
        return "ACTIVE".equals(this.estado);
    }

    public void desactivar() {
        this.estado = "INACTIVE";
    }

    public void activar() {
        this.estado = "ACTIVE";
    }

    public boolean tieneSaldo() {
        return this.saldo != null && this.saldo > 0;
    }

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
}