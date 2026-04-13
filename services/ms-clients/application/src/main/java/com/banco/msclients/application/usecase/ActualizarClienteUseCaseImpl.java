package com.banco.msclients.application.usecase;

import com.banco.msclients.application.port.in.ActualizarClienteUseCase;
import com.banco.msclients.application.port.out.ClientePersistencePort;
import com.banco.msclients.domain.model.Cliente;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of ActualizarClienteUseCase.
 * Handles the business logic for updating an existing Cliente.
 */
@Service
public class ActualizarClienteUseCaseImpl implements ActualizarClienteUseCase {
    
    private final ClientePersistencePort persistencePort;
    
    public ActualizarClienteUseCaseImpl(ClientePersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }
    
    @Override
    public Cliente ejecutar(String clienteId, Cliente cliente) {
        // Validate clienteId
        if (clienteId == null || clienteId.isEmpty()) {
            throw new IllegalArgumentException("Cliente ID es requerido");
        }
        
        // Check if exists
        Optional<Cliente> existing = persistencePort.findById(clienteId);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId);
        }
        
        // Update the ID
        cliente.setClienteId(clienteId);
        
        // Preserve the estado from existing if not provided
        if (cliente.getEstado() == null) {
            cliente.setEstado(existing.get().getEstado());
        }
        
        return persistencePort.save(cliente);
    }
}