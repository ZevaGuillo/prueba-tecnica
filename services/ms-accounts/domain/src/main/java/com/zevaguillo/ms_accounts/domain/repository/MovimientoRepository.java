package com.zevaguillo.ms_accounts.domain.repository;

import com.zevaguillo.ms_accounts.domain.model.Movimiento;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovimientoRepository {
    Movimiento save(Movimiento movimiento);
    Optional<Movimiento> findById(String movimientoId);
    List<Movimiento> findByCuentaId(String cuentaId);
    List<Movimiento> findByCuentaIdAndFechaBetween(String cuentaId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    Optional<Movimiento> findByTransactionId(String transactionId);
    boolean existsByTransactionId(String transactionId);
}