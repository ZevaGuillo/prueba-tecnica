package com.zevaguillo.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zevaguillo.application.exception.CuentaAlreadyExistsException;
import com.zevaguillo.application.port.in.CrearCuentaUseCase;
import com.zevaguillo.application.port.out.CuentaPersistencePort;
import com.zevaguillo.application.port.out.OutboxEventPersistencePort;
import com.zevaguillo.domain.model.Cuenta;
import com.zevaguillo.domain.model.OutboxEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CrearCuentaUseCaseImpl implements CrearCuentaUseCase {

    private final CuentaPersistencePort persistencePort;
    private final OutboxEventPersistencePort outboxPort;
    private final ObjectMapper objectMapper;

    public CrearCuentaUseCaseImpl(CuentaPersistencePort persistencePort,
                                  OutboxEventPersistencePort outboxPort,
                                  ObjectMapper objectMapper) {
        this.persistencePort = persistencePort;
        this.outboxPort = outboxPort;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Cuenta ejecutar(Cuenta cuenta) {
        if (cuenta.getNumeroCuenta() == null || cuenta.getNumeroCuenta().isBlank()) {
            throw new IllegalArgumentException("Número de cuenta es requerido");
        }
        if (cuenta.getClienteId() == null || cuenta.getClienteId().isBlank()) {
            throw new IllegalArgumentException("Cliente ID es requerido");
        }
        if (persistencePort.existsByNumeroCuenta(cuenta.getNumeroCuenta())) {
            throw new CuentaAlreadyExistsException("Número de cuenta ya existe: " + cuenta.getNumeroCuenta());
        }
        if (persistencePort.existsById(cuenta.getCuentaId())) {
            throw new CuentaAlreadyExistsException("Cuenta con ID " + cuenta.getCuentaId() + " ya existe");
        }

        if (cuenta.getEstado() == null) {
            cuenta.setEstado("ACTIVE");
        }
        if (cuenta.getSaldo() == null) {
            cuenta.setSaldo(0.0);
        }

        Cuenta guardado = persistencePort.save(cuenta);

        try {
            String payload = objectMapper.writeValueAsString(guardado);
            OutboxEvent event = new OutboxEvent(
                    UUID.randomUUID().toString(),
                    guardado.getCuentaId(),
                    "Cuenta",
                    "CuentaCreada",
                    "cuenta-creada",
                    payload,
                    "PENDING",
                    LocalDateTime.now()
            );
            outboxPort.save(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializando evento CuentaCreada", e);
        }

        return guardado;
    }
}
