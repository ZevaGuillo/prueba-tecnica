package com.zevaguillo.application.usecase;

import com.zevaguillo.application.port.in.CrearPersonaUseCase;
import com.zevaguillo.application.port.out.PersonaPersistencePort;
import com.zevaguillo.domain.model.Persona;
import org.springframework.stereotype.Service;

/**
 * Implementation of CrearPersonaUseCase.
 * Handles the business logic for creating a new Persona.
 */
@Service
public class CrearPersonaUseCaseImpl implements CrearPersonaUseCase {
    
    private final PersonaPersistencePort persistencePort;
    
    public CrearPersonaUseCaseImpl(PersonaPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }
    
    @Override
    public Persona ejecutar(Persona persona) {
        // Business validation - identification is required
        if (persona.getIdentificacion() == null || persona.getIdentificacion().isEmpty()) {
            throw new IllegalArgumentException("Identificacion es requerida");
        }
        
        // Business validation - nombre is required
        if (persona.getNombre() == null || persona.getNombre().isEmpty()) {
            throw new IllegalArgumentException("Nombre es requerido");
        }
        
        // Save to persistence
        return persistencePort.save(persona);
    }
}