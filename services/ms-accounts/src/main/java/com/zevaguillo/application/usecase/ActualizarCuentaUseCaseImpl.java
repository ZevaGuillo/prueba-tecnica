package com.zevaguillo.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zevaguillo.application.port.in.ActualizarCuentaUseCase;
import com.zevaguillo.application.port.out.CuentaPersistencePort;
import com.zevaguillo.application.port.out.OutboxEventPersistencePort;
import com.zevaguillo.domain.model.Cuenta;
import com.zevaguillo.domain.model.OutboxEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ActualizarCuentaUseCaseImpl implements ActualizarCuentaUseCase {

    private final CuentaPersistencePort persistencePort;
    private final OutboxEventPersistencePort outboxPort;
    private final ObjectMapper objectMapper;

    public ActualizarCuentaUseCaseImpl(CuentaPersistencePort persistencePort,
                                       OutboxEventPersistencePort outboxPort,
                                       ObjectMapper objectMapper) {
        this.persistencePort = persistencePort;
        this.outboxPort = outboxPort;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Cuenta ejecutar(Cuenta cuenta) {
        if (cuenta.getCuentaId() == null) {
            throw new IllegalArgumentException("Cuenta ID es requerido");
        }
        if (!persistencePort.existsById(cuenta.getCuentaId())) {
            throw new IllegalArgumentException("Cuenta no encontrada: " + cuenta.getCuentaId());
        }

        Cuenta existente = persistencePort.findById(cuenta.getCuentaId()).get();

        if (cuenta.getEstado() != null) {
            existente.setEstado(cuenta.getEstado());
        }

        Cuenta guardado = persistencePort.save(existente);

        try {
            String payload = objectMapper.writeValueAsString(guardado);
            OutboxEvent event = new OutboxEvent(
                    UUID.randomUUID().toString(),
                    guardado.getCuentaId(),
                    "Cuenta",
                    "CuentaActualizada",
                    "cuenta-actualizada",
                    payload,
                    "PENDING",
                    LocalDateTime.now()
            );
            outboxPort.save(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializando evento CuentaActualizada", e);
        }

        return guardado;
    }
}
