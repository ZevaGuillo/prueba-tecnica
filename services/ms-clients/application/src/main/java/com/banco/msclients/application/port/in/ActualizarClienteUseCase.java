package com.banco.msclients.application.port.in;

import com.banco.msclients.domain.model.Cliente;

/**
 * Input Port - Update Cliente Use Case
 * Interface for controllers to update existing clientes.
 */
public interface ActualizarClienteUseCase {
    
    /**
     * Executes the update cliente use case.
     * @param clienteId the cliente ID to update
     * @param cliente the cliente data with updates
     * @return the updated cliente
     * @throws IllegalArgumentException if cliente not found
     */
    Cliente ejecutar(String clienteId, Cliente cliente);
}