package com.zevaguillo.ms_accounts.application.port.in;

import com.zevaguillo.ms_accounts.domain.model.Movimiento;

/**
 * Input Port - Registrar Movimiento Use Case (CRÍTICO - lógica de negocio principal)
 */
public interface RegistrarMovimientoUseCase {
    Movimiento ejecutar(Movimiento movimiento, String transactionId);
}