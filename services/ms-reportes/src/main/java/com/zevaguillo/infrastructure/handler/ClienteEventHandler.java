package com.zevaguillo.infrastructure.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.zevaguillo.application.dto.event.ClienteCreadoEvent;
import com.zevaguillo.domain.model.ProcessedEvent;
import com.zevaguillo.domain.model.ReporteCliente;
import com.zevaguillo.infrastructure.persistence.mapper.ReporteEntityMapper;
import com.zevaguillo.infrastructure.persistence.repository.ProcessedEventRepository;
import com.zevaguillo.infrastructure.persistence.repository.ReporteClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ClienteEventHandler {

    private static final Logger log = LoggerFactory.getLogger(ClienteEventHandler.class);

    private final ReporteClienteRepository clienteRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public ClienteEventHandler(ReporteClienteRepository clienteRepository,
                        ProcessedEventRepository processedEventRepository,
                        ObjectMapper objectMapper) {
        this.clienteRepository = clienteRepository;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void handle(String eventJson) {
        try {
            ClienteCreadoEvent event = objectMapper.readValue(eventJson, ClienteCreadoEvent.class);
            String eventId = event.getEventId();
            ClienteCreadoEvent.Payload payload = event.getPayload();

            if (payload == null) {
                // Compatibilidad con productores que envian el cliente en el root del JSON.
                JsonNode root = objectMapper.readTree(eventJson);
                payload = new ClienteCreadoEvent.Payload();
                payload.setClienteId(root.path("clienteId").asText(null));
                payload.setNombre(root.path("nombre").asText(null));
                payload.setIdentificacion(root.path("identificacion").asText(null));
                payload.setEmail(root.path("email").asText(null));
                payload.setTelefono(root.path("telefono").asText(null));
                if (eventId == null || eventId.isBlank()) {
                    String id = payload.getClienteId() != null ? payload.getClienteId() : payload.getIdentificacion();
                    if (id != null) {
                        eventId = "cliente-" + id;
                    }
                }
            }

            if (eventId != null && processedEventRepository.existsById(eventId)) {
                if (log.isInfoEnabled()) {
                    log.info("Evento duplicado ignorado: {}", eventId);
                }
                return;
            }

            ReporteCliente cliente = new ReporteCliente(
                payload.getClienteId(),
                payload.getNombre(),
                payload.getIdentificacion(),
                payload.getEmail(),
                payload.getTelefono()
            );

            clienteRepository.save(ReporteEntityMapper.toEntity(cliente));
            if (eventId != null && !eventId.isBlank()) {
                processedEventRepository.save(ReporteEntityMapper.toEntity(new ProcessedEvent(eventId, "ClienteCreadoEvent")));
            }

            if (log.isInfoEnabled()) {
                log.info("Cliente procesado: {}", payload.getClienteId());
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error procesando evento ClienteCreadoEvent: {}", e.getMessage());
            }
            throw new RuntimeException(e);
        }
    }
}
