package com.zevaguillo.ms_accounts.application.port.in;

import com.zevaguillo.ms_accounts.domain.model.Cuenta;

/**
 * Input Port - Delete Cuenta Use Case (Soft Delete)
 */
public interface EliminarCuentaUseCase {
    void ejecutar(String cuentaId);
}