package com.zevaguillo.ms_accounts.application.port.out;

import com.zevaguillo.ms_accounts.domain.model.Cuenta;
import java.util.List;
import java.util.Optional;

/**
 * Output Port - Cuenta Persistence
 */
public interface CuentaPersistencePort {
    Cuenta save(Cuenta cuenta);
    Optional<Cuenta> findById(String cuentaId);
    List<Cuenta> findAll();
    List<Cuenta> findByClienteId(String clienteId);
    List<Cuenta> findByEstado(String estado);
    boolean existsByNumeroCuenta(String numeroCuenta);
    boolean existsById(String cuentaId);
    void delete(String cuentaId);
}