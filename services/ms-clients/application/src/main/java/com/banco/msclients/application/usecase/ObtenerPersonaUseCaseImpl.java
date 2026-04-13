package com.banco.msclients.application.usecase;

import com.banco.msclients.application.port.in.ObtenerPersonaUseCase;
import com.banco.msclients.application.port.out.PersonaPersistencePort;
import com.banco.msclients.domain.model.Persona;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of ObtenerPersonaUseCase.
 * Handles the business logic for querying Persona entities.
 */
@Service
public class ObtenerPersonaUseCaseImpl implements ObtenerPersonaUseCase {
    
    private final PersonaPersistencePort persistencePort;
    
    public ObtenerPersonaUseCaseImpl(PersonaPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }
    
    @Override
    public Optional<Persona> porId(String identificacion) {
        if (identificacion == null || identificacion.isEmpty()) {
            throw new IllegalArgumentException("Identificacion es requerida");
        }
        return persistencePort.findById(identificacion);
    }
    
    @Override
    public List<Persona> todas() {
        return persistencePort.findAll();
    }
}