package com.banco.msclients.application.usecase;

import com.banco.msclients.application.port.in.ObtenerClienteUseCase;
import com.banco.msclients.application.port.out.ClientePersistencePort;
import com.banco.msclients.domain.model.Cliente;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of ObtenerClienteUseCase.
 * Handles the business logic for querying Cliente entities.
 */
@Service
public class ObtenerClienteUseCaseImpl implements ObtenerClienteUseCase {
    
    private final ClientePersistencePort persistencePort;
    
    public ObtenerClienteUseCaseImpl(ClientePersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }
    
    @Override
    public Optional<Cliente> porId(String clienteId) {
        if (clienteId == null || clienteId.isEmpty()) {
            throw new IllegalArgumentException("Cliente ID es requerido");
        }
        return persistencePort.findById(clienteId);
    }
    
    @Override
    public List<Cliente> todos() {
        return persistencePort.findAll();
    }
    
    @Override
    public List<Cliente> activos() {
        return persistencePort.findByEstado("ACTIVE");
    }
}