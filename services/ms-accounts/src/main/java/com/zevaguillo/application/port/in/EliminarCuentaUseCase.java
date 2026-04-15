package com.zevaguillo.application.port.in;

/**
 * Input Port - Delete Cuenta Use Case (Soft Delete)
 */
public interface EliminarCuentaUseCase {
    void ejecutar(String cuentaId);
}