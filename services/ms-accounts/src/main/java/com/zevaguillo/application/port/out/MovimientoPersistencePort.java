package com.zevaguillo.application.port.out;

import com.zevaguillo.domain.model.Movimiento;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Output Port - Movimiento Persistence
 */
public interface MovimientoPersistencePort {
    Movimiento save(Movimiento movimiento);
    Optional<Movimiento> findById(String movimientoId);
    List<Movimiento> findByCuentaId(String cuentaId);
    List<Movimiento> findByCuentaIdAndFechaBetween(String cuentaId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    Optional<Movimiento> findByTransactionId(String transactionId);
    boolean existsByTransactionId(String transactionId);
}