package com.zevaguillo.ms_accounts.application.usecase;

import com.zevaguillo.ms_accounts.application.port.in.ActualizarCuentaUseCase;
import com.zevaguillo.ms_accounts.application.port.out.CuentaEventPort;
import com.zevaguillo.ms_accounts.application.port.out.CuentaPersistencePort;
import com.zevaguillo.ms_accounts.domain.model.Cuenta;
import org.springframework.stereotype.Service;

@Service
public class ActualizarCuentaUseCaseImpl implements ActualizarCuentaUseCase {

    private final CuentaPersistencePort persistencePort;
    private final CuentaEventPort eventPort;

    public ActualizarCuentaUseCaseImpl(CuentaPersistencePort persistencePort, CuentaEventPort eventPort) {
        this.persistencePort = persistencePort;
        this.eventPort = eventPort;
    }

    @Override
    public Cuenta ejecutar(Cuenta cuenta) {
        if (cuenta.getCuentaId() == null) {
            throw new IllegalArgumentException("Cuenta ID es requerido");
        }
        if (!persistencePort.existsById(cuenta.getCuentaId())) {
            throw new IllegalArgumentException("Cuenta no encontrada: " + cuenta.getCuentaId());
        }

        Cuenta existente = persistencePort.findById(cuenta.getCuentaId()).get();
        
        if (cuenta.getEstado() != null) {
            existente.setEstado(cuenta.getEstado());
        }

        Cuenta guardado = persistencePort.save(existente);
        eventPort.publicarCuentaActualizada(guardado);

        return guardado;
    }
}