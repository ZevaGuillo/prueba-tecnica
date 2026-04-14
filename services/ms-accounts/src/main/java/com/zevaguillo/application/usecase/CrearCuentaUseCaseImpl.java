package com.zevaguillo.application.usecase;

import com.zevaguillo.application.exception.CuentaAlreadyExistsException;
import com.zevaguillo.application.port.in.CrearCuentaUseCase;
import com.zevaguillo.application.port.out.CuentaEventPort;
import com.zevaguillo.application.port.out.CuentaPersistencePort;
import com.zevaguillo.domain.model.Cuenta;
import org.springframework.stereotype.Service;

@Service
public class CrearCuentaUseCaseImpl implements CrearCuentaUseCase {

    private final CuentaPersistencePort persistencePort;
    private final CuentaEventPort eventPort;

    public CrearCuentaUseCaseImpl(CuentaPersistencePort persistencePort, CuentaEventPort eventPort) {
        this.persistencePort = persistencePort;
        this.eventPort = eventPort;
    }

    @Override
    public Cuenta ejecutar(Cuenta cuenta) {
        if (cuenta.getNumeroCuenta() == null || cuenta.getNumeroCuenta().isBlank()) {
            throw new IllegalArgumentException("Número de cuenta es requerido");
        }
        if (cuenta.getClienteId() == null || cuenta.getClienteId().isBlank()) {
            throw new IllegalArgumentException("Cliente ID es requerido");
        }
        if (persistencePort.existsByNumeroCuenta(cuenta.getNumeroCuenta())) {
            throw new CuentaAlreadyExistsException("Número de cuenta ya existe: " + cuenta.getNumeroCuenta());
        }
        if (persistencePort.existsById(cuenta.getCuentaId())) {
            throw new CuentaAlreadyExistsException("Cuenta con ID " + cuenta.getCuentaId() + " ya existe");
        }

        if (cuenta.getEstado() == null) {
            cuenta.setEstado("ACTIVE");
        }
        if (cuenta.getSaldo() == null) {
            cuenta.setSaldo(0.0);
        }

        Cuenta guardado = persistencePort.save(cuenta);
        eventPort.publicarCuentaCreada(guardado);

        return guardado;
    }
}