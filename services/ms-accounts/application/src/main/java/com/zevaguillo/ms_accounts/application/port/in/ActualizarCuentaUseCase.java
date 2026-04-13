package com.zevaguillo.ms_accounts.application.port.in;

import com.zevaguillo.ms_accounts.domain.model.Cuenta;

/**
 * Input Port - Update Cuenta Use Case
 */
public interface ActualizarCuentaUseCase {
    Cuenta ejecutar(Cuenta cuenta);
}