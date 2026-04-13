package com.banco.msclients.application.port.in;

import com.banco.msclients.domain.model.Persona;

/**
 * Input Port - Create Persona Use Case
 * Interface for controllers to create new personas.
 */
public interface CrearPersonaUseCase {
    
    /**
     * Executes the create persona use case.
     * @param persona the persona to create
     * @return the created persona
     * @throws IllegalArgumentException if required fields are missing
     */
    Persona ejecutar(Persona persona);
}