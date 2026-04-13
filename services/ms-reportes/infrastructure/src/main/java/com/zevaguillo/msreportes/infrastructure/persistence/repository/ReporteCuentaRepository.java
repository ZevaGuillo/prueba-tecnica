package com.zevaguillo.msreportes.infrastructure.persistence.repository;

import com.zevaguillo.msreportes.infrastructure.persistence.entity.ReporteCuentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReporteCuentaRepository extends JpaRepository<ReporteCuentaEntity, UUID> {
    List<ReporteCuentaEntity> findByClienteId(UUID clienteId);
}