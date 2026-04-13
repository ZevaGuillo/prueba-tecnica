package com.banco.msclients.infrastructure.persistence;

import com.banco.msclients.domain.model.Persona;
import com.banco.msclients.infrastructure.persistence.entity.PersonaEntity;

/**
 * Mapper utility for converting between Persona domain and entity objects.
 */
public final class PersonaMapper {
    
    private PersonaMapper() {
    }
    
    public static PersonaEntity toEntity(Persona persona) {
        if (persona == null) {
            return null;
        }
        PersonaEntity entity = new PersonaEntity();
        entity.setIdentificacion(persona.getIdentificacion());
        entity.setNombre(persona.getNombre());
        entity.setGenero(persona.getGenero());
        entity.setEdad(persona.getEdad());
        entity.setDireccion(persona.getDireccion());
        entity.setTelefono(persona.getTelefono());
        return entity;
    }
    
    public static Persona toDomain(PersonaEntity entity) {
        if (entity == null) {
            return null;
        }
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