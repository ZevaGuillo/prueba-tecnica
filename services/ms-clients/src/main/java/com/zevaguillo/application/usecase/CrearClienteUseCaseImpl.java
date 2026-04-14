package com.zevaguillo.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zevaguillo.application.exception.ClienteYaExisteException;
import com.zevaguillo.application.port.in.CrearClienteUseCase;
import com.zevaguillo.application.port.out.ClientePersistencePort;
import com.zevaguillo.application.port.out.OutboxEventPersistencePort;
import com.zevaguillo.domain.model.Cliente;
import com.zevaguillo.domain.model.OutboxEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CrearClienteUseCaseImpl implements CrearClienteUseCase {

    private final ClientePersistencePort persistencePort;
    private final OutboxEventPersistencePort outboxPort;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public CrearClienteUseCaseImpl(ClientePersistencePort persistencePort,
                                   OutboxEventPersistencePort outboxPort,
                                   PasswordEncoder passwordEncoder,
                                   ObjectMapper objectMapper) {
        this.persistencePort = persistencePort;
        this.outboxPort = outboxPort;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Cliente ejecutar(Cliente cliente) {
        if (cliente.getIdentificacion() == null || cliente.getIdentificacion().isEmpty()) {
            throw new IllegalArgumentException("Identificacion es requerida");
        }

        if (cliente.getNombre() == null || cliente.getNombre().isEmpty()) {
            throw new IllegalArgumentException("Nombre es requerido");
        }

        if (cliente.getClienteId() == null || cliente.getClienteId().isBlank()) {
            throw new IllegalArgumentException("Cliente ID es requerido");
        }

        if (persistencePort.existsById(cliente.getClienteId())) {
            throw new ClienteYaExisteException("Ya existe un cliente con ID: " + cliente.getClienteId());
        }

        if (cliente.getEstado() == null) {
            cliente.setEstado("ACTIVE");
        }

        if (cliente.getContrasena() == null || cliente.getContrasena().isBlank()) {
            throw new IllegalArgumentException("Contrasena es requerida");
        }
        cliente.setContrasena(passwordEncoder.encode(cliente.getContrasena()));

        Cliente guardado = persistencePort.save(cliente);

        try {
            String payload = objectMapper.writeValueAsString(guardado);
            OutboxEvent event = new OutboxEvent(
                    UUID.randomUUID().toString(),
                    guardado.getClienteId(),
                    "Cliente",
                    "ClienteCreado",
                    "cliente-events",
                    payload,
                    "PENDING",
                    LocalDateTime.now()
            );
            outboxPort.save(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializando evento ClienteCreado", e);
        }

        return guardado;
    }
}
