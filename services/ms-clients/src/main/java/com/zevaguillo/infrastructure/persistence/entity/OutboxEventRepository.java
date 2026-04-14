package com.zevaguillo.infrastructure.persistence.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, String> {
    List<OutboxEventEntity> findByStatus(String status);
}
