package com.zevaguillo.application.port.in;

import com.zevaguillo.domain.model.Cliente;

/**
 * Input Port - Create Cliente Use Case
 * Interface for controllers to create new clientes.
 */
public interface CrearClienteUseCase {
    
    /**
     * Executes the create cliente use case.
     * @param cliente the cliente to create
     * @return the created cliente
     * @throws IllegalArgumentException if required fields are missing
     */
    Cliente ejecutar(Cliente cliente);
}