package com.zevaguillo.application.port.in;

import com.zevaguillo.domain.model.Cuenta;

/**
 * Input Port - Delete Cuenta Use Case (Soft Delete)
 */
public interface EliminarCuentaUseCase {
    void ejecutar(String cuentaId);
}