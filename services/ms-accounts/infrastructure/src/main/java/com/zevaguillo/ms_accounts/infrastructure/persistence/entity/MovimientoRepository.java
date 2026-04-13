package com.zevaguillo.ms_accounts.infrastructure.persistence.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovimientoRepository extends JpaRepository<MovimientoEntity, String> {
    List<MovimientoEntity> findByCuentaId(String cuentaId);
    List<MovimientoEntity> findByCuentaIdAndFechaBetween(String cuentaId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    Optional<MovimientoEntity> findByTransactionId(String transactionId);
    boolean existsByTransactionId(String transactionId);
}