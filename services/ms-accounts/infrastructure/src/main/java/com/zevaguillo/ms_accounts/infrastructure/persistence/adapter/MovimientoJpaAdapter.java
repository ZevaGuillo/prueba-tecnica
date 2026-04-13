package com.zevaguillo.ms_accounts.infrastructure.persistence.adapter;

import com.zevaguillo.ms_accounts.application.port.out.MovimientoPersistencePort;
import com.zevaguillo.ms_accounts.domain.model.Movimiento;
import com.zevaguillo.ms_accounts.infrastructure.persistence.entity.MovimientoEntity;
import com.zevaguillo.ms_accounts.infrastructure.persistence.entity.MovimientoRepository;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MovimientoJpaAdapter implements MovimientoPersistencePort {

    private final MovimientoRepository repository;

    public MovimientoJpaAdapter(MovimientoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Movimiento save(Movimiento movimiento) {
        MovimientoEntity entity = toEntity(movimiento);
        MovimientoEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Movimiento> findById(String movimientoId) {
        return repository.findById(movimientoId).map(this::toDomain);
    }

    @Override
    public List<Movimiento> findByCuentaId(String cuentaId) {
        return repository.findByCuentaId(cuentaId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movimiento> findByCuentaIdAndFechaBetween(String cuentaId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return repository.findByCuentaIdAndFechaBetween(cuentaId, fechaInicio, fechaFin)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Movimiento> findByTransactionId(String transactionId) {
        return repository.findByTransactionId(transactionId).map(this::toDomain);
    }

    @Override
    public boolean existsByTransactionId(String transactionId) {
        return repository.existsByTransactionId(transactionId);
    }

    private MovimientoEntity toEntity(Movimiento movimiento) {
        MovimientoEntity entity = new MovimientoEntity();
        entity.setMovimientoId(movimiento.getMovimientoId());
        entity.setCuentaId(movimiento.getCuentaId());
        entity.setTipoMovimiento(movimiento.getTipoMovimiento());
        entity.setValor(movimiento.getValor());
        entity.setSaldoResultante(movimiento.getSaldoResultante());
        entity.setFecha(movimiento.getFecha());
        entity.setTransactionId(movimiento.getTransactionId());
        return entity;
    }

    private Movimiento toDomain(MovimientoEntity entity) {
        Movimiento movimiento = new Movimiento();
        movimiento.setMovimientoId(entity.getMovimientoId());
        movimiento.setCuentaId(entity.getCuentaId());
        movimiento.setTipoMovimiento(entity.getTipoMovimiento());
        movimiento.setValor(entity.getValor());
        movimiento.setSaldoResultante(entity.getSaldoResultante());
        movimiento.setFecha(entity.getFecha());
        movimiento.setTransactionId(entity.getTransactionId());
        return movimiento;
    }
}