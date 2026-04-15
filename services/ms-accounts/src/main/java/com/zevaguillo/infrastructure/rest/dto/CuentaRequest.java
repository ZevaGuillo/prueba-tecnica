package com.zevaguillo.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public class CuentaRequest {

    @NotNull(groups = Create.class, message = "cuentaId es requerido")
    @Null(groups = Update.class, message = "cuentaId no debe proporcionarse en actualización")
    private String cuentaId;

    @NotBlank(groups = Create.class, message = "numeroCuenta es requerido")
    private String numeroCuenta;

    @NotBlank(groups = Create.class, message = "tipoCuenta es requerido")
    private String tipoCuenta;

    private Double saldo;

    @NotBlank(groups = Create.class, message = "clienteId es requerido")
    private String clienteId;

    @NotBlank(groups = Create.class, message = "estado es requerido")
    private String estado;

    public interface Create { }

    public interface Update { }

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

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}