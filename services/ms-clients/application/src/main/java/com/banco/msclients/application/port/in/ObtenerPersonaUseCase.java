package com.banco.msclients.application.port.in;

import com.banco.msclients.domain.model.Persona;
import java.util.List;
import java.util.Optional;

/**
 * Input Port - Get Persona Use Case
 * Interface for controllers to query personas.
 */
public interface ObtenerPersonaUseCase {
    
    /**
     * Gets a persona by its identification.
     * @param identificacion the identification number
     * @return optional containing the persona if found
     */
    Optional<Persona> porId(String identificacion);
    
    /**
     * Gets all personas.
     * @return list of all personas
     */
    List<Persona> todas();
}