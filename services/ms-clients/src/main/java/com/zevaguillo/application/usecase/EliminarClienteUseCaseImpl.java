package com.zevaguillo.application.usecase;

import com.zevaguillo.application.port.in.EliminarClienteUseCase;
import com.zevaguillo.application.port.out.ClientePersistencePort;
import com.zevaguillo.domain.model.Cliente;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of EliminarClienteUseCase.
 * Handles the business logic for deleting (logical) a Cliente.
 * This performs a logical delete (sets estado to INACTIVE).
 */
@Service
public class EliminarClienteUseCaseImpl implements EliminarClienteUseCase {
    
    private final ClientePersistencePort persistencePort;
    
    public EliminarClienteUseCaseImpl(ClientePersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }
    
    @Override
    public void ejecutar(String clienteId) {
        // Validate clienteId
        if (clienteId == null || clienteId.isEmpty()) {
            throw new IllegalArgumentException("Cliente ID es requerido");
        }
        
        // Check if exists
        Optional<Cliente> existing = persistencePort.findById(clienteId);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId);
        }
        
        // Logical delete - set estado to INACTIVE
        Cliente cliente = existing.get();
        cliente.desactivar();
        
        persistencePort.save(cliente);
    }
}