package com.zevaguillo.application.port.out;

import com.zevaguillo.domain.model.OutboxEvent;

import java.util.List;

public interface OutboxEventPersistencePort {
    void save(OutboxEvent event);
    List<OutboxEvent> findPendingEvents();
    void markAsPublished(String eventId);
}
