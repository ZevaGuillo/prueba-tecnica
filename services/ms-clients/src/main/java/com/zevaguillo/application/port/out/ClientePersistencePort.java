package com.zevaguillo.application.port.out;

import com.zevaguillo.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

/**
 * Output Port - Cliente Persistence
 * Interface for adapters to handle Cliente persistence operations.
 * This is the "driven" port in Hexagonal Architecture.
 */
public interface ClientePersistencePort {
    
    /**
     * Saves a cliente (create or update).
     * @param cliente the cliente to save
     * @return the saved cliente
     */
    Cliente save(Cliente cliente);
    
    /**
     * Finds a cliente by its ID.
     * @param clienteId the cliente ID
     * @return optional containing the cliente if found
     */
    Optional<Cliente> findById(String clienteId);
    
    /**
     * Finds all clientes.
     * @return list of all clientes
     */
    List<Cliente> findAll();
    
    /**
     * Finds clientes by their estado.
     * @param estado the estado (ACTIVE, INACTIVE)
     * @return list of clientes with the specified estado
     */
    List<Cliente> findByEstado(String estado);
    
    /**
     * Deletes a cliente by its ID.
     * @param clienteId the cliente ID to delete
     */
    void delete(String clienteId);
    
    /**
     * Checks if a cliente exists by its ID.
     * @param clienteId the cliente ID
     * @return true if exists
     */
    boolean existsById(String clienteId);
}