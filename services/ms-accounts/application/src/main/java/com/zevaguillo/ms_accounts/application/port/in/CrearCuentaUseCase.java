package com.zevaguillo.ms_accounts.application.port.in;

import com.zevaguillo.ms_accounts.domain.model.Cuenta;

/**
 * Input Port - Create Cuenta Use Case
 */
public interface CrearCuentaUseCase {
    Cuenta ejecutar(Cuenta cuenta);
}