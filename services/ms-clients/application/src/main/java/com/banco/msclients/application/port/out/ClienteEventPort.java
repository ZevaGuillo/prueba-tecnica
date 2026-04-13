package com.banco.msclients.application.port.out;

import com.banco.msclients.domain.model.Cliente;

/**
 * Output Port - Cliente Event Publishing
 * Interface for adapters to publish Cliente domain events (e.g., to Kafka).
 * This is the "driven" port in Hexagonal Architecture.
 */
public interface ClienteEventPort {
    
    /**
     * Publishes a ClienteCreated event.
     * @param cliente the created cliente
     */
    void publicarClienteCreado(Cliente cliente);
}