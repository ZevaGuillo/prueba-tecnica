package com.zevaguillo.ms_accounts.application.usecase;

import com.zevaguillo.ms_accounts.application.port.in.ObtenerCuentaUseCase;
import com.zevaguillo.ms_accounts.application.port.out.CuentaPersistencePort;
import com.zevaguillo.ms_accounts.domain.model.Cuenta;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ObtenerCuentaUseCaseImpl implements ObtenerCuentaUseCase {

    private final CuentaPersistencePort persistencePort;

    public ObtenerCuentaUseCaseImpl(CuentaPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    @Override
    public Cuenta obtenerPorId(String cuentaId) {
        return persistencePort.findById(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + cuentaId));
    }

    @Override
    public List<Cuenta> obtenerPorCliente(String clienteId) {
        return persistencePort.findByClienteId(clienteId);
    }

    @Override
    public List<Cuenta> obtenerTodas() {
        return persistencePort.findAll();
    }
}