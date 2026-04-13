package com.zevaguillo.ms_accounts.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;

public class MovimientoRequest {

    @NotNull(groups = Create.class, message = "movimientoId es requerido")
    @Null(groups = Update.class, message = "movimientoId no debe proporcionarse en actualización")
    private String movimientoId;

    @NotBlank(groups = Create.class, message = "cuentaId es requerido")
    private String cuentaId;

    @NotBlank(groups = Create.class, message = "tipoMovimiento es requerido")
    private String tipoMovimiento;

    @NotNull(groups = Create.class, message = "valor es requerido")
    @Positive(groups = Create.class, message = "valor debe ser positivo")
    private Double valor;

    @Null(groups = Update.class, message = "transactionId no debe proporcionarse en actualización")
    private String transactionId;

    public interface Create {}
    public interface Update {}

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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}