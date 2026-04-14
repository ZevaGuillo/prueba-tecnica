package com.zevaguillo.domain.repository;

import com.zevaguillo.domain.model.Persona;
import java.util.List;
import java.util.Optional;

public interface PersonaRepository {
    Persona save(Persona persona);
    Optional<Persona> findById(String identificacion);
    List<Persona> findAll();
    void delete(String identificacion);
    boolean existsById(String identificacion);
}