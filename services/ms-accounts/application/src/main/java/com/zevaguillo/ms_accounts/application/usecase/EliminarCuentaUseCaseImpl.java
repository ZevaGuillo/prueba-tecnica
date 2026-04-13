package com.zevaguillo.ms_accounts.application.usecase;

import com.zevaguillo.ms_accounts.application.port.in.EliminarCuentaUseCase;
import com.zevaguillo.ms_accounts.application.port.out.CuentaPersistencePort;
import com.zevaguillo.ms_accounts.domain.model.Cuenta;
import org.springframework.stereotype.Service;

@Service
public class EliminarCuentaUseCaseImpl implements EliminarCuentaUseCase {

    private final CuentaPersistencePort persistencePort;

    public EliminarCuentaUseCaseImpl(CuentaPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    @Override
    public void ejecutar(String cuentaId) {
        if (!persistencePort.existsById(cuentaId)) {
            throw new IllegalArgumentException("Cuenta no encontrada: " + cuentaId);
        }

        Cuenta cuenta = persistencePort.findById(cuentaId).get();
        
        if (cuenta.tieneSaldo()) {
            throw new IllegalStateException("No se puede eliminar cuenta con saldo activo");
        }

        cuenta.setEstado("INACTIVE");
        persistencePort.save(cuenta);
    }
}