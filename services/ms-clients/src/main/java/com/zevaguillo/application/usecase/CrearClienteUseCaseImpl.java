package com.zevaguillo.application.usecase;

import com.zevaguillo.application.exception.ClienteYaExisteException;
import com.zevaguillo.application.port.in.CrearClienteUseCase;
import com.zevaguillo.application.port.out.ClienteEventPort;
import com.zevaguillo.application.port.out.ClientePersistencePort;
import com.zevaguillo.domain.model.Cliente;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of CrearClienteUseCase.
 * Handles the business logic for creating a new Cliente.
 */
@Service
public class CrearClienteUseCaseImpl implements CrearClienteUseCase {
    
    private final ClientePersistencePort persistencePort;
    private final ClienteEventPort eventPort;
    private final PasswordEncoder passwordEncoder;
    
    public CrearClienteUseCaseImpl(ClientePersistencePort persistencePort, 
                                   ClienteEventPort eventPort,
                                   PasswordEncoder passwordEncoder) {
        this.persistencePort = persistencePort;
        this.eventPort = eventPort;
        this.passwordEncoder = passwordEncoder;
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

        if (cliente.getClienteId() == null || cliente.getClienteId().isBlank()) {
            throw new IllegalArgumentException("Cliente ID es requerido");
        }

        if (persistencePort.existsById(cliente.getClienteId())) {
            throw new ClienteYaExisteException("Ya existe un cliente con ID: " + cliente.getClienteId());
        }
        
        // Set default estado if not provided
        if (cliente.getEstado() == null) {
            cliente.setEstado("ACTIVE");
        }

        // Store password as BCrypt hash, never plaintext.
        if (cliente.getContrasena() == null || cliente.getContrasena().isBlank()) {
            throw new IllegalArgumentException("Contrasena es requerida");
        }
        cliente.setContrasena(passwordEncoder.encode(cliente.getContrasena()));
        
        // Save to persistence
        Cliente guardado = persistencePort.save(cliente);
        
        // Publish event
        eventPort.publicarClienteCreado(guardado);
        
        return guardado;
    }
}