package com.banco.msclients.infrastructure.persistence.adapter;

import com.banco.msclients.application.port.out.PersonaPersistencePort;
import com.banco.msclients.domain.model.Persona;
import com.banco.msclients.infrastructure.persistence.entity.PersonaEntity;
import com.banco.msclients.infrastructure.persistence.entity.PersonaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PersonaJpaAdapter - JPA implementation of PersonaPersistencePort
 * Adapter that transforms between domain and JPA entities.
 */
@Repository
public class PersonaJpaAdapter implements PersonaPersistencePort {
    
    private final PersonaRepository repository;
    
    public PersonaJpaAdapter(PersonaRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public Persona save(Persona persona) {
        PersonaEntity entity = toEntity(persona);
        PersonaEntity saved = repository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public Optional<Persona> findById(String identificacion) {
        return repository.findById(identificacion).map(this::toDomain);
    }
    
    @Override
    public List<Persona> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }
    
    @Override
    public void delete(String identificacion) {
        repository.deleteById(identificacion);
    }
    
    @Override
    public boolean existsById(String identificacion) {
        return repository.existsById(identificacion);
    }
    
    // Mapper methods
    private PersonaEntity toEntity(Persona persona) {
        PersonaEntity entity = new PersonaEntity();
        entity.setIdentificacion(persona.getIdentificacion());
        entity.setNombre(persona.getNombre());
        entity.setGenero(persona.getGenero());
        entity.setEdad(persona.getEdad());
        entity.setDireccion(persona.getDireccion());
        entity.setTelefono(persona.getTelefono());
        return entity;
    }
    
    private Persona toDomain(PersonaEntity entity) {
        Persona persona = new Persona();
        persona.setIdentificacion(entity.getIdentificacion());
        persona.setNombre(entity.getNombre());
        persona.setGenero(entity.getGenero());
        persona.setEdad(entity.getEdad());
        persona.setDireccion(entity.getDireccion());
        persona.setTelefono(entity.getTelefono());
        return persona;
    }
}