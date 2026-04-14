package com.zevaguillo.infrastructure.persistence.adapter;

import com.zevaguillo.application.port.out.OutboxEventPersistencePort;
import com.zevaguillo.domain.model.OutboxEvent;
import com.zevaguillo.infrastructure.persistence.entity.OutboxEventEntity;
import com.zevaguillo.infrastructure.persistence.entity.OutboxEventRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OutboxJpaAdapter implements OutboxEventPersistencePort {

    private final OutboxEventRepository repository;

    public OutboxJpaAdapter(OutboxEventRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(OutboxEvent event) {
        OutboxEventEntity entity = toEntity(event);
        repository.save(entity);
    }

    @Override
    public List<OutboxEvent> findPendingEvents() {
        return repository.findByStatus("PENDING").stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsPublished(String eventId) {
        repository.findById(eventId).ifPresent(entity -> {
            entity.setStatus("PUBLISHED");
            entity.setPublishedAt(LocalDateTime.now());
            repository.save(entity);
        });
    }

    private OutboxEventEntity toEntity(OutboxEvent event) {
        OutboxEventEntity entity = new OutboxEventEntity();
        entity.setId(event.getId());
        entity.setAggregateId(event.getAggregateId());
        entity.setAggregateType(event.getAggregateType());
        entity.setEventType(event.getEventType());
        entity.setTopic(event.getTopic());
        entity.setPayload(event.getPayload());
        entity.setStatus(event.getStatus());
        entity.setCreatedAt(event.getCreatedAt());
        entity.setPublishedAt(event.getPublishedAt());
        return entity;
    }

    private OutboxEvent toDomain(OutboxEventEntity entity) {
        OutboxEvent event = new OutboxEvent();
        event.setId(entity.getId());
        event.setAggregateId(entity.getAggregateId());
        event.setAggregateType(entity.getAggregateType());
        event.setEventType(entity.getEventType());
        event.setTopic(entity.getTopic());
        event.setPayload(entity.getPayload());
        event.setStatus(entity.getStatus());
        event.setCreatedAt(entity.getCreatedAt());
        event.setPublishedAt(entity.getPublishedAt());
        return event;
    }
}
