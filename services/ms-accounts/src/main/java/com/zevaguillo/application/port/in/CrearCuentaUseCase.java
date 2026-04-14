package com.zevaguillo.application.port.in;

import com.zevaguillo.domain.model.Cuenta;

/**
 * Input Port - Create Cuenta Use Case
 */
public interface CrearCuentaUseCase {
    Cuenta ejecutar(Cuenta cuenta);
}