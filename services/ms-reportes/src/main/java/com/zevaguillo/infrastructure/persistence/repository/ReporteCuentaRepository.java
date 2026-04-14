package com.zevaguillo.infrastructure.persistence.repository;

import com.zevaguillo.infrastructure.persistence.entity.ReporteCuentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReporteCuentaRepository extends JpaRepository<ReporteCuentaEntity, UUID> {
    List<ReporteCuentaEntity> findByClienteId(String clienteId);
    Optional<ReporteCuentaEntity> findByCuentaId(String cuentaId);
}