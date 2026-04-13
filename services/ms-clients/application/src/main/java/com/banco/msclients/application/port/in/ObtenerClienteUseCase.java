package com.banco.msclients.application.port.in;

import com.banco.msclients.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

/**
 * Input Port - Get Cliente Use Case
 * Interface for controllers to query clientes.
 */
public interface ObtenerClienteUseCase {
    
    /**
     * Gets a cliente by its ID.
     * @param clienteId the cliente ID
     * @return optional containing the cliente if found
     */
    Optional<Cliente> porId(String clienteId);
    
    /**
     * Gets all clientes.
     * @return list of all clientes
     */
    List<Cliente> todos();
    
    /**
     * Gets all active clientes.
     * @return list of active clientes
     */
    List<Cliente> activos();
}