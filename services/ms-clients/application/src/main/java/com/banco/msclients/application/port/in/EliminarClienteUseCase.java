package com.banco.msclients.application.port.in;

/**
 * Input Port - Delete Cliente Use Case
 * Interface for controllers to delete clientes.
 */
public interface EliminarClienteUseCase {
    
    /**
     * Executes the delete cliente use case (logical delete - sets INACTIVE).
     * @param clienteId the cliente ID to delete
     * @throws IllegalArgumentException if cliente not found
     */
    void ejecutar(String clienteId);
}