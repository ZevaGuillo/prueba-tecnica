package com.zevaguillo.infrastructure.outbox;

import com.zevaguillo.application.port.out.OutboxEventPersistencePort;
import com.zevaguillo.domain.model.OutboxEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxEventRelayService {

    private static final Logger log = LoggerFactory.getLogger(OutboxEventRelayService.class);

    private final OutboxEventPersistencePort outboxPort;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxEventRelayService(OutboxEventPersistencePort outboxPort,
                                   @Qualifier("outboxKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxPort = outboxPort;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${outbox.relay.delay-ms:1000}")
    public void relay() {
        List<OutboxEvent> pendingEvents = outboxPort.findPendingEvents();
        for (OutboxEvent event : pendingEvents) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getAggregateId(), event.getPayload());
                outboxPort.markAsPublished(event.getId());
                log.info("Outbox event published: id={} type={} topic={}", event.getId(), event.getEventType(), event.getTopic());
            } catch (Exception e) {
                log.error("Error publishing outbox event id={}: {}", event.getId(), e.getMessage());
            }
        }
    }
}
