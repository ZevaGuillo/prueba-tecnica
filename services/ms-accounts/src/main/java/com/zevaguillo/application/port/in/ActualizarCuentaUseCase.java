package com.zevaguillo.application.port.in;

import com.zevaguillo.domain.model.Cuenta;

/**
 * Input Port - Update Cuenta Use Case
 */
public interface ActualizarCuentaUseCase {
    Cuenta ejecutar(Cuenta cuenta);
}