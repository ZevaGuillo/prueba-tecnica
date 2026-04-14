package com.zevaguillo.infrastructure.persistence.repository;

import com.zevaguillo.infrastructure.persistence.entity.ReporteClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReporteClienteRepository extends JpaRepository<ReporteClienteEntity, UUID> {
    List<ReporteClienteEntity> findByClienteId(String clienteId);
}