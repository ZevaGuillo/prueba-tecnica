package com.zevaguillo.infrastructure.persistence.repository;

import com.zevaguillo.infrastructure.persistence.entity.ReporteMovimientoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface ReporteMovimientoRepository extends JpaRepository<ReporteMovimientoEntity, UUID> {

    Page<ReporteMovimientoEntity> findByClienteIdAndFechaBetweenOrderByFechaDesc(
            String clienteId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable);
}