package com.zevaguillo.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zevaguillo.application.exception.TransaccionDuplicadaException;
import com.zevaguillo.application.port.in.RegistrarMovimientoUseCase;
import com.zevaguillo.application.port.out.CuentaPersistencePort;
import com.zevaguillo.application.port.out.MovimientoPersistencePort;
import com.zevaguillo.application.port.out.OutboxEventPersistencePort;
import com.zevaguillo.domain.model.Cuenta;
import com.zevaguillo.domain.model.Movimiento;
import com.zevaguillo.domain.model.OutboxEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegistrarMovimientoUseCaseImpl implements RegistrarMovimientoUseCase {

    private static final String TIPO_RETIRO = "RETIRO";

    private final CuentaPersistencePort cuentaPersistencePort;
    private final MovimientoPersistencePort movimientoPersistencePort;
    private final OutboxEventPersistencePort outboxPort;
    private final ObjectMapper objectMapper;

    public RegistrarMovimientoUseCaseImpl(
            CuentaPersistencePort cuentaPersistencePort,
            MovimientoPersistencePort movimientoPersistencePort,
            OutboxEventPersistencePort outboxPort,
            ObjectMapper objectMapper) {
        this.cuentaPersistencePort = cuentaPersistencePort;
        this.movimientoPersistencePort = movimientoPersistencePort;
        this.outboxPort = outboxPort;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Movimiento ejecutar(Movimiento movimiento, String transactionId) {
        if (transactionId != null && !transactionId.isEmpty()) {
            if (movimientoPersistencePort.existsByTransactionId(transactionId)) {
                throw new TransaccionDuplicadaException(
                        "Transaccion duplicada: X-Transaction-Id '" + transactionId + "' ya fue procesado. " +
                        "Si quieres registrar otro movimiento, usa un X-Transaction-Id diferente."
                );
            }
        }

        if (movimiento.getCuentaId() == null) {
            throw new IllegalArgumentException("Cuenta ID es requerido");
        }
        if (movimiento.getTipoMovimiento() == null) {
            throw new IllegalArgumentException("Tipo de movimiento es requerido");
        }
        if (movimiento.getValor() == null || movimiento.getValor() <= 0) {
            throw new IllegalArgumentException("Valor debe ser mayor a 0");
        }

        Cuenta cuenta = cuentaPersistencePort.findById(movimiento.getCuentaId())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + movimiento.getCuentaId()));

        if (!cuenta.isActiva()) {
            throw new IllegalStateException("Cuenta no está activa");
        }

        Double nuevoSaldo;
        if (TIPO_RETIRO.equals(movimiento.getTipoMovimiento())) {
            if (cuenta.getSaldo() + movimiento.getValor() < 0) {
                throw new IllegalStateException("Saldo no disponible");
            }
            nuevoSaldo = cuenta.getSaldo() - movimiento.getValor();
        } else {
            nuevoSaldo = cuenta.getSaldo() + movimiento.getValor();
        }

        cuenta.setSaldo(nuevoSaldo);
        cuentaPersistencePort.save(cuenta);

        movimiento.setSaldoResultante(nuevoSaldo);
        if (transactionId != null && !transactionId.isEmpty()) {
            movimiento.setTransactionId(transactionId);
        }

        Movimiento guardado = movimientoPersistencePort.save(movimiento);

        try {
            String payload = objectMapper.writeValueAsString(guardado);
            OutboxEvent event = new OutboxEvent(
                    UUID.randomUUID().toString(),
                    guardado.getMovimientoId(),
                    "Movimiento",
                    "MovimientoRegistrado",
                    "movimiento-registrado",
                    payload,
                    "PENDING",
                    LocalDateTime.now()
            );
            outboxPort.save(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializando evento MovimientoRegistrado", e);
        }

        return guardado;
    }
}
