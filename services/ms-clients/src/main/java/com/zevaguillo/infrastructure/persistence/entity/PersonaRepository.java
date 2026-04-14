package com.zevaguillo.infrastructure.persistence.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * PersonaRepository - JPA Repository for PersonaEntity
 */
@Repository
public interface PersonaRepository extends JpaRepository<PersonaEntity, String> {
}