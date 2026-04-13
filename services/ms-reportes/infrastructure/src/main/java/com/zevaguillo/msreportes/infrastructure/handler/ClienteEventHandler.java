package com.zevaguillo.msreportes.infrastructure.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zevaguillo.msreportes.application.dto.event.ClienteCreadoEvent;
import com.zevaguillo.msreportes.domain.model.ProcessedEvent;
import com.zevaguillo.msreportes.domain.model.ReporteCliente;
import com.zevaguillo.msreportes.infrastructure.persistence.mapper.ReporteEntityMapper;
import com.zevaguillo.msreportes.infrastructure.persistence.repository.ProcessedEventRepository;
import com.zevaguillo.msreportes.infrastructure.persistence.repository.ReporteClienteRepository;
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

            if (processedEventRepository.existsById(eventId)) {
                log.info("Evento duplicado ignorado: {}", eventId);
                return;
            }

            ClienteCreadoEvent.Payload payload = event.getPayload();
            ReporteCliente cliente = new ReporteCliente(
                payload.getClienteId(),
                payload.getNombre(),
                payload.getIdentificacion(),
                payload.getEmail(),
                payload.getTelefono()
            );

            clienteRepository.save(ReporteEntityMapper.toEntity(cliente));
            processedEventRepository.save(ReporteEntityMapper.toEntity(new ProcessedEvent(eventId, "ClienteCreadoEvent")));

            log.info("Cliente procesado: {}", payload.getClienteId());
        } catch (Exception e) {
            log.error("Error procesando evento ClienteCreadoEvent: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
