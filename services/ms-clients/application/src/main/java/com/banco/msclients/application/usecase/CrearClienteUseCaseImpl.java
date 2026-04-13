package com.banco.msclients.application.usecase;

import com.banco.msclients.application.port.in.CrearClienteUseCase;
import com.banco.msclients.application.port.out.ClienteEventPort;
import com.banco.msclients.application.port.out.ClientePersistencePort;
import com.banco.msclients.domain.model.Cliente;
import org.springframework.stereotype.Service;

/**
 * Implementation of CrearClienteUseCase.
 * Handles the business logic for creating a new Cliente.
 */
@Service
public class CrearClienteUseCaseImpl implements CrearClienteUseCase {
    
    private final ClientePersistencePort persistencePort;
    private final ClienteEventPort eventPort;
    
    public CrearClienteUseCaseImpl(ClientePersistencePort persistencePort, 
                                   ClienteEventPort eventPort) {
        this.persistencePort = persistencePort;
        this.eventPort = eventPort;
    }
    
    @Override
    public Cliente ejecutar(Cliente cliente) {
        // Business validation - identification is required
        if (cliente.getIdentificacion() == null || cliente.getIdentificacion().isEmpty()) {
            throw new IllegalArgumentException("Identificacion es requerida");
        }
        
        // Business validation - nombre is required
        if (cliente.getNombre() == null || cliente.getNombre().isEmpty()) {
            throw new IllegalArgumentException("Nombre es requerido");
        }
        
        // Set default estado if not provided
        if (cliente.getEstado() == null) {
            cliente.setEstado("ACTIVE");
        }
        
        // Save to persistence
        Cliente guardado = persistencePort.save(cliente);
        
        // Publish event
        eventPort.publicarClienteCreado(guardado);
        
        return guardado;
    }
}