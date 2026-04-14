package com.zevaguillo.application.port.in;

import com.zevaguillo.domain.model.Cuenta;
import java.util.List;

/**
 * Input Port - Get Cuenta Use Case
 */
public interface ObtenerCuentaUseCase {
    Cuenta obtenerPorId(String cuentaId);
    List<Cuenta> obtenerPorCliente(String clienteId);
    List<Cuenta> obtenerTodas();
}