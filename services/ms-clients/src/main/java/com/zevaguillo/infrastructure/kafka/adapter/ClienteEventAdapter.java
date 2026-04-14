package com.zevaguillo.infrastructure.kafka.adapter;

import com.zevaguillo.application.port.out.ClienteEventPort;
import com.zevaguillo.domain.model.Cliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import org.springframework.kafka.support.SendResult;

/**
 * ClienteEventAdapter - Kafka implementation of ClienteEventPort
 * Publishes client lifecycle events to Kafka.
 */
@Component
public class ClienteEventAdapter implements ClienteEventPort {
    
    private static final Logger log = LoggerFactory.getLogger(ClienteEventAdapter.class);
    private static final String CLIENTE_EVENTS_TOPIC = "cliente-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ClienteEventAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    @Override
    public void publicarClienteCreado(Cliente cliente) {
        log.info("EVENT: ClienteCreated - clienteId: {}, identificacion: {}, nombre: {}",
                cliente.getClienteId(), cliente.getIdentificacion(), cliente.getNombre());

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(CLIENTE_EVENTS_TOPIC, cliente.getClienteId(), cliente);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Error publicando evento ClienteCreated en topic {} para clienteId {}: {}",
                        CLIENTE_EVENTS_TOPIC, cliente.getClienteId(), ex.getMessage(), ex);
                return;
            }
            log.info("Evento ClienteCreated publicado en topic {} partition {} offset {}",
                    CLIENTE_EVENTS_TOPIC,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
        });
    }
}