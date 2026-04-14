package com.zevaguillo.application.usecase;

import com.zevaguillo.application.port.in.ActualizarClienteUseCase;
import com.zevaguillo.application.port.out.ClientePersistencePort;
import com.zevaguillo.domain.model.Cliente;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of ActualizarClienteUseCase.
 * Handles the business logic for updating an existing Cliente.
 */
@Service
public class ActualizarClienteUseCaseImpl implements ActualizarClienteUseCase {
    
    private final ClientePersistencePort persistencePort;
    private final PasswordEncoder passwordEncoder;
    
    public ActualizarClienteUseCaseImpl(ClientePersistencePort persistencePort,
                                        PasswordEncoder passwordEncoder) {
        this.persistencePort = persistencePort;
        this.passwordEncoder = passwordEncoder;
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

        // Preserve existing hash if password is not provided on update.
        if (cliente.getContrasena() == null || cliente.getContrasena().isBlank()) {
            cliente.setContrasena(existing.get().getContrasena());
        } else {
            cliente.setContrasena(passwordEncoder.encode(cliente.getContrasena()));
        }
        
        return persistencePort.save(cliente);
    }
}