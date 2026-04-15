package com.zevaguillo.infrastructure.kafka.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zevaguillo.application.port.out.ClienteCachePersistencePort;
import com.zevaguillo.domain.model.ClienteCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ClienteEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ClienteEventConsumer.class);

    private final ClienteCachePersistencePort cachePersistencePort;
    private final ObjectMapper objectMapper;

    public ClienteEventConsumer(ClienteCachePersistencePort cachePersistencePort,
                                ObjectMapper objectMapper) {
        this.cachePersistencePort = cachePersistencePort;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "cliente-events", groupId = "ms-accounts-group")
    public void consume(String message) {
        log.info("Recibido evento de cliente en ms-accounts: {}", message);
        try {
            JsonNode root = objectMapper.readTree(message);

            String clienteId = extractField(root, "clienteId");
            String nombre = extractField(root, "nombre");
            String estado = extractField(root, "estado");

            if (clienteId == null || clienteId.isBlank()) {
                log.warn("Evento de cliente sin clienteId, ignorado: {}", message);
                return;
            }

            ClienteCache cache = new ClienteCache(
                    clienteId,
                    nombre,
                    estado != null ? estado : "ACTIVE",
                    LocalDateTime.now()
            );
            cachePersistencePort.upsert(cache);
            log.info("Cliente sincronizado en cache: clienteId={}", clienteId);

        } catch (Exception e) {
            log.error("Error procesando evento de cliente en cache: {}", e.getMessage());
        }
    }

    /**
     * Extrae un campo del JSON soportando dos formatos:
     * - Payload plano: { "clienteId": "...", "nombre": "..." }
     * - Payload envuelto: { "eventId": "...", "payload": { "clienteId": "...", "nombre": "..." } }
     */
    private String extractField(JsonNode root, String field) {
        JsonNode payload = root.path("payload");
        if (!payload.isMissingNode() && !payload.isNull()) {
            String value = payload.path(field).asText(null);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        String value = root.path(field).asText(null);
        return (value != null && !value.isBlank()) ? value : null;
    }
}
