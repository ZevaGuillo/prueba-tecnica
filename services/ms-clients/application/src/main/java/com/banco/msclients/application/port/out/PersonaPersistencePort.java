package com.banco.msclients.application.port.out;

import com.banco.msclients.domain.model.Persona;
import java.util.List;
import java.util.Optional;

/**
 * Output Port - Persona Persistence
 * Interface for adapters to handle Persona persistence operations.
 * This is the "driven" port in Hexagonal Architecture.
 */
public interface PersonaPersistencePort {
    
    /**
     * Saves a persona (create or update).
     * @param persona the persona to save
     * @return the saved persona
     */
    Persona save(Persona persona);
    
    /**
     * Finds a persona by its identification.
     * @param identificacion the identification number
     * @return optional containing the persona if found
     */
    Optional<Persona> findById(String identificacion);
    
    /**
     * Finds all personas.
     * @return list of all personas
     */
    List<Persona> findAll();
    
    /**
     * Deletes a persona by its identification.
     * @param identificacion the identification to delete
     */
    void delete(String identificacion);
    
    /**
     * Checks if a persona exists by its identification.
     * @param identificacion the identification
     * @return true if exists
     */
    boolean existsById(String identificacion);
}