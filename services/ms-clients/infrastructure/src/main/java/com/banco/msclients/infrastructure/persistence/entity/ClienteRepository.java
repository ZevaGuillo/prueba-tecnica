package com.banco.msclients.infrastructure.persistence.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ClienteRepository - JPA Repository for ClienteEntity
 */
@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, String> {
    
    List<ClienteEntity> findByEstado(String estado);
}